package edu.ucsf.rbvi.cddApp.internal.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cytoscape.model.CyIdentifiable;

import edu.ucsf.rbvi.cddApp.internal.model.CDDHit;
import edu.ucsf.rbvi.cddApp.internal.model.CDDFeature;
import edu.ucsf.rbvi.cddApp.internal.model.PDBStructure;

public class NetUtils {
	final static String RCSB_ID = "http://www.rcsb.org/pdb/rest/idStatus";
	final static String RCSB_DESCRIBE = "http://www.rcsb.org/pdb/rest/describeMol";
	final static String CDD_BATCH_URL = "http://www.ncbi.nlm.nih.gov/Structure/bwrpsb/bwrpsb.cgi";
	final static String CDD_BATCH_QUERY = "db=cdd&smode=auto&useid1=true&filter=true&evalue=0.01&dmode=rep&qdefl=false&ccdefl=false";
	final static String CDD_TERM_URL = "http://www.ncbi.nlm.nih.gov/cdd/";

	/**
	 * Get the hits from CDD.  This should be easier than it is.  By going through the
	 * search interface we pay a lot of unnecessary overhead.  At some point, this should
	 * be refactored to just pull this information directly by ID
	 */
	public static Map<CyIdentifiable, List<CDDHit>>
					getHitsFromCDD(String queryString, Map<String, CyIdentifiable> reverseMap) throws Exception {
		Map<CyIdentifiable, List<CDDHit>> hitMap = new HashMap<CyIdentifiable, List<CDDHit>>();
		BufferedReader in = queryCDD("hits", queryString);
		List<String> inputLines = new ArrayList<String>();
		String line;
		while ((line  = in.readLine()) != null) {
			System.out.println("Hit line from CDD: "+line);
			line = line.trim();
			// Skip over the header lines
			if (line.startsWith("#") || line.startsWith("Query") || line.length()==0) continue;
			CDDHit hit = new CDDHit(line);
			if (reverseMap.containsKey(hit.getProteinId())) {
				CyIdentifiable cyId = reverseMap.get(hit.getProteinId());
				if (!hitMap.containsKey(cyId))
					hitMap.put(cyId, new ArrayList<CDDHit>());
				hitMap.get(cyId).add(hit);
			}
		}
		return hitMap;
	}

	public static Map<CyIdentifiable, List<CDDFeature>>
					getFeaturesFromCDD(String queryString, Map<String, CyIdentifiable> reverseMap) 
					throws Exception {

		Map<CyIdentifiable, List<CDDFeature>> featureMap = new HashMap<CyIdentifiable, List<CDDFeature>>();
		BufferedReader in = queryCDD("feats", queryString);
		List<String> inputLines = new ArrayList<String>();
		String line;
		while ((line  = in.readLine()) != null) {
			System.out.println("Feature line from CDD: "+line);
			line = line.trim();
			// Skip over the header lines
			if (line.startsWith("#") || line.startsWith("Query") || line.length()==0) continue;
			CDDFeature feature = new CDDFeature(line);
			if (reverseMap.containsKey(feature.getProteinId())) {
				CyIdentifiable cyId = reverseMap.get(feature.getProteinId());
				if (!featureMap.containsKey(cyId))
					featureMap.put(cyId, new ArrayList<CDDFeature>());
				featureMap.get(cyId).add(feature);
			}
		}
		return featureMap;
	}

	/**
	 * Modify the reverseMap so that only valid PDB IDs remain.
	 */
	public static void validatePDBIds(String queryString,
	                                  Map<String, CyIdentifiable> reverseMap) throws Exception {
		URL url = new URL(RCSB_ID+"?"+queryString);
		System.out.println("Querying "+RCSB_ID+" with query: \n-->"+queryString);
		Pattern r = Pattern.compile("structureId=\"(.*?)\" status=\"(.*?)\"");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(false);
		con.setDoInput(true);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String line;
		while ((line = in.readLine()) != null) {
			Matcher m = r.matcher(line);
			System.out.println("ID line from RCSB: "+line);
			if (m.find()) {
				String id = m.group(1), status = m.group(2);
				if (!status.equals("CURRENT") && !status.equals("OBSOLETE")) {
					reverseMap.remove(id);
				}
			}
		}
		in.close();
	}

