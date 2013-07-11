package edu.ucsf.rbvi.cddApp.internal.tasks;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.work.TaskIterator;

public class LoadCDDDomainTaskFactory extends AbstractNetworkTaskFactory {

	public TaskIterator createTaskIterator(CyNetwork arg0) {
		// TODO Auto-generated method stub
		return new TaskIterator(new LoadCDDDomainTask(arg0.getDefaultNodeTable()));
	}

}
