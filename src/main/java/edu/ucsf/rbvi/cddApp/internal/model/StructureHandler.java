package edu.ucsf.rbvi.cddApp.internal.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cytoscape.command.AvailableCommands;
import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskObserver;

import edu.ucsf.rbvi.cddApp.internal.util.CyUtils;

/**
 * StructureHandler
 * 
 */
public class StructureHandler implements TaskObserver {
	CDDDomainManager domainManager;
	AvailableCommands availableCommands;
	CommandExecutorTaskFactory commandExecutor;
	SynchronousTaskManager taskManager;
	Map<String, String> structureToModelMap;
	Map<String, Integer> chainToFirstResidueMap;
	Map<String, Map<Integer, String>> chainToSequenceNumberMap;
	Map<String, String> selectionMap;
	String currentCommand = null;
	String commandResults = null;

	// StructureViz namespace
	static String NAMESPACE = "structureViz";

	// StructureViz commands
	static String LAUNCH = "launch";
	static String OPENCOMMAND = "open";
	static String SENDCOMMAND = "send";

	// Arguments
	static String STRUCTURE_T = "pdbID";
	static String NODE_T = "nodeList";
	static String COMMAND = "command";

	// Chimera commands
	static String LISTCOMMAND = "list models";
	static String LISTRESIDUES = "list residues spec ";
	static String SELECT = "select";
	static String UNSELECT = "~select";

	public StructureHandler(CDDDomainManager domainManager) {
		this.domainManager = domainManager;
		this.availableCommands = domainManager.getService(AvailableCommands.class);
		this.commandExecutor = domainManager.getService(CommandExecutorTaskFactory.class);
		this.taskManager = domainManager.getService(SynchronousTaskManager.class);
		structureToModelMap = new HashMap<String, String>();
		chainToFirstResidueMap = new HashMap<String, Integer>();
		chainToSequenceNumberMap = new HashMap<String, Map<Integer, String>>();
		selectionMap = new HashMap<String, String>();
	}

	public boolean structureVizAvailable() {
		List<String> namespaces = availableCommands.getNamespaces();
		if (namespaces.contains("structureViz"))
			return true;
		return false;
	}

	public void openStructure(String node, String structure) {
		// Update the structure map to see if this structure
		// is already loaded
		updateStructureMap();
		if (structureToModelMap.containsKey(getStructure(structure)))
			return;

		// Nope, load it
		Map<String, Object> args = new HashMap<String, Object>();
		String[] structChain = structure.split("\\.");
		args.put(STRUCTURE_T, structChain[0]);
		// args.put(NODE_T, node);
		execute(OPENCOMMAND, args);

		// Now, update our map of structures to models
		updateStructureMap();

		// Now, find the structure in our structure map and update
		// our chain to first residue map
		updateResidueMap(structure);
	}

	public void select(String chain, String selectionString) {
		// Chain is of the form structure.chainId and selectionString
		// should either a comma-separated list of residues or a 
		// residue range
		// System.out.println("select = "+selectionString);
		String str = buildSelectionString(chain, selectionString);
		if (str != null) {
			String selString = str;
			for (String s: selectionMap.values())
				selString += "|"+s;
			// System.out.println("Sending "+SELECT+" "+selString+" to chimera");
			sendCommand(SELECT+" "+ selString);
			selectionMap.put(chain+" "+selectionString, str);
		}
	}

	public void unSelect(String chain, String selectionString) {
		if (selectionMap.containsKey(chain+" "+selectionString))
			selectionMap.remove(chain+" "+selectionString);

		// Two possibilities. If we've just removed everything from our
		// map, we just want to clear this selection.  Otherwise,
		// we want to update the selections
		if (selectionMap.size() == 0) {
			sendCommand(UNSELECT);
		} else {
			String selString = "";
			for (String s: selectionMap.values())
				selString += "|"+s;
			// System.out.println("Sending "+SELECT+" "+selString.substring(1)+" to chimera");
			sendCommand(SELECT+" "+ selString.substring(1));
		}
	}

	public void updateStructureMap() {
		launchChimera();
		Pattern p = Pattern.compile("model id #(\\d+) type Molecule name (....)");
		List<String> modelList = sendCommand(LISTCOMMAND);
		if (modelList != null) {
			structureToModelMap = new HashMap<String, String>();
			for (String line: modelList) {
				Matcher m = p.matcher(line);
				if (m.find())
					structureToModelMap.put(m.group(2), m.group(1));
			}
		}
	}

