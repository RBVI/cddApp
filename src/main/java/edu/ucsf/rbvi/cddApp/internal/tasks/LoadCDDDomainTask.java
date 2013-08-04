package edu.ucsf.rbvi.cddApp.internal.tasks;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyTable;
import org.cytoscape.task.AbstractTableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskMonitor.Level;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

public class LoadCDDDomainTask extends AbstractTableTask {

	@Tunable(description="Choose column to load domains from")
	public ListSingleSelection<String> loadColumn;
	private CyTable table;
	private List<Long> entry = null;
	
	public LoadCDDDomainTask(CyTable table) {
		super(table);
		ArrayList<String> columns = new ArrayList<String>();
		for (CyColumn c: table.getColumns()) {
			columns.add(c.getName());
		}
		loadColumn = new ListSingleSelection<String>(columns);
		this.table = table;
	}

	public void setEntry(List<Long> entry) {this.entry = entry;}
	
	@Override
	public void run(TaskMonitor monitor) throws Exception {
		monitor.setTitle("Load CDD Domains");
		monitor.setStatusMessage("Load CDD Domains");
		String queries = null, pdbQueries = null, colName = loadColumn.getSelectedValue();
		HashMap<String, Long> idTable = new HashMap<String, Long>();
		HashMap<String, List<String>> pdbIdTable;
		HashMap<String, String> revPdbIdTable = new HashMap<String, String>();
		List<Long> queryRange;
		List<String> pdbId;
		if (entry == null)
			queryRange = table.getPrimaryKey().getValues(Long.class);
		else {
			queryRange = entry;
		}
		for (long cyId: queryRange) {
			String proteinId = table.getRow(cyId).get(colName, String.class);
			if (pdbQueries == null) pdbQueries = "structureId=" + proteinId;
			else pdbQueries = pdbQueries + "," + proteinId;
		}
		pdbId = validPDBId(monitor, new URL("http://www.rcsb.org/pdb/rest/idStatus?" + pdbQueries));
		String cleanedPdbQueries = null;
		for (String s: pdbId) {
			if (cleanedPdbQueries == null) cleanedPdbQueries = "structureId=" + s;
			else cleanedPdbQueries = cleanedPdbQueries + "," + s;
		}
		pdbIdTable = retrieveFromPDB(monitor, new URL("http://www.rcsb.org/pdb/rest/describeMol?" + cleanedPdbQueries));
		for (String s: pdbIdTable.keySet()) {
			System.out.println(s);
			for (String s1: pdbIdTable.get(s)) System.out.println(s1);
		}
		for (String s: pdbId) System.out.println(s);
		
		for (long cyId: queryRange) {
			String proteinIdParent = table.getRow(cyId).get(colName, String.class);
			List<String> proteinIds = pdbIdTable.get(proteinIdParent);
			if (proteinIds == null) {
				proteinIds = new ArrayList<String>();
				proteinIds.add(proteinIdParent);
			}
			idTable.put(proteinIdParent, cyId);
			for (String proteinId: proteinIds) {
				if (queries == null) queries = "queries=" + proteinId;
				else
					queries = queries + "&queries=" + proteinId;
				revPdbIdTable.put(proteinId, proteinIdParent);
			}
		}
		
		BufferedReader in = retrieveFromDatabase(monitor, new URL("http://www.ncbi.nlm.nih.gov/Structure/bwrpsb/bwrpsb.cgi"), queries + "&db=cdd&smode=auto&useid1=true&filter=true&&evalue=0.01&tdata=hits&dmode=rep&qdefl=false&ccdefl=false");
		monitor.setStatusMessage("Downloading domain information...");
		if (table.getColumn("CDD-Accession") == null)
			table.createListColumn("CDD-Accession", String.class, false);
		if (table.getColumn("PDB-Chain") == null)
			table.createListColumn("PDB-Chain", String.class, false);;
		if (table.getColumn("CDD-Hit-Type") == null)
			table.createListColumn("CDD-Hit-Type", String.class, false);
		if (table.getColumn("CDD-From") == null)
			table.createListColumn("CDD-From", Long.class, false);
		if (table.getColumn("CDD-To") == null)
			table.createListColumn("CDD-To", Long.class, false);
		HashMap<String, List<String>>	accessionMap = new HashMap<String, List<String>>(),
										pdbChainMap = new HashMap<String, List<String>>(),
										hitTypeMap = new HashMap<String, List<String>>();
		HashMap<String, List<Long>>	fromMap = new HashMap<String, List<Long>>(),
									toMap = new HashMap<String, List<Long>>();
		String line;
		while ((line = in.readLine()) != null) {
			try {
				String[] record = line.split("\t");
				String	proteinIdChain = record[0].split(" ")[2].split("\\(")[0],
						proteinId = revPdbIdTable.get(proteinIdChain),
						hitType = record[1],
						accession = record[7];
				long	from = Integer.parseInt(record[3]),
						to = Integer.parseInt(record[4]);
			//	System.out.println(proteinId);
			//	System.out.println(accession);
				if (! accessionMap.containsKey(proteinId)) 
					accessionMap.put(proteinId, new ArrayList<String>());
				accessionMap.get(proteinId).add(accession);
				if (! pdbChainMap.containsKey(proteinId))
					pdbChainMap.put(proteinId, new ArrayList<String>());
				pdbChainMap.get(proteinId).add(proteinIdChain);
				if (!hitTypeMap.containsKey(proteinId))
					hitTypeMap.put(proteinId, new ArrayList<String>());
				hitTypeMap.get(proteinId).add(hitType);
				if (!fromMap.containsKey(proteinId))
					fromMap.put(proteinId, new ArrayList<Long>());
				fromMap.get(proteinId).add(from);
				if (!toMap.containsKey(proteinId))
					toMap.put(proteinId, new ArrayList<Long>());
				toMap.get(proteinId).add(to);
			} catch (ArrayIndexOutOfBoundsException e) {}
		}
		in.close();
		for (String s: accessionMap.keySet()) {
			table.getRow(idTable.get(s)).set("CDD-Accession", accessionMap.get(s));
			table.getRow(idTable.get(s)).set("PDB-Chain", pdbChainMap.get(s));
			table.getRow(idTable.get(s)).set("CDD-Hit-Type", hitTypeMap.get(s));
			table.getRow(idTable.get(s)).set("CDD-From", fromMap.get(s));
			table.getRow(idTable.get(s)).set("CDD-To", toMap.get(s));
		}
		
		in = retrieveFromDatabase(monitor, new URL("http://www.ncbi.nlm.nih.gov/Structure/bwrpsb/bwrpsb.cgi"), queries + "&db=cdd&smode=auto&useid1=true&filter=true&&evalue=0.01&tdata=feats&dmode=rep&qdefl=false&ccdefl=false");
		monitor.setStatusMessage("Downloading functional site information...");
		if (table.getColumn("CDD-Feature") == null)
			table.createListColumn("CDD-Feature", String.class, false);
		if (table.getColumn("PDB-Chain-Features") == null)
			table.createListColumn("PDB-Chain-Features", String.class, false);
		if (table.getColumn("CDD-Feature-Type") == null)
			table.createListColumn("CDD-Feature-Type", String.class, false);
		if (table.getColumn("CDD-Feature-Site") == null)
			table.createListColumn("CDD-Feature-Site", String.class, false);
		accessionMap = new HashMap<String, List<String>>();
		HashMap<String, List<String>>	featuresPdbChain = new HashMap<String, List<String>>(),
										featureTypeMap = new HashMap<String, List<String>>(),
										featureSiteMap = new HashMap<String, List<String>>();
		while ((line = in.readLine()) != null) {
			try {
				String[] record = line.split("\t");
				String	proteinIdChain = record[0].split(" ")[2].split("\\(")[0],
						proteinId = revPdbIdTable.get(proteinIdChain),
						featureType = record[1],
						accession = record[2],
						featureSite = record[3];
			//	System.out.println(proteinId);
			//	System.out.println(accession);
				if (! accessionMap.containsKey(proteinId)) 
					accessionMap.put(proteinId, new ArrayList<String>());
				accessionMap.get(proteinId).add(accession);
				if (!featuresPdbChain.containsKey(proteinId))
					featuresPdbChain.put(proteinId, new ArrayList<String>());
				featuresPdbChain.get(proteinId).add(proteinIdChain);
				if (! featureTypeMap.containsKey(proteinId))
					featureTypeMap.put(proteinId, new ArrayList<String>());
				featureTypeMap.get(proteinId).add(featureType);
				if (! featureSiteMap.containsKey(proteinId))
					featureSiteMap.put(proteinId, new ArrayList<String>());
				featureSiteMap.get(proteinId).add(featureSite);
			} catch (ArrayIndexOutOfBoundsException e) {}
		}
		in.close();
		for (String s: accessionMap.keySet()) {
			table.getRow(idTable.get(s)).set("CDD-Feature", accessionMap.get(s));
			table.getRow(idTable.get(s)).set("PDB-Chain-Features", featuresPdbChain.get(s));
			table.getRow(idTable.get(s)).set("CDD-Feature-Type", featureTypeMap.get(s));
			table.getRow(idTable.get(s)).set("CDD-Feature-Site", featureSiteMap.get(s));
		}
		monitor.setStatusMessage("Finished.");
	}
	
