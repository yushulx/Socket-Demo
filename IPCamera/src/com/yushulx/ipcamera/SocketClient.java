package com.yushulx.ipcamera;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.yushulx.ipcamera.CameraPreview.ImageBuffer;

public class SocketClient extends Thread {
	private Socket mSocket;
	private CameraPreview mCameraPreview;
	
	public SocketClient(CameraPreview preview) {
	    mCameraPreview = preview;
		start();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		try {
			mSocket = new Socket("192.168.100.109", 2014);
			BufferedOutputStream outputStream = new BufferedOutputStream(mSocket.getOutputStream());
			BufferedInputStream inputStream = new BufferedInputStream(mSocket.getInputStream());
			
			JsonObject jsonObj = new JsonObject();
            jsonObj.addProperty("type", "data");
            jsonObj.addProperty("length", mCameraPreview.getPreviewSize());
            
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
                        // send data
                        ImageBuffer buffer;
                        int i = 0;
                        while (true) {
//                            i = i % 4;
//                            buffer = mCameraPreview.getBuffer()[i];
//                            synchronized (buffer) {
//                                outputStream.write(buffer.buff);
//                                outputStream.flush();
//                            }
//                            
//                            ++i;
                            outputStream.write(mCameraPreview.getSingleBuffer());
                            outputStream.flush();
                            
                            if (Thread.currentThread().isInterrupted())
                                break;
                        }
                        
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
