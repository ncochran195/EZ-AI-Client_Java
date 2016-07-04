package com.ezai.client.gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3562453975355628719L;
	private BufferedImage image;
	private String imageFilename;
	
	public ImagePanel(String filename) {
		try {
			image = ImageIO.read(new File(filename));
			imageFilename = filename;
		} catch (IOException ex) {
			// handle exception...
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, 400, 225, this); // see javadoc for more info on the
										// parameters
	}

	
	public void updateImage() {
		try {
			image = ImageIO.read(new File(imageFilename));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
