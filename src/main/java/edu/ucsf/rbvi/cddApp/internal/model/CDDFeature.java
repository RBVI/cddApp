package edu.ucsf.rbvi.cddApp.internal.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

import edu.ucsf.rbvi.cddApp.internal.util.CyUtils;

/**
 * CDDFeature
 * 
 */
public class CDDFeature implements Comparable<CDDFeature> {
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

		if (features == null ||
				features.size() != chainFeatures.size() ||
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

	/**
	 * Remove overlaps in a sorted list.  If the next hit in the list overlaps with the
	 * end of the previous hit remove it.  If the features completely overlap, keep the most
	 * specific hit.
	 */
	public static List<CDDFeature> removeOverlaps(List<CDDFeature> features) {
		List<CDDFeature> newFeatures = new ArrayList<CDDFeature>();
		CDDFeature lastFeature = null;
		Collections.sort(features);
		for (CDDFeature feature: features) {
			// System.out.println("Looking at "+hit);
			if (lastFeature == null || feature.getFrom() > lastFeature.getTo()) {
				// System.out.println("Adding "+hit);
				newFeatures.add(feature);
				lastFeature = feature;
			} else {
				// They overlap.  Figure out which one to show.
				int index = newFeatures.size()-1;
				// Is it a complete overlap?
				if (lastFeature.getFrom() == feature.getFrom() && 
				    lastFeature.getTo() == feature.getTo()) {
					// Yes, we're done
					continue;
				} else {
					// Is the second feature within the first feature?
					if (feature.getTo() <= lastFeature.getTo()) {
						// System.out.println("Splitting "+lastFeature.getName());
						// Yes, insert it and split lastFeature
						CDDFeature truncatedFeature = 
							new CDDFeature(lastFeature.getProteinId(),lastFeature.getAccession(), 
						                 lastFeature.getFeatureType(), lastFeature.getFrom(), feature.getFrom());
						// System.out.println("Updating "+truncatedFeature);
						newFeatures.set(index,truncatedFeature);
						// System.out.println("Adding "+hit);
						newFeatures.add(feature);
						lastFeature = feature;
						if (lastFeature.getTo() > feature.getTo()) {
							truncatedFeature = 
								new CDDFeature(lastFeature.getProteinId(),lastFeature.getAccession(),
							                 lastFeature.getFeatureType(), feature.getTo(), lastFeature.getTo());
							// System.out.println("Adding "+truncatedFeature);
							newFeatures.add(truncatedFeature);
							lastFeature = truncatedFeature;
						}
						continue;
					}

					// More complicated.  We need to truncate the first one
					CDDFeature truncatedFeature = 
						new CDDFeature(lastFeature.getProteinId(),lastFeature.getAccession(), 
						               lastFeature.getFeatureType(), lastFeature.getFrom(), feature.getFrom());
					// System.out.println("Updating "+truncatedFeature);
					newFeatures.set(index,truncatedFeature);
					// System.out.println("Adding "+feature);
					newFeatures.add(feature);
					lastFeature = feature;
				}
			}
		}
		return newFeatures;
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
	List<Long> featureLocations;
	long from;
	long to;

	public CDDFeature() {
	}

	public CDDFeature(String proteinId, String accession, String type, String site) {
		this.proteinId = proteinId;
		this.accession = accession;
		this.featureType = type;
		this.featureSite = site;
		this.featureLocations = getFeatureLocations();
	}

	// Special version of the constructor for pie charts
	public CDDFeature(String proteinId, String accession, String type, long from, long to) {
		this.proteinId = proteinId;
		this.accession = accession;
		this.featureType = type;
		this.featureSite = "";
		this.featureLocations = new ArrayList<Long>();
		this.from = from;
		this.to = to;
	}

	public CDDFeature(String line) {
		String[] record = line.split("\t");

		// Special processing for PDB chains?
		this.proteinId = record[0].split(" ")[2].split("\\(")[0];
		this.featureType = record[1];
		this.accession = record[2];
		this.featureSite = record[3];
		this.featureLocations = getFeatureLocations();
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

	public long getFrom() { return from; }
	public long getTo() { return to; }

	public long[] getRange() {
		long[] range = new long[2];
		range[0] = from;
		range[1] = to;
		return range;
	}

	public List<Long> getFeatureLocations() {
		from = -1;
		to = -1;
		if (featureLocations != null) return featureLocations;
		Pattern p = Pattern.compile("[A-Za-z](\\d+)");

		featureLocations = new ArrayList<Long>();
		if (featureSite == null || featureSite.length() == 0) return featureLocations;

		String[] sites = featureSite.split(",");
		for (String site: sites) {
			// Look for number or residue type followed by a number
			Matcher m = p.matcher(site.trim());
			if (m.matches())
				site = m.group(1);
			try {
				long l = Long.parseLong(site.trim());
				featureLocations.add(l);
				if (from == -1 || l < from) 
					from = l;
				else if (to == -1 || l > to) 
					to = l;
			} catch (NumberFormatException e) {
				// Not sure what this site is -- skip it.
			}
		}
		return featureLocations;
	}

	public int compareTo(CDDFeature o) {
		if (getFrom() == o.getFrom())
			return 0;
		else if (getFrom() < o.getFrom())
			return -1;
		else
			return 1;
	}

}
