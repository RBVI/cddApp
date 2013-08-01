package edu.ucsf.rbvi.cddApp.internal.tasks;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.osgi.framework.BundleContext;

import edu.ucsf.rbvi.cddApp.internal.util.CyUtils;
import edu.ucsf.rbvi.cddApp.internal.util.Messages;
import edu.ucsf.rbvi.cddApp.internal.util.SendCommandThread;

/**
 * Open structure from Chimera
 * 
 * @author Nadezhda Doncheva
 *
 */
public class HighlightSitesTask extends AbstractTask {

	private CyNetworkView netView;
	private BundleContext context;
//	private static int counter = 0;
	private String commands = "select ";
	private CyNode singleNode = null;
	
	public HighlightSitesTask(BundleContext bc, CyNetworkView aNetView) {
		context = bc;
		netView = aNetView;
	}

	public HighlightSitesTask(BundleContext bc, View<CyNode> v, CyNetworkView aNetView) {
		context = bc;
		singleNode = v.getModel();
		netView = aNetView;
	}
	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		NetworkViewTaskFactory openTaskFactory = (NetworkViewTaskFactory) CyUtils.getService(
				context, NetworkViewTaskFactory.class, Messages.SV_OPENCOMMANDTASK);
		System.out.println(singleNode);
		if (openTaskFactory != null || singleNode != null) {
		//	insertTasksAfterCurrentTask(openTaskFactory.createTaskIterator(netView));
			List<CyNode> selectedNodes;
			String openFiles = "";
			if (singleNode == null)
				selectedNodes = CyTableUtil.getNodesInState(netView.getModel(), CyNetwork.SELECTED, true);
			else {
				selectedNodes = new ArrayList<CyNode>();
				selectedNodes.add(singleNode);
			}
			CyTable table = netView.getModel().getDefaultNodeTable();
			for (CyNode n: selectedNodes) {
			/*	TaskFactory sendCommandFactory = (TaskFactory) CyUtils.getService(context,
						TaskFactory.class, Messages.SV_SENDCOMMANDTASK);
				if (sendCommandFactory != null && sendCommandFactory.isReady()) {
					TunableSetter tunableSetter = (TunableSetter) CyUtils.getService(context,
							TunableSetter.class);
					Map<String, Object> tunables = new HashMap<String, Object>();
					tunables.put(Messages.SV_COMMANDTUNABLE, "select #" + counter + ":.A");
					insertTasksAfterCurrentTask(sendCommandFactory.createTaskIterator());
				} */
			//	new SendCommandThread().sendChimeraCommand(context, "open " + table.getRow(n.getSUID()).get("pdbFileName", String.class));
				openFiles = openFiles + " " + table.getRow(n.getSUID()).get("pdbFileName", String.class);
				List<String> featureType = table.getRow(n.getSUID()).getList("CDD-Feature-Type", String.class);
				List<String> features = table.getRow(n.getSUID()).getList("CDD-Feature-Site", String.class);
				commands = commands + " #" + HighlightDomainTask.counter + ":";
				for (int i = 0; i < featureType.size(); i++) {
					if (featureType.get(i).equals("specific")) {
						for (String s: features.get(i).split(","))
							commands = commands + s.substring(1,s.length()) + ",";
					}
				}
				commands = commands.substring(0, commands.length()-1);
				HighlightDomainTask.counter++;
			}
			new SendCommandThread().sendChimeraCommand(context, "open" + openFiles);
			new SendCommandThread().sendChimeraCommand(context, commands);
		}
	}

	@ProvidesTitle
	public String getTitle() {
		return "Open structure";
	}

}