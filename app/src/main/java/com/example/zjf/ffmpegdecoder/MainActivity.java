package com.example.zjf.ffmpegdecoder;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback{
	private static final String TAG = "MainActivity";
	private Button button,btnPlay;
	private EditText editText1,editText2,editText3;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private DecodeAsyncTask myTask;
	String folderurl;
	String file_path;
	static {
		System.loadLibrary("native-lib");
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		folderurl = Environment.getExternalStorageDirectory().getPath();
		button = (Button) findViewById(R.id.button);
		btnPlay = (Button) findViewById(R.id.play);
		editText1 = (EditText) findViewById(R.id.editText1);
		editText2 = (EditText) findViewById(R.id.editText2);
		editText3 =  (EditText)findViewById(R.id.editText3);
		surfaceView = (SurfaceView)findViewById(R.id.sfvSurface);
		surfaceHolder = surfaceView.getHolder();

		surfaceHolder.addCallback(this);

		file_path = folderurl+"/"+editText3.getText().toString();
		myTask = new DecodeAsyncTask();
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//startDecode();
				final String inputurl = folderurl+"/"+editText1.getText().toString();
				final String outputurl = folderurl+"/"+editText2.getText().toString();
				Log.d(TAG,"" + inputurl + "         " + outputurl);
				myTask.execute(inputurl,outputurl);
			}
		});

		btnPlay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				surfaceView = (SurfaceView)findViewById(R.id.sfvSurface);
				surfaceHolder = surfaceView.getHolder();

				surfaceHolder.addCallback(MainActivity.this);
				file_path = folderurl+"/"+editText3.getText().toString();
			}
		});
	}


	@Override
	protected void onPause() {
		super.onPause();
		//如果异步任务不为空 并且状态是 运行时  ，就把他取消这个加载任务
		if(myTask !=null && myTask.getStatus() == AsyncTask.Status.RUNNING){
			myTask.cancel(true);
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				FFmpeg.play(file_path,surfaceHolder.getSurface());
			}
		}).start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (holder != null) {
			holder = null;
		}
	}


	private class DecodeAsyncTask extends AsyncTask<String,Void,String>{

		@Override
		protected String doInBackground(String... strings) {
			FFmpeg.decode(strings[0],strings[1]);
			return null;
		}
	}
}
