package edu.ucsf.rbvi.cddApp.internal.model;

import java.awt.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
 * TODO: Change pie chart to reflect each protein's length.  Areas outside of a
 * domain should be clear (background color) or white.  Areas within a domain are
 * colored and the domains all have the same color.  Start of slice and size of slice
 * should reflect the position on the protein.
 *
 * Probably want to drop features and only do this for domains...
 * 
 */
public class PieChart {
	final public static String DOMAIN_SIZE = "CDD-Domain-Size";
	final public static String DOMAIN_CHART = "CDD-Domain-Chart";
	final public static String FEATURE_SIZE = "CDD-Feature-Size";
	final public static String FEATURE_CHART = "CDD-Feature-Chart";
	static float nextDomainHue; // For our color calculations
	static boolean dEven; // For our color calculations
	static float nextFeatureHue; // For our color calculations
	static boolean fEven; // For our color calculations

	public static void updatePieChartColumn(CyNetwork network, Map<CyIdentifiable, List<CDDHit>>hitMap,
	                                        Map<CyIdentifiable, List<CDDFeature>>featureMap) {

		// Create columns
		CyUtils.createColumn(network.getDefaultNodeTable(), DOMAIN_SIZE, List.class, Long.class);
		CyUtils.createColumn(network.getDefaultNodeTable(), DOMAIN_CHART, String.class, null);
		CyUtils.createColumn(network.getDefaultNodeTable(), FEATURE_SIZE, List.class, Long.class);
		CyUtils.createColumn(network.getDefaultNodeTable(), FEATURE_CHART, String.class, null);

		// TODO: Create the domain map
		Map<String, String> colorMap = new HashMap<String, String>();
		nextDomainHue = 0.6f;
		dEven = true;
		nextFeatureHue = 0.15f;
		fEven = true;

		for (CyIdentifiable cyId: hitMap.keySet()) {
			List<CDDHit> hitList = hitMap.get(cyId);
			if (hitList == null) { hitList = new ArrayList<CDDHit>(); }

			List<CDDFeature> featureList = featureMap.get(cyId);
			if (featureList == null) { featureList = new ArrayList<CDDFeature>(); }

			long maxLength = findMax(hitList, featureList);

			// Create the domains pie chart. 
			updateDomains(network, cyId, hitList, colorMap, maxLength);

			// Create the features pie chart. 
			updateFeatures(network, cyId, featureList, colorMap, maxLength);
		}
	}

	static private void updateDomains(CyNetwork network, CyIdentifiable cyId, List<CDDHit> hitList, 
	                                  Map<String, String> colorMap, long maxLength) {
		List<Long> domainSize = new ArrayList<Long>();
		List<PieChart> slices = new ArrayList<PieChart>();

		// First, sort the hitList.  We want all hits in order of their starting locations.
		// Overlapping hits should be removed
		List<CDDHit> hits = CDDHit.removeOverlaps(hitList);

		long location = 1;
		for (CDDHit hit: hits) {
			// System.out.println("hit: "+hit);
			// System.out.println("Current location = "+location);
			// TODO: See if we've seen this domain before.  If we have, use it's color
			// otherwise add a color for it
			if (location < hit.getFrom()) {
				// System.out.println("Adding grey slice from "+location+" to "+hit.getFrom());
				domainSize.add(hit.getFrom()-location);
				slices.add(new PieChart("", "lightgrey", location, hit.getFrom()));
			}

			// System.out.println("Adding domain slice from "+hit.getFrom()+" to "+hit.getTo());
			String colorString = PieChart.getColor(colorMap, hit);
			slices.add(new PieChart(hit.getName(), colorString, hit.getFrom(), hit.getTo()));
			domainSize.add(hit.getTo() - hit.getFrom());
			location = hit.getTo();
		}

		if (location < maxLength) {
				domainSize.add(maxLength-location);
				slices.add(new PieChart("", "lightgrey", location, maxLength));
		}

		// OK, reverse our lists
		Collections.reverse(domainSize);
		Collections.reverse(slices);

		String colorList = PieChart.getColorList(slices);
		String domainList = PieChart.getNameList(slices);

		network.getRow(cyId).set(DOMAIN_SIZE, domainSize);
		network.getRow(cyId).set(DOMAIN_CHART, "piechart: labelstyle=\"bold\" arcstart=\"90\" attributelist=\""+DOMAIN_SIZE+
		                                      "\" colorlist=\""+colorList+"\" labellist=\""+domainList+"\"");
	}

	static private void updateFeatures(CyNetwork network, CyIdentifiable cyId, 
	                                   List<CDDFeature> features, Map<String, String> colorMap, long max) {
		List<Long> featureSize = new ArrayList<Long>();
		List<PieChart> slices = new ArrayList<PieChart>();

		// Find the range of each feature and create a pie slice for that range.  We also create
		// transparent pie slices between feature ranges.  This allows us to overlay this chart
		// over the domain chart
		List<CDDFeature> sites = CDDFeature.removeOverlaps(features);

		long location = 1;
		for (CDDFeature feature: sites) {
			long[] range = feature.getRange();
			if (location < range[0]) {
				// System.out.println("Adding transparent slice from "+location+" to "+range[0]);
				featureSize.add(range[0]-location);
				slices.add(new PieChart("", "#00000000", location, range[0]));
			}

			String colorString = PieChart.getColor(colorMap, feature);
			slices.add(new PieChart(feature.getAccession(), colorString, range[0], range[1]));
			featureSize.add(range[1] - range[0]);
			location = range[1];
		}

		if (location < max) {
				featureSize.add(max-location);
				slices.add(new PieChart("", "#00000000", location, max));
		}


		// OK, reverse our lists
		Collections.reverse(featureSize);
		Collections.reverse(slices);

		String colorList = PieChart.getColorList(slices);
		String siteList = PieChart.getNameList(slices);

		network.getRow(cyId).set(FEATURE_SIZE, featureSize);
		network.getRow(cyId).set(FEATURE_CHART, "piechart: labelsize=4 arcstart=\"90\" attributelist=\""+FEATURE_SIZE+
		                                      "\" colorlist=\""+colorList+"\" labellist=\""+siteList+"\"");
	}

	static private long findMax(List<CDDHit> domains, List<CDDFeature> features) {
		long max = 0;
		for (CDDHit hit: domains) {
			if (hit.getTo() > max) max = hit.getTo();
		}

		for (CDDFeature feature: features) {
			long[] range = feature.getRange();
			if (range[1] > max) max = range[1];
		}

		return max;
	}

	static private String getColorList(List<PieChart> slices) {
		String colors = null;
		for (PieChart slice: slices) {
			if (colors == null)
				colors = slice.color;
			else
				colors = colors + ","+slice.color;
		}
		return colors;
	}

	private static String getNameList(List<PieChart> slices) {
		String names = null;
		for (PieChart slice: slices) {
			if (names == null)
				names = slice.label;
			else
				names = names + ","+slice.label;
		}
		return names;
	}

	private static String getColor(Map<String, String> colorMap, CDDFeature feature) {
		if (colorMap.containsKey(feature.getAccession()))
			return colorMap.get(feature.getAccession());
		float brightness = 0.5f;
		float saturation = 1.0f;
		float hue = nextFeatureHue;
		if (!fEven) {
			nextFeatureHue = nextFeatureHue+0.5f;
			fEven = true;
		} else {
			nextFeatureHue = nextFeatureHue+0.6f;
			fEven = false;
		}

		String color = getColor(hue, saturation, brightness, 100);
		colorMap.put(feature.getAccession(), color);
		return color;
	}

	private static String getColor(Map<String, String> colorMap, CDDHit hit) {
		if (colorMap.containsKey(hit.getName()))
			return colorMap.get(hit.getName());
		float brightness = 1.0f;
		float saturation = 1.0f;
		if (hit.getHitType().equalsIgnoreCase("specific")) {
			saturation = 0.5f;
			// brightness = 0.9f;
		}
		float hue = nextDomainHue;
		if (!dEven) {
			nextDomainHue = nextDomainHue+0.5f;
			dEven = true;
		} else {
			nextDomainHue = nextDomainHue+0.6f;
			dEven = false;
		}

		if (nextDomainHue > 1.0) nextDomainHue = nextDomainHue-1.0f;
		String color = getColor(hue, saturation, brightness, 255);
		colorMap.put(hit.getName(), color);
		return color;
	}

	private static String getColor(float hue, float saturation, float brightness, int alpha) {
		Color c = Color.getHSBColor(hue, saturation, brightness);
		// System.out.println("Hit: "+hit);
		// System.out.println("hsb="+hue+","+saturation+","+brightness);
		// System.out.println("Color = "+c);

		// enhancedGraphics wants colors in rgba, but java uses argb.
		// Do this the "hard way"
		String color = String.format("#%02x%02x%02x%02x",c.getRed(),c.getGreen(),c.getBlue(), alpha);
		// System.out.println("Color string = "+color);
		return color;
	}

	protected String label;
	protected String color;
	protected long from, to;

	public PieChart(String label, String color, long from, long to) {
		this.label = label;
		this.color = color;
		this.from = from;
		this.to = to;
	}
}
