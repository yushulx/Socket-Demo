package com.yushulx.ipcamera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private static final String TAG = "camera";
    private Size mPreviewSize;
    private byte[] mCallbackBuffer;
    private byte[] mImageData;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        mPreviewSize = mCamera.getParameters().getPreviewSize();
        int format = mCamera.getParameters().getPreviewFormat();
        mCallbackBuffer = new byte[mPreviewSize.width * mPreviewSize.height * ImageFormat.getBitsPerPixel(format) / 8];
        mCamera.setPreviewCallbackWithBuffer(mPreviewCallback);
        mCamera.addCallbackBuffer(mCallbackBuffer);
        
        initBuff();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try {
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.stopPreview();
            resetBuff();
            
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewCallback(mPreviewCallback);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
    
    public void setCamera(Camera camera) {
    	mCamera = camera;
    }
    
    private ImageBuffer[] mBuffer = new ImageBuffer[4];
    private byte[] mSingleBuffer;
    
    public synchronized byte[] getSingleBuffer() {
        byte[] buffer = new byte[mCallbackBuffer.length];
        System.arraycopy(mSingleBuffer, 0, buffer, 0, mCallbackBuffer.length);
        return buffer;
    }
    
    public ImageBuffer[] getBuffer() {
        return mBuffer;
    }
    
    private int count = 0;
    private void initBuff() {
        for (int i = 0; i < mBuffer.length; ++i) {
            mBuffer[i] = new ImageBuffer(mCallbackBuffer.length);
        }
        mSingleBuffer = new byte[mCallbackBuffer.length];
    }
    
    private void resetBuff() {
        count = 0;
        for (int i = 0; i < mBuffer.length; ++i) {
            mBuffer[i].isAvailable = false;;
        }
    }
    
    public int getPreviewSize() {
        return mCallbackBuffer.length;
    }
    
    public class ImageBuffer {
        public byte[] buff;
        public boolean isAvailable;
        
        public ImageBuffer(int len) {
            buff = new byte[len];
            isAvailable = false;
        }
    }
    
    private Camera.PreviewCallback mPreviewCallback = new PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
//            if (mImageData == null) {
//                mImageData = data;
//                saveRAW(data);
//                saveYUV(data);
//            }
//            else {
//                mImageData = data;
//            }
            
//            count = count % 4;
//            synchronized (mBuffer[count]) {
//                System.arraycopy(data, 0, mBuffer[count].buff, 0, data.length);
//                mBuffer[count].isAvailable = true;
//            }
            
            synchronized (CameraPreview.this) {
                System.arraycopy(data, 0, mSingleBuffer, 0, data.length);
            }
            
            mCamera.addCallbackBuffer(mCallbackBuffer);
        }
    };
    
    private void saveYUV(byte[] byteArray) {

        YuvImage im = new YuvImage(byteArray, ImageFormat.NV21, mPreviewSize.width, mPreviewSize.height, null);
        Rect r = new Rect(0, 0, mPreviewSize.width, mPreviewSize.height);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        im.compressToJpeg(r, 100, baos);

        try {
            FileOutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/yuv.jpg");
            output.write(baos.toByteArray());
            output.flush();
            output.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }
    
    private void saveRAW(byte[] byteArray) {
        try {
            FileOutputStream file = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/test.yuv"));
            try {
                file.write(mImageData);
                file.flush();
                file.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
