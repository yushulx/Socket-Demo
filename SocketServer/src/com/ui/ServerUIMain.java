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
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.data.DataListener;
import com.io.SocketServer;
 
public class ServerUIMain extends JPanel implements DataListener{
           
    BufferedImage img;
 
    @Override
    public void paint(Graphics g) {
        Random r = new Random();
        if (img != null) {
            synchronized (img) {
                g.drawImage(img, r.nextInt(50), r.nextInt(50), null);
            }
        }
    }
 
    public ServerUIMain() {
        try {
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream("test.yuv"));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[960 * 720];
            System.out.println("buffer = " + (960 * 720));
            int len;
            int sum = 0;
            try {
                while ((len = inputStream.read(buffer)) != -1) {
                    System.out.println(len);
                    outputStream.write(buffer, 0, len);
                    sum += len;
                }
                System.out.println("sum = " + sum);
                int[] rgbArray = Utils.convertYUVtoRGB(outputStream.toByteArray(), 960, 720);
                img = new BufferedImage(960, 720, BufferedImage.TYPE_4BYTE_ABGR);
                img.setRGB(0, 0, 960, 720, rgbArray, 0, 960);
                
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
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
 
    @Override
    public Dimension getPreferredSize() {
        if (img == null) {
             return new Dimension(960,720);
        } else {
           return new Dimension(img.getWidth(null), img.getHeight(null));
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
	public void onDirty(byte[] data) {
		// TODO Auto-generated method stub
		updateUI(data);
	}
}