	private BufferedReader retrieveFromDatabase(TaskMonitor monitor, URL url, String postParams) throws Exception {
		monitor.setStatusMessage("Connecting to NCBI CDD...");
	//	URL url = new URL("http://www.ncbi.nlm.nih.gov/Structure/bwrpsb/bwrpsb.cgi");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestMethod("POST");

		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(postParams);
		wr.flush();
		wr.close();
		
		int status = -1;
		String cdsid = null;
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String line;
		while ((line = in.readLine()) != null) {
			String[] record = line.split("\\s");
			if (record[0].equals("#status")) status = Integer.parseInt(record[1]);
			if (record[0].equals("#cdsid")) cdsid = record[1];
		//	System.out.println(line);
		}
		in.close();
	//	System.out.println(status);
	//	System.out.println(cdsid);
		
		monitor.setStatusMessage("Waiting for response from CDD database...");
		while (status != 0) {
			Thread.sleep(5000);
			url = new URL("http://www.ncbi.nlm.nih.gov/Structure/bwrpsb/bwrpsb.cgi");
			con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestMethod("POST");
			wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes("cdsid=" + cdsid);
			wr.flush();
			wr.close();
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			while ((line = in.readLine()) != null) {
				String[] record = line.split("\\s");
				try {
				if (record[0].equals("#status"))
					status = Integer.parseInt(record[1]);
				} catch (NumberFormatException e) {}
				if (status == 0) break;
			//	System.out.println(line);
			}
			if (status != 0) in.close();
		//	System.out.println(status);
		//	System.out.println(cdsid);
		}
		return in;
	}
	
