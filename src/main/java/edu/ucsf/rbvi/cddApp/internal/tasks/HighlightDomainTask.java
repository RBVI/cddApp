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
public class HighlightDomainTask extends AbstractTask {

	private CyNetworkView netView;
	private BundleContext context;
	static int counter = 0;
	private String commands = "select ";
	private CyNode singleNode = null;
	
	public HighlightDomainTask(BundleContext bc, CyNetworkView aNetView) {
		context = bc;
		netView = aNetView;
	}
	
	public HighlightDomainTask(BundleContext bc, View<CyNode> v, CyNetworkView aNetView) {
		context = bc;
		singleNode = v.getModel();
		netView = aNetView;
	}
	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		NetworkViewTaskFactory openTaskFactory = (NetworkViewTaskFactory) CyUtils.getService(
				context, NetworkViewTaskFactory.class, Messages.SV_OPENCOMMANDTASK);
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
				List<String> hitType = table.getRow(n.getSUID()).getList("CDD-Hit-Type", String.class);
				List<String> pdbChain = table.getRow(n.getSUID()).getList("PDB-Chain", String.class);
				List<Long> cddBegin = table.getRow(n.getSUID()).getList("CDD-From", Long.class);
				List<Long> cddEnd = table.getRow(n.getSUID()).getList("CDD-To", Long.class);
				for (int i = 0; i < hitType.size(); i++) {
					if (hitType.get(i).equals("specific")) {
						commands = commands + " #" + counter + ":" + cddBegin.get(i) + "-" + cddEnd.get(i) + "." + pdbChain.get(i).charAt(pdbChain.get(i).length()-1);
					}
				}
				counter++;
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