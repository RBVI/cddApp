package edu.ucsf.rbvi.cddApp.internal;

import static org.cytoscape.work.ServiceProperties.COMMAND;
// Commented out until 3.2 is released
// import static org.cytoscape.work.ServiceProperties.COMMAND_DESCRIPTION;
import static org.cytoscape.work.ServiceProperties.COMMAND_NAMESPACE;
import static org.cytoscape.work.ServiceProperties.IN_MENU_BAR;
import static org.cytoscape.work.ServiceProperties.MENU_GRAVITY;
import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TITLE;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.work.TaskFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ucsf.rbvi.cddApp.internal.model.CDDDomainManager;
import edu.ucsf.rbvi.cddApp.internal.tasks.LoadCDDDomainNetworkViewTaskFactory;
import edu.ucsf.rbvi.cddApp.internal.tasks.LoadCDDDomainNodeViewTaskFactory;
import edu.ucsf.rbvi.cddApp.internal.tasks.LoadCDDDomainsCommandTaskFactory;
import edu.ucsf.rbvi.cddApp.internal.tasks.LoadCDDDomainTaskFactory;
import edu.ucsf.rbvi.cddApp.internal.tasks.ShowCDDDomainTaskFactory;
import edu.ucsf.rbvi.cddApp.internal.tasks.ShowDomainChartsTaskFactory;
import edu.ucsf.rbvi.cddApp.internal.tasks.ShowDomainsPanelTaskFactory;
import edu.ucsf.rbvi.cddApp.internal.tasks.ShowStructureDiagramTaskFactory;
import edu.ucsf.rbvi.cddApp.internal.tasks.UnselectCDDDomainTaskFactory;

public class CyActivator extends AbstractCyActivator {
	static String COMMAND_DESCRIPTION = "commandDescription";
	private static Logger logger = LoggerFactory
			.getLogger(edu.ucsf.rbvi.cddApp.internal.CyActivator.class);

	public CyActivator() {
		super();
	}

