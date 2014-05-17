package edu.ucsf.rbvi.cddApp.internal.ui;

import java.util.List;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JTable;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableCellRenderer;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;

import edu.ucsf.rbvi.cddApp.internal.model.CDDDomainManager;
import edu.ucsf.rbvi.cddApp.internal.model.CDDFeature;
import edu.ucsf.rbvi.cddApp.internal.model.CDDHit;

/**
 * Displays information on the domains of a protein from the CDD in the Results panel.
 * @author Allan Wu
 *
 */
public class ChainTable extends JTable {
	final CDDDomainManager domainManager;
	final CyIdentifiable cyId;
	final String chain;
	final static String[] columnNames = {"Domain","Type","Location","Show?"};
	final DomainTableModel tableModel;
	final JTable table;

	public ChainTable(CyIdentifiable cyId, String chain, CDDDomainManager manager) {
		super(new DomainTableModel(cyId, manager, columnNames, chain));
		tableModel = (DomainTableModel)this.getModel();
		this.domainManager = manager;
		this.cyId = cyId;
		this.table = this;
		this.chain = chain;
		this.addMouseListener(new HyperlinkMouseAdapter());
		this.addMouseMotionListener(new HyperlinkMouseAdapter());
		this.setRowSelectionAllowed(false);
	}

	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		Component c = super.prepareRenderer(renderer, row, column);
		if (c instanceof JComponent) {
			JComponent jc = (JComponent) c;
			jc.setToolTipText(tableModel.getTooltipText(row, column));
		}
		return c;
	}

	public class HyperlinkMouseAdapter extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			int row = table.rowAtPoint(new Point(e.getX(), e.getY()));
			int column = table.columnAtPoint(new Point(e.getX(), e.getY()));
			if (!tableModel.hasLink(row, column))
				return;
			domainManager.openURL(tableModel.getURLLink(row, column));
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			int row = table.rowAtPoint(new Point(e.getX(), e.getY()));
			int column = table.columnAtPoint(new Point(e.getX(), e.getY()));
			if (tableModel.hasLink(row, column))
				table.setCursor(new Cursor(Cursor.HAND_CURSOR));
			else
				table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}
}
