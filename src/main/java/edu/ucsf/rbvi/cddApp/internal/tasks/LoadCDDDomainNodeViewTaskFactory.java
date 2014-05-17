package edu.ucsf.rbvi.cddApp.internal.tasks;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNodeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;
import edu.ucsf.rbvi.cddApp.internal.model.CDDDomainManager;

public class LoadCDDDomainNodeViewTaskFactory extends AbstractNodeViewTaskFactory {
	final CDDDomainManager domainManager;

	public LoadCDDDomainNodeViewTaskFactory(CDDDomainManager manager) {
		super();
		this.domainManager = manager;
	}

	public TaskIterator createTaskIterator(View<CyNode> arg0, CyNetworkView arg1) {
		LoadCDDDomainTask task = new LoadCDDDomainTask(arg1.getModel(), domainManager);
		List<Long> suids = new ArrayList<Long>();
		suids.add(arg0.getModel().getSUID());
		task.setEntry(suids);
		return new TaskIterator(task);
	}

}
