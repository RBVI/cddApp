package edu.ucsf.rbvi.cddApp.internal.tasks;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cytoscape.command.util.NodeList;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import edu.ucsf.rbvi.cddApp.internal.model.CDDDomainManager;
import edu.ucsf.rbvi.cddApp.internal.model.CDDFeature;
import edu.ucsf.rbvi.cddApp.internal.model.CDDHit;
import edu.ucsf.rbvi.cddApp.internal.model.PieChart;
import edu.ucsf.rbvi.cddApp.internal.model.StructureHandler;
import edu.ucsf.rbvi.cddApp.internal.util.CyUtils;

public class ShowChartsTask extends AbstractTask {
	@Tunable(description="Network to get CDD information from", context="nogui")
	public CyNetwork network;

	@Tunable(description="Show/Hide domain charts?", context="nogui")
	public boolean domain = true;

	@Tunable(description="Show/Hide feature charts?", context="nogui")
	public boolean feature = true;

	final CDDDomainManager domainManager;

	private VisualMappingManager vmm;
	private VisualMappingFunctionFactory passthroughMapper;
	private VisualLexicon lex;
	private CyNetworkViewManager viewManager;

	/**
	 * Constructor for loading CDD Domain from the CDD website.
	 * @param net CyNetwork to load the domain.
	 * @param manager The CDD Domain manager
	 */
	public ShowChartsTask(CDDDomainManager manager) {
		super();
		this.domainManager = manager;
		vmm = manager.getService(VisualMappingManager.class);
		passthroughMapper = manager.getService(VisualMappingFunctionFactory.class, "(mapping.type=passthrough)");
		lex = manager.getService(RenderingEngineManager.class).getDefaultVisualLexicon();
		viewManager = manager.getService(CyNetworkViewManager.class);
	}

	/**
	 * Show the domain or feature on a pdb structure
	 */
	@Override
	public void run(TaskMonitor monitor) throws Exception {
		monitor.setTitle("Show CDD Charts on Nodes");

		if (network == null)
			network = domainManager.getCurrentNetwork();

		Collection<CyNetworkView> views = viewManager.getNetworkViews(network);
		if (views == null || views.size() == 0) return;

		for (CyNetworkView view: views) {
			// Get the current visual style
			VisualStyle style = vmm.getVisualStyle(view);
			setMapping(style, PieChart.DOMAIN_CHART, "NODE_CUSTOMGRAPHICS_2", domain);
			setMapping(style, PieChart.FEATURE_CHART, "NODE_CUSTOMGRAPHICS_1", feature);
			style.apply(view);
		}
	}

	private void setMapping(VisualStyle style, String column, String property, boolean show) {
		// Get the appropriate property
		VisualProperty cgl = lex.lookup(CyNode.class, property);

		if (show) {
			// Activate the appropriate pie chart
			PassthroughMapping pMapping = 
				(PassthroughMapping) passthroughMapper.createVisualMappingFunction(column, String.class, cgl);
			style.addVisualMappingFunction(pMapping);
		} else {
			// De-activate the appropriate pie chart
			style.removeVisualMappingFunction(cgl);
		}
	}

	@ProvidesTitle
	public String getTitle() { return "Showing CDD charts on nodes"; }

}
