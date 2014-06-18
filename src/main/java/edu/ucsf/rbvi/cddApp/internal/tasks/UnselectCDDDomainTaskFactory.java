package edu.ucsf.rbvi.cddApp.internal.tasks;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import edu.ucsf.rbvi.cddApp.internal.model.CDDDomainManager;

public class UnselectCDDDomainTaskFactory extends AbstractTaskFactory {
	final CDDDomainManager domainManager;

	public UnselectCDDDomainTaskFactory(CDDDomainManager manager) {
		this.domainManager = manager;
	}

	public TaskIterator createTaskIterator() {
		// TODO Auto-generated method stub
		return new TaskIterator(new ShowCDDDomainTask(domainManager, false));
	}

}
