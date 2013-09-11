package edu.ucsf.rbvi.cddApp.internal.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskObserver;
import org.osgi.framework.BundleContext;

import edu.ucsf.rbvi.cddApp.internal.util.CyUtils;
import edu.ucsf.rbvi.cddApp.internal.util.Messages;
import edu.ucsf.rbvi.cddApp.internal.util.SendCommandThread;

/**
 * Open structure from Chimera
 * 
 * @author Allan Wu
 *
 */
public class HighlightSitesTask extends AbstractTask implements TaskObserver {

	private CyNetworkView netView;
	private BundleContext context;
	private String commands = "select ";
	private CyNode singleNode = null;
	private boolean networkViewIndicator;
	private View<CyNode> v;
	
	public HighlightSitesTask(BundleContext bc, CyNetworkView aNetView) {
		context = bc;
		netView = aNetView;
		networkViewIndicator = true;
	}

	public HighlightSitesTask(BundleContext bc, View<CyNode> v, CyNetworkView aNetView) {
		context = bc;
		singleNode = v.getModel();
		netView = aNetView;
		this.v = v;
		networkViewIndicator = false;
	}
	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		TaskManager taskManager = (TaskManager) CyUtils.getService(context, TaskManager.class);
		if (networkViewIndicator) {
			NetworkViewTaskFactory openNetworkViewTaskFactory = (NetworkViewTaskFactory) CyUtils.getService(
					context, NetworkViewTaskFactory.class, Messages.SV_OPENCOMMANDTASK);
			if (openNetworkViewTaskFactory != null)
				taskManager.execute(openNetworkViewTaskFactory.createTaskIterator(netView), this);
		}
		else {
			NodeViewTaskFactory openNodeViewTaskFactory = (NodeViewTaskFactory) CyUtils.getService(
					context, NodeViewTaskFactory.class, Messages.SV_OPENCOMMANDTASK);
			if (openNodeViewTaskFactory != null && singleNode != null)
				taskManager.execute(openNodeViewTaskFactory.createTaskIterator(v, netView), this);
		}
	}

	@ProvidesTitle
	public String getTitle() {
		return "Open structure";
	}

	public void allFinished(FinishStatus arg0) {
		List<String> models = new SendCommandThread().sendChimeraCommand(context, "list models");
		Pattern p = Pattern.compile("model id #(\\d+) type Molecule name (....)");
		HashMap<String, String> modelName = new HashMap<String, String>();
		for (String s: models) {
			Matcher m = p.matcher(s);
			if (m.find())
				modelName.put(m.group(2), m.group(1));
		}
		List<CyNode> selectedNodes;
		if (singleNode == null)
			selectedNodes = CyTableUtil.getNodesInState(netView.getModel(), CyNetwork.SELECTED, true);
		else {
			selectedNodes = new ArrayList<CyNode>();
			selectedNodes.add(singleNode);
		}
		CyTable table = netView.getModel().getDefaultNodeTable(),
				netTable = netView.getModel().getDefaultNetworkTable();
		String pdbFileName = netTable.getRow(netView.getModel().getSUID()).get("pdbFileName", String.class);
		for (CyNode n: selectedNodes) {
			List<String> pdbChain = table.getRow(n.getSUID()).getList("PDB-Chain-Features", String.class);
			List<String> featureType = table.getRow(n.getSUID()).getList("CDD-Feature-Type", String.class);
			List<String> features = table.getRow(n.getSUID()).getList("CDD-Feature-Site", String.class);
			for (int i = 0; i < featureType.size(); i++) {
				if (featureType.get(i).equals("specific")) {
					for (String s: features.get(i).split(","))
						commands = commands + " #" + modelName.get(table.getRow(n.getSUID()).get(pdbFileName, String.class)) + ":" + s.substring(1,s.length()) + "." + pdbChain.get(i).charAt(pdbChain.get(i).length()-1);
				}
			}
		}
	//	new SendCommandThread().sendChimeraCommand(context, "open" + openFiles);
		new SendCommandThread().sendChimeraCommand(context, commands);
	}

	public void taskFinished(ObservableTask arg0) {
		// TODO Auto-generated method stub
		
	}
}