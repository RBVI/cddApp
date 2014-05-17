package edu.ucsf.rbvi.cddApp.internal.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

import edu.ucsf.rbvi.cddApp.internal.util.CyUtils;

/**
 * CDDFeature
 * 
 */
public class CDDFeature {
	final static String CDD_FEATURE = "CDD-Feature"; // accession number of CDD domain
	final static String PDB_CHAIN_FEATURES = "PDB-Chain-Features"; // if it is a PDB ID, the chain ID (in the form of PDB ID + chain)
	final static String CDD_FEATURE_TYPE = "CDD-Feature-Type"; // type of domain
	final static String CDD_FEATURE_SITE = "CDD-Feature-Site"; // lower limit of the domain

	public static void createFeatureColumns(CyNetwork network) {
		CyTable nodeTable = network.getDefaultNodeTable();
		CyUtils.createColumn(nodeTable, CDD_FEATURE, List.class, String.class);
		CyUtils.createColumn(nodeTable, PDB_CHAIN_FEATURES, List.class, String.class);
		CyUtils.createColumn(nodeTable, CDD_FEATURE_TYPE, List.class, String.class);
		CyUtils.createColumn(nodeTable, CDD_FEATURE_SITE, List.class, String.class);
	}

	public static void updateColumns(CyNetwork network, Map<CyIdentifiable, List<CDDFeature>> featureMap) {
		for (CyIdentifiable cyId: featureMap.keySet()) {
			updateStringColumn(network, cyId, CDD_FEATURE, featureMap.get(cyId));
			updateStringColumn(network, cyId, PDB_CHAIN_FEATURES, featureMap.get(cyId));
			updateStringColumn(network, cyId, CDD_FEATURE_TYPE, featureMap.get(cyId));
			updateStringColumn(network, cyId, CDD_FEATURE_SITE, featureMap.get(cyId));
		}
	}
	
	public static List<CDDFeature> reloadFeatures(CyNetwork network, CyIdentifiable id) {
		if (!CyUtils.checkColumn(network.getDefaultNodeTable(), CDD_FEATURE, List.class, String.class))
			return null;

		List<String> features = network.getRow(id).getList(CDD_FEATURE, String.class);
		List<String> chainFeatures = network.getRow(id).getList(PDB_CHAIN_FEATURES, String.class);
		List<String> types = network.getRow(id).getList(CDD_FEATURE_TYPE, String.class);
		List<String> sites = network.getRow(id).getList(CDD_FEATURE_SITE, String.class);

		if (features.size() != chainFeatures.size() ||
				features.size() != types.size() ||
				features.size() != sites.size())
			return null;

		List<CDDFeature> cddFeatures = new ArrayList<CDDFeature>();
		for (int i = 0; i < features.size(); i++) {
			CDDFeature hit = new CDDFeature(chainFeatures.get(i), features.get(i), types.get(i), sites.get(i));
			cddFeatures.add(hit);
		}
		return cddFeatures;
	}

	private static void updateStringColumn(CyNetwork network, CyIdentifiable cyId,
	                                       String columnName, List<CDDFeature> features) {
		List<String> dataList = new ArrayList<String>();
		for (CDDFeature feature: features) {
			if (columnName.equals(CDD_FEATURE))
				dataList.add(feature.getAccession());
			else if (columnName.equals(CDD_FEATURE_TYPE))
				dataList.add(feature.getFeatureType());
			else if (columnName.equals(CDD_FEATURE_SITE))
				dataList.add(feature.getFeatureSite());
			else if (columnName.equals(PDB_CHAIN_FEATURES))
				dataList.add(feature.getProteinId());
		}
		network.getRow(cyId).set(columnName, dataList);
	}

	String proteinId;
	String featureType;
	String accession;
	String featureSite;

	public CDDFeature() {
	}

	public CDDFeature(String proteinId, String accession, String type, String site) {
		this.proteinId = proteinId;
		this.accession = accession;
		this.featureType = type;
		this.featureSite = site;
	}

	public CDDFeature(String line) {
		String[] record = line.split("\t");

		// Special processing for PDB chains?
		this.proteinId = record[0].split(" ")[2].split("\\(")[0];
		this.featureType = record[1];
		this.accession = record[2];
		this.featureSite = record[3];
	}

	public String getProteinId() {
		return proteinId;
	}

	public String getFeatureType() {
		return featureType;
	}

	public String getFeatureSite() {
		return featureSite;
	}

	public String getAccession() {
		return accession;
	}

}
