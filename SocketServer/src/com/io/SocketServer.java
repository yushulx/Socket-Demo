package com.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer extends Thread {
	private ServerSocket mServer;
	private DataListener mDataListener;

	public SocketServer() {
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();

		System.out.println("server's waiting");
		BufferedInputStream inputStream = null;
		BufferedOutputStream outputStream = null;
		Socket socket = null;
		ByteArrayOutputStream byteArray = null;
		try {
			mServer = new ServerSocket(2014);
			while (!Thread.currentThread().isInterrupted()) {
				if (byteArray != null)
					byteArray.reset();
				else
					byteArray = new ByteArrayOutputStream();

				socket = mServer.accept();
				System.out.println("new socket");
				
				inputStream = new BufferedInputStream(socket.getInputStream());
				outputStream = new BufferedOutputStream(socket.getOutputStream());
				
				byte[] buff = new byte[1024];
				int len = 0;
				String msg = null;
				while ((len = inputStream.read(buff)) != -1) {
					
					msg = new String(buff, 0, len);
					if (msg.equals("who")) {
					    System.out.println("who's sending msg?");
					    outputStream.write(new String("who").getBytes());
			            outputStream.flush();
					}
					else if (msg.equals("data")) {
					    System.out.println("receiving data...");
					    outputStream.write(new String("data").getBytes());
	                    outputStream.flush();
					}
					else {
					    byteArray.write(buff, 0, len);
					}
				}
				
				System.out.println("received file");

				if (mDataListener != null) {
					mDataListener.onDirty(byteArray.toByteArray());
				}

				socket.close();
				socket = null;
				
				inputStream.close();
				inputStream = null;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}

				if (socket != null) {
					socket.close();
				}
				
				if (byteArray != null) {
					byteArray.close();
				}
				
			} catch (IOException e) {

			}

		}

	}

	public void setOnDataListener(DataListener listener) {
		mDataListener = listener;
	}
}
