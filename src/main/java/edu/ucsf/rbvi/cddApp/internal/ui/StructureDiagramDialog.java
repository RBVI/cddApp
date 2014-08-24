package edu.ucsf.rbvi.cddApp.internal.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;

import edu.ucsf.rbvi.cddApp.internal.model.CDDDomainManager;
import edu.ucsf.rbvi.cddApp.internal.util.CyUtils;

/**
 * Displays information on the domains of a protein from the CDD in the Results panel.
 * @author Allan Wu
 *
 */
public class StructureDiagramDialog extends JDialog {
	
	final CDDDomainManager domainManager;
	final CyIdentifiable cyId;
	final CyNetwork net;

	public StructureDiagramDialog(JFrame parent, CyIdentifiable cyId, CDDDomainManager manager) {
		super(parent, "Structure Diagram for "+CyUtils.getName(manager.getCurrentNetwork(), cyId), false);
		this.domainManager = manager;
		this.cyId = cyId;
		this.net = manager.getCurrentNetwork();

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(new StructureDiagramPanel(net, cyId, manager), 
										BorderLayout.CENTER);
		contentPane.add(createButtonBox(), BorderLayout.PAGE_END);
		setContentPane(contentPane);
		pack();
		setVisible(true);
		// contentPane.setBounds(0, 0, 800, 400);
		setSize(800,150);
	}

	JPanel createButtonBox() {
		JPanel buttonBox = new JPanel(new BorderLayout());
		buttonBox.setBorder(
							BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		JButton okButton = new JButton("OK");
		buttonBox.add(okButton, BorderLayout.EAST);

		return buttonBox;
	}
}
