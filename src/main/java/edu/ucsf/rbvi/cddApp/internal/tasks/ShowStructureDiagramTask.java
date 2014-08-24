package edu.ucsf.rbvi.cddApp.internal.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.swing.JDialog;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNodeViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import edu.ucsf.rbvi.cddApp.internal.model.CDDDomainManager;
import edu.ucsf.rbvi.cddApp.internal.ui.StructureDiagramDialog;
import edu.ucsf.rbvi.cddApp.internal.util.CyUtils;

public class ShowStructureDiagramTask extends AbstractNodeViewTask {
	final CyNetwork network;
	final CyNode node;
	final CDDDomainManager domainManager;

	/**
	 * Constructor for loading CDD Domain from the CDD website.
	 * @param net CyNetwork to load the domain.
	 * @param manager The CDD Domain manager
	 */
	public ShowStructureDiagramTask(View<CyNode> nodeView,
									                CyNetworkView netView, 
																	CDDDomainManager manager) {
		super(nodeView, netView);
		this.domainManager = manager;
		network = netView.getModel();
		node = nodeView.getModel();
	}

	/**
	 * Show the domain or feature on a pdb structure
	 */
	@Override
	public void run(TaskMonitor monitor) throws Exception {
		monitor.setTitle("Show Structure Diagram for "+
										 CyUtils.getName(network, node));

		JDialog stDialog = new StructureDiagramDialog(null, node, domainManager);
	}

	@ProvidesTitle
	public String getTitle() { return "Showing Structure Diagram"; }

}
