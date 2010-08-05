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
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.redhorse.quickstart.quickstart.AppsAdapter;

//Need the following import to get access to the app resources, since this
//class is in a sub-package.

public class AppAll extends Activity implements OnItemClickListener {

	private GridView mGrid;
	private dbStartConfigAdapter dbStart = null;
	private List<ResolveInfo> mApps;
	private List<ResolveInfo> mAllApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbStart = new dbStartConfigAdapter(this);
        dbStart.open();
        
		loadApps(); // do this in onresume?

        setContentView(R.layout.appall);
		mGrid = (GridView) findViewById(R.id.myGrid);
		mGrid.setAdapter(new AppsAdapter());
		mGrid.setOnItemClickListener((OnItemClickListener) this);
		Button button = (Button) findViewById(R.id.Button01);
		button.setOnClickListener(Button01Listener);
    }

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		ResolveInfo info = mApps.get(position);

		Intent i = getIntent();
		Bundle b = new Bundle();
		b.putString("msg", "open");
		b.putString("packageName", info.activityInfo.packageName);
		b.putString("name", info.activityInfo.name);
		i.putExtras(b);
		this.setResult(RESULT_OK, i);
		dbStart.close();
		this.finish();
	}

    private OnClickListener Button01Listener = new OnClickListener() {
        public void onClick(View v) {        	
    		Intent i = getIntent();
    		Bundle b = new Bundle();
    		b.putString("msg", "back");
    		i.putExtras(b);
    		AppAll.this.setResult(RESULT_OK, i);
    		dbStart.close();
	        AppAll.this.finish();
        }
    };

    private void loadApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		mAllApps = getPackageManager().queryIntentActivities(mainIntent, 0);
		mApps = new ArrayList<ResolveInfo>();
		Cursor c = dbStart.getAllItems();
		Iterator it1 = mAllApps.iterator();
		while (it1.hasNext()) {
			ResolveInfo info = (ResolveInfo) it1.next();
			mApps.add(info);
		}
		c.close();
    }

    public class AppsAdapter extends BaseAdapter {
    	
        public AppsAdapter() {
        }

        public View getView(int position, View convertView, ViewGroup parent) {

        	ResolveInfo info = mApps.get(position);
            convertView = LayoutInflater.from(getApplicationContext()).inflate  
            (R.layout.listitem,null);  
              
            TextView mTextView = (TextView)convertView.findViewById(R.id.imageTitle);  
            mTextView.setText(info.activityInfo.loadLabel(getPackageManager()).toString());  
            ImageView mImageView = (ImageView)convertView.findViewById(R.id.imageView);  
            mImageView.setImageDrawable(info.activityInfo.loadIcon(getPackageManager()));  
            return convertView;          	
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

        public View addTitleView(Drawable image, String title){  
            LinearLayout layout = new LinearLayout(AppAll.this);  
            layout.setOrientation(LinearLayout.HORIZONTAL);
              
            ImageView iv = new ImageView(AppAll.this);  
            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
          	iv.setLayoutParams(new GridView.LayoutParams(50, 50));
            iv.setImageDrawable(image);  
              
            layout.addView(iv);  
              
              
            TextView tv = new TextView(AppAll.this);
            tv.setText(title);  
              
            layout.addView(tv,  
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));  
              
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
	        break;
		case ITEM_ID_ABOUT:
			Intent setting = new Intent();
			setting.setClass(AppAll.this, Feedback.class);
			startActivity(setting);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
