package trass.callrec;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener
{
	private MediaRecorder	_recorder;
	private final String	_fileName	= Environment.getExternalStorageDirectory()
												.getAbsolutePath() + "/test.mp4";
	private MediaPlayer		_player;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		((Button)findViewById(R.id.HandleRecordingBtn)).setOnClickListener(this);
		((Button)findViewById(R.id.HandlePlayBtn)).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void startRecording()
	{
		_recorder = new MediaRecorder();
		_recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		_recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		_recorder.setOutputFile(_fileName);
		_recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

		try
		{
			_recorder.prepare();
		}
		catch (Exception e)
		{
			Log.e("CallRec", "prepare() failed");
		}

		try
		{
			_recorder.start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void stopRecording()
	{
		_recorder.stop();
		_recorder.release();
		_recorder = null;
	}

	private void startPlaying()
	{
		_player = new MediaPlayer();
		try
		{
			_player.setDataSource(_fileName);
			_player.prepare();
			_player.start();
		}
		catch (Exception e)
		{
			Log.e("CallRec", "prepare() failed");
		}
	}

	private void stopPlaying()
	{
		_player.release();
		_player = null;
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.HandleRecordingBtn)
		{
			Button b = (Button) v;
			if (_recorder != null)
			{
				stopRecording();
				b.setText("Start Recording");
			}
			else
			{
				startRecording();
				b.setText("Stop Recording");
			}
		}
		else if (v.getId() == R.id.HandlePlayBtn)
		{
			Button b = (Button) v;
			if (_player != null)
			{
				stopPlaying();
				b.setText("Start Playing");
			}
			else
			{
				startPlaying();
				b.setText("Stop Playing");
			}
		}
	}

	@Override
	public void onPause()
	{
		super.onPause();
		if (_recorder != null)
			stopRecording();

		if (_player != null)
			stopPlaying();
	}
}