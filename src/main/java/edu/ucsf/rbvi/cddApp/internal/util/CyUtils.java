package edu.ucsf.rbvi.cddApp.internal.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * Utilities for RINalyzer2
 * 
 * @author Nadezhda Doncheva
 *
 */
public abstract class CyUtils {

	public static Object getService(BundleContext bc, Class<?> serviceClass) {
		if (bc.getServiceReference(serviceClass.getName()) != null) {
			return bc.getService(bc.getServiceReference(serviceClass.getName()));
		}
		return null;
	}

	public static Object getService(BundleContext bc, Class<?> serviceClass, String filter) {
		try {
			ServiceReference[] services = bc.getServiceReferences(serviceClass.getName(), filter);
			if (services != null && services.length > 0) {
				return bc.getService(services[0]);
			}
		} catch (Exception ex) {
			// ignore
			// ex.printStackTrace();
		}
		return null;
	}

	public static JFrame getCyFrame(BundleContext bc) {
		CySwingApplication cyApp = (CySwingApplication) getService(bc, CySwingApplication.class);
		if (cyApp != null) {
			return cyApp.getJFrame();
		}
		return null;
	}

	public static List<String> getAllNetNames(CyNetworkManager manager, Set<CyNetwork> networks) {
		List<String> netNames = new ArrayList<String>();
		for (CyNetwork network : networks) {
			if (manager.networkExists(network.getSUID())) {
				String netName = network.getRow(network).get(CyNetwork.NAME, String.class);
				if (netName != null && netName.trim() != "") {
					netNames.add(netName);
				}
			}
		}
		return netNames;
	}

	public static CyNetwork getNetwork(CyNetworkManager manager, String name) {
		for (CyNetwork network : manager.getNetworkSet()) {
			String netName = network.getRow(network).get(CyNetwork.NAME, String.class);
			if (netName.equals(name)) {
				return network;
			}
		}
		return null;
	}

	public static CyNode getNode(CyNetwork network, String name, String attr) {
		for (CyNode node : network.getNodeList()) {
			if (network.getRow(node).isSet(attr)
					&& network.getRow(node).get(attr, String.class).equals(name)) {
				return node;
			}
		}
		return null;
	}

	public static String getCyName(CyNetwork network, CyIdentifiable cyId) {
		return getCyName(network, cyId, CyNetwork.NAME);
	}

	public static String getCyName(CyNetwork network, CyIdentifiable cyId, String attr) {
		if (cyId instanceof CyNode && network.containsNode((CyNode) cyId)
				&& network.getRow(cyId).isSet(attr)) {
			return network.getRow(cyId).get(attr, String.class);
		} else if (cyId instanceof CyEdge && network.containsEdge((CyEdge) cyId)
				&& network.getRow(cyId).isSet(attr)) {
			return network.getRow(cyId).get(attr, String.class);
		} else {
			return "";
		}
	}

	public static List<String> getStringAttributes(CyNetwork network, Class<?> cyClass) {
		List<String> attrColumns = new ArrayList<String>();
		if (network != null) {
			CyTable table = null;
			if (cyClass.equals(CyNode.class)) {
				table = network.getDefaultNodeTable();
			} else {
				table = network.getDefaultEdgeTable();
			}
			for (CyColumn column : table.getColumns()) {
				if (column.getType().equals(String.class)) {
					attrColumns.add(column.getName());
				}
			}
		}
		return attrColumns;
	}

	public static List<String> getNumericAttributes(CyNetwork network, Class<?> cyClass) {
		List<String> attrColumns = new ArrayList<String>();
		if (network != null) {
			CyTable table = null;
			if (cyClass.equals(CyNode.class)) {
				table = network.getDefaultNodeTable();
			} else {
				table = network.getDefaultEdgeTable();
			}
			for (CyColumn column : table.getColumns()) {
				if (column.getType().equals(Integer.class) || column.getType().equals(Double.class)
						|| column.getType().equals(Float.class)) {
					attrColumns.add(column.getName());
				}
			}
		}
		return attrColumns;
	}

	public static List<String> getStringAttrValues(CyNetwork network, String attrName,
			Class<?> cyClass) {
		List<String> items = new ArrayList<String>();
		if (network != null) {
			CyTable table = null;
			if (cyClass.equals(CyNode.class)) {
				table = network.getDefaultNodeTable();
			} else {
				table = network.getDefaultEdgeTable();
			}
			CyColumn column = table.getColumn(attrName);
			if (column != null) {
				List<String> values = column.getValues(String.class);
				if (values != null) {
					for (String value : values) {
						if (value != null && !items.contains(value)) {
							items.add(value);
						}
					}
				}
			}
		}
		return items;
	}

	/**
	 * Return all different values of an edge attribute with the name
	 * <code>aAttrName</code> and a data type {@link String} in the network
	 * <code>aNetwork</code> as a set.
	 * 
	 * @param aNetwork
	 *            Cytoscape network.
	 * @param aAttrName
	 *            Name of the edge attribute, whose values have to be returned.
	 * @return List of the string values of the edge attribute
	 *         <code>aAttrName</code> in the network <code>aNetwork</code>.
	 */
	public static List<String> getEdgeTypes(CyNetwork aNetwork, String aAttrName) {
		List<String> edgeTypes = new ArrayList<String>();
		for (CyEdge edge : aNetwork.getEdgeList()) {
			final String edgeAttr = aNetwork.getRow(edge).get(CyEdge.INTERACTION, String.class);
			if (edgeAttr != null && !edgeTypes.contains(edgeAttr)) {
				edgeTypes.add(edgeAttr);
			}
		}
		return edgeTypes;
	}

