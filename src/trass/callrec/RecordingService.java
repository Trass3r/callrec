package trass.callrec;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class RecordingService extends Service
{
	static final String			EXTRA_PHONE_NUMBER	= "trass.callrec.recNumber";
	private MediaRecorder		_recorder;
	private String				_number;
	private static final String	SAVE_DIR = Environment.getExternalStorageDirectory()
	                                       .getAbsolutePath() + "/callrec/";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy()
	{
		Log.i("CallRec", "onDestroy:");
		Toast.makeText(this, "stopping call recording", Toast.LENGTH_SHORT).show();

		// in case of a missed or blocked incoming call start hasn't been called yet
		// so ignore illegal state exception in that case
		try
		{
			_recorder.stop();
		}
		catch (Exception e) {}

		_recorder.release();
		_recorder = null;

		super.onDestroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.i("CallRec", "onStartCommand:");
		// Log.w("CallRec", intent.getExtras().keySet().toString());
		Log.i("CallRec", String.valueOf(flags));
		Log.i("CallRec", String.valueOf(startId));

		final String number = intent.getStringExtra(EXTRA_PHONE_NUMBER);
		if (number != null)
		{
			// save the number for later
			_number = number;
			Log.w("CallRec", "number is " + number);
/*
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
			int src = sharedPref.getInt("pref_audio_src", MediaRecorder.AudioSource.VOICE_CALL);
			Log.w("CallRec", "src is now " + String.valueOf(src));
*/
			// and already prepare the media recorder
			_recorder = new MediaRecorder();
			_recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
//			_recorder.setAudioEncodingBitRate(bitRate);
			_recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			_recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

			// now just return, make sure this is same value as below
			return START_STICKY;
		}

		// number was null, so there should be a phone call now

		// include time and number in the filename
		String time = (new SimpleDateFormat("yyyyMMdd-HHmmssZ"))
		              .format(new Date());
		_recorder.setOutputFile(SAVE_DIR + time + "#" + _number + ".mp4");

		try
		{
			_recorder.prepare();
			_recorder.start();
			Toast.makeText(this, "started call recording", Toast.LENGTH_SHORT).show();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Toast.makeText(this, "failed to start call recording", Toast.LENGTH_LONG).show();
		}

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null; // = don't bind
	}
}