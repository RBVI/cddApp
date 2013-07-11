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
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

public class LoadCDDDomainTask extends AbstractTableTask {

	@Tunable(description="Choose column to load domains from")
	public ListSingleSelection<String> loadColumn;
	private CyTable table;
	
	public LoadCDDDomainTask(CyTable table) {
		super(table);
		ArrayList<String> columns = new ArrayList<String>();
		for (CyColumn c: table.getColumns()) {
			columns.add(c.getName());
		}
		loadColumn = new ListSingleSelection<String>(columns);
		this.table = table;
	}

	@Override
	public void run(TaskMonitor arg0) throws Exception {
		String queries = null, colName = loadColumn.getSelectedValue();
		HashMap<String, Long> idTable = new HashMap<String, Long>();
		for (long cyId: table.getPrimaryKey().getValues(Long.class)) {
			String proteinId = table.getRow(cyId).get(colName, String.class);
			if (queries == null) queries = "queries=" + proteinId;
			else
				queries = queries + "&queries=" + proteinId;
			idTable.put(proteinId, cyId);
		}
		URL url = new URL("http://www.ncbi.nlm.nih.gov/Structure/bwrpsb/bwrpsb.cgi");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestMethod("POST");

		String query = queries + "&db=cdd&smode=auto&useid1=true&filter=true&&evalue=0.01&tdata=hits&dmode=rep&qdefl=false&ccdefl=false";
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(query);
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
		
		if (table.getColumn("CDD-Accession") == null)
			table.createListColumn("CDD-Accession", String.class, false);
		HashMap<String, List<String>> accessionMap = new HashMap<String, List<String>>(); 
		while ((line = in.readLine()) != null) {
			try {
				String[] record = line.split("\t");
				String	proteinId = record[0].split(" ")[2].split("\\(")[0],
						accession = record[7];
			//	System.out.println(proteinId);
			//	System.out.println(accession);
				if (! accessionMap.containsKey(proteinId)) 
					accessionMap.put(proteinId, new ArrayList<String>());
				accessionMap.get(proteinId).add(accession);
			} catch (ArrayIndexOutOfBoundsException e) {}
		}
		in.close();
		for (String s: accessionMap.keySet()) {
			table.getRow(idTable.get(s)).set("CDD-Accession", accessionMap.get(s));
		}
	}
}
