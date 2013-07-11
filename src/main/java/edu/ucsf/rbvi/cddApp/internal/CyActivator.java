package edu.ucsf.rbvi.cddApp.internal;

import static org.cytoscape.work.ServiceProperties.COMMAND;
import static org.cytoscape.work.ServiceProperties.COMMAND_NAMESPACE;
import static org.cytoscape.work.ServiceProperties.INSERT_SEPARATOR_BEFORE;
import static org.cytoscape.work.ServiceProperties.IN_MENU_BAR;
import static org.cytoscape.work.ServiceProperties.MENU_GRAVITY;
import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TITLE;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.task.TableTaskFactory;
import org.cytoscape.task.read.LoadVizmapFileTaskFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.TaskFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ucsf.rbvi.cddApp.internal.tasks.LoadCDDDomainNodeViewTaskFactory;
import edu.ucsf.rbvi.cddApp.internal.tasks.LoadCDDDomainTaskFactory;

public class CyActivator extends AbstractCyActivator {
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
		
		LoadCDDDomainTaskFactory loadCDDDomain = new LoadCDDDomainTaskFactory();
		Properties settingsProps = new Properties();
		settingsProps.setProperty(PREFERRED_MENU, "Apps.cddApp");
		settingsProps.setProperty(TITLE, "Load CDD Domains for Network");
		settingsProps.setProperty(COMMAND, "loadCDDDomains4network");
		settingsProps.setProperty(COMMAND_NAMESPACE, "cddApp");
		settingsProps.setProperty(IN_MENU_BAR, "true");
		// settingsProps.setProperty(ENABLE_FOR, "network");
		settingsProps.setProperty(MENU_GRAVITY, "1.0");
		registerService(bc, loadCDDDomain, NetworkTaskFactory.class, settingsProps);

		LoadCDDDomainNodeViewTaskFactory loadCDDDomainNodeView = new LoadCDDDomainNodeViewTaskFactory();
		Properties nodeViewProps = new Properties();
		nodeViewProps.setProperty(PREFERRED_MENU, "Apps.cddApp");
		nodeViewProps.setProperty(TITLE, "Load CDD Domains for Node");
		nodeViewProps.setProperty(COMMAND, "loadCDDDomains4node");
		nodeViewProps.setProperty(COMMAND_NAMESPACE, "cddApp");
		nodeViewProps.setProperty(IN_MENU_BAR, "true");
		// settingsProps.setProperty(ENABLE_FOR, "network");
		nodeViewProps.setProperty(MENU_GRAVITY, "1.0");
		registerService(bc, loadCDDDomainNodeView, NodeViewTaskFactory.class, nodeViewProps);
	}
}
