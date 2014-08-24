package edu.ucsf.rbvi.cddApp.internal.tasks;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNodeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;

import edu.ucsf.rbvi.cddApp.internal.model.CDDDomainManager;
import edu.ucsf.rbvi.cddApp.internal.model.PieChart;

public class ShowStructureDiagramTaskFactory 
				extends AbstractNodeViewTaskFactory {
	final CDDDomainManager domainManager;

	public ShowStructureDiagramTaskFactory(CDDDomainManager manager) {
		this.domainManager = manager;
	}

	public TaskIterator createTaskIterator(View<CyNode> nodeView,
									                       CyNetworkView networkView) {
		return new TaskIterator(
										new ShowStructureDiagramTask(nodeView, 
														                     networkView, 
																								 domainManager));
	}

	public boolean isReady(View<CyNode> nodeView,
									       CyNetworkView networkView) {
		if (PieChart.getDomainChart(networkView.getModel(), nodeView.getModel()) == null) return false;
		return true;
	}

}
