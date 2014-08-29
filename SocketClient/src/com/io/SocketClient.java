package com.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketClient extends Thread {
	private byte[] mData;
	private Socket mSocket;
	
	public void send(byte[] data) {
		mData = data;
		start();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		try {
			mSocket = new Socket("127.0.0.1", 2014);
			BufferedOutputStream outputStream = new BufferedOutputStream(mSocket.getOutputStream());
			BufferedInputStream inputStream = new BufferedInputStream(mSocket.getInputStream());
			byte[] buff = new byte[1024];
			int len = 0;
            String msg = null;
            outputStream.write(new String("who").getBytes());
            outputStream.flush();
            boolean isOver = false;
            
            while (!isOver && (len = inputStream.read(buff)) != -1) {
                msg = new String(buff, 0, len);

                System.out.println("client msg " + msg);
                
                if (msg.equals("who")) {
                    System.out.println(msg);
                    outputStream.write(new String("data").getBytes());
                    outputStream.flush();
                }
                else if (msg.equals("data")) {
                    System.out.println(msg);
                    isOver = true;
                    outputStream.write(mData);
                    outputStream.flush();
                    break;
                }
            }

			outputStream.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				mSocket.close();
				mSocket = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("data sent");
	}
	
	public void close() {
		if (mSocket != null) {
			try {
				mSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
