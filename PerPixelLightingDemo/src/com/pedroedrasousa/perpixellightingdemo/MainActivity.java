package com.pedroedrasousa.perpixellightingdemo;

import com.pedroedrasousa.engine.EngineGLSurfaceView;
import com.pedroedrasousa.engine.Renderer;
import com.pedroedrasousa.perpixellightingdemo.R;

import android.util.DisplayMetrics;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;

public class MainActivity extends Activity {

	private EngineGLSurfaceView	mGLSurfaceView;
	private PerPixelLighting	mEngineRenderer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mGLSurfaceView = (EngineGLSurfaceView)findViewById(R.id.gl_surface_view);
	    
	    // Check if the system supports OpenGL ES 2.0
	    final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
	    final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
	 
	    if (supportsEs2) {
			final DisplayMetrics displayMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			
			mEngineRenderer = new PerPixelLighting(this, displayMetrics.density);
			
	        // Request an OpenGL ES 2.0 compatible context and set the renderer
	        mGLSurfaceView.setEGLContextClientVersion(2);
	        mGLSurfaceView.setRenderer((Renderer)mEngineRenderer);
	        mGLSurfaceView.setTouchEventHandler(mEngineRenderer);
	    }
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}
	  
	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	protected void onResume() {

		if (mGLSurfaceView != null) {
			mGLSurfaceView.onResume();
		}
		
		if (mEngineRenderer!= null) {
			mEngineRenderer.onResume();
		}

		super.onResume();
	}

	@Override
	protected void onPause() {
		
		if (mGLSurfaceView != null) {
			mGLSurfaceView.onPause();
		}

		if (mEngineRenderer!= null) {
			mEngineRenderer.onPause();
		}
		
		super.onPause();
	}
	
	@Override
	public void onBackPressed() {
	    finish();
	}
}