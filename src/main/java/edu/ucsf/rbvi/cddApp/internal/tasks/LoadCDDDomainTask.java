package edu.ucsf.rbvi.cddApp.internal.tasks;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cytoscape.command.util.NodeList;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import edu.ucsf.rbvi.cddApp.internal.model.CDDDomainManager;

public class LoadCDDDomainTask extends AbstractTask {
	public static String NOCOL = "-- None (use PDB IDs only) --";
	public static String NOPDB = "-- None (no PDB IDs) --";
	
	@Tunable(description="Choose column containing primary identifier")
	public ListSingleSelection<String> idColumn; // Column to load
	@Tunable(description="Choose column containing PDB id")
	public ListSingleSelection<String> pdbColumn; // Column to load
	private CyTable table;
	@Tunable(description="Network to annotate with CDD information", context="nogui")
	public CyNetwork network;

	public NodeList nodeList = new NodeList(null);
	@Tunable(description="Nodes to annotate with CDD information", context="nogui")
	public NodeList getnodeList() {
		if (network == null) {
			network = domainManager.getCurrentNetwork();
		}
		nodeList.setNetwork(network);
		return nodeList;
	}

	public void setnodeList(NodeList setValue) {
	}


	private List<CyIdentifiable> entry = null;
	final CDDDomainManager domainManager;

	/**
	 * Constructor for loading CDD Domain from the CDD website.
	 * @param net CyNetwork to load the domain.
	 * @param manager The CDD Domain manager
	 */
	public LoadCDDDomainTask(CyNetwork net, CDDDomainManager manager) {
		super();
		this.domainManager = manager;

		if (net != null)
			this.network = net;
		else
			this.network = domainManager.getCurrentNetwork();

		this.table = network.getDefaultNodeTable();
		ArrayList<String> columns = new ArrayList<String>();
		columns.add(NOCOL);

		ArrayList<String> pdbColumns = new ArrayList<String>();
		pdbColumns.add(NOPDB);
		for (CyColumn c: table.getColumns()) {
			if (c.getName().equals("SUID")) continue;
			columns.add(c.getName());
			if (c.getType().equals(String.class) || 
			    (c.getType().equals(List.class) && c.getListElementType().equals(String.class)))
				pdbColumns.add(c.getName());
		}
		idColumn = new ListSingleSelection<String>(columns);
		idColumn.setSelectedValue(CyNetwork.NAME);
		pdbColumn = new ListSingleSelection<String>(pdbColumns);

	}

	/**
	 * Specify the set of nodes for which to load CDD Domain information. If this function is not
	 * called, domains for all nodes will be loaded.
	 * @param entry List of SUID of nodes.
	 */
	public void setEntry(List<CyIdentifiable> entry) {this.entry = entry;}

	@ProvidesTitle
	public String getTitle() { return "Set Column Values"; }
	
	/**
	 * Load domain information from CDD. Also gets information from the PDB if it is a PDB ID.
	 * Domain information is loaded into the node table, with the following columns:
	 * CDD-Accession accession number of CDD domain
	 * PDB-Chain if it is a PDB ID, the chain ID (in the form of PDB ID + chain)
	 * CDD-Hit-Type type of domain
	 * CDD-From lower limit of the domain
	 * CDD-To upper limit of the domain
	 * CDD-Feature type of feature site
	 * PDB-Chain-Features chain containing the CDD-Feature
	 * CDD-Feature-Type type of CDD feature
	 * CDD-Feature-Site list of residues of the feature site
	 * CDD-Domain-Size size of the domain
	 * CDD-Domain-Chart a Custom Graphics component which shows the domains as
	   a piechart on the node. The size of the domain is proportional to the
	   size of the slice of the pie.
	 */
	@Override
	public void run(TaskMonitor monitor) throws Exception {
		monitor.setTitle("Load CDD Domains");
		if (network == null)
			network = domainManager.getCurrentNetwork();

		String queries = null, pdbQueries = null, colName = idColumn.getSelectedValue();
		String pdbColName = pdbColumn.getSelectedValue();

		if (!colName.equals(NOCOL)) {
			monitor.setStatusMessage("Load CDD Domains");
			domainManager.loadDomains(monitor, network, colName, entry);
		}
		if (!pdbColName.equals(NOPDB)) {
			monitor.setStatusMessage("Loading PDB information");
			domainManager.loadPDBInfo(monitor, network, pdbColName, entry);
		}

	}

}
