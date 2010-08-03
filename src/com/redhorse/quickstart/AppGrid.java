/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redhorse.quickstart;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

//Need the following import to get access to the app resources, since this
//class is in a sub-package.

public class AppGrid extends Activity implements OnItemClickListener {

	private GridView mGrid;
	private dbStartConfigAdapter dbStart = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        dbStart = new dbStartConfigAdapter(this);
        dbStart.open();

        loadApps(); // do this in onresume?

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
	}

	private OnClickListener Button01Listener = new OnClickListener() {
		public void onClick(View v) {
			Intent i = getIntent();
			Bundle b = new Bundle();
			b.putString("msg", "hide");
			i.putExtras(b);
			AppGrid.this.setResult(RESULT_OK, i);
			AppGrid.this.finish();
		}
	};

	private OnClickListener Button02Listener = new OnClickListener() {
		public void onClick(View v) {
			Intent i = getIntent();
			Bundle b = new Bundle();
			b.putString("msg", "config");
			i.putExtras(b);
			AppGrid.this.setResult(RESULT_OK, i);
			AppGrid.this.finish();
		}
	};

	private OnClickListener Button03Listener = new OnClickListener() {
		public void onClick(View v) {
			Intent i = getIntent();
			Bundle b = new Bundle();
			b.putString("msg", "quit");
			i.putExtras(b);
			AppGrid.this.setResult(RESULT_OK, i);
			AppGrid.this.finish();
		}
	};

	private List<ResolveInfo> mApps;
	private List<ResolveInfo> mAllApps;

	private void loadApps() {
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		mAllApps = getPackageManager().queryIntentActivities(mainIntent, 0);
		mApps = new ArrayList<ResolveInfo>();
		Cursor c = dbStart.getAllItems();
		Iterator it1 = mAllApps.iterator();
		while (it1.hasNext()) {
			boolean found=false;
			ResolveInfo info = (ResolveInfo) it1.next();
			if (c.moveToFirst()) {
				do {
					int idColumn = c.getColumnIndex(dbStart.KEY_ROWID);
					int pkgnameColumn = c.getColumnIndex(dbStart.KEY_PKGNAME);
					int appnameColumn = c.getColumnIndex(dbStart.KEY_APPNAME);
					int contentColumn = c.getColumnIndex(dbStart.KEY_CONTENT);
					if (c.getString(pkgnameColumn).equals(info.activityInfo.packageName) && c.getString(appnameColumn).equalsIgnoreCase(info.activityInfo.name)) {
						found = true;
						break;
					}
				} while (c.moveToNext());
			}
			if (found) mApps.add(info);
		}

	}

	// 重点在这里面
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Log.e("grid1", "click");
		ResolveInfo info = mApps.get(position);
		Log.e("grid1", info.activityInfo.packageName);

		Intent intent = new Intent();
		intent.setClassName(info.activityInfo.packageName,
				info.activityInfo.name);
		startActivity(intent);
	}

	public class AppsAdapter extends BaseAdapter {
		public AppsAdapter() {
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// ImageView i;
			// if (convertView == null) {
			// i = new ImageView(AppGrid.this);
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

			LinearLayout layout = new LinearLayout(AppGrid.this);
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
			LinearLayout layout = new LinearLayout(AppGrid.this);
			layout.setOrientation(LinearLayout.VERTICAL);

			ImageView iv = new ImageView(AppGrid.this);
			iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
			iv.setLayoutParams(new GridView.LayoutParams(40, 40));
			iv.setImageDrawable(image);

			layout.addView(iv);

			TextView tv = new TextView(AppGrid.this);
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

}
