package com.redhorse.quickstart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.redhorse.quickstart.quickstart;
import com.redhorse.quickstart.R;
import com.redhorse.quickstart.quickstart.AppsAdapter;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class quickstart extends Activity implements OnItemClickListener {

	private GridView mGrid;
	private dbStartConfigAdapter dbStart = null;
	private List<ResolveInfo> mApps;
	private List<ResolveInfo> mAllApps;
	private static final int STARTALL_REQUEST = 0;
	private static final int STARTCONFIG_REQUEST = 1;
	private static final int STARTWEIBO_REQUEST = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		notification(this, "小红马快速启动：随时启动你的最爱!");
        Intent intent = new Intent();
        intent.setClass(this, ServiceRed.class);
        startService(intent);
        
		dbStart = new dbStartConfigAdapter(this);
        dbStart.open();

        loadApps();

		setContentView(R.layout.appgrid);
		mGrid = (GridView) findViewById(R.id.myGrid);
		mGrid.setAdapter(new AppsAdapter());
		mGrid.setOnItemClickListener(this);
		Button button = (Button) findViewById(R.id.Button01);
		button.setOnClickListener(Button01Listener);
		button = (Button) findViewById(R.id.Button02);
		button.setOnClickListener(Button02Listener);
		button = (Button) findViewById(R.id.Button03);
		button.setOnClickListener(Button03Listener);
		button = (Button) findViewById(R.id.weibogrid);
		button.setOnClickListener(weibogridListener);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Intent it = new Intent();
		switch (requestCode) {
		case STARTCONFIG_REQUEST:
			switch (resultCode) {
			case RESULT_OK:
				Bundle b = data.getExtras();
				String msg = b.getString("msg");
				if (msg.equalsIgnoreCase("save")) {
			        loadApps();
					mGrid.setAdapter(new AppsAdapter());
				} else if (msg.equalsIgnoreCase("config")) {
				}
				break;
			default:
				finish();
				break;
			}
			break;
		case STARTALL_REQUEST:
			switch (resultCode) {
			case RESULT_OK:
				Bundle b = data.getExtras();
				String msg = b.getString("msg");
				if (msg.equalsIgnoreCase("back")) {
				} else if (msg.equalsIgnoreCase("open")) {
					String packageName = b.getString("packageName");
					String name = b.getString("name");
					Iterator it1 = mAllApps.iterator();
					ResolveInfo info = null;
					while (it1.hasNext()) {
						info = (ResolveInfo) it1.next();
						if (packageName.equals(info.activityInfo.packageName) && name.equalsIgnoreCase(info.activityInfo.name)) {
//							Intent intent = new Intent();
//							intent.setClassName(info.activityInfo.packageName,
//									info.activityInfo.name);
//							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//							startActivity(intent);
							final Intent intent = new Intent(Intent.ACTION_MAIN, null);  
							intent.addCategory(Intent.CATEGORY_LAUNCHER);  
							final ComponentName cn = new ComponentName(info.activityInfo.packageName, info.activityInfo.name);  
							intent.setComponent(cn);  
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
							startActivity( intent);
							break;
						}
					}
					finish();
				}
				break;
			default:
				finish();
				break;
			}
			break;
		case STARTWEIBO_REQUEST:
			break;
		default:
			finish();
			break;
		}
		Log.e("quickstart", "back");
	}

	private OnClickListener Button01Listener = new OnClickListener() {
		public void onClick(View v) {
			Intent setting = new Intent();
			setting.setClass(quickstart.this, AppAll.class);
			startActivityForResult(setting, STARTALL_REQUEST);
		}
	};

	private OnClickListener Button02Listener = new OnClickListener() {
		public void onClick(View v) {
			Intent setting = new Intent();
			setting.setClass(quickstart.this, AppConfig.class);
			startActivityForResult(setting, STARTCONFIG_REQUEST);
		}
	};

	private OnClickListener Button03Listener = new OnClickListener() {
		public void onClick(View v) {
			Intent i = getIntent();
			Bundle b = new Bundle();
			b.putString("msg", "quit");
			i.putExtras(b);
			quickstart.this.setResult(RESULT_OK, i);
			dbStart.close();
			quickstart.this.finish();
		}
	};

	private OnClickListener weibogridListener = new OnClickListener() {
		public void onClick(View v) {
			Intent setting = new Intent();
			setting.setClass(quickstart.this, weibo.class);
			startActivityForResult(setting, STARTWEIBO_REQUEST);
		}
	};

	private void loadApps() {
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		mAllApps = getPackageManager().queryIntentActivities(mainIntent, 0);
		mApps = new ArrayList<ResolveInfo>();
		Cursor c = dbStart.getAllItems();
//		Iterator it1 = mAllApps.iterator();
//		while (it1.hasNext()) {
//			boolean found=false;
//			ResolveInfo info = (ResolveInfo) it1.next();
//			if (c.moveToFirst()) {
//				do {
//					int idColumn = c.getColumnIndex(dbStart.KEY_ROWID);
//					int pkgnameColumn = c.getColumnIndex(dbStart.KEY_PKGNAME);
//					int appnameColumn = c.getColumnIndex(dbStart.KEY_APPNAME);
//					int contentColumn = c.getColumnIndex(dbStart.KEY_CONTENT);
//					if (c.getString(pkgnameColumn).equals(info.activityInfo.packageName) && c.getString(appnameColumn).equalsIgnoreCase(info.activityInfo.name)) {
//						found = true;
//						break;
//					}
//				} while (c.moveToNext());
//			}
//			if (found) mApps.add(info);
//		}
		if (c.moveToFirst()) {
			do {
				int idColumn = c.getColumnIndex(dbStart.KEY_ROWID);
				int pkgnameColumn = c.getColumnIndex(dbStart.KEY_PKGNAME);
				int appnameColumn = c.getColumnIndex(dbStart.KEY_APPNAME);
				int contentColumn = c.getColumnIndex(dbStart.KEY_CONTENT);
				Iterator it1 = mAllApps.iterator();
				boolean found=false;
				ResolveInfo info = null;
				while (it1.hasNext()) {
					info = (ResolveInfo) it1.next();
					if (c.getString(pkgnameColumn).equals(info.activityInfo.packageName) && c.getString(appnameColumn).equalsIgnoreCase(info.activityInfo.name)) {
						found = true;
						break;
					}
				}
				if (found) mApps.add(info);
			} while (c.moveToNext());
		}
		c.close();
	}

	// 重点在这里面
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Log.e("grid1", "click");
		ResolveInfo info = mApps.get(position);
		Log.e("grid1", info.activityInfo.packageName);
		
		// 旧代码 导致拨号和联系人无法打开
