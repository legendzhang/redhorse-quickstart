package com.redhorse.quickstart;

import com.redhorse.quickstart.AppGrid;
import com.redhorse.quickstart.R;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class quickstart extends Activity {

	private static final int STARTGRID_REQUEST = 0;
	private static final int STARTCONFIG_REQUEST = 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.main);
		Intent it = new Intent();
		it.setClass(this, AppGrid.class);
		// startActivity(it);
		startActivityForResult(it, STARTGRID_REQUEST);
		notification(this, "小红马快速启动：随时启动你的最爱!");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Intent it = new Intent();
		switch (requestCode) {
		case STARTGRID_REQUEST:
			switch (resultCode) {
			case RESULT_OK:
				Bundle b = data.getExtras();
				String msg = b.getString("msg");
				if (msg.equalsIgnoreCase("quit")) {
					finish();
				} else if (msg.equalsIgnoreCase("hide")) {

				} else if (msg.equalsIgnoreCase("config")) {
					it = new Intent();
					it.setClass(this, AppConfig.class);
					startActivityForResult(it, STARTCONFIG_REQUEST);
				}
				break;
			default:
				finish();
				break;
			}
			break;
		case STARTCONFIG_REQUEST:
			it = new Intent();
			it.setClass(this, AppGrid.class);
			startActivityForResult(it, STARTGRID_REQUEST);
			break;
		default:
			finish();
			break;
		}
		Log.e("quickstart", "back");
	}

	private NotificationManager mNM;

	private void notification(Context ctx, String msginfo) {
		try {
			mNM = (NotificationManager) ctx
					.getSystemService(Context.NOTIFICATION_SERVICE);
			Intent intent = new Intent(ctx, quickstart.class);
			CharSequence appName = ctx.getString(R.string.app_name);
			Notification notification = new Notification(R.drawable.icon_noborder,
					appName, System.currentTimeMillis());
			notification.flags = Notification.FLAG_NO_CLEAR;
			CharSequence appDescription = msginfo;
			notification.setLatestEventInfo(ctx, appName, appDescription,
					PendingIntent.getActivity(ctx, 0, intent,
							PendingIntent.FLAG_CANCEL_CURRENT));
			mNM.notify(0, notification);
		} catch (Exception e) {
			mNM = null;
		}
	}
}