package com.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.data.DataListener;
import com.io.SocketServer;
 
public class ServerUIMain extends Component implements DataListener{
           
    BufferedImage img;
 
    public void paint(Graphics g) {
        Random r = new Random();
        if (img != null) {
            synchronized (img) {
                g.drawImage(img, r.nextInt(50), r.nextInt(50), null);
            }
        }
    }
 
    public ServerUIMain() {
    	SocketServer server = new SocketServer();
        server.setOnDataListener(this);
        server.start();
    }
    
    private void updateUI(byte[] data) {
    	ByteArrayInputStream input = new ByteArrayInputStream(data);
    	try {
    	    if (img != null) {
    	        synchronized (img) {
                    img = ImageIO.read(input);
                }
    	    }
    	    else
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
 
        f.add(new ServerUIMain());
        f.pack();
        f.setVisible(true);
    }

	@Override
	public void onDirty(byte[] data) {
		// TODO Auto-generated method stub
		updateUI(data);
	}
}
