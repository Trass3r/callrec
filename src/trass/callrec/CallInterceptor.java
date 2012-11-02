package trass.callrec;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class CallInterceptor extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		// the phone number isn't included in the STATE_OFFHOOK intent
		// so we need all this shit here to get it

		if (intent.getAction() == Intent.ACTION_NEW_OUTGOING_CALL)
		{
			// TODO: the call might have been intercepted and number changed by another receiver
			// modified number: getResultData()
			// For consistency, any receiver whose purpose is to prohibit phone calls should have a priority of 0,
			// to ensure it will see the final phone number to be dialed.
			// Any receiver whose purpose is to rewrite phone numbers to be called should have a positive priority.
			// Negative priorities are reserved for the system for this broadcast; using them may cause problems.

			//
			String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			Toast.makeText(context, "outgoing call to #"+number, Toast.LENGTH_SHORT).show();

			// need to send the number to our service for later use
			Intent sint = new Intent(context, RecordingService.class);
			sint.putExtra(RecordingService.EXTRA_PHONE_NUMBER, number);
			context.startService(sint);
		}
		else if (intent.getAction() == TelephonyManager.ACTION_PHONE_STATE_CHANGED)
		{
			String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

			if (state.equals(TelephonyManager.EXTRA_STATE_RINGING))
			{
				String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
				Toast.makeText(context, "incoming call from #"+number, Toast.LENGTH_LONG).show();

				// need to send the number to our service for later use
				Intent sint = new Intent(context, RecordingService.class);
				sint.putExtra(RecordingService.EXTRA_PHONE_NUMBER, number);
				context.startService(sint);
			}
			else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
			{
				Toast.makeText(context, "abgenommen", Toast.LENGTH_SHORT).show();

				// now finally start recording with our previously sent number above
				Intent sint = new Intent(context, RecordingService.class);
				context.startService(sint);
			}
			else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE))
			{
				Toast.makeText(context, "scheinbar aufgelegt", Toast.LENGTH_LONG).show();
				Intent sint = new Intent(context, RecordingService.class);
				context.stopService(sint);
			}
		}
	}
}
