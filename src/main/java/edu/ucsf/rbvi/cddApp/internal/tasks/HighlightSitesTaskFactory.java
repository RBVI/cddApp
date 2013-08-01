package edu.ucsf.rbvi.cddApp.internal.tasks;

import org.cytoscape.model.CyNode;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.osgi.framework.BundleContext;

import edu.ucsf.rbvi.cddApp.internal.util.CyUtils;
import edu.ucsf.rbvi.cddApp.internal.util.Messages;

/**
 * Open structure from Chimera
 * 
 * @author Nadezhda Doncheva
 *
 */
public class HighlightSitesTaskFactory extends AbstractTaskFactory implements NetworkViewTaskFactory, NodeViewTaskFactory {

	private BundleContext context;

	public HighlightSitesTaskFactory(BundleContext bc) {
		context = bc;
	}

	public boolean isReady(CyNetworkView netView) {
		NetworkViewTaskFactory openTaskFactory = (NetworkViewTaskFactory) CyUtils.getService(context,
				NetworkViewTaskFactory.class, Messages.SV_OPENCOMMANDTASK);
		if (openTaskFactory != null && netView != null) {
			return openTaskFactory.isReady(netView);
		}
		return false;

	}

	public TaskIterator createTaskIterator() {
		return null;
	}

	public TaskIterator createTaskIterator(CyNetworkView netView) {
		return new TaskIterator(new HighlightSitesTask(context, netView));
	}

	public TaskIterator createTaskIterator(View<CyNode> arg0, CyNetworkView arg1) {
		// TODO Auto-generated method stub
		return new TaskIterator(new HighlightSitesTask(context, arg0,arg1));
	}

	public boolean isReady(View<CyNode> arg0, CyNetworkView arg1) {
		// TODO Auto-generated method stub
		return true;
	}

}