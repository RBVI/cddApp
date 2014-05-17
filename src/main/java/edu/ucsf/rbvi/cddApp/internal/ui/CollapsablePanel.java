package edu.ucsf.rbvi.cddApp.internal.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.ucsf.rbvi.cddApp.internal.model.CDDDomainManager;

public class CollapsablePanel extends JPanel {
	private static String RIGHT_ARROW = "\uF0DA";
	private static String DOWN_ARROW = "\uF0D7";

	JPanel contentPanel_;
	HeaderPanel headerPanel_;

	private class HeaderPanel extends JPanel implements ActionListener {
		Font font;
		JButton expandButton;
		JLabel label;
		boolean expanded = false;

		public HeaderPanel(String text, boolean collapsed) {
			font = new Font("sans-serif", Font.PLAIN, 11);

			this.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
			this.expanded = !collapsed;

			if (collapsed)
				expandButton = new JButton(RIGHT_ARROW);
			else
				expandButton = new JButton(DOWN_ARROW);
			expandButton.addActionListener(this);
			expandButton.setBorderPainted(false);
			expandButton.setContentAreaFilled(false);
			expandButton.setOpaque(false);
			expandButton.setFocusPainted(false);
			expandButton.setFont(CDDDomainManager.getAwesomeFont().deriveFont(17.0f));
			this.add(expandButton);
			label = new JLabel(text);
			label.setFont(font);
			this.add(label);
			this.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		}

		public void setText(String text) {
			System.out.println("Setting label text to: "+text);
			label.setText("<html>"+text+"</html>");
		}

		public void actionPerformed(ActionEvent e) {
			toggleSelection();
		}

		public void setButton(String buttonState) {
			expandButton.setText(buttonState);
		}

	}

	public CollapsablePanel(String text, JPanel panel, boolean collapsed) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		headerPanel_ = new HeaderPanel(text, collapsed);

		setBackground(new Color(200, 200, 220));
		contentPanel_ = panel;

		add(headerPanel_);
		add(contentPanel_);
		contentPanel_.setVisible(!collapsed);
	}

	public void setLabel(String label) {
		headerPanel_.setText(label);
	}

	public void addContent(JComponent panel) {
		contentPanel_.add(panel);
	}

	public JPanel getContent() {
		return contentPanel_;
	}

	public void toggleSelection() {
		if (contentPanel_.isShowing()) {
			headerPanel_.setButton(RIGHT_ARROW);
			contentPanel_.setVisible(false);
		} else {
			contentPanel_.setVisible(true);
			headerPanel_.setButton(DOWN_ARROW);
		}

		validate();

		headerPanel_.repaint();
	}

	public void collapse() {
		if (contentPanel_.isShowing())
			toggleSelection();
	}

	public void expand() {
		if (!contentPanel_.isShowing())
			toggleSelection();
	}
}