	/**
	 * Get the list of chains for each PDB ID
	 */
	public static Map<String, CyIdentifiable> getPDBChains(String queryString, 
	                                                       Map<String, CyIdentifiable>reverseMap,
	                                                       Map<CyIdentifiable, List<PDBStructure>>chainMap)
					                                               throws Exception{
		Map<String, CyIdentifiable> newReverseMap = new HashMap<String, CyIdentifiable>();
		Map<String, PDBStructure> structureMap = new HashMap<String, PDBStructure>();

		URL url = new URL(RCSB_DESCRIBE+"?"+queryString);
		System.out.println("Querying "+RCSB_DESCRIBE+" with query: \n-->"+queryString);
		Pattern r = Pattern.compile("<chain id=\"([A-Z])\"");
		Pattern s = Pattern.compile("<structureId id=\"(.*?)\"");
		String structure = null;
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(false);
		con.setDoInput(true);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String line;
		while ((line = in.readLine()) != null) {
			System.out.println("DESCRIBE line from RCSB: "+line);
			Matcher mr = r.matcher(line);
			Matcher ms = s.matcher(line);
			String chain = null;
			if (ms.find()) structure = ms.group(1);
			if (mr.find()) chain = mr.group(1);
			if (structure != null && chain != null) {
				CyIdentifiable id = reverseMap.get(structure);
				System.out.println("Adding "+structure+"."+chain+" to id "+id.toString());
				newReverseMap.put(structure+"."+chain, id);
				if (!chainMap.containsKey(id))
					chainMap.put(id, new ArrayList<PDBStructure>());

				if (!structureMap.containsKey(structure))
					structureMap.put(structure, new PDBStructure(structure, null));

				structureMap.get(structure).addChain(chain);
				chainMap.get(id).add(structureMap.get(structure));
			}
		}
		in.close();
		return newReverseMap;
	}

	public static String buildPDBQuery(Map<String, CyIdentifiable> reverseMap, 
	                                   Map<CyIdentifiable, List<String>> idMap) {
		String query = null;
		for (CyIdentifiable id: idMap.keySet()) {
			List<String> idList = idMap.get(id);
			if (idList == null || idList.size() == 0) continue;
			for (String proteinId: idList) {
				if (query == null) 
					query = "structureId="+proteinId;
				else
					query += ","+proteinId;
				reverseMap.put(proteinId, id);
			}
		}
		return query;
	}

	public static String buildPDBQuery(Map<String, CyIdentifiable> reverseMap) {
		String query = null;
		for (String q: reverseMap.keySet()) {
			System.out.println("Node id for structure "+q+" is "+reverseMap.get(q));
			if (query == null && q != null && q.length() > 0)
				query = "structureId="+q;
			else if (q != null && q.length() > 0)
				query += ","+q;
		}
		return query;
	}

	public static String buildCDDQuery(Map<String, CyIdentifiable> reverseMap, 
	                                   Map<CyIdentifiable, List<String>> idMap) {
		String query = "";
		for (CyIdentifiable id: idMap.keySet()) {
			List<String> idList = idMap.get(id);
			if (idList == null || idList.size() == 0) continue;
			for (String proteinId: idList) {
				query += "&queries="+proteinId;
				reverseMap.put(proteinId, id);
			}
		}
		return query;
	}

	public static String buildCDDQuery(Map<String, CyIdentifiable> reverseMap) { 
		String query = "";
		for (String q: reverseMap.keySet()) {
			query += "&queries="+q;
		}
		return query;
	}


	public static BufferedReader queryCDD (String dataType, String query) throws Exception {
		URL url = new URL(CDD_BATCH_URL);
		String queryString = CDD_BATCH_QUERY+"&tdata="+dataType+query;
		System.out.println("Querying "+CDD_BATCH_URL+" with query: \n-->"+queryString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestMethod("POST");

		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(queryString);
		wr.flush();
		wr.close();

		int status = -1;
		String cdsid = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String line;
		while ((line = in.readLine()) != null) {
			String[] record = line.split("\\s");
			// if (record[0].equals("#status")) status = Integer.parseInt(record[1]);
			if (record[0].equals("#cdsid")) cdsid = record[1];
		}
		in.close();

		System.out.println("cdsid = "+cdsid);

		while (status != 0) {
			Thread.sleep(500);
			url = new URL(CDD_BATCH_URL+"?cdsid="+cdsid);
			con = (HttpURLConnection) url.openConnection();
			con.setDoInput(true);
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			while ((line = in.readLine()) != null) {
				String[] record = line.split("\\s");
				try {
					if (record[0].equals("#status"))
						status = Integer.parseInt(record[1]);
				} catch (NumberFormatException e) {}
				if (status == 0) break;
			}
			if (status != 0) in.close();
		}
		return in;
	}

	public static String makeCDDLink(String text, String term) {
		return "<a href="+CDD_TERM_URL+"?term="+term+">"+text+"</a>";
	}

	public static String makeCDDURL(String term) {
		return CDD_TERM_URL+"?term="+term;
	}
}
