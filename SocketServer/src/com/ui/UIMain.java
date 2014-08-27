package com.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.io.SocketServer;
 
public class UIMain extends Component {
           
    BufferedImage img;
 
    public void paint(Graphics g) {
        g.drawImage(img, 0, 0, null);
    }
 
    public UIMain() {

    }
    
    public void updateUI(byte[] data) {
    	ByteArrayInputStream input = new ByteArrayInputStream(data);
    	try {
			img = ImageIO.read(input);
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	repaint();
    }
 
    public Dimension getPreferredSize() {
        if (img == null) {
             return new Dimension(640,480);
        } else {
           return new Dimension(img.getWidth(null), img.getHeight(null));
       }
    }
 
    public static void main(String[] args) {
 
        JFrame f = new JFrame("Load Image Sample");
             
        f.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
 
        f.add(new UIMain());
        f.pack();
        f.setVisible(true);
        
        SocketServer server = new SocketServer();
        server.start();
    }
}
