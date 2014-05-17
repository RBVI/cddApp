package edu.ucsf.rbvi.cddApp.internal.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

/**
 * Utilities for various cytoscape functions
 * 
 *
 */
public class CyUtils {
	// Column names
	final static String CDD_ACCESSION = "CDD-Accession"; // accession number of CDD domain
	final static String PDB_CHAIN = "PDB-Chain"; // if it is a PDB ID, the chain ID (in the form of PDB ID + chain)
	final static String CDD_HIT_TYPE = "CDD-Hit-Type"; // type of domain
	final static String CDD_FROM = "CDD-From"; // lower limit of the domain
	final static String CDD_TO = "CDD-To"; // upper limit of the domain
	final static String CDD_FEATURE = "CDD-Feature"; // type of feature site
	final static String PDB_CHAIN_FEATURES = "PDB-Chain-Features"; // chain containing the CDD-Feature
	final static String CDD_FEATURE_TYPE = "CDD-Feature-Type"; // type of CDD feature
	final static String CDD_FEATURE_SITE = "CDD-Feature-Site"; // list of residues of the feature site
	final static String CDD_DOMAIN_SIZE = "CDD-Domain-Size"; // size of the domain
	final static String CDD_DOMAIN_CHART = "CDD-Domain-Chart"; // a Custom Graphics component which shows the domains as
	//

	// Maybe should be in util/CyUtils?
	public static void createColumn(CyTable table, String columnName, Class type, Class elementType) {
		CyColumn column = table.getColumn(columnName);
		if (column != null) {
			if (!column.getType().equals(type))
				throw new RuntimeException("Column "+columnName+" already exists, but has a different type");
			if (column.getType().equals(List.class) && !column.getListElementType().equals(elementType))
				throw new RuntimeException("List column "+columnName+" already exists, but has a different element type");
			return;
		}
		if (type.equals(List.class))
			table.createListColumn(columnName, elementType, false);
		else
			table.createColumn(columnName, type, false);
		return;
	}

	public static boolean checkColumn(CyTable table, String columnName, Class type, Class elementType) {
		CyColumn column = table.getColumn(columnName);
		if (column == null || column.getType() != type)
			return false;
		if (type.equals(List.class) && column.getListElementType() != elementType)
			return false;
		return true;
	}

	public static String getName(CyNetwork network, CyIdentifiable id) {
		return network.getRow(id).get(CyNetwork.NAME, String.class);
	}

	public static CyIdentifiable getIdentifiable(CyNetwork network, Long suid) {
		if (network.getNode(suid) != null)
			return (CyIdentifiable)network.getNode(suid);
		else if (network.getEdge(suid) != null)
			return (CyIdentifiable)network.getEdge(suid);
		else
			return (CyIdentifiable)network;
	}
}
