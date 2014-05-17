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
	String currentCommand = null;
	String commandResults = null;

	// StructureViz namespace
	static String NAMESPACE = "structureViz";

	// StructureViz commands
	static String LAUNCH = "launchChimera";
	static String OPENCOMMAND = "openStructuresNodes";
	static String SENDCOMMAND = "sendCommand";

	// Arguments
	static String STRUCTURE_T = "structureTunable";
	static String COMMAND = "command";

	// Chimera commands
	static String LISTCOMMAND = "list models";
	static String SELECT = "select";
	static String UNSELECT = "~select";

	public StructureHandler(CDDDomainManager domainManager) {
		this.domainManager = domainManager;
		this.availableCommands = domainManager.getService(AvailableCommands.class);
		this.commandExecutor = domainManager.getService(CommandExecutorTaskFactory.class);
		this.taskManager = domainManager.getService(SynchronousTaskManager.class);
		structureToModelMap = new HashMap<String, String>();
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
		args.put(STRUCTURE_T, node+"|"+structChain[0]);
		execute(OPENCOMMAND, args);

		// Now, update our map of structures to models
		updateStructureMap();
	}

	public void select(String chain, String selectionString) {
		// Chain is of the form structure.chainId and selectionString
		// should either a comma-separated list of residues or a 
		// residue range
		String str = buildSelectionString(getModel(chain), selectionString, getChain(chain));
		if (str != null)
			sendCommand(SELECT+" "+ str);

	}

	public void unSelect(String chain, String selectionString) {
		String str = buildSelectionString(getModel(chain), selectionString, getChain(chain));
		if (str != null)
			sendCommand(UNSELECT+" "+ str);
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
		System.out.println("Executing command: "+NAMESPACE+" "+SENDCOMMAND+" "+COMMAND+"="+command);
		execute(SENDCOMMAND, args);
		System.out.println("From Chimera: "+commandResults);
		String[] resultsString = commandResults.split("\n");
		return Arrays.asList(resultsString);
	}

	private void execute(String command, Map<String, Object> args) {
		currentCommand = command;
		System.out.println("Executing command: "+NAMESPACE+" "+command);
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

	private String buildSelectionString(String model, String domain, String chain) {
		if (model == null || domain == null || chain == null) return null;

		String[] range = domain.split("-");
		if (range.length == 2) {
			return "#"+model+":"+domain+"."+chain;
		}

		String selectionString = "#"+model+":";
		String[] sites = domain.split(",");
		for (String site: sites) {
			selectionString += site+"."+chain+",";
		}
		return selectionString.substring(0, selectionString.length()-1);
	}
}
