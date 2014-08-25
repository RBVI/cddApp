package edu.ucsf.rbvi.cddApp.internal.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.RoundRectangle2D.Double;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;

import edu.ucsf.rbvi.cddApp.internal.model.CDDDomainManager;
import edu.ucsf.rbvi.cddApp.internal.model.PieChart;
import edu.ucsf.rbvi.cddApp.internal.util.CyUtils;

/**
 * Displays information on the domains of a protein from the CDD in the Results panel.
 * @author Allan Wu
 *
 */
public class StructureDiagramPanel extends JPanel {
	
	final CDDDomainManager domainManager;
	final CyIdentifiable cyId;
	final CyNetwork net;
	List<Color> colorList;
	List<String> labelList;
	List<Long> sizes;
	long length = 0;

	public static final String LABEL_FONT=Font.SANS_SERIF;
	public static final int LABEL_STYLE=Font.BOLD;
	public static final int LABEL_SIZE=12;

	public static final String AXIS_FONT=Font.SANS_SERIF;
	public static final int AXIS_STYLE=Font.PLAIN;
	public static final int AXIS_SIZE=8;

	public static final Font labelFont = new Font(LABEL_FONT, LABEL_STYLE, LABEL_SIZE);
	public static final Font axisFont = new Font(AXIS_FONT, AXIS_STYLE, AXIS_SIZE);

	public StructureDiagramPanel(CyNetwork net, CyIdentifiable cyId, CDDDomainManager manager) {
		super();
		this.domainManager = manager;
		this.cyId = cyId;
		this.net = net;

		String chartString = PieChart.getDomainChart(net, cyId);
		// System.out.println("chartString = "+chartString);

		// Skip over the chart type
		chartString = chartString.substring(10);
		colorList = getColorList(chartString);
		labelList = getLabelList(chartString);
		while (labelList.size() < colorList.size()) {
			labelList.add("");
		}
		sizes = PieChart.getDomainSizes(net, cyId);
		for (Long size: sizes) length += size;

		// Because the pie charts work in reverse, we need to unwind
		// these lists
		Collections.reverse(colorList);
		Collections.reverse(labelList);
		Collections.reverse(sizes);
	}

	List<Color> getColorList(String chartString) {
		List<Color> list = new ArrayList<Color>();
		int start = chartString.indexOf("colorlist=\"")+11;
		int end = chartString.indexOf("\"", start);
		String colorString = chartString.substring(start, end);
		String[] colorArray = colorString.split(",");
		for (String color: colorArray) {
			if (color.startsWith("#")) {
				// Get the integer representation
				Integer rgb = Integer.parseInt(color.substring(1,7),16);
				list.add(Color.decode(rgb.toString()));
			} else if (color.equals("lightgrey")) {
				list.add(Color.lightGray);
			}
		}

		return list;
	}

	List<String> getLabelList(String chartString) {
		List<String> list = new ArrayList<String>();
		int start = chartString.indexOf("labellist=\"")+11;
		int end = chartString.indexOf("\"", start);
		String labelString = chartString.substring(start, end);
		String[] labelArray = labelString.split(",");
		for (String label: labelArray) {
			list.add(label);
		}
		return list;
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
		                     RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, 
		                     RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, 
		                     RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
		                     RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		Rectangle bounds = getBounds();
		int margin = 10;
		double startx = bounds.getX()+margin;
		double endx = bounds.getX()+bounds.getWidth()-margin*2;
		double maxlength = endx-startx;

		double scale = maxlength/length;
		double ymid = bounds.getY()+bounds.getHeight()/2;

		long start = 0;
		// Draw each domain
		g2d.setStroke(new BasicStroke(0.5f));
		g2d.setFont(labelFont);
		for (int domain = 0; domain < sizes.size(); domain++) {
			long size = sizes.get(domain);
			Color color = colorList.get(domain);
			if (!color.equals(Color.lightGray)) {
				double x = scaleX(startx, scale, start);
				double width = size*scale;
				RoundRectangle2D d = new RoundRectangle2D.Double(x, ymid-20, width, 40, 20, 20);
				g2d.setPaint(color);
				g2d.fill(d);
				g2d.setColor(Color.BLACK);
				g2d.draw(d);
			}
			start = start+size;
		}

		// Draw the labels.  We do this in a separate path
		// to avoid one domain overwriting the label of another
		start = 0;
		for (int domain = 0; domain < sizes.size(); domain++) {
			long size = sizes.get(domain);
			Color color = colorList.get(domain);
			if (!color.equals(Color.lightGray)) {
				/* Convert the color to a contrasting color
				float[] hsbColor = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				hsbColor[0] = hsbColor[0]+0.5f;
				if (hsbColor[0]>1.0f) hsbColor[0] = hsbColor[0] - 1.0f;
				color = Color.getHSBColor(hsbColor[0], hsbColor[1], hsbColor[2]);
				g2d.setColor(color);
				*/
				String label = labelList.get(domain);
				double x = scaleX(startx, scale, start);
				double width = size*scale;
				int stringWidth = g2d.getFontMetrics().stringWidth(label);
				double offset = (width-stringWidth)/2;
				if ((int)(x+offset) < 0)
					g2d.drawString(label, 1, (int)(ymid-6));
				else
					g2d.drawString(label, (int)(x+offset), (int)(ymid-6));
			}
			start = start+size;
		}

		// Draw the axis
		g2d.setFont(axisFont);
		g2d.setStroke(new BasicStroke(1));
		g2d.setColor(Color.BLACK);
		g2d.drawLine((int)startx, 
								 (int)ymid,
								 (int)endx,
								 (int)ymid);


		// Draw a tick every 50 bp
		g2d.setColor(Color.DARK_GRAY);
		for (int i = 50; i < length; i=i+50) {
			drawTick(g2d, i, startx, ymid, scale);
		}

		g2d.setColor(Color.BLACK);
		drawTick(g2d, 0, startx, ymid, scale);
		drawTick(g2d, (int)length, startx, ymid, scale);
	}

	double scaleX(double start, double scale, long value) {
		return (value*scale)+start;
	}

	private void drawTick(Graphics2D g2d, int bp, double startx, 
	                       double ymid, double scale) {
		double x = scaleX(startx, scale, bp);
		g2d.drawLine((int)x, (int)(ymid+4), (int)x, (int)(ymid));
		String label = String.valueOf(bp);
		int center = g2d.getFontMetrics().stringWidth(label)/2;
		g2d.drawString(label, (int)(x-center), (int)(ymid+15));
	}

}
