package edu.ucsf.rbvi.cddApp.internal.tasks;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.osgi.framework.BundleContext;

import edu.ucsf.rbvi.cddApp.internal.util.CyUtils;

public class StructurePanelTaskFactory extends AbstractTaskFactory {

	private BundleContext context;
	public boolean isReady() {
		CyApplicationManager manager = (CyApplicationManager) CyUtils.getService(context, CyApplicationManager.class);
		if (manager.getCurrentNetwork() != null && manager.getCurrentNetwork().getDefaultNetworkTable().getColumn("pdbFileName") != null)
			return true;
		else return false;
	}
	
	public StructurePanelTaskFactory(BundleContext context) {
		this.context = context;
	}
	
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new StructurePanelTask(context));
	}

}
