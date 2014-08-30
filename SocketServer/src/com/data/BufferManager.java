package com.data;

import java.util.ArrayList;

public class BufferManager {
    private ImageBuffer[] mBufferQueue;
    private int mFillCount = 0;
    private final int mFrameLength;
    private int mShiftCount = 0;
    private boolean mIsFirstFrame = true;
    
    public BufferManager(int frameLength) {
        // TODO Auto-generated constructor stub
        mFrameLength = frameLength;
        mBufferQueue = new ImageBuffer[4];
        for (int i = 0; i < 4; ++i) {
            mBufferQueue[i] = new ImageBuffer(mFrameLength);
        }
    }
    
    public void fillBuffer(byte[] data, int len) {
        mFillCount = mFillCount % 4;
        
//        if (mIsFirstFrame) {
//            mIsFirstFrame = false;
//            mBufferQueue[mFillCount].fillBuffer(data, len);
//        }
        
//        if (len < mFrameLength) {
//            mShiftCount = mFrameLength - len;
//        }
//        else {
            if (mShiftCount != 0) {
                if (mShiftCount < len) {
                    mBufferQueue[mFillCount].fillBuffer(data, mShiftCount);
                    ++mFillCount;
                    mBufferQueue[mFillCount].fillBuffer(data, len - mShiftCount);
                    mShiftCount = mFrameLength - len + mShiftCount;
                }
                else if (mShiftCount == len) {
                    mBufferQueue[mFillCount].fillBuffer(data, mShiftCount);
                    mShiftCount = 0;
                    ++mFillCount;
                }
                else {
                    mBufferQueue[mFillCount].fillBuffer(data, len);
                    mShiftCount = mShiftCount - len;
                }
            }
            else { // best case
                mBufferQueue[mFillCount].fillBuffer(data, len);
                
                if (len < mFrameLength) {
                    mShiftCount = mFrameLength - len;
                }
                else {
                    ++mFillCount;
                }
            }
//        }
    }
    
    public void setOnDataListener(DataListener listener) {
        for (ImageBuffer buffer : mBufferQueue) {
            buffer.setOnDataListener(listener);
        }
    }
}