	private HashMap<String, List<String>> retrieveFromPDB(TaskMonitor monitor, URL url) throws Exception {
		monitor.setStatusMessage("Downloading chains from PDB...");
	//	URL url = new URL("http://www.ncbi.nlm.nih.gov/Structure/bwrpsb/bwrpsb.cgi");
		HashMap<String, List<String>> pdbIdMap = new HashMap<String, List<String>>();
		Pattern r = Pattern.compile("<chain id=\"([A-Z])\""),
				s = Pattern.compile("<structureId id=\"(.*?)\"");
		String pdbFile = null;
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		
		con.setDoOutput(true);
		con.setDoInput(true);
		
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String line;
		while ((line = in.readLine()) != null) {
			Matcher m = r.matcher(line), m1 = s.matcher(line);
			String chain = null;
			if (m1.find()) pdbFile = m1.group(1);
			if (m.find()) chain = m.group(1);
			if (pdbFile != null && chain != null) {
				List<String> chains = pdbIdMap.get(pdbFile);
				if (chains == null) {
					chains = new ArrayList<String>();
					pdbIdMap.put(pdbFile, chains);
				}
				chains.add(pdbFile + chain);
			}
		//	System.out.println(line);
		}
		in.close();
		return pdbIdMap;
	}
	
	private List<String> validPDBId(TaskMonitor monitor, URL url) throws Exception {
		monitor.setStatusMessage("Downloading valid PDB IDs...");
	//	URL url = new URL("http://www.ncbi.nlm.nih.gov/Structure/bwrpsb/bwrpsb.cgi");
		List<String> pdbIdList = new ArrayList<String>();
		Pattern r = Pattern.compile("structureId=\"(.*?)\" status=\"(.*?)\"");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		
		con.setDoOutput(true);
		con.setDoInput(true);
		
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String line;
		while ((line = in.readLine()) != null) {
			Matcher m = r.matcher(line);
			if (m.find()) {
				String id = m.group(1), status = m.group(2);
				if (status.equals("CURRENT") || status.equals("OBSOLETE")) pdbIdList.add(id);
			}
		}
		in.close();
		return pdbIdList;
	}
}
