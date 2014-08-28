package com.io;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

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
		Socket socket = null;
		ByteArrayOutputStream byteArray = null;
		ArrayList<Byte> array = new  ArrayList<Byte>();
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
				byte[] buff = new byte[1024];
				int len = 0;
				while ((len = inputStream.read(buff)) != -1) {
					byteArray.write(buff, 0, len);
				}
				
				System.out.println("received file");

				if (mDataListener != null) {
//					byteArray.flush();
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
