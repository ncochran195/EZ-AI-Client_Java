package com.ezai.client.gui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
//import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import com.ezai.client.handler.ConfigurationHandler;

public class TestGui extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7671414338194240216L;

	private static TestGui instance;
	ImagePanel imagePanel = null;

	JPanel mainPanel = null;

	JTextArea textArea = null;

	private TestGui() {
	}

	private TestGui(String s) {
		super(s);
		setSize(400, 400);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				System.exit(0);
			}
		});

		try {
			imagePanel = new ImagePanel(ConfigurationHandler.getInstance().getValue("imageInFilename"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mainPanel = new JPanel();
		mainPanel.setSize(400, 400);
		mainPanel.setLayout(new GridLayout(2, 1));

		mainPanel.add(imagePanel);

		textArea = new JTextArea(5, 10);
		DefaultCaret caret = (DefaultCaret) textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		mainPanel.add(scrollPane);

		add(mainPanel);

		setResizable(false);
		setVisible(true);

	}

	public static TestGui getInstance() {
		if (instance == null)
			instance = new TestGui("EZ-AI Client");
		return instance;
	}

	public void updateImage() {
		imagePanel.updateImage();
	}

	public void addText(String text) {
		textArea.append(text);
		textArea.append("\n");
		// textArea.setCaretPosition(textArea.getDocument().getLength());
	}

	public void forceUpdate() {
		update(getGraphics());
	}
}
