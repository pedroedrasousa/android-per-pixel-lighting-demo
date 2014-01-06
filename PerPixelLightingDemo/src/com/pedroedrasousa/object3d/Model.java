package com.pedroedrasousa.object3d;

import java.io.IOException;
import java.io.InputStream;

import com.pedroedrasousa.engine.Texture;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

public class Model {
	
	private Mesh	mMesh;
	private Texture	mBaseMap		= new Texture();
	private Texture	mNormalMap		= new Texture();
	private float[]	mModelMatrix	= new float[16];
	
	public Model() {
		Matrix.setIdentityM(mModelMatrix, 0);
	}
	
	public void LoadFromObj(Context context, String assetName) {
		
		ObjLoader objLoader = new ObjLoader();
		
    	InputStream inputStream;
		try {
			inputStream = context.getAssets().open(assetName);
			mMesh = objLoader.load(context, inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setBaseMap(Texture texture) {
		mBaseMap = texture;
	}
	
	public void setNormalMap(Texture texture) {
		mNormalMap = texture;
	}
	
	public void bindVertexBuffers() {
		mMesh.bindVertexBuffers();
	}
	
	public void unbindVertexBuffers() {
		mMesh.unbindVertexBuffers();
	}
	
	public void render() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mBaseMap.getHandle());
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mNormalMap.getHandle());
		mMesh.render();
	}
	
	public final float[] getModelMatrix() {
		return mModelMatrix;
	}
}
