package edu.ucsf.rbvi.cddApp.internal.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TunableSetter;
import org.osgi.framework.BundleContext;

/**
 * Interface for sending commands to Chimera
 * 
 * @author Nadezhda Doncheva
 *
 */
public class SendCommandThread extends Thread {

	public SendCommandThread() {
	}

	public List<String> sendChimeraCommand(BundleContext context, String command) {
		TaskFactory sendCommandFactory = (TaskFactory) CyUtils.getService(context,
				TaskFactory.class, Messages.SV_SENDCOMMANDTASK);
		if (sendCommandFactory != null && sendCommandFactory.isReady()) {
			TunableSetter tunableSetter = (TunableSetter) CyUtils.getService(context,
					TunableSetter.class);
			Map<String, Object> tunables = new HashMap<String, Object>();
			tunables.put(Messages.SV_COMMANDTUNABLE, command);
			TaskManager<?, ?> tm = (TaskManager<?, ?>) CyUtils.getService(context,
					TaskManager.class);
			tm.execute(tunableSetter.createTaskIterator(sendCommandFactory.createTaskIterator(),
					tunables));
		}
		CyTableManager manager = (CyTableManager) CyUtils.getService(context, CyTableManager.class);
		CyTable chimOutputTable = null;
		Set<CyTable> tables = manager.getAllTables(true);
		for (CyTable table : tables) {
			if (table.getTitle().equals(Messages.SV_CHIMERATABLE)) {
				chimOutputTable = table;
			}
		}
		if (chimOutputTable != null) {
			while (true) {
				if (chimOutputTable.rowExists(command)) {
					if (chimOutputTable.getRow(command).isSet(Messages.SV_CHIMERAOUTPUT)) {
						List<String> output = chimOutputTable.getRow(command).getList(
								Messages.SV_CHIMERAOUTPUT, String.class);
						chimOutputTable.getRow(command).set(Messages.SV_CHIMERAOUTPUT, null);
						return output;
					}
				}
			}
		}
		return null;
	}
}