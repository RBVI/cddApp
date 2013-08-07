package edu.ucsf.rbvi.cddApp.internal.ui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskObserver;
import org.osgi.framework.BundleContext;

import edu.ucsf.rbvi.cddApp.internal.util.CyUtils;
import edu.ucsf.rbvi.cddApp.internal.util.Messages;
import edu.ucsf.rbvi.cddApp.internal.util.SendCommandThread;

public class OpenStructurePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4900641711767068453L;
	private HashMap<Long, List<String>> pdbIdsTable;
	private HashMap<String, List<Long>> pdbId2Nodes;
	private HashMap<Long, JPanel> panelTable;
	private HashMap<String, List<JCheckBox>> pdbChainsCheckbox;
	private HashMap<String, Boolean> pdbDisplayed;
	private HashMap<Integer, DomainInfo> domainTable;
	private HashMap<Integer, Boolean> domainDisplayed;
	private CyNetwork network;
	private CyTable table;
	private JScrollPane scrollPane;
	private JPanel panel;
	private BundleContext bc;
	private TaskManager taskManager;
	private CyNetworkView networkView;
	private HashMap<String, String> modelName;
	private int counter = 0;
	
	public OpenStructurePanel(BundleContext context) {
		taskManager = (TaskManager) CyUtils.getService(context, TaskManager.class);
		pdbIdsTable = new HashMap<Long, List<String>>();
		pdbId2Nodes = new HashMap<String, List<Long>>();
		panelTable = new HashMap<Long, JPanel>();
		pdbChainsCheckbox = new HashMap<String, List<JCheckBox>>();
		pdbDisplayed = new HashMap<String, Boolean>();
		domainTable = new HashMap<Integer, DomainInfo>();
		domainDisplayed = new HashMap<Integer, Boolean>();
		CyApplicationManager manager  = (CyApplicationManager) CyUtils.getService(context, CyApplicationManager.class);
		networkView = manager.getCurrentNetworkView();
		network = networkView.getModel();
		table = network.getDefaultNodeTable();
		String colName = network.getDefaultNetworkTable().getRow(network.getSUID()).get("pdbFileName", String.class);
		List<Long> queryRange = table.getPrimaryKey().getValues(Long.class);
		bc = context;
		if (table.getColumn(colName).getListElementType() == null && table.getColumn(colName).getType() == String.class) {
			for (long cyId: queryRange) {
				List<String> l = new ArrayList<String>();
				for (String s: table.getRow(cyId).get(colName, String.class).split(",")) {
					l.add(s);
					List<Long> temp = pdbId2Nodes.get(s);
					if (temp == null) {
						temp = new ArrayList<Long>();
						pdbId2Nodes.put(s, temp);
					}
					temp.add(cyId);
				}
				pdbIdsTable.put(cyId, l);
			}
		}
		else if (table.getColumn(colName).getListElementType() == String.class) {
			for (long cyId: queryRange) {
				pdbIdsTable.put(cyId, table.getRow(cyId).getList(colName, String.class));
				for (String s: table.getRow(cyId).getList(colName, String.class)) {
					List<Long> temp = pdbId2Nodes.get(s);
					if (temp == null) {
						temp = new ArrayList<Long>();
						pdbId2Nodes.put(s, temp);
					}
					temp.add(cyId);
				}
			}
		}
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		for (final long cyId: queryRange) {
			List<String>	pdbChain = table.getRow(cyId).getList("PDB-Chain", String.class),
							pdbChainFeature = table.getRow(cyId).getList("PDB-Chain-Features", String.class);
			if ((pdbChain != null && pdbChain.size() != 0 && pdbChain.get(0).length() > 0) &&
					(pdbChainFeature != null && pdbChainFeature.size() != 0 && pdbChainFeature.get(0).length() > 0)) {
				JPanel nodePanel = new JPanel();
				nodePanel.setLayout(new BoxLayout(nodePanel, BoxLayout.LINE_AXIS));
				JLabel label = new JLabel(table.getRow(cyId).get(colName, String.class));
				label.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
				nodePanel.add(label);
				JButton button = new JButton("Open Structure");
				button.addActionListener(new ActionListener() {
					/* Yo dawg, I heard you like nesting, so I put an anonymous class in your
					 * anonymous class so your program can handle an event while it handles an event. */
					public void actionPerformed(ActionEvent e) {
						NetworkViewTaskFactory openNetworkViewTaskFactory = (NetworkViewTaskFactory) CyUtils.getService(
								bc, NetworkViewTaskFactory.class, Messages.SV_OPENCOMMANDTASK);
						for (Long l: table.getPrimaryKey().getValues(Long.class)) {
							if (l == cyId)
								table.getRow(l).set(CyNetwork.SELECTED, true);
							else
								table.getRow(l).set(CyNetwork.SELECTED, false);
						}
						taskManager.execute(openNetworkViewTaskFactory.createTaskIterator(networkView),
								new TaskObserver() {
									
									public void taskFinished(ObservableTask arg0) {
										// TODO Auto-generated method stub
										
									}
									
									public void allFinished() {
										loadCheckBoxes();
										panel.revalidate();
									}
								});
					}
				});
				nodePanel.add(button);
				JPanel chainsPanel = new JPanel();
				chainsPanel.setLayout(new BoxLayout(chainsPanel, BoxLayout.PAGE_AXIS));
				panelTable.put(cyId, chainsPanel);
				panel.add(nodePanel);
				panel.add(chainsPanel);
				for (String s: pdbIdsTable.get(cyId)) {
					List<String>	accession = table.getRow(cyId).getList("CDD-Accession", String.class),
									domainChain = table.getRow(cyId).getList("PDB-Chain", String.class),
									feature = table.getRow(cyId).getList("CDD-Feature", String.class),
									featureChain = table.getRow(cyId).getList("PDB-Chain-Features", String.class),
									featureSite = table.getRow(cyId).getList("CDD-Feature-Site", String.class);
					List<Long>	from = table.getRow(cyId).getList("CDD-From", Long.class),
								to = table.getRow(cyId).getList("CDD-To", Long.class);
					List<JCheckBox> allCheckBox = new ArrayList<JCheckBox>();
					for (int i = 0; i < accession.size(); i++) {
						if (domainChain.get(i).substring(0, domainChain.get(i).length()-1).equals(s)) {
							final int index = counter;
							DomainInfo thisDomain = new DomainInfo(domainChain.get(i), from.get(i), to.get(i));
							domainTable.put(counter, thisDomain);
							final JCheckBox thisBox = new JCheckBox(domainChain.get(i) + " " + accession.get(i) + " (" + from.get(i) + "-" + to.get(i) + ")");
							thisBox.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
							thisBox.addItemListener(new ItemListener() {
								
								public void itemStateChanged(ItemEvent e) {
									if (thisBox.isSelected())
										domainDisplayed.put(index, true);
									if (! thisBox.isSelected())
										domainDisplayed.remove(index);
									highlightDomain();
								}
							});
							allCheckBox.add(thisBox);
							counter++;
						}
					}
					for (int i = 0; i < featureChain.size(); i++) {
						if (featureChain.get(i).substring(0, featureChain.get(i).length()-1).equals(s)) {
							final int index = counter;
							List<Long> residues = new ArrayList<Long>();
							for (String residue: featureSite.get(i).split(","))
								residues.add((long) Integer.parseInt(residue.substring(1,residue.length())));
							DomainInfo thisDomain = new DomainInfo(featureChain.get(i), residues);
							domainTable.put(counter, thisDomain);
							final JCheckBox thisBox = new JCheckBox(featureChain.get(i) + " " + feature.get(i) + " " + featureSite.get(i));
							thisBox.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
							thisBox.addItemListener(new ItemListener() {
								
								public void itemStateChanged(ItemEvent e) {
									if (thisBox.isSelected())
										domainDisplayed.put(index, true);
									if (! thisBox.isSelected())
										domainDisplayed.remove(index);
									highlightDomain();
								}
							});
							allCheckBox.add(thisBox);
							counter++;
						}
					}
					pdbChainsCheckbox.put(s, allCheckBox);
				}
			}
		}
		scrollPane = new JScrollPane(panel);
		add(scrollPane);
		loadCheckBoxes();
	}
	
	private void loadCheckBoxes() {
		List<String> models = new SendCommandThread().sendChimeraCommand(bc, "list models");
		Pattern p = Pattern.compile("model id #(\\d+) type Molecule name (....)");
		modelName = new HashMap<String, String>();
		if (models != null)
			for (String s: models) {
				Matcher m = p.matcher(s);
				if (m.find())
					modelName.put(m.group(2), m.group(1));
			}
		for (String pdb: modelName.keySet()) {
			if (pdbDisplayed.get(pdb) == null || ! pdbDisplayed.get(pdb))
				for (Long panelId: pdbId2Nodes.get(pdb))
					for (JCheckBox box: pdbChainsCheckbox.get(pdb))
						panelTable.get(panelId).add(box);
			pdbDisplayed.put(pdb, true);
		}
	}
	
	private void highlightDomain() {
		String command = "";
		for (Integer i: domainDisplayed.keySet())
			if (domainDisplayed.get(i)) {
				DomainInfo thisDomain = domainTable.get(i);
				if (thisDomain.residues == null)
					command = command + " #" + modelName.get(thisDomain.pdbId.substring(0, thisDomain.pdbId.length()-1)) + ":" + thisDomain.from + "-" + thisDomain.to + "." + thisDomain.pdbId.charAt(thisDomain.pdbId.length()-1);
				else {
					String sites = null;
					char chain = thisDomain.pdbId.charAt(thisDomain.pdbId.length()-1);
					for (Long l: thisDomain.residues) {
						if (sites == null) sites = l + "." + chain;
						else sites = sites + "," + l + "." + chain;
					}
					command = command + " #" + modelName.get(thisDomain.pdbId.substring(0, thisDomain.pdbId.length()-1)) + ":" + sites;
				}
			}
		if (command.length() > 0)
			new SendCommandThread().sendChimeraCommand(bc, "select" + command);
		else new SendCommandThread().sendChimeraCommand(bc, "~select");
	}
	
	private class DomainInfo {
		public String pdbId;
		public long from;
		public long to;
		public List<Long> residues;
		
		public DomainInfo(String pdbId, long from, long to) {
			this.pdbId = pdbId;
			this.from = from;
			this.to = to;
			this.residues = null;
		}
		
		public DomainInfo(String pdbId, List<Long> residues) {
			this.pdbId = pdbId;
			this.from = 0;
			this.to = 0;
			this.residues = residues;			
		}
	}
}