	public void start(BundleContext bc) {

		// See if we have a graphics console or not
		boolean haveGUI = true;
		ServiceReference ref = bc.getServiceReference(CySwingApplication.class.getName());

		if (ref == null) {
			haveGUI = false;
			// Issue error and return
		}

		// Get some services we'll need
		CyApplicationManager appManager = getService(bc, CyApplicationManager.class);
		OpenBrowser openBrowser = getService(bc, OpenBrowser.class);
		CyServiceRegistrar serviceRegistrar = getService(bc, CyServiceRegistrar.class);

		// Create our manager object
		CDDDomainManager manager = new CDDDomainManager(appManager, openBrowser, serviceRegistrar);

		// Resister our manager object to listen for SetCurrentNetwork events
		registerService(bc, manager, SetCurrentNetworkListener.class, new Properties());
		
		LoadCDDDomainTaskFactory loadCDDDomain = new LoadCDDDomainTaskFactory(manager);
		Properties settingsProps = new Properties();
		settingsProps.setProperty(PREFERRED_MENU, "Apps.cddApp");
		settingsProps.setProperty(TITLE, "Load CDD Domains for Network");
		settingsProps.setProperty(IN_MENU_BAR, "true");
		settingsProps.setProperty(MENU_GRAVITY, "1.0");
		registerService(bc, loadCDDDomain, NetworkTaskFactory.class, settingsProps);
		
		LoadCDDDomainNetworkViewTaskFactory loadCDDDomainNetworkView = 
			new LoadCDDDomainNetworkViewTaskFactory(manager);
		Properties networkViewProps = new Properties();
		networkViewProps.setProperty(PREFERRED_MENU, "Apps.cddApp");
		networkViewProps.setProperty(TITLE, "Load CDD Domains for selected Node(s)");
		networkViewProps.setProperty(IN_MENU_BAR, "true");
		networkViewProps.setProperty(MENU_GRAVITY, "2.0");
		registerService(bc, loadCDDDomainNetworkView, NetworkViewTaskFactory.class, networkViewProps);

		LoadCDDDomainNodeViewTaskFactory loadCDDDomainNodeView = 
			new LoadCDDDomainNodeViewTaskFactory(manager);
		Properties nodeViewProps = new Properties();
		nodeViewProps.setProperty(PREFERRED_MENU, "Apps.cddApp");
		nodeViewProps.setProperty(TITLE, "Load CDD Domains for Node");
		nodeViewProps.setProperty(MENU_GRAVITY, "1.0");
		registerService(bc, loadCDDDomainNodeView, NodeViewTaskFactory.class, nodeViewProps);

		if (haveGUI) {
			ShowDomainsPanelTaskFactory showPanel = 
				new ShowDomainsPanelTaskFactory(manager, true);
			Properties showPanelProps = new Properties();
			showPanelProps.setProperty(PREFERRED_MENU, "Apps.cddApp");
			showPanelProps.setProperty(TITLE, "Show CDD Domain Panel");
			showPanelProps.setProperty(IN_MENU_BAR, "true");
			showPanelProps.setProperty(MENU_GRAVITY, "3.0");
			showPanelProps.setProperty(COMMAND_NAMESPACE, "cdd");
			showPanelProps.setProperty(COMMAND, "show panel");
			showPanelProps.setProperty(COMMAND_DESCRIPTION, 
		                          "Show the CDD Domain Panel");
			registerService(bc, showPanel, TaskFactory.class, showPanelProps);

			ShowDomainsPanelTaskFactory hidePanel = 
				new ShowDomainsPanelTaskFactory(manager, false);
			Properties hidePanelProps = new Properties();
			hidePanelProps.setProperty(PREFERRED_MENU, "Apps.cddApp");
			hidePanelProps.setProperty(TITLE, "Hide CDD Domain Panel");
			hidePanelProps.setProperty(IN_MENU_BAR, "true");
			hidePanelProps.setProperty(MENU_GRAVITY, "4.0");
			hidePanelProps.setProperty(COMMAND_NAMESPACE, "cdd");
			hidePanelProps.setProperty(COMMAND, "hide panel");
			hidePanelProps.setProperty(COMMAND_DESCRIPTION, 
		                          "Hide the CDD Domain Panel");
			registerService(bc, hidePanel, TaskFactory.class, hidePanelProps);

			ShowStructureDiagramTaskFactory showDiagram = 
				new ShowStructureDiagramTaskFactory(manager);
			Properties structureProps = new Properties();
			structureProps.setProperty(PREFERRED_MENU, "Apps.cddApp");
			structureProps.setProperty(TITLE, "Show domain architecture diagram");
			structureProps.setProperty(MENU_GRAVITY, "2.0");
			registerService(bc, showDiagram, NodeViewTaskFactory.class, structureProps);
		}

		// Commands
		Properties loadProps = new Properties();
		loadProps.setProperty(COMMAND_NAMESPACE, "cdd");
		loadProps.setProperty(COMMAND, "load");
		loadProps.setProperty(COMMAND_DESCRIPTION, 
		                      "Annotate a network with CDD information");
		LoadCDDDomainsCommandTaskFactory loadTaskFactory = 
			new LoadCDDDomainsCommandTaskFactory(manager);
		registerService(bc, loadTaskFactory, TaskFactory.class, loadProps);

		//
		Properties selectProps = new Properties();
		selectProps.setProperty(COMMAND_NAMESPACE, "cdd");
		selectProps.setProperty(COMMAND, "select"); //network=, nodeList=, chains=, domains=, sites=
		selectProps.setProperty(COMMAND_DESCRIPTION, 
		                        "Show the structure and select the domains or features");
		ShowCDDDomainTaskFactory selectTaskFactory = new ShowCDDDomainTaskFactory(manager);
		registerService(bc, selectTaskFactory, TaskFactory.class, selectProps);

		//
		Properties showProps = new Properties();
		showProps.setProperty(COMMAND_NAMESPACE, "cdd");
		showProps.setProperty(COMMAND, "show charts"); //network=, domains=, features=
		showProps.setProperty(COMMAND_DESCRIPTION, 
		                      "Show the pie charts for domains or features");
		ShowDomainChartsTaskFactory showTaskFactory = new ShowDomainChartsTaskFactory(manager);
		registerService(bc, showTaskFactory, TaskFactory.class, showProps);

		//
		Properties unselectProps = new Properties();
		unselectProps.setProperty(COMMAND_NAMESPACE, "cdd");
		unselectProps.setProperty(COMMAND, "unselect"); //network=, nodeList=, chains=, domains=, sites=
		unselectProps.setProperty(COMMAND_DESCRIPTION, 
		                          "Unselect domains or features in the structure");
		UnselectCDDDomainTaskFactory unselectTaskFactory = new UnselectCDDDomainTaskFactory(manager);
		registerService(bc, unselectTaskFactory, TaskFactory.class, unselectProps);

	}
}
