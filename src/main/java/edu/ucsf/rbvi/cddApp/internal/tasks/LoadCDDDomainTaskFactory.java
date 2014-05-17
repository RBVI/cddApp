package edu.ucsf.rbvi.cddApp.internal.tasks;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.work.TaskIterator;

import edu.ucsf.rbvi.cddApp.internal.model.CDDDomainManager;

public class LoadCDDDomainTaskFactory extends AbstractNetworkTaskFactory {
	final CDDDomainManager domainManager;

	public LoadCDDDomainTaskFactory(CDDDomainManager manager) {
		this.domainManager = manager;
	}

	public TaskIterator createTaskIterator(CyNetwork arg0) {
		// TODO Auto-generated method stub
		return new TaskIterator(new LoadCDDDomainTask(arg0, domainManager));
	}

}
