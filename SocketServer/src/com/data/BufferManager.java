package com.data;

public class BufferManager {
    private ImageBuffer[] mBufferQueue;
    private int mFillCount = 0;
    private final int mFrameLength;
    private int mRemained = 0;
    private static final int MAX_BUFFER_COUNT = 2;
    
    public BufferManager(int frameLength, int width, int height) {
        // TODO Auto-generated constructor stub
        mFrameLength = frameLength;
        mBufferQueue = new ImageBuffer[MAX_BUFFER_COUNT];
        for (int i = 0; i < MAX_BUFFER_COUNT; ++i) {
            mBufferQueue[i] = new ImageBuffer(mFrameLength, width, height);
        }
    }
    
	public void fillBuffer(byte[] data, int len) {
		mFillCount = mFillCount % MAX_BUFFER_COUNT;
		if (mRemained != 0) {
			if (mRemained < len) {
				mBufferQueue[mFillCount].fillBuffer(data, 0, mRemained);
				++mFillCount;
				if (mFillCount == MAX_BUFFER_COUNT)
					mFillCount = 0;
				mBufferQueue[mFillCount].fillBuffer(data, mRemained, len - mRemained);
				mRemained = mFrameLength - len + mRemained;
			} else if (mRemained == len) {
				mBufferQueue[mFillCount].fillBuffer(data, 0, mRemained);
				mRemained = 0;
				++mFillCount;
				if (mFillCount == MAX_BUFFER_COUNT)
                    mFillCount = 0;
			} else {
				mBufferQueue[mFillCount].fillBuffer(data, 0, len);
				mRemained = mRemained - len;
			}
		} else {
			mBufferQueue[mFillCount].fillBuffer(data, 0, len);

			if (len < mFrameLength) {
				mRemained = mFrameLength - len;
			} else {
				++mFillCount;
				if (mFillCount == MAX_BUFFER_COUNT)
				    mFillCount = 0;
			}
		}
	}
    
    public void setOnDataListener(DataListener listener) {
        for (ImageBuffer buffer : mBufferQueue) {
            buffer.setOnDataListener(listener);
        }
    }
}
