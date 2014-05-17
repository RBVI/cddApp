package edu.ucsf.rbvi.cddApp.internal.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;

import edu.ucsf.rbvi.cddApp.internal.model.CDDDomainManager;

/**
 * Displays information on the domains of a protein from the CDD in the Results panel.
 * @author Allan Wu
 *
 */
public class ChainsPanel extends CollapsablePanel {
	
	final CDDDomainManager domainManager;
	final CyIdentifiable cyId;
	final String chain;

	public ChainsPanel(CyIdentifiable cyId, String chain, CDDDomainManager manager) {
		super("", new JPanel(), true);
		JPanel panel = getContent();
		this.domainManager = manager;
		this.cyId = cyId;
		this.chain = chain;

		setLabel("<b>Chain:</b> "+domainManager.getSummary(cyId, chain));
		Border etched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		Border empty = BorderFactory.createEmptyBorder(0,5,0,5);
		setBorder(BorderFactory.createCompoundBorder(empty, etched));

		ChainTable table = new ChainTable(cyId, chain, domainManager);
		panel.setLayout(new BorderLayout());
		panel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		panel.add(table, BorderLayout.CENTER);
	}
}
