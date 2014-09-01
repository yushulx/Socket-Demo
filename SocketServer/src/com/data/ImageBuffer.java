package com.data;

import java.io.ByteArrayOutputStream;

public class ImageBuffer {

    public boolean isFull = false;
    private int mTotalLength = 0;
    private final int mFrameLength;
    private DataListener mListener;
    private ByteArrayOutputStream mByteArrayOutputStream;
    private int mWidth, mHeight;
    
    public ImageBuffer(int frameLength, int width, int height) {
        mByteArrayOutputStream = new ByteArrayOutputStream();
        mFrameLength = frameLength;
        mWidth = width;
        mHeight = height;
    }
    
    public int isFull() {
        return mFrameLength - mTotalLength;
    }
    
    public void setOnDataListener(DataListener listener) {
        mListener = listener;
    }
    
    public int fillBuffer(byte[] data, int len) {
        mTotalLength += len;
        mByteArrayOutputStream.write(data, 0, len);
        
        if (mListener != null && mTotalLength == mFrameLength) {
        	long t = System.currentTimeMillis();
            mListener.onDirty(mByteArrayOutputStream.toByteArray(), mWidth, mHeight);
            mByteArrayOutputStream.reset();
            System.out.println("received file");
            mTotalLength = 0;
            System.out.println("time cost = " + (System.currentTimeMillis() - t));
        }
        
        return 0;
    }
}
