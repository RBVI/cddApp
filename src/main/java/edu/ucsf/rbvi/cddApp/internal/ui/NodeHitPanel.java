package edu.ucsf.rbvi.cddApp.internal.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;

import edu.ucsf.rbvi.cddApp.internal.model.CDDDomainManager;

/**
 * Displays information on the domains of a protein from the CDD in the Results panel.
 * @author Allan Wu
 *
 */
public class NodeHitPanel extends CollapsablePanel {
	
	final CDDDomainManager domainManager;
	final CyIdentifiable cyId;

	public NodeHitPanel(CyIdentifiable cyId, CDDDomainManager manager) {
		super("", new JPanel(), true);
		JPanel panel = getContent();
		this.domainManager = manager;
		this.cyId = cyId;

		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

		if (domainManager.hasChains(cyId)) {
			setLabel("<font color=\"green\"><b>Node:</b> "+domainManager.getSummary(cyId)+"</font>");
			setBackground(new Color(0, 100, 0));
			// System.out.println("Node "+cyId+" has chains");
			panel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
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
			for (String chain: domainManager.getChains(cyId)) {
				c.gridy = gridy;
				panel.add(new ChainsPanel(cyId, chain, domainManager),c);
				gridy++;
			}
			c.fill = GridBagConstraints.BOTH;
			c.gridy = gridy;
			// Show all of the chains
			expand();
		} else {
			setLabel("<font color=\"blue\"><b>Node:</b> "+domainManager.getSummary(cyId)+"</font>");
			setBackground(new Color(0, 0, 100));
			DomainTable table = new DomainTable(cyId, domainManager);
			panel.setLayout(new BorderLayout());
			panel.add(table.getTableHeader(), BorderLayout.PAGE_START);
			panel.add(table, BorderLayout.CENTER);
		}
	}
}