	public static void selectNodes(BundleContext context, CyNetwork network, Set<CyNode> nodes) {
		for (CyNode node : network.getNodeList()) {
			if (nodes.contains(node)) {
				network.getRow(node).set(CyNetwork.SELECTED, true);
			} else {
				network.getRow(node).set(CyNetwork.SELECTED, false);
			}
		}
		CyNetworkViewManager manager = (CyNetworkViewManager) getService(context,
				CyNetworkViewManager.class);
		for (CyNetworkView view : manager.getNetworkViewSet()) {
			if (view.getModel().equals(network)) {
				view.updateView();
			}
		}

	}

	/**
	 * Go over all nodes in the network and save an array with five label parts
	 * (pdb id, chain id, residue index, iCode, residue type). If there is no
	 * pdb id, the first element of the array is <code>null</code>, if the node
	 * label can't be split by ":" or it has more than 5 elements after split,
	 * the whole label is stored as residue type. If there is no chain, residue
	 * number, etc. the corresponding value in the array is set to "". The
	 * arrays are stored in a map keyed by the nodes.
	 */
	public static Map<CyNode, String[]> splitNodeLabels(CyNetwork aNetwork, String nameAttr) {
		final Map<CyNode, String[]> nodeLabels = new HashMap<CyNode, String[]>();
		for (final CyNode node : aNetwork.getNodeList()) {
			final String nodeName = aNetwork.getRow(node).get(nameAttr, String.class);
			final String[] labels = nodeName.split(":");
			String[] labelList = null;
			// Node name should be pdb1abc:A:42:_:GLU or A:42:_:GLU
			if (labels.length > 1) {
				if (labels.length == 4) {
					// labelList = getLabelArray(null, labels[0], labels[1],
					// labels[2], labels[3]);
					labelList = getLabelArray(null, labels[0], labels[1], labels[2], labels[3]);
				} else if (labels.length == 5) {
					// labelList = getLabelArray(labels[0], labels[1],
					// labels[2], labels[3], labels[4]);
					labelList = getLabelArray(labels[0], labels[1], labels[2], labels[3], labels[4]);
				} else {
					// labelList = getLabelArray(null, null, null, null,
					// node.getIdentifier());
					labelList = getLabelArray(null, "", "", "", nodeName);
				}
			} else {
				// labelList = getLabelArray(null, null, null, null, labels[0]);
				labelList = getLabelArray(null, "", "", "", labels[0]);
			}
			if (labelList != null) {
				nodeLabels.put(node, labelList);
			}
		}
		return nodeLabels;
	}

	/**
	 * Get a string array with the different label parts from the input, in the
	 * following order: pdb id, chain id, residue index, iCode, residue type.
	 * 
	 * @param aPdb
	 *            Pdb identifier (at the 0th position in the array).
	 * @param aChain
	 *            Chain identifier (at the 1th position in the array).
	 * @param aIndex
	 *            Residue index (at the 2th position in the array).
	 * @param aIcode
	 *            Insertion code (at the 3th position in the array).
	 * @param aType
	 *            Residue type (at the 4th position in the array).
	 * @return String array with the different label parts.
	 */
	private static String[] getLabelArray(String aPdb, String aChain, String aIndex, String aIcode,
			String aType) {
		final String[] labels = new String[5];
		labels[0] = aPdb;
		labels[1] = aChain;
		labels[2] = aIndex;
		labels[3] = aIcode;
		labels[4] = aType;
		return labels;
	}

	/**
	 * Get a formatted node label. Only the parts of the label are included,
	 * that the user has checked.
	 * 
	 * @param aSeparator
	 *            Separator between the label parts.
	 * @param visibleLabels
	 *            Array with flags for each label part: [pdb id, chain id,
	 *            residue index, iCode, residue type]
	 * @param labelArray
	 *            Array with the label parts: [pdb id, chain id, residue index,
	 *            iCode, residue type]
	 * @param defValue
	 *            Default value for the label, if no of the parts is checked.
	 * @param threeCode
	 *            Flag indicating whether amino acid residues name is displayed
	 *            as 3-letter or 1-letter code.
	 * @return Formatted label in different order! [pdb id, residue type, chain
	 *         id, residue index, insertion code]
	 */
	public static String getLabel(String aSeparator, boolean[] visibleLabels, String[] labelArray,
			String defValue, boolean threeCode) {
		StringBuilder sb = new StringBuilder();
		// pdb id
		if (visibleLabels[0] && labelArray[0] != null) {
			sb.append(labelArray[0]);
			sb.append(aSeparator);
		}
		// residue type
		if (visibleLabels[4] && labelArray[4] != null) {
			if (!threeCode && Messages.aaNames.containsKey(labelArray[4])) {
				final String descr = Messages.aaNames.get(labelArray[4]);
				sb.append(descr.substring(0, 1));
			} else {
				sb.append(labelArray[4]);
			}
			sb.append(aSeparator);
		}
		// residue index
		if (visibleLabels[2] && labelArray[2] != null) {
			sb.append(labelArray[2]);
			// insertion Code
			if (visibleLabels[3] && labelArray[3] != null) {
				sb.append(labelArray[3]);
			}
			sb.append(aSeparator);
		}
		// chain id
		if (visibleLabels[1] && labelArray[1] != null) {
			sb.append(labelArray[1]);
			sb.append(aSeparator);
		}
		String label = sb.toString().trim();
		if (label.length() == 0) {
			label = defValue;
		}
		return label;
	}
}