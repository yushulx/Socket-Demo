package com.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.data.BufferManager;
import com.data.DataListener;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class SocketServer extends Thread {
	private ServerSocket mServer;
	private DataListener mDataListener;
	private BufferManager mBufferManager;

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
				
				byte[] buff = new byte[256];
				byte[] imageBuff = null;
				int len = 0;
				String msg = null;
				// read msg
				while ((len = inputStream.read(buff)) != -1) {
					
					msg = new String(buff, 0, len);
					System.out.println(msg);
					// JSON analysis
	                JsonParser parser = new JsonParser();
	                boolean isJSON = true;
	                JsonElement element = null;
	                try {
	                    element =  parser.parse(msg);
	                }
	                catch (JsonParseException e) {
	                    System.out.println("exception: " + e);
	                    isJSON = false;
	                }
	                if (isJSON && element != null) {
	                    JsonObject obj = element.getAsJsonObject();
	                    element = obj.get("type");
	                    if (element != null && element.getAsString().equals("data")) {
	                        element = obj.get("length");
	                        if (element != null) {
	                            int length = element.getAsInt();
	                            imageBuff = new byte[length];
	                            mBufferManager = new BufferManager(length);
	                            mBufferManager.setOnDataListener(mDataListener);
	                            break;
	                        }
	                        
	                    }
	                }
	                else {
	                    byteArray.write(buff, 0, len);
	                    break;
	                }
				}
				
				if (imageBuff != null) {
				    JsonObject jsonObj = new JsonObject();
		            jsonObj.addProperty("state", "ok");
		            outputStream.write(jsonObj.toString().getBytes());
		            outputStream.flush();
		            // read image data
		            int sum = 0;
		            int buffLen = imageBuff.length;
				    while ((len = inputStream.read(imageBuff)) != -1) {
				        System.out.println("len = " + len);
//	                    byteArray.write(imageBuff, 0, len);
//	                    sum += len;
//	                    
//	                    if (mDataListener != null && sum == buffLen) {
//	                        sum = 0;
//	                        mDataListener.onDirty(byteArray.toByteArray());
//	                        System.out.println("received file");
//	                        byteArray.reset();
//	                    }
	                    
	                 // buffer manager
	                    mBufferManager.fillBuffer(imageBuff, len);
	                }
				}
				
				inputStream.close();
				inputStream = null;
				
				outputStream.close();
				
				socket.close();
                socket = null;
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
