package com.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

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
			
			JsonObject jsonObj = new JsonObject();
            jsonObj.addProperty("type", "data");
            jsonObj.addProperty("length", mData.length);
            
			byte[] buff = new byte[256];
			int len = 0;
            String msg = null;
            outputStream.write(jsonObj.toString().getBytes());
            outputStream.flush();
                        
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
                    element = obj.get("state");
                    if (element != null && element.getAsString().equals("ok")) {
                        outputStream.write(mData);
                        outputStream.write(mData);
                        outputStream.write(mData);
                        outputStream.write(mData);
                        outputStream.write(mData);
                        outputStream.write(mData);
                        outputStream.write(mData);
                        outputStream.write(mData);
                        outputStream.flush();
                        break;
                    }
                }
                else {
                    break;
                }
            }

			outputStream.close();
			inputStream.close();
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