//		Intent intent = new Intent();
//		intent.setClassName(info.activityInfo.packageName,
//				info.activityInfo.name);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		startActivity(intent);
		final Intent intent = new Intent(Intent.ACTION_MAIN, null);  
		intent.addCategory(Intent.CATEGORY_LAUNCHER);  
		final ComponentName cn = new ComponentName(info.activityInfo.packageName, info.activityInfo.name);  
		intent.setComponent(cn);  
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		startActivity( intent);
		
		Intent i = getIntent();
		Bundle b = new Bundle();
		b.putString("msg", "quit");
		i.putExtras(b);
		quickstart.this.setResult(RESULT_OK, i);
		dbStart.close();
		quickstart.this.finish();
	}

	public class AppsAdapter extends BaseAdapter {
		public AppsAdapter() {
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// ImageView i;
			// if (convertView == null) {
			// i = new ImageView(quickstart.this);
			// i.setScaleType(ImageView.ScaleType.FIT_CENTER);
			// i.setLayoutParams(new GridView.LayoutParams(50, 50));
			// } else {
			// i = (ImageView) convertView;
			// }
			//
			// ResolveInfo info = mApps.get(position);
			// i.setImageDrawable(info.activityInfo.loadIcon(getPackageManager()));
			//
			// return i;

			LinearLayout layout = new LinearLayout(quickstart.this);
			layout.setOrientation(LinearLayout.VERTICAL);

			ResolveInfo info = mApps.get(position);
			layout.addView(addTitleView(
					info.activityInfo.loadIcon(getPackageManager()),
					info.activityInfo.loadLabel(getPackageManager()).toString()));

			return layout;
		}

		public final int getCount() {
			return mApps.size();
		}

		public final Object getItem(int position) {
			return mApps.get(position);
		}

		public final long getItemId(int position) {
			return position;
		}

		public View addTitleView(Drawable image, String title) {
			LinearLayout layout = new LinearLayout(quickstart.this);
			layout.setOrientation(LinearLayout.VERTICAL);

			ImageView iv = new ImageView(quickstart.this);
			iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
			iv.setLayoutParams(new GridView.LayoutParams(40, 40));
			iv.setImageDrawable(image);

			layout.addView(iv);

			TextView tv = new TextView(quickstart.this);
			// tv.setTransformationMethod(SingleLineTransformationMethod.getInstance());
			tv.setSingleLine(true);
			tv.setText(title);

			layout.addView(tv, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));

			layout.setGravity(Gravity.CENTER);
			return layout;
		}

	}

	// 创建菜单
	private final static int ITEM_ID_SETTING = 11;
	private final static int ITEM_ID_ABOUT = 12;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
//		menu.add(1, ITEM_ID_SETTING, 0, R.string.setting).setIcon(
//				R.drawable.menu_syssettings);
		menu.add(1, ITEM_ID_ABOUT, 0, R.string.about).setIcon(
				R.drawable.menu_help);
		return true;
	}

	// 给菜单加事件
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case ITEM_ID_SETTING:
//			Intent setting = new Intent();
//			setting.setClass(redhorse.this, quickstart.class);
//			startActivity(setting);
			break;
		case ITEM_ID_ABOUT:
			Intent setting = new Intent();
			setting.setClass(quickstart.this, Feedback.class);
			startActivity(setting);
			break;
		}
		return super.onOptionsItemSelected(item);
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