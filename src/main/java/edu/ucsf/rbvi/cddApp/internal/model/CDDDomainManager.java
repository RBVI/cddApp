package edu.ucsf.rbvi.cddApp.internal.model;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskMonitor;

import edu.ucsf.rbvi.cddApp.internal.util.CyUtils;
import edu.ucsf.rbvi.cddApp.internal.util.NetUtils;

/**
 * CDDDomainManager
 * 
 */
public class CDDDomainManager {
	private static Font awesomeFont = null;
	final CyApplicationManager appManager;
	final CyEventHelper eventHelper;
	final OpenBrowser openBrowser;
	final CyServiceRegistrar serviceRegistrar;
	final private Pattern pattern;
	final StructureHandler structureHandler;
	Map<CyIdentifiable, List<CDDHit>> hitMap = null;
	Map<CyIdentifiable, List<CDDFeature>> featureMap = null;
	Map<CyIdentifiable, List<PDBStructure>> pdbChainMap = null;
	private boolean ignoringSelection = false;

	public static Font getAwesomeFont() {
		if (awesomeFont == null) {
			try {
			awesomeFont = Font.createFont(Font.TRUETYPE_FONT,
			                              CDDDomainManager.class.getResourceAsStream("/fonts/fontawesome-webfont.ttf"));
			} catch (final Exception e) {
				throw new RuntimeException("Error loading custom fonts.", e);
			}
		}
		return awesomeFont;
	}

	public CDDDomainManager(CyApplicationManager appManager, OpenBrowser openBrowser, CyServiceRegistrar serviceRegistrar) {
		this.appManager = appManager;
		this.openBrowser = openBrowser;
		this.serviceRegistrar = serviceRegistrar;
		this.pattern = Pattern.compile("(gi)(\\d+)");
		if (getCurrentNetwork() != null) {
			try {
			reLoadDomains(getCurrentNetwork());
			} catch (Exception e) { e.printStackTrace(); }
		} else {
			this.hitMap = new HashMap<CyIdentifiable, List<CDDHit>>();
			this.featureMap = new HashMap<CyIdentifiable, List<CDDFeature>>();
			this.pdbChainMap = new HashMap<CyIdentifiable, List<PDBStructure>>();
		}
		this.structureHandler = new StructureHandler(this);
		this.eventHelper = getService(CyEventHelper.class);
	}

	public CyNetwork getCurrentNetwork() {
		return appManager.getCurrentNetwork();
	}

	public CyNetworkView getCurrentNetworkView() {
		return appManager.getCurrentNetworkView();
	}

	public String getCurrentNetworkName() {
		CyNetwork network = getCurrentNetwork();
		return network.getRow(network).get(CyNetwork.NAME, String.class);
	}

	public <S> S getService(Class<S> serviceClass) {
		return serviceRegistrar.getService(serviceClass);
	}

	public <S> S getService(Class<S> serviceClass, String filter) {
		return serviceRegistrar.getService(serviceClass, filter);
	}

	public void openURL(String url) {
		if (url != null || url.length() > 0)
			openBrowser.openURL(url);
	}

	public StructureHandler getStructureHandler() {
		return structureHandler;
	}

	// Implement a critical section mechanism for node selections
	public boolean ignoreSelection() { return ignoringSelection; }
	public void setIgnoreSelection(boolean ignore) {
		// This is unfortunately a little tricky. If we're
		// going to re-enable selection, we need to let things
		// flush out first, so what we want to do is to 
		// flush all payload events, then sleep a little before
		// re-enabling selection.
		if (!ignore) {
			// System.out.println("Flushing payload events");
			eventHelper.flushPayloadEvents();
			Thread t = new Thread() {
				public void run() {
					try {
					Thread.sleep(500);
					} catch(Exception e) {};
					// System.out.println("Setting ignoring selection to false");
					resetIgnoreSelection();
				}
			};
			t.start();
		} else {
			ignoringSelection = ignore;
		}
	}

	private void resetIgnoreSelection() {ignoringSelection = false;}

	public void loadDomains(final TaskMonitor monitor, CyNetwork network, String idColumn) {
		Map<CyIdentifiable, List<String>> idMap = getIdentifiers(network, idColumn);
		if (idMap == null || idMap.size() == 0) return;

		hitMap = new HashMap<CyIdentifiable, List<CDDHit>>();
		featureMap = new HashMap<CyIdentifiable, List<CDDFeature>>();
		pdbChainMap = new HashMap<CyIdentifiable, List<PDBStructure>>();

		Map<String, CyIdentifiable> reverseMap = new HashMap<String, CyIdentifiable>();
		String queryString = NetUtils.buildCDDQuery(reverseMap, idMap);
		loadCDDInfo(monitor, network, queryString, reverseMap);

		// Update the charts
		PieChart.updatePieChartColumn(network, hitMap, featureMap);

		// Finally, clear our pointer to the PDB column
		PDBStructure.updatePDBColumn(network, null);
	}

	public void reLoadDomains(CyNetwork network) {
		hitMap = new HashMap<CyIdentifiable, List<CDDHit>>();
		featureMap = new HashMap<CyIdentifiable, List<CDDFeature>>();
		pdbChainMap = new HashMap<CyIdentifiable, List<PDBStructure>>();

		// System.out.println("Reloading domains");

		boolean havePDB = PDBStructure.havePDB(network);

		for (CyNode node: network.getNodeList()) {
			CyIdentifiable id = (CyIdentifiable)node;
			List<CDDHit> hitList = CDDHit.reloadHits(network, id);
			if (hitList == null || hitList.size() == 0) continue;
			// System.out.println("Found "+hitList.size()+" domains for "+id);
			hitMap.put(id, hitList);

			List<CDDFeature> featureList = CDDFeature.reloadFeatures(network, id);
			if (featureList == null || featureList.size() == 0) continue;
			// System.out.println("Found "+featureList.size()+" features for "+id);
			featureMap.put(id, featureList);

			// Reload chains...
			List<PDBStructure> chains = PDBStructure.reloadStructures(network, id, hitList);
			if (chains != null && chains.size() > 0)
				pdbChainMap.put(id, chains);
		}

		// Update the charts
		PieChart.updatePieChartColumn(network, hitMap, featureMap);
	}

	public boolean hasChains(CyIdentifiable identifiable) {
		if (pdbChainMap == null)
			return false;
		// System.out.println("Looking at chains for "+identifiable);
		boolean result =  pdbChainMap.containsKey(identifiable);
		// if (result)
		// 	System.out.println(""+identifiable+" has chains");
		return result;
	}

	public List<String> getChains(CyIdentifiable identifiable) {
		if (pdbChainMap == null || !pdbChainMap.containsKey(identifiable))
			return null;
		List<PDBStructure> structures = pdbChainMap.get(identifiable);
		return PDBStructure.getFullNames(structures);
	}

	public String getSummary(CyIdentifiable id) {
		if (!hitMap.containsKey(id))
			return null;

		List<CDDHit> cddHits = getHits(id);
		List<CDDFeature> cddFeatures = getFeatures(id);
		List<PDBStructure> structChains = null;

		if (pdbChainMap != null && pdbChainMap.containsKey(id)) {
			structChains = pdbChainMap.get(id);
		}

		String summary = "<i>"+CyUtils.getName(getCurrentNetwork(), id)+"</i>";
		if (structChains != null)
			summary += "has "+structChains.size()+" structures and "+
			           PDBStructure.countUniqueChains(structChains)+" chains";
		else
			summary += " has "+cddHits.size()+" domains with "+cddFeatures.size()+" features";
		return summary;
	}

	public String getSummary(CyIdentifiable id, String chain) {
		if (!hitMap.containsKey(id))
			return null;

		// System.out.println("Looking at chain: "+chain);
		List<CDDHit> cddHits = getHits(id, chain);
		if (cddHits == null) {
			// System.out.println("Can't find any hits for chain: "+chain);
			return null;
		}

		List<CDDFeature> cddFeatures = getFeatures(id, chain);
		if (cddFeatures == null) {
			// System.out.println("Can't find any features for chain: "+chain);
			return null;
		}
		String summary = "<i>"+chain+"</i>";
		summary += " has "+cddHits.size()+" domains with "+cddFeatures.size()+" features";
		return summary;
	}

	/**
	 * Returns all of the hits for this node
	 */
	public List<CDDHit> getHits(CyIdentifiable identifiable) {
		if (hitMap != null && hitMap.containsKey(identifiable))
			return hitMap.get(identifiable);
		return null;
	}

	/**
	 * Returns all of the hits for this node and a particular chain
	 */
	public List<CDDHit> getHits(CyIdentifiable identifiable, String chain) {
		List<CDDHit> cddHits = getHits(identifiable);
		if (cddHits == null || !pdbChainMap.containsKey(identifiable) ||
				PDBStructure.getStructure(pdbChainMap.get(identifiable), chain) == null)
			return null;

		List<CDDHit> cddChainHits = new ArrayList<CDDHit>();
		for (CDDHit hit: cddHits) {
			if (hit.getProteinId().equalsIgnoreCase(chain))
				cddChainHits.add(hit);
		}
		return cddChainHits;
	}

