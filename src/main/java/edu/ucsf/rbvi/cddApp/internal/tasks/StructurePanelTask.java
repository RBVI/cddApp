package edu.ucsf.rbvi.cddApp.internal.tasks;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.AbstractTask;
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
		OpenStructurePanel panel = new OpenStructurePanel(context);
		JScrollPane scrollPane = panel.scrollPane();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(scrollPane);
		frame.pack();
		frame.setVisible(true);
	}

}