	private void	updateResidueMap(String structure) {
		String model = structureToModelMap.get(getStructure(structure));
		String chain = getChain(structure);
		String spec = " #"+model+":."+chain;
		Map<Integer, String> sequenceNumberMap = new HashMap<Integer, String>();
		// This requires Java 7
		/* Pattern p = Pattern.compile("residue id #(\\d+):(\\d*)(?<insertion>[A-Z]?)(?<chain>\\.?[0-9A-Za-z]*) type ([A-Z][A-Z][A-Z])( index )?(?<seqnum>\\d*)");
		List<String> residueList = sendCommand(LISTRESIDUES+spec);
		int firstResidue = residueList.size();
		for (String line: residueList) {
			Matcher m = p.matcher(line);
			// System.out.println("From Chimera: "+line);
			if (m.find()) {
				if (m.group("seqnum") != null && m.group("seqnum").length() > 0) {
					int seqNumber = Integer.parseInt(m.group("seqnum"));
					// System.out.println("Found seqnum: "+seqNumber);
					if (m.group("insertion") != null && m.group("insertion").length() > 0)
						sequenceNumberMap.put(seqNumber, m.group(2)+m.group("insertion"));
					else
						sequenceNumberMap.put(seqNumber, m.group(2));
				} else {
					// TODO: Handle insertion codes!
					int res = Integer.parseInt(m.group(2));
					if (res < firstResidue)
						firstResidue = res;
				}
			}
		}
		*/
		// Java 6 version
		Pattern p = Pattern.compile("residue id #(\\d+):(\\d*)([A-Z])?(\\.?[0-9A-Za-z]*)? type ([A-Z]{3})( index )?(\\d*)?");
		List<String> residueList = sendCommand(LISTRESIDUES+spec);
		int firstResidue = residueList.size();
		for (String line: residueList) {
			Matcher m = p.matcher(line);
			if (m.find()) {
				if (m.group(7) != null && m.group(7).length() > 0) {
					int seqNumber = Integer.parseInt(m.group(7));
					if (m.group(3) != null && m.group(3).length() > 0)
						sequenceNumberMap.put(seqNumber, m.group(2)+m.group(3));
					else
						sequenceNumberMap.put(seqNumber, m.group(2));
				} else {
					int residueNumber = Integer.parseInt(m.group(2));
					if (residueNumber < firstResidue)
						firstResidue = residueNumber;
				}
			}
		}
		if (sequenceNumberMap.size() > 0)
			chainToSequenceNumberMap.put(structure, sequenceNumberMap);
		// System.out.println("First residue for "+structure+" is "+firstResidue);
		chainToFirstResidueMap.put(structure, firstResidue); // structure includes chain!
	}

	public void allFinished(FinishStatus finishStatus) {
	}

	public void taskFinished(ObservableTask task) {
		if (currentCommand == null) return;

		if (currentCommand.equals(SENDCOMMAND)) {
			commandResults = task.getResults(String.class);
		}
		currentCommand = null;
	}

	private void launchChimera() {
		execute(LAUNCH, new HashMap<String, Object>());
	}

	private List<String> sendCommand(String command) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put(COMMAND, command);
		// System.out.println("Executing command: "+NAMESPACE+" "+SENDCOMMAND+" "+COMMAND+"="+command);
		execute(SENDCOMMAND, args);
		// System.out.println("From Chimera: "+commandResults);
		String[] resultsString = commandResults.split("\n");
		return Arrays.asList(resultsString);
	}

	private void execute(String command, Map<String, Object> args) {
		currentCommand = command;
		// System.out.println("Executing command: "+NAMESPACE+" "+command);
		taskManager.execute(
			commandExecutor.createTaskIterator(NAMESPACE, command, args, this), this);
	}

	private String getStructure(String chain) {
		String[] structChain = chain.split("\\.");
		return structChain[0];
	}

	private String getChain(String chain) {
		String[] structChain = chain.split("\\.");
		return structChain[1];
	}

	private String getModel(String chain) {
		String struct = getStructure(chain);
		if (structureToModelMap.containsKey(struct))
			return structureToModelMap.get(struct);
		return null;
	}

	private String buildSelectionString(String structure, String domain) {
		if (structure == null || domain == null) return null;
		String model = getModel(structure);
		if (model == null) return null;
		String chain = getChain(structure);

		String[] range = domain.split("-");
		if (range.length == 2) {
			String from = convertResidueNumber(structure, range[0]);
			String to = convertResidueNumber(structure, range[1]);
			return "#"+model+":"+from+"-"+to+"."+chain;
		}

		String selectionString = "#"+model+":";
		String[] sites = domain.split(",");
		for (String site: sites) {
			selectionString += convertResidue(structure, site)+"."+chain+",";
		}
		return selectionString.substring(0, selectionString.length()-1);
	}

	private String convertResidueNumber(String structure, String residueNumber) {
		// System.out.println("Converting residue number "+residueNumber+" for structure "+structure);
		// See if we have the actual index (best case)
		if (chainToSequenceNumberMap.containsKey(structure)) {
			// Sweet -- we have the new Chimera!  Use our map
			return chainToSequenceNumberMap.get(structure).get(Integer.parseInt(residueNumber));
		}

		if (!chainToFirstResidueMap.containsKey(structure))
			return residueNumber;

		int firstResidue = chainToFirstResidueMap.get(structure);
		// System.out.println("Converting residue number, firstResidue = "+firstResidue);
		int residue = Integer.parseInt(residueNumber);
		// System.out.println("Converted residue number="+(residue+firstResidue-1));
		return Integer.toString(residue+firstResidue-1);
	}

	private String convertResidue(String structure, String site) {
		String residue = site.substring(0,1); // NOTE: assumes single letter AA designation
		int residueNumber = Integer.parseInt(site.substring(1));
		if (chainToSequenceNumberMap.containsKey(structure)) {
			// Sweet -- we have the new Chimera!  Use our map
			return chainToSequenceNumberMap.get(structure).get(residueNumber);
		}

		if (!chainToFirstResidueMap.containsKey(structure))
			return site;

		int firstResidue = chainToFirstResidueMap.get(structure);
		return Integer.toString(residueNumber+firstResidue-1);
	}
}
