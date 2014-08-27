package com.io;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer extends Thread {
	private ServerSocket mServer;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		System.out.println("server's waiting");
		try {
			mServer = new ServerSocket(2014);
			Socket socket = mServer.accept();
			BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
			byte[] buff = new byte[1024];
			FileOutputStream fileStream = new FileOutputStream(new File("test.png"));
			while (input.read(buff) != -1) {
				fileStream.write(buff);
			}
			
			fileStream.flush();
			fileStream.close();
			input.close();
			System.out.println("received file");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
