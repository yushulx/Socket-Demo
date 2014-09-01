package com.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.data.BufferManager;
import com.data.DataListener;
import com.io.SocketServer;
 
public class ServerUIMain extends JPanel implements DataListener{
	private LinkedList<BufferedImage> mQueue = new LinkedList<BufferedImage>();
	private static final int MAX_BUFFER = 15;
           
    BufferedImage mImage, mLastFrame;
 
    @Override
    public void paint(Graphics g) {
        synchronized (mQueue) {
        	if (mQueue.size() > 0) {
        		mLastFrame = mQueue.poll();
        	}	
        }
        if (mLastFrame != null) {
        	g.drawImage(mLastFrame, 0, 0, null);
        }
        else if (mImage != null) {
            g.drawImage(mImage, 0, 0, null);
        }
    }
 
    public ServerUIMain() {
    	SocketServer server = new SocketServer();
        server.setOnDataListener(this);
        server.start();
    }
    
    private void updateUI(byte[] data, int width, int height) {
    	BufferedImage bufferedImage = null;
		int[] rgbArray = Utils.convertYUVtoRGB(data, width, height);
		bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		bufferedImage.setRGB(0, 0, width, height, rgbArray, 0, width);

        synchronized (mQueue) {
        	if (mQueue.size() ==  MAX_BUFFER) {
        		mLastFrame = mQueue.poll();
        	}	
        	mQueue.add(bufferedImage);
        }
   
        repaint();
    }
 
    @Override
    public Dimension getPreferredSize() {
        if (mImage == null) {
             return new Dimension(960,720); // init window size
        } else {
           return new Dimension(mImage.getWidth(null), mImage.getHeight(null));
       }
    }
 
    public static void main(String[] args) {
 
        JFrame f = new JFrame("Monitor");
             
        f.addWindowListener(new WindowAdapter(){
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
 
        f.add(new ServerUIMain());
        f.pack();
        f.setVisible(true);
    }

	@Override
	public void onDirty(byte[] data, int width, int height) {
		// TODO Auto-generated method stub
		updateUI(data, width, height);
	}
}