	/**
	 * Returns all of the unique hits for this node.  This routine will
	 * first check to see if we have multiple PDBs and/or multiple chains
	 * and find all of the hits which aren't simple duplicates.
	 */
	public List<CDDHit> getUniqueHits(CyIdentifiable identifiable) {
		if (pdbChainMap != null && !pdbChainMap.containsKey(identifiable))
			return getHits(identifiable);
		// TODO: fixme
		return getHits(identifiable);
	}

	/**
	 * Returns all of the features for this node
	 */
	public List<CDDFeature> getFeatures(CyIdentifiable identifiable) {
		if (featureMap != null && featureMap.containsKey(identifiable))
			return featureMap.get(identifiable);
		return null;
	}

	/**
	 * Returns all of the unique features for this node.  This routine will
	 * first check to see if we have multiple PDBs and/or multiple chains
	 * and find all of the hits which aren't simple duplicates.
	 */
	public List<CDDFeature> getUniqueFeatures(CyIdentifiable identifiable) {
		if (!pdbChainMap.containsKey(identifiable))
			return getFeatures(identifiable);
		// TODO: fixme
		return getFeatures(identifiable);
	}

	/**
	 * Returns all of the features for this node and a particular chain
	 */
	public List<CDDFeature> getFeatures(CyIdentifiable identifiable, String chain) {
		List<CDDFeature> cddFeatures = getFeatures(identifiable);
		if (cddFeatures == null || !pdbChainMap.containsKey(identifiable) ||
				PDBStructure.getStructure(pdbChainMap.get(identifiable), chain) == null)
			return null;

		List<CDDFeature> cddChainFeatures = new ArrayList<CDDFeature>();
		for (CDDFeature feature: cddFeatures) {
			if (feature.getProteinId().equalsIgnoreCase(chain))
				cddChainFeatures.add(feature);
		}
		return cddChainFeatures;
	}

	public void loadPDBInfo(final TaskMonitor monitor, final CyNetwork network, String idColumn) {
		Map<CyIdentifiable, List<String>> idMap = getIdentifiers(network, idColumn);
		if (idMap == null || idMap.size() == 0) return;

		// Get the valid IDs from the PDB
		monitor.setStatusMessage("Validating PDB IDs");
		Map<String, CyIdentifiable> reverseMap = new HashMap<String, CyIdentifiable>();
		String queryString = NetUtils.buildPDBQuery(reverseMap, idMap);
		try {
			NetUtils.validatePDBIds(queryString, reverseMap);
		} catch (Exception e) {
			monitor.showMessage(TaskMonitor.Level.ERROR, "Failed to validate PDB IDs: "+e.getMessage());
			return;
		}

		// Now, we know that all of the IDs in the reverseMap are valid PDB IDs.  Now get the
		// chains for each ID
		queryString = NetUtils.buildPDBQuery(reverseMap);
		try {
			// This will get all of the chains from PDB and give us a new
			// map that goes from chains to CyIdentifiable
			pdbChainMap = new HashMap<CyIdentifiable, List<PDBStructure>>();
			reverseMap = NetUtils.getPDBChains(queryString, reverseMap, pdbChainMap);
		} catch (Exception e) {
			e.printStackTrace();
			monitor.showMessage(TaskMonitor.Level.ERROR, "Failed to get PDB Chains: "+e.getMessage());
			return;
		}

		// Clear out our maps that we're going to override
		for (CyIdentifiable cyId: pdbChainMap.keySet()) {
			hitMap.remove(cyId);
			featureMap.remove(cyId);
		}

		// Now we can actually query the CDD
		queryString = NetUtils.buildCDDQuery(reverseMap);
		loadCDDInfo(monitor, network, queryString, reverseMap);

		// Finally, update our pointer to the PDB column
		PDBStructure.updatePDBColumn(network, idColumn);
	}

	private void loadCDDInfo(final TaskMonitor monitor, final CyNetwork network,
	                         final String queryString, Map<String, CyIdentifiable> reverseMap) {

		// monitor.setStatusMessage("Getting hits from CDD");
		CDDHit.createHitColumns(network);
		CDDFeature.createFeatureColumns(network);

		monitor.setStatusMessage("Getting information from CDD");
		Future<String> hitTask = loadCDDHits(monitor, queryString, reverseMap, hitMap);
		Future<String> featureTask = loadCDDFeatures(monitor, queryString, reverseMap, featureMap);

		while (!hitTask.isDone() || !featureTask.isDone()) {
			try {
				Thread.sleep(500);
			} catch (Exception e) {}
		}

		CDDHit.updateColumns(network, hitMap);
		CDDFeature.updateColumns(network, featureMap);
		/*
		{
			// At some point, this should be done in parallel
			monitor.setStatusMessage("Getting hits from CDD");
			try {
				NetUtils.getHitsFromCDD(queryString, reverseMap, hitMap); // Pass down monitor?
				CDDHit.updateColumns(network, hitMap);
			} catch (Exception e) {
				monitor.showMessage(TaskMonitor.Level.ERROR, "Failed to get hits: "+e.getMessage());
				return;
			}
		}

		{
			monitor.setStatusMessage("Getting features from CDD");
			try {
				NetUtils.getFeaturesFromCDD(queryString, reverseMap, featureMap); // Pass down monitor?
				CDDFeature.updateColumns(network, featureMap);
			} catch (Exception e) {
				monitor.showMessage(TaskMonitor.Level.ERROR, "Failed to get features: "+e.getMessage());
				return;
			}
			monitor.setStatusMessage("Done.  Loaded "+featureMap.values().size()+" features for "+
			                         featureMap.keySet().size()+" nodes");
		}
		*/
	}

	private final ExecutorService pool = Executors.newFixedThreadPool(2);

	private Future<String> loadCDDHits(final TaskMonitor monitor, final String queryString, 
	                                   final Map<String, CyIdentifiable> reverseMap,
												          	 final Map<CyIdentifiable, List<CDDHit>> hitMap) {
		return pool.submit(new Callable<String>() {
			@Override
			public String call() throws Exception {
				try {
					NetUtils.getHitsFromCDD(queryString, reverseMap, hitMap); // Pass down monitor?
				} catch (Exception e) {
					monitor.showMessage(TaskMonitor.Level.ERROR, "Failed to get hits: "+e.getMessage());
					return e.getMessage();
				}
				return "done";
			}
		});
	}

	private Future<String> loadCDDFeatures(final TaskMonitor monitor, final String queryString, 
	                                       final Map<String, CyIdentifiable> reverseMap,
												          	     final Map<CyIdentifiable, List<CDDFeature>> featureMap) {
		return pool.submit(new Callable<String>() {
			@Override
			public String call() throws Exception {
				try {
					NetUtils.getFeaturesFromCDD(queryString, reverseMap, featureMap); // Pass down monitor?
				} catch (Exception e) {
					monitor.showMessage(TaskMonitor.Level.ERROR, "Failed to get features: "+e.getMessage());
					return e.getMessage();
				}
				return "done";
			}
		});
	}

	/**
	 * Fetch ids for each node from the designated column.  This method will take care
	 * of handling List columns as well as comma-separated lists of strings.
	 */
	private Map<CyIdentifiable, List<String>> getIdentifiers(CyNetwork network, String columnName) {
		Map<CyIdentifiable, List<String>> idMap = new HashMap<CyIdentifiable, List<String>>();
		List<CyNode> nodes = network.getNodeList();
		for (CyNode node: nodes) {
			List<String> ids = getIdentifiers(network, node, columnName);
			if (ids != null && ids.size() > 0)
				idMap.put(node, ids);
		}
		return idMap;
	}

	private List<String> getIdentifiers(CyNetwork network, CyIdentifiable node, String columnName) {
		CyColumn column = network.getDefaultNodeTable().getColumn(columnName);
		if (column == null) return null;

		List<String> idList = null;
		if (column.getType().equals(List.class) && column.getListElementType().equals(String.class)) {
			idList = network.getRow(node).getList(columnName, String.class);
		} else if (column.getType().equals(List.class)) {
			// We have a list, but it's not a string list.  This is going to be a bit harder
			Class type = column.getListElementType();
			List list = network.getRow(node).getList(columnName, type);
			if (list != null && list.size() > 0) {
				idList = new ArrayList<String>();
				for (Object t: list) {
					idList.add(t.toString());
				}
			}
		} else if (column.getType().equals(String.class)) {
			String s = network.getRow(node).get(columnName, String.class);
			if (s != null) {
				idList = new ArrayList<String>();
				// Look out for comma-separated strings!
				String[] splits = s.split(",");
				if (splits.length > 1) {
					for (String split: splits)
						idList.add(split.trim());
				} else
					idList.add(s);
			}
		} else {
			Class type = column.getType();
			Object t = network.getRow(node).get(columnName, type);
			if (t != null) {
				idList = new ArrayList<String>();
				idList.add(t.toString());
			}
		}
		if (idList == null || idList.size() == 0) return idList;

		// Finally, go through our list and see if we have any gi numbers.  If so, fix them up...
		for (int i = 0; i < idList.size(); i++) {
			String s = idList.get(i);
			Matcher m = pattern.matcher(s);
			if (m.matches())
				idList.set(i, "gi|"+m.group(2));
		}
		return idList;
	}
}
