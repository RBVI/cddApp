package edu.ucsf.rbvi.cddApp.internal.tasks;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.osgi.framework.BundleContext;

import edu.ucsf.rbvi.cddApp.internal.ui.OpenStructurePanel;
import edu.ucsf.rbvi.cddApp.internal.util.CyUtils;

public class StructurePanelTask extends AbstractTask {

	private BundleContext context;
	
	public StructurePanelTask(BundleContext bc) {
		context = bc;
	}
	@Override
	public void run(TaskMonitor arg0) throws Exception {
		CyApplicationManager manager  = (CyApplicationManager) CyUtils.getService(context, CyApplicationManager.class);
		JFrame frame = new JFrame(manager.getCurrentNetwork().getDefaultNetworkTable().getRow(manager.getCurrentNetwork().getSUID()).get(CyNetwork.NAME, String.class));
		JPanel panel = new OpenStructurePanel(context);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
	}

}
