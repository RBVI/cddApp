package edu.ucsf.rbvi.cddApp.internal.tasks;

import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
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
public class HighlightDomainTaskFactory extends AbstractTaskFactory implements NetworkViewTaskFactory {

	private BundleContext context;

	public HighlightDomainTaskFactory(BundleContext bc) {
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
		return new TaskIterator(new HighlightDomainTask(context, netView));
	}

}