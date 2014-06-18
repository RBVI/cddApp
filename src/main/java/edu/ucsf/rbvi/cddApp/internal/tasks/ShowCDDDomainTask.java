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

import org.cytoscape.command.util.NodeList;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import edu.ucsf.rbvi.cddApp.internal.model.CDDDomainManager;
import edu.ucsf.rbvi.cddApp.internal.model.CDDFeature;
import edu.ucsf.rbvi.cddApp.internal.model.CDDHit;
import edu.ucsf.rbvi.cddApp.internal.model.StructureHandler;
import edu.ucsf.rbvi.cddApp.internal.util.CyUtils;

public class ShowCDDDomainTask extends AbstractTask {
	@Tunable(description="Network to get CDD information from", context="nogui")
	public CyNetwork network;

	public NodeList nodeList = new NodeList(null);
	@Tunable(description="Nodes to get CDD information from", context="nogui")
	public NodeList getnodeList() {
		if (network == null) {
			network = domainManager.getCurrentNetwork();
		}
		nodeList.setNetwork(network);
		return nodeList;
	}

	public void setnodeList(NodeList setValue) {
	}

	@Tunable(description="Chain to show domain or feature on", context="nogui" /*, required="true" */)
	public String pdbChain;

	@Tunable(description="Domain to show", context="nogui")
	public String domain;

	@Tunable(description="Features to show", context="nogui")
	public String feature;

	final CDDDomainManager domainManager;
	final boolean show;

	/**
	 * Constructor for loading CDD Domain from the CDD website.
	 * @param net CyNetwork to load the domain.
	 * @param manager The CDD Domain manager
	 */
	public ShowCDDDomainTask(CDDDomainManager manager, boolean show) {
		super();
		this.domainManager = manager;
		this.show = show;

	}

	/**
	 * Show the domain or feature on a pdb structure
	 */
	@Override
	public void run(TaskMonitor monitor) throws Exception {
		monitor.setTitle("Show CDD Domains on Structure");
		if (network == null)
			network = domainManager.getCurrentNetwork();

		System.out.println("Getting nodes");
		List<CyNode> nodes = nodeList.getValue();
		if (nodeList == null) return;

		System.out.println("Checking chain: "+pdbChain);
		if (pdbChain == null || pdbChain.length() == 0) return;

		System.out.println("Checking domain: "+domain);
		System.out.println("Checking feature: "+feature);
		if ((domain == null || domain.length() == 0) &&
			  (feature == null || feature.length() == 0)) return;

		for (CyNode node: nodes) {
			System.out.println("Node: "+node);
			String selectString = null;
			if (domain != null) {
				List<CDDHit> hits = domainManager.getHits((CyIdentifiable)node, pdbChain);
				System.out.println("Found "+hits.size()+" domains");
				if (hits == null || hits.size() == 0)
					continue;
				for (CDDHit hit: hits) {
					if (hit.getName().equalsIgnoreCase(domain)) {
						selectString = Long.toString(hit.getFrom()) + "-" + Long.toString(hit.getTo());
						break;
					}
				}
			} else if (feature != null) {
				List<CDDFeature> features = domainManager.getFeatures((CyIdentifiable)node, pdbChain);
				if (features == null || features.size() == 0)
					continue;
				for (CDDFeature site: features) {
					if (site.getAccession().equalsIgnoreCase(feature)) {
						selectString = site.getFeatureSite();
						break;
					}
				}
			}

			if (selectString != null) {
				StructureHandler sh = domainManager.getStructureHandler();
				if (show) {
					// Open the structure
					sh.openStructure(CyUtils.getName(network, (CyIdentifiable)node),pdbChain);
					// Select the site or domain
					sh.select(pdbChain, selectString);
				} else {
					sh.unSelect(pdbChain, selectString);
				}
			}

		}

	}

	@ProvidesTitle
	public String getTitle() { return "Showing CDD domains on structure"; }

}
