package com.pedroedrasousa.perpixellightingdemo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.pedroedrasousa.engine.Renderer;
import com.pedroedrasousa.engine.Texture;
import com.pedroedrasousa.engine.Vec3;
import com.pedroedrasousa.engine.ShaderProgram;
import com.pedroedrasousa.object3d.Model;
import com.pedroedrasousa.perpixellightingdemo.R;


public class PerPixelLighting implements Renderer, OnTouchListener {

    private Context	mContext;
    private float	mDisplayDensity;
    
    // Matrices
	private float[] mProjectionMatrix	= new float[16];
	private float[] mVMatrix			= new float[16];
	private float[] mMVMatrix			= new float[16];
	private float[] mMVPMatrix			= new float[16];
	
	// Camera
	private Vec3	mCameraEye    		= new Vec3(0.0f, 3.0f, 7.0f);
	private Vec3	mCameraCenter 		= new Vec3(0.0f, 0.0f, 0.0f);
	private Vec3	mCameraUp     		= new Vec3(0.0f, 1.0f, 0.0f);
	
    // Shader programs
    private ShaderProgram mLightingShader;
    private ShaderProgram mPointShader;
	
	private Vec3		mLightPos		= new Vec3();
	private float		mLightPosTheta	= 0.0f;		// Will be incremented to move light around
    private FloatBuffer	mLightVertexData;			// Vertex data representing the light
    
	Model mModel = new Model();
	
    private int[] mViewport = new int[4];	// Viewport information (x0, y0, x1, y1)
    private float mScreenRatio;				// Width / Height
    
	private float mTouchPrevX;
	private float mTouchPrevY;
    
	public PerPixelLighting(Context context, float displayDensity) {
		mContext = context;
		mDisplayDensity = displayDensity;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		
		mLightingShader	= new ShaderProgram(mContext, R.raw.lighting_vert, R.raw.lighting_frag);
		mPointShader	= new ShaderProgram(mContext, R.raw.point_vert, R.raw.point_frag);

    	mModel.LoadFromObj(mContext, "teapot.obj");
     	mModel.setBaseMap(new Texture(mContext, "stone_d.png"));   	
    	mModel.setNormalMap(new Texture(mContext, "stone_n.png"));
    	
	    // Initialize the buffers that will represent the light position
	    float[] pointVertexData = {0.0f, 0.0f, 0.0f};
	    mLightVertexData = ByteBuffer.allocateDirect(pointVertexData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
	    mLightVertexData.put(pointVertexData).position(0);
	}
	
	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		mViewport[0] = 0;
	    mViewport[1] = 0;
	    mViewport[2] = width;
	    mViewport[3] = height;

		GLES20.glViewport(0, 0, width, height);
		
	    // Create the perspective projection matrix
	    // Width will vary as per aspect ratio
		mScreenRatio  = (float) width /  Math.max(mViewport[3], 1);
		
	    final float near	= 0.1f;
	    final float far		= 100.0f;
	    final float fov		= 45.0f;
	    final float top		= (float)Math.tan((float)(fov * (float)Math.PI / 360.0f)) * near;
	    final float bottom	= -top;
	    final float left	= mScreenRatio * bottom;
	    final float right	= mScreenRatio * top;

	    Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
	    
		Matrix.setLookAtM(	mVMatrix, 0, 	mCameraEye.x,    mCameraEye.y,    mCameraEye.z,
				  							mCameraCenter.x, mCameraCenter.y, mCameraCenter.z,
				  							mCameraUp.x,     mCameraUp.y,     mCameraUp.z);
	}

	@Override
	public void onDrawFrame(GL10 unused) {
		
	    // Update light position
	    mLightPosTheta += 0.02f;
	    mLightPos.assign((float)Math.sin(mLightPosTheta) * 2.5f, 1.5f + (float)Math.sin(mLightPosTheta * 0.5f) * 1.5f, 4.0f);

		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        
	    // Build Model View and Model View Projection Matrices
	    Matrix.multiplyMM(mMVMatrix, 0, mVMatrix, 0, mModel.getModelMatrix() , 0);
	    Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0);

	    // Enable the shader program
	    mLightingShader.useProgram();
	    
	    //Specify matrix information
        mLightingShader.uniformMatrix4fv("uVMatrix", 1, false, mVMatrix, 0);	    
        mLightingShader.uniformMatrix4fv("uMVMatrix", 1, false, mMVMatrix, 0);
	    mLightingShader.uniformMatrix4fv("uMVPMatrix", 1, false, mMVPMatrix, 0);
	    mLightingShader.uniformMatrix4fv("uNormalMatrix", 1, false, mMVMatrix, 0);
	    
