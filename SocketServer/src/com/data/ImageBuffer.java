package com.data;

import java.io.ByteArrayOutputStream;

public class ImageBuffer {

    public boolean isFull = false;
    private int mTotalLength = 0;
    private final int mFrameLength;
    private DataListener mListener;
    private ByteArrayOutputStream mByteArrayOutputStream;
    
    public ImageBuffer(int frameLength) {
        mByteArrayOutputStream = new ByteArrayOutputStream();
        mFrameLength = frameLength;
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
            mListener.onDirty(mByteArrayOutputStream.toByteArray());
            mByteArrayOutputStream.reset();
            System.out.println("received file");
            mTotalLength = 0;
        }
        
        return 0;
    }
}
