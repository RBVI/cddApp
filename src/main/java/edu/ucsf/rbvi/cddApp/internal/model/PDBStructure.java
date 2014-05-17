package edu.ucsf.rbvi.cddApp.internal.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

import edu.ucsf.rbvi.cddApp.internal.util.CyUtils;

/**
 * PDBStructure
 * 
 */
public class PDBStructure {
	final public static String PDB_FILENAME = "pdbFileName";

	public static void updatePDBColumn(CyNetwork network, String column) {
		CyTable netTable = network.getDefaultNetworkTable();
		CyUtils.createColumn(netTable, PDB_FILENAME, String.class, null);
		network.getRow(network).set(PDB_FILENAME, column);
	}

	public static boolean havePDB(CyNetwork network) {
		if (!CyUtils.checkColumn(network.getDefaultNetworkTable(), PDB_FILENAME, String.class, null))
			return false;

		String pdbColumn = getPDBColumnName(network);
		if (pdbColumn == null || pdbColumn.length() == 0)
			return false;

		return true;
	}

	public static String getPDBColumnName(CyNetwork network) {
		if (!CyUtils.checkColumn(network.getDefaultNetworkTable(), PDB_FILENAME, String.class, null))
			return null;
		return network.getRow(network).get(PDB_FILENAME, String.class);
	}

	public static List<PDBStructure> reloadStructures(CyNetwork network,
	                                                  CyIdentifiable id, List<CDDHit> hits) {

		List<String> structures = getStructures(network, id);
		if (structures == null || structures.size() == 0)
			return null;

		List<PDBStructure> structureList = new ArrayList<PDBStructure>();
		Map<String, PDBStructure> structureMap = new HashMap<String, PDBStructure>();

		// We actually use the CDDHit information to reload our structures because
		// this is where the validated information gets stored
		for (CDDHit hit: hits) {
			String proteinId = hit.getProteinId();
			String[] structChain = proteinId.split("\\.");
			if (structChain.length == 2) {
				if (!structureMap.containsKey(structChain[0])) {
					PDBStructure s = new PDBStructure(structChain[0], null);
					structureMap.put(structChain[0], s);
					structureList.add(s);
				}
				structureMap.get(structChain[0]).addChain(structChain[1]);
			}
		}
		if (structureList.size() == 0)
			return null;
		return structureList;
	}

	public static List<String> getStructures(CyNetwork network, CyIdentifiable id) {
		String pdbColumn = getPDBColumnName(network);
		if (pdbColumn == null || pdbColumn.length() == 0)
			return null;

		if (!network.getRow(id).isSet(pdbColumn))
			return null;

		List<String> columnListString = null;
		if (network.getDefaultNodeTable().getColumn(pdbColumn).getType() == List.class)
			columnListString = network.getRow(id).getList(pdbColumn, String.class);
		else {
			String str = network.getRow(id).get(pdbColumn, String.class);
			if (str != null && str.length() > 0) {
				String[] splitStr = str.split(",");
				columnListString = Arrays.asList(splitStr);
			}
		}
		return columnListString;
	}

	public static List<String> getFullNames(List<PDBStructure> structs) {
		if (structs == null || structs.size() == 0) return null;
		List<String> fullNames = null;
		for (PDBStructure struct: structs) {
			if (struct.getChains() != null && struct.getChains().size() > 0) {
				if (fullNames == null)
					fullNames = new ArrayList<String>();
				fullNames.addAll(struct.getFullNames());
			}
		}
		return fullNames;
	}

	public static int countChains(List<PDBStructure> structs) {
		if (structs == null || structs.size() == 0) return 0;
		int chains = 0;
		for (PDBStructure struct: structs) {
			if (struct.getChains() != null) {
				chains += struct.getChains().size();
			}
		}
		return chains;
	}

	public static int countUniqueChains(List<PDBStructure> structs) {
		if (structs == null || structs.size() == 0) return 0;
		List<PDBStructure> uStructs = getUniqueStructures(structs);
		return countChains(uStructs);
	}

	public static List<PDBStructure> getUniqueStructures(List<PDBStructure> structs) {
		Map<String, PDBStructure> stMap = new HashMap<String, PDBStructure>();
		for (PDBStructure struct: structs) {
			if (!stMap.containsKey(struct.getStructure()))
				stMap.put(struct.getStructure(), struct);
		}
		return new ArrayList<PDBStructure>(stMap.values());
	}

	public static PDBStructure getStructure(List<PDBStructure> structs, String chain) {
		if (structs == null || structs.size() == 0 || chain == null || chain.length() == 0)
			return null;

		String[] chainSplit = chain.split("\\.");
		if (chainSplit.length != 2)
				return null;

		for (PDBStructure struct: structs) {
			if (struct.getStructure().equalsIgnoreCase(chainSplit[0]) &&
					struct.containsChain(chainSplit[1]))
				return struct;
		}
		return null;
	}



	String structure;
	List<String> chains;

	public PDBStructure() {
	}

	public PDBStructure(String struct, List<String> chains) {
		this.structure = struct;
		this.chains = chains;
	}

	public void addChain(String chain) {
		if (chains == null)
			chains = new ArrayList<String>();
		if (!containsChain(chain))
			chains.add(chain);
	}

	public boolean containsChain(String chain) {
		if (chains == null)
			return false;
		for (String c: chains) {
			if (c.equalsIgnoreCase(chain))
				return true;
		}
		return false;
	}

	public String getStructure() { return structure; }
	public List<String> getChains() { return chains; }

	public List<String> getFullNames() {
		if (chains == null || chains.size() == 0) return null;
		List<String>fullNames = new ArrayList<String>();
		for (String chain: chains)
			fullNames.add(structure+"."+chain);
		return fullNames;
	}
}
