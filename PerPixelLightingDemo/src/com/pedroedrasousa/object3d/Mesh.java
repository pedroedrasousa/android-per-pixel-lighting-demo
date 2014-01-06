package com.pedroedrasousa.object3d;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;

public class Mesh {
	
	private static final int BYTES_PER_FLOAT = 4;
	private static final int BYTES_PER_SHORT = 2;

	private FloatBuffer	mVertexBuffer;
	private ShortBuffer	mIndexBuffer;
	
	private int mVBO[] = new int[2];
	
	public void setVertexData(float[] data, short[] indices) {
		
		ByteBuffer  byteBuf;
		
		// Generate two buffer objects, one for the vertex data, other for the indices.
		GLES20.glGenBuffers(2, mVBO, 0);
		
		// Vertex data buffer
		byteBuf = ByteBuffer.allocateDirect(data.length * BYTES_PER_FLOAT);
		byteBuf.order(ByteOrder.nativeOrder());
		mVertexBuffer = byteBuf.asFloatBuffer();
		mVertexBuffer.put(data);
		mVertexBuffer.position(0);
		
		// Create a new data store for the vertex data buffer.
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVBO[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mVertexBuffer.capacity() * BYTES_PER_FLOAT, mVertexBuffer, GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		
		// Index buffer
		byteBuf = ByteBuffer.allocateDirect(indices.length * BYTES_PER_SHORT);
		byteBuf.order(ByteOrder.nativeOrder());
		mIndexBuffer = byteBuf.asShortBuffer();
		mIndexBuffer.put(indices);
		mIndexBuffer.position(0);
		
		// Create a new data store for the index buffer.
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mVBO[1]);
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, mIndexBuffer.capacity() * BYTES_PER_SHORT, mIndexBuffer, GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	public void bindVertexBuffers() {
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVBO[0]);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mVBO[1]);
	}
	
	public void unbindVertexBuffers() {
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	public void render() {
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndexBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, 0);
	}
}
