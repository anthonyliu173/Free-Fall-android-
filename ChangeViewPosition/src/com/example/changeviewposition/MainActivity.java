package com.example.changeviewposition;

import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;

public class MainActivity extends Activity implements SensorEventListener{

	//parameters for falling ball
	private WindowManager windowManager;
	private ImageView img1;
	WindowManager.LayoutParams params;

	//Position of falling ball refreshes every 50ms
	Timer timer;
	TimerTask timerTask;
	private int TimeInterval = 50;

	// handler in charge of TimerTask
	final Handler handler = new Handler();
	
	
	//Accelerometer
	private float mLastX, mLastY;
	private float Vx, Vy;
	private boolean mInitialized;
	private SensorManager mSensorManager;
    private Sensor mAccelerometer;

	//Bounce factor
	private float bounce = (float) 0.8;
	
	//Gravity factor
	private int AccFactor = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//set initial speed of falling ball to 0
		Vx =(float) 0.0;
		Vy =(float) 0.0;
		
		//get the size of display screen
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		//set up image of the falling object
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		img1 = new ImageView(this);
		img1.setImageResource(R.drawable.freefallball);
		int height = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 60, getResources()
						.getDisplayMetrics());
		params = new WindowManager.LayoutParams(
				height, height, WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		//set up initial position of the falling ball
		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.x = (size.x)/2;
		params.y = (size.y)/2;

		windowManager.addView(img1, params);
		
		//register accelerometer
		mInitialized = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	public void autoMove(){
		
		//update speed of the falling ball based on measured Ax and Ay
		Vx = Vx-mLastX*TimeInterval/1000;
		Vy = Vy+mLastY*TimeInterval/1000;
		
		//update the position of the falling ball based on Vx and Vy
		WindowManager.LayoutParams paramsF = params;
		paramsF.x =  paramsF.x + 
				(int) (AccFactor*Vx);
		paramsF.y = paramsF.y +  (int) (AccFactor*Vy);
		
		
		if(paramsF.x > (880) ){
			paramsF.x = (880);
			BounceX();
		}
		if(paramsF.x < 80 ){
			paramsF.x = 80;
			BounceX();
		}
		if(paramsF.y > 1400 ){
			paramsF.y = 1400;
			BounceY();
		}
		if(paramsF.y < 80 ){
			paramsF.y = 80;
			BounceY();
		}
		windowManager.updateViewLayout(img1, paramsF);
	}
	
	public void BounceX(){
		Vx = (float) (-bounce*Vx);
	}
	
	public void BounceY(){
		Vy = (float) (-bounce*Vy);
	}


	@Override
	protected void onResume() {
		super.onResume();
		// onResume we start our timer so it can start when the app comes from
		// the background
		startTimer();
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}

	public void startTimer() {
		// set a new Timer
		timer = new Timer();
		// initialize the TimerTask's job
		initializeTimerTask();
		// schedule the timer, after the first 5000ms the TimerTask will run
		// every 50ms
		timer.schedule(timerTask, 500, TimeInterval); //
	}
	
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

	public void initializeTimerTask() {
		timerTask = new TimerTask() {
			public void run() {
				// use a handler to run a toast that shows the current timestamp
				handler.post(new Runnable() {
					public void run() {
						autoMove();
					}
				});
			}
		};
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		float x = event.values[0];
		float y = event.values[1];
		if (!mInitialized) {
			mLastX = x;
			mLastY = y;
			mInitialized = true;
		} else {
//			float deltaX = Math.abs(mLastX - x);
//			float deltaY = Math.abs(mLastY - y);
//			float deltaZ = Math.abs(mLastZ - z);
//			if (deltaX < NOISE) deltaX = (float)0.0;
//			if (deltaY < NOISE) deltaY = (float)0.0;
//			if (deltaZ < NOISE) deltaZ = (float)0.0;
			mLastX = x;
			mLastY = y;
		}
	}

}
