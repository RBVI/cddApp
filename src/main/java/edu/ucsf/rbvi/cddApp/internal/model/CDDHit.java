package edu.ucsf.rbvi.cddApp.internal.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

import edu.ucsf.rbvi.cddApp.internal.util.CyUtils;

/**
 * CDDHit
 * 
 */
public class CDDHit {
	final static String CDD_ACCESSION = "CDD-Accession"; // accession number of CDD domain
	final static String CDD_NAME = "CDD-Name"; // accession number of CDD domain
	final static String PDB_CHAIN = "PDB-Chain"; // if it is a PDB ID, the chain ID (in the form of PDB ID + chain)
	final static String CDD_HIT_TYPE = "CDD-Hit-Type"; // type of domain
	final static String CDD_FROM = "CDD-From"; // lower limit of the domain
	final static String CDD_TO = "CDD-To"; // upper limit of the domain

	public static void createHitColumns(CyNetwork network) {
		CyTable nodeTable = network.getDefaultNodeTable();
		CyUtils.createColumn(nodeTable, CDD_ACCESSION, List.class, String.class);
		CyUtils.createColumn(nodeTable, CDD_NAME, List.class, String.class);
		CyUtils.createColumn(nodeTable, PDB_CHAIN, List.class, String.class);
		CyUtils.createColumn(nodeTable, CDD_HIT_TYPE, List.class, String.class);
		CyUtils.createColumn(nodeTable, CDD_FROM, List.class, Long.class);
		CyUtils.createColumn(nodeTable, CDD_TO, List.class, Long.class);
	}

	public static void updateColumns(CyNetwork network, Map<CyIdentifiable, List<CDDHit>> hitMap) {
		for (CyIdentifiable cyId: hitMap.keySet()) {
			updateStringColumn(network, cyId, CDD_ACCESSION, hitMap.get(cyId));
			updateStringColumn(network, cyId, CDD_NAME, hitMap.get(cyId));
			updateStringColumn(network, cyId, PDB_CHAIN, hitMap.get(cyId));
			updateStringColumn(network, cyId, CDD_HIT_TYPE, hitMap.get(cyId));
			updateLongColumn(network, cyId, CDD_FROM, hitMap.get(cyId));
			updateLongColumn(network, cyId, CDD_TO, hitMap.get(cyId));
		}
	}

	public static List<CDDHit> reloadHits(CyNetwork network, CyIdentifiable id) {
		if (!CyUtils.checkColumn(network.getDefaultNodeTable(), CDD_ACCESSION, List.class, String.class))
			return null;

		List<String> accessions = network.getRow(id).getList(CDD_ACCESSION, String.class);
		List<String> names = network.getRow(id).getList(CDD_NAME, String.class);
		List<String> types = network.getRow(id).getList(CDD_HIT_TYPE, String.class);
		List<String> chains = network.getRow(id).getList(PDB_CHAIN, String.class);
		List<Long> fromList = network.getRow(id).getList(CDD_FROM, Long.class);
		List<Long> toList = network.getRow(id).getList(CDD_TO, Long.class);

		if (accessions.size() != names.size() ||
				accessions.size() != chains.size() ||
				accessions.size() != types.size() ||
				accessions.size() != fromList.size() ||
				accessions.size() != toList.size())
			return null;

		List<CDDHit> cddHits = new ArrayList<CDDHit>();
		for (int i = 0; i < accessions.size(); i++) {
			CDDHit hit = new CDDHit(chains.get(i), accessions.get(i), names.get(i), types.get(i),
			                        fromList.get(i), toList.get(i));
			cddHits.add(hit);
		}
		return cddHits;
	}

	/**
	 * Return the list of chains for this identifiable.  This list is actually a
	 * list of chains or a list of protein names.
	 */
	public static List<String> getChains(CyNetwork network, CyIdentifiable cyId) {
		if (CyUtils.checkColumn(network.getDefaultNodeTable(), PDB_CHAIN, List.class, String.class))
			return network.getRow(cyId).getList(PDB_CHAIN, String.class);
		return null;
	}

	private static void updateLongColumn(CyNetwork network, CyIdentifiable cyId,
	                                     String columnName, List<CDDHit> hits) {
		List<Long> dataList = new ArrayList<Long>();
		for (CDDHit hit: hits) {
			if (columnName.equals(CDD_FROM))
				dataList.add(hit.getFrom());
			else if (columnName.equals(CDD_TO))
				dataList.add(hit.getTo());
		}
		network.getRow(cyId).set(columnName, dataList);
	}

	private static void updateStringColumn(CyNetwork network, CyIdentifiable cyId,
	                                       String columnName, List<CDDHit> hits) {
		List<String> dataList = new ArrayList<String>();
		for (CDDHit hit: hits) {
			if (columnName.equals(CDD_ACCESSION))
				dataList.add(hit.getAccession());
			else if (columnName.equals(CDD_NAME))
				dataList.add(hit.getName());
			else if (columnName.equals(CDD_HIT_TYPE))
				dataList.add(hit.getHitType());
			else if (columnName.equals(PDB_CHAIN))
				dataList.add(hit.getProteinId());
		}
		network.getRow(cyId).set(columnName, dataList);
	}

	String proteinId;
	String hitType;
	String accession;
	String name;
	long from;
	long to;

	public CDDHit() {
	}

	public CDDHit(String proteinId, String accession, String name, String hitType, long from, long to) {
			this.proteinId = proteinId;
			this.hitType = hitType;
			this.accession = accession;
			this.name = name;
			this.to = to;
			this.from = from;
	}

	public CDDHit(String line) {
		String[] record = line.split("\t");

		// Special processing for PDB chains?
		this.proteinId = record[0].split(" ")[2].split("\\(")[0];
		this.hitType = record[1];
		this.accession = record[7];
		this.name = record[8];
		this.from = Integer.parseInt(record[3]);
		this.to = Integer.parseInt(record[4]);
	}

	public String getProteinId() {
		return proteinId;
	}

	public String getHitType() {
		return hitType;
	}

	public String getAccession() {
		return accession;
	}

	public String getName() {
		return name;
	}

	public long getFrom() {
		return from;
	}

	public long getTo() {
		return to;
	}
}
