package edu.ucsf.rbvi.cddApp.internal.ui;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;

import edu.ucsf.rbvi.cddApp.internal.model.CDDDomainManager;
import edu.ucsf.rbvi.cddApp.internal.model.CDDFeature;
import edu.ucsf.rbvi.cddApp.internal.model.CDDHit;
import edu.ucsf.rbvi.cddApp.internal.model.StructureHandler;
import edu.ucsf.rbvi.cddApp.internal.util.CyUtils;
import edu.ucsf.rbvi.cddApp.internal.util.NetUtils;

/**
 * Displays information on the domains of a protein from the CDD in the Results panel.
 * @author Allan Wu
 *
 */
public class DomainTableModel extends AbstractTableModel {
	final CDDDomainManager domainManager;
	final CyIdentifiable cyId;
	final String[] columnNames;
	final String chain;
	final List<CDDHit> hits;
	final List<CDDFeature> features;
	final boolean chainTable;
	final List<Boolean> shown;
	final StructureHandler svHandler;

	public DomainTableModel(CyIdentifiable cyId, CDDDomainManager manager, 
	                        String[] columnNames, String chain) {
		this.domainManager = manager;
		this.cyId = cyId;
		this.chain = chain;
		this.svHandler = domainManager.getStructureHandler();
		chainTable = (columnNames.length == 4 && svHandler.structureVizAvailable()) ? true : false;
		if (!chainTable) {
			hits = manager.getHits(cyId);
			features = manager.getFeatures(cyId);
			this.columnNames = Arrays.copyOf(columnNames,3);
			shown = null;
		} else {
			hits = manager.getHits(cyId, chain);
			features = manager.getFeatures(cyId, chain);
			shown = new ArrayList<Boolean>(hits.size()+features.size());
			this.columnNames = columnNames;
			for (int i = 0; i < (hits.size()+features.size()); i++)
				shown.add(Boolean.FALSE);
		}
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return hits.size()+features.size();
	}

	public String getColumnName(int column) {
		if (column < columnNames.length)
			return columnNames[column];
		return null;
	}

	public Class<?> getColumnClass(int column) {
		if (chainTable && column == 3)
			return Boolean.class;
		return String.class;
	}

	public boolean isCellEditable(int rowIndex, int column) {
		if (chainTable && column == 3)
			return true;
		return false;
	}

	public Object getValueAt(int rowIndex, int column) {
		if (rowIndex >= hits.size()) {
			// Feature row
			CDDFeature feature = features.get(rowIndex-hits.size());
			switch (column) {
				case 0:
					return feature.getAccession();
				case 1:
					return feature.getFeatureType()+" feature";
				case 2:
					return feature.getFeatureSite();
				case 3:
					return shown.get(rowIndex);
			}
		} else {
			// Hits row
			CDDHit hit = hits.get(rowIndex);
			switch (column) {
				case 0:
					return "<html>"+NetUtils.makeCDDLink(hit.getName(), hit.getAccession())+"</html>";
				case 1:
					return hit.getHitType()+" domain";
				case 2:
					return ""+hit.getFrom()+"-"+hit.getTo();
				case 3:
					return shown.get(rowIndex);
			}
		}
		return null;
	}

	public void setValueAt(Object value, int rowIndex, int column) {
		System.out.println("SetValue at: "+rowIndex+","+column+" to "+value);
		System.out.println("  current value at: "+rowIndex+","+column+" is "+getValueAt(rowIndex, column));
		if (column != 3 || !(value instanceof Boolean)) return;
		Boolean boolValue = (Boolean)value;
		if (shown.get(rowIndex).equals(boolValue)) return;

		if (boolValue) {
			domainManager.setIgnoreSelection(true);
			System.out.println("Selection ignored");
			// Show the structure & select the domain/site
			svHandler.openStructure(CyUtils.getName(domainManager.getCurrentNetwork(),cyId),chain);
			svHandler.select(chain, (String)getValueAt(rowIndex, 2));
			// System.out.println("Selection not ignored");
			domainManager.setIgnoreSelection(false);
		} else {
			domainManager.setIgnoreSelection(true);
			// Unselect the domain/site
			svHandler.unSelect(chain, (String)getValueAt(rowIndex, 2));
			domainManager.setIgnoreSelection(false);
		}
		shown.set(rowIndex, boolValue);
	}

	public boolean hasLink(int rowIndex, int column) {
		if (column == 0 && rowIndex < hits.size())
			return true;
		return false;
	}

	public String getURLLink(int rowIndex, int column) {
		if (!hasLink(rowIndex, column)) 
			return null;
		return NetUtils.makeCDDURL(hits.get(rowIndex).getAccession());
	}

	public String getTooltipText(int row, int column) {
		if (chainTable && column == 3)
			return "Click to open structure and select in UCSF Chimera";
		if (hasLink(row, column)) {
			return hits.get(row).getName();
		}
		return (String) getValueAt(row, column);
	}

}
