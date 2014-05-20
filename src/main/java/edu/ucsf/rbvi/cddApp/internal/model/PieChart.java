package edu.ucsf.rbvi.cddApp.internal.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

import edu.ucsf.rbvi.cddApp.internal.util.CyUtils;

/**
 * PieChart
 * 
 */
public class PieChart {
	final public static String DOMAIN_SIZE = "CDD-Domain-Size";
	final public static String DOMAIN_CHART = "CDD-Domain-Chart";

	public static void updatePieChartColumn(CyNetwork network, Map<CyIdentifiable, List<CDDHit>>hitMap,
	                                        Map<CyIdentifiable, List<CDDFeature>>featureMap) {

		// Create columns
		CyUtils.createColumn(network.getDefaultNodeTable(), DOMAIN_SIZE, List.class, Long.class);
		CyUtils.createColumn(network.getDefaultNodeTable(), DOMAIN_CHART, String.class, null);

		for (CyIdentifiable cyId: hitMap.keySet()) {
			List<CDDHit> hitList = hitMap.get(cyId);
			if (hitList == null) { hitList = new ArrayList<CDDHit>(); }
			List<CDDFeature> featureList = featureMap.get(cyId);
			if (featureList == null) { featureList = new ArrayList<CDDFeature>(); }
			List<Long> domainSize = new ArrayList<Long>();
			String domains = null;
			long domainLengths = 0;
			for (CDDHit hit: hitList) {
				domainSize.add(hit.getTo() - hit.getFrom());
				domainLengths += hit.getTo() - hit.getFrom();
				if (domains == null)
					domains = hit.getName();
				else
					domains = domains + "," + hit.getName();
			}
			long featureLengths = 0;
			for (CDDFeature feature: featureList) {
				featureLengths += feature.getFeatureSite().split(",").length;
				if (domains == null)
					domains = feature.getAccession();
				else
					domains = domains + "," + feature.getAccession();
			}

			if (featureLengths > 0) {
				for (CDDFeature feature: featureList) {
					domainSize.add(feature.getFeatureSite().split(",").length * domainLengths / featureLengths);
				}

				network.getRow(cyId).set(DOMAIN_SIZE, domainSize);
				network.getRow(cyId).set(DOMAIN_CHART, "piechart: attributelist=\""+DOMAIN_SIZE+
			                                       "\" colorlist=\"contrasting\" labellist=\""+domains+"\"");
			}
		}
	}
}
