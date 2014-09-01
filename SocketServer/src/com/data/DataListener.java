package com.data;

public interface DataListener {
	public void onDirty(byte[] data, int width, int height);
}