	    // Bind the model vertex buffers
		mModel.bindVertexBuffers();
	    
		// Specify the location of vertex attributes
		mLightingShader.enableVertexAttribArray("aVertPos");
	    mLightingShader.vertexAttribPointer("aVertPos", 3, GLES20.GL_FLOAT, false, 14*4, 0);
	    mLightingShader.enableVertexAttribArray("aNormal");
	    mLightingShader.vertexAttribPointer("aNormal", 3, GLES20.GL_FLOAT, false, 14*4, 3*4);
	    mLightingShader.enableVertexAttribArray("aTangent");	    
	    mLightingShader.vertexAttribPointer("aTangent", 3, GLES20.GL_FLOAT, false, 14*4, 6*4);
	    mLightingShader.enableVertexAttribArray("aBinormal");
	    mLightingShader.vertexAttribPointer("aBinormal", 3, GLES20.GL_FLOAT, false, 14*4, 9*4);
	    mLightingShader.enableVertexAttribArray("aTexCoords");
	    mLightingShader.vertexAttribPointer("aTexCoords", 2, GLES20.GL_FLOAT, false, 14*4, 12*4);

	    // Specify the light position
	    mLightingShader.uniform3f("aLightPos", mLightPos.x, mLightPos.y, mLightPos.z);
	    
	    // Specify texture units
		mLightingShader.uniform1i("uBaseMap", 0);
	    mLightingShader.uniform1i("uNormalMap", 1);
		
	    // Render the model using previously specified data
	    mModel.render();
	    
	    // Model vertex buffers aren't needed anymore
	    mModel.unbindVertexBuffers();
	    
	    // Disable previously enabled vertex attributes
		mLightingShader.disableVertexAttribArray("aVertPos");
	    mLightingShader.disableVertexAttribArray("aNormal");
	    mLightingShader.disableVertexAttribArray("aTangent");	    
	    mLightingShader.disableVertexAttribArray("aBinormal");
	    mLightingShader.disableVertexAttribArray("aTexCoords");
	    
	    //
	    // Render a point representing the light
	    
	    // Build light model matrix
	    float[] lightModelMatrix = new float[16];
	    Matrix.setIdentityM(lightModelMatrix, 0);
	    Matrix.translateM(lightModelMatrix, 0, mLightPos.x, mLightPos.y, mLightPos.z);
	    
	    // Build Model View and Model View Projection Matrices
	    Matrix.multiplyMM(mMVMatrix, 0, mVMatrix, 0, lightModelMatrix , 0);
	    Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0);
	    
	    mLightVertexData.position(0);
	    mPointShader.useProgram();
	    GLES20.glVertexAttribPointer(mPointShader.getAttribLocation("aVertPos"), 3, GLES20.GL_FLOAT, false, 0, mLightVertexData);
	    mPointShader.enableVertexAttribArray("aVertPos");
	    mPointShader.uniformMatrix4fv("uMVPMatrix", 1, false, mMVPMatrix, 0);
	    GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
	    mPointShader.disableVertexAttribArray("aVertPos");
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		
		float x = event.getX();
		float y = event.getY();
		
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			float deltaX = (x - mTouchPrevX) / mDisplayDensity;
			float deltaY = (y - mTouchPrevY) / mDisplayDensity;
			
			// Set a matrix that contains the current rotation.
			final float[] currentRotationMatrix = new float[16];
			Matrix.setIdentityM(currentRotationMatrix, 0);
			Matrix.rotateM(currentRotationMatrix, 0, deltaX, 0.0f, 0.001f, 0.0f);
			Matrix.rotateM(currentRotationMatrix, 0, deltaY, 0.001f, 0.0f, 0.0f);
			
			Matrix.multiplyMM(mModel.getModelMatrix(), 0, currentRotationMatrix, 0, mModel.getModelMatrix(), 0);
		}

		mTouchPrevX = x;
		mTouchPrevY = y;
		
		return true;
	}

	@Override
	public void onResume() {
		;
	}

	@Override
	public void onPause() {
		;
	}

	@Override
	public int getViewportWidth() {
		return mViewport[2];
	}

	@Override
	public int getViewportHeight() {
		return mViewport[3];
	}

	@Override
	public void onSurfaceDestroyed() {
		;
	}

	@Override
	public void onScreenOnOffToggled(boolean isScreenOn) {
		;
	}
}
