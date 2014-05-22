package edu.ucsf.rbvi.cddApp.internal.tasks;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskIterator;

import edu.ucsf.rbvi.cddApp.internal.model.CDDDomainManager;

public class LoadCDDDomainNetworkViewTaskFactory extends AbstractNetworkViewTaskFactory {
	final CDDDomainManager domainManager;

	public LoadCDDDomainNetworkViewTaskFactory(CDDDomainManager manager) {
		super();
		this.domainManager = manager;
	}
	
	public boolean isReady(CyNetworkView networkView) {
		if (super.isReady(networkView))
			return ! CyTableUtil.getNodesInState(networkView.getModel(), CyNetwork.SELECTED, true).isEmpty();
		else return false;
	}
	
	public TaskIterator createTaskIterator(CyNetworkView arg0) {
		CyNetwork network = arg0.getModel();
		List<CyIdentifiable> selectedNodes = 
			new ArrayList<CyIdentifiable>(CyTableUtil.getNodesInState(arg0.getModel(), CyNetwork.SELECTED, true));
		LoadCDDDomainTask task = new LoadCDDDomainTask(network, domainManager);
		task.setEntry(selectedNodes);
		return new TaskIterator(task);
	}

}
