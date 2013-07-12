package edu.ucsf.rbvi.cddApp.internal.tasks;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskIterator;

public class LoadCDDDomainNetworkViewTaskFactory extends
		AbstractNetworkViewTaskFactory {
	
	public boolean isReady(CyNetworkView networkView) {
		return ! CyTableUtil.getNodesInState(networkView.getModel(), CyNetwork.SELECTED, true).isEmpty();
	}
	
	public TaskIterator createTaskIterator(CyNetworkView arg0) {
		CyTable table = arg0.getModel().getDefaultNodeTable();
		List<CyNode> selectedNodes = CyTableUtil.getNodesInState(arg0.getModel(), CyNetwork.SELECTED, true);
		List<Long> selected = new ArrayList<Long>();
		for (CyNode n: selectedNodes) selected.add(n.getSUID());
		LoadCDDDomainTask task = new LoadCDDDomainTask(table);
		task.setEntry(selected);
		return new TaskIterator(task);
	}

}
