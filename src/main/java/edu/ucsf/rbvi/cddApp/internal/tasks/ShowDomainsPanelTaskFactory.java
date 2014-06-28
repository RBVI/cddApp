package edu.ucsf.rbvi.cddApp.internal.tasks;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import edu.ucsf.rbvi.cddApp.internal.model.CDDDomainManager;

public class ShowDomainsPanelTaskFactory extends AbstractTaskFactory {
	final CDDDomainManager domainManager;
	final boolean show;

	public ShowDomainsPanelTaskFactory(CDDDomainManager manager, boolean show) {
		this.domainManager = manager;
		this.show = show;
	}

	public TaskIterator createTaskIterator() {
		// TODO Auto-generated method stub
		return new TaskIterator(new ShowDomainsPanelTask(domainManager, show));
	}

}
