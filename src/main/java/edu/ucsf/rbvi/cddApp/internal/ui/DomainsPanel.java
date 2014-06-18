package edu.ucsf.rbvi.cddApp.internal.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.events.RowSetRecord;
import org.cytoscape.model.events.RowsSetEvent;
import org.cytoscape.model.events.RowsSetListener;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;

import edu.ucsf.rbvi.cddApp.internal.model.CDDDomainManager;
import edu.ucsf.rbvi.cddApp.internal.model.PieChart;
import edu.ucsf.rbvi.cddApp.internal.util.CyUtils;

/**
 * Displays information on the domains of a protein from the CDD in the Results panel.
 * @author Allan Wu
 *
 */
public class DomainsPanel extends JPanel 
                          implements CytoPanelComponent, RowsSetListener, ActionListener {
	
	final CDDDomainManager domainManager;
	// private JEditorPane textArea;
	private JPanel topPanel;
	private JPanel controlPanel;
	private JScrollPane scrollPane;
	private ConcurrentMap<CyIdentifiable, Boolean> selectedNodes;
	private VisualMappingManager vmm;
	private VisualMappingFunctionFactory passthroughMapper;
	private VisualLexicon lex;
	/**
	 * 
	 */
	private static final long serialVersionUID = 4255348824636450908L;

	/**
	 * 
	 * @param manager CyApplication manager of this instance of Cytoscape
	 * @param openBrowser class that opens the default browser from Cytoscape
	 */
	public DomainsPanel(CDDDomainManager manager) {
		this.domainManager = manager;
		setLayout(new BorderLayout());
		topPanel = new JPanel(new GridBagLayout());
		controlPanel = createControls();
		add(controlPanel, BorderLayout.NORTH);
		scrollPane = new JScrollPane(topPanel);
		add(scrollPane, BorderLayout.CENTER);
		selectedNodes = new ConcurrentHashMap<CyIdentifiable, Boolean>();
		vmm = manager.getService(VisualMappingManager.class);
		passthroughMapper = manager.getService(VisualMappingFunctionFactory.class, "(mapping.type=passthrough)");
		lex = manager.getService(RenderingEngineManager.class).getDefaultVisualLexicon();
	}

	public JPanel createControls() {
		JCheckBox domainCharts = new JCheckBox("Show domain charts");
		domainCharts.setActionCommand("domain");
		domainCharts.addActionListener(this);
		JCheckBox featureCharts = new JCheckBox("Show feature charts");
		featureCharts.setActionCommand("feature");
		featureCharts.addActionListener(this);
		JPanel panel = new JPanel();
		Border etched = BorderFactory.createEtchedBorder();
		Border title = BorderFactory.createTitledBorder(etched, "Pie Chart Controls", TitledBorder.LEFT, TitledBorder.TOP);
		panel.setBorder(title);
		panel.add(domainCharts);
		panel.add(featureCharts);
		return panel;

	}

	public void handleEvent(RowsSetEvent arg0) {
		if (domainManager.ignoreSelection()) return;
		// Clear the list of nodes...
		topPanel.removeAll();
		GridBagConstraints c = new GridBagConstraints();

		try {
		CyNetwork network = domainManager.getCurrentNetwork();
		// If we're not getting selection, we're not interested
		if (network == null || !arg0.containsColumn(CyNetwork.SELECTED)) return;
		CyTable table = network.getDefaultNodeTable();
		// Oops, not relevant to us...
		if (table.getColumn("PDB-Chain") == null) return;
		String message = "";
		Collection<RowSetRecord> record = arg0.getPayloadCollection();
		for (RowSetRecord r: record) {
			CyIdentifiable cyId = CyUtils.getIdentifiable(network, r.getRow().get(CyNetwork.SUID, Long.class));

			if (domainManager.getHits(cyId)!=null || domainManager.getHits(cyId).size() == 0)
				selectedNodes.put(cyId,r.getRow().get(CyNetwork.SELECTED, Boolean.class));
		}

		// These are the same for each row
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 0;
		c.insets = new Insets(0,0,5,0);
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		int gridy = 0;
		for (CyIdentifiable cyId: selectedNodes.keySet()) {
			if (!selectedNodes.get(cyId)) continue;
			// Create CollapsablePanel
			NodeHitPanel cp = new NodeHitPanel(cyId, domainManager);
			// Add it to our JPanel
			c.gridy = gridy;
			topPanel.add(cp,c);
			gridy++;
		}
		c.fill = GridBagConstraints.BOTH;
		c.gridy = gridy;
		c.weighty = 1.0;
		topPanel.add(new JPanel(),c);
		} catch (Exception e){e.printStackTrace();}
	}

	public void actionPerformed(ActionEvent e) {
		// Get the current visual style
		VisualStyle style = vmm.getVisualStyle(domainManager.getCurrentNetworkView());

		String command = e.getActionCommand();
		String column = PieChart.FEATURE_CHART;
		String property = "NODE_CUSTOMGRAPHICS_1";
		if (command.equals("domain")) {
			column = PieChart.DOMAIN_CHART;
			property = "NODE_CUSTOMGRAPHICS_2";
		}

		// Get the appropriate property
		VisualProperty cgl = lex.lookup(CyNode.class, property);

		if (((JCheckBox)e.getSource()).isSelected()) {
			// Activate the appropriate pie chart
			PassthroughMapping pMapping = 
				(PassthroughMapping) passthroughMapper.createVisualMappingFunction(column, String.class, cgl);
			style.addVisualMappingFunction(pMapping);
		} else {
			// De-activate the appropriate pie chart
			style.removeVisualMappingFunction(cgl);
		}
		style.apply(domainManager.getCurrentNetworkView());
	}

	public Component getComponent() {
		// TODO Auto-generated method stub
		return this;
	}

	public CytoPanelName getCytoPanelName() {
		// TODO Auto-generated method stub
		return CytoPanelName.EAST;
	}

	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTitle() {
		// TODO Auto-generated method stub
		return "CDD Domains";
	}

}
