package edu.ucsf.rbvi.cddApp.internal.tasks;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.command.util.NodeList;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.events.RowsSetListener;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import edu.ucsf.rbvi.cddApp.internal.model.CDDDomainManager;
import edu.ucsf.rbvi.cddApp.internal.model.CDDFeature;
import edu.ucsf.rbvi.cddApp.internal.model.CDDHit;
import edu.ucsf.rbvi.cddApp.internal.model.StructureHandler;
import edu.ucsf.rbvi.cddApp.internal.ui.DomainsPanel;
import edu.ucsf.rbvi.cddApp.internal.util.CyUtils;

public class ShowDomainsPanelTask extends AbstractTask {
	final CDDDomainManager manager;
	final boolean show;

	/**
	 * Constructor for loading CDD Domain from the CDD website.
	 * @param net CyNetwork to load the domain.
	 * @param manager The CDD Domain manager
	 */
	public ShowDomainsPanelTask(CDDDomainManager manager, boolean show) {
		super();
		this.manager = manager;
		this.show = show;

	}

	/**
	 * Show the domain or feature on a pdb structure
	 */
	@Override
	public void run(TaskMonitor monitor) throws Exception {
		CySwingApplication swingApplication = manager.getService(CySwingApplication.class);
		CytoPanel cytoPanel = swingApplication.getCytoPanel(CytoPanelName.EAST);

		if (show) {
			DomainsPanel domainsPanel = new DomainsPanel(manager);
			manager.registerService(domainsPanel, CytoPanelComponent.class, new Properties());
			manager.registerService(domainsPanel, RowsSetListener.class, new Properties());
			manager.setDomainsPanel(domainsPanel);
			if (cytoPanel.getState() == CytoPanelState.HIDE)
				cytoPanel.setState(CytoPanelState.DOCK);
		} else {
			DomainsPanel domainsPanel = manager.getDomainsPanel();
			if (domainsPanel == null) return;
			manager.unregisterService(domainsPanel, CytoPanelComponent.class);
			manager.unregisterService(domainsPanel, RowsSetListener.class);
			if (cytoPanel.getCytoPanelComponentCount() == 0)
				cytoPanel.setState(CytoPanelState.HIDE);
		}
	}

	@ProvidesTitle
	public String getTitle() { return "Showing CDD Results Panel"; }

}
