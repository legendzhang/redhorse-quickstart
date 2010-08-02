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
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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

import java.util.List;

//Need the following import to get access to the app resources, since this
//class is in a sub-package.

public class AppConfig extends Activity implements OnItemClickListener {

	GridView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadApps(); // do this in onresume?

        setContentView(R.layout.applist);
        mList = (GridView) findViewById(R.id.list);
        mList.setAdapter(new AppsAdapter());
        mList.setOnItemClickListener(this);   
        Button button = (Button)findViewById(R.id.Button01);
        button.setOnClickListener(Button01Listener);
        button = (Button)findViewById(R.id.Button02);
        button.setOnClickListener(Button02Listener);
    }

    private OnClickListener Button01Listener = new OnClickListener() {
        public void onClick(View v) {
			Intent i = getIntent();  
	        Bundle b = new Bundle();  
	        b.putString("msg", "hide");  
	        i.putExtras(b);  
	        AppConfig.this.setResult(RESULT_OK, i);  
	        AppConfig.this.finish();
        }
    };

    private OnClickListener Button02Listener = new OnClickListener() {
        public void onClick(View v) {
			Intent i = getIntent();  
	        Bundle b = new Bundle();  
	        b.putString("msg", "config");  
	        i.putExtras(b);  
	        AppConfig.this.setResult(RESULT_OK, i);  
	        AppConfig.this.finish();
        }
    };

    private List<ResolveInfo> mApps;

    private void loadApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        mApps = getPackageManager().queryIntentActivities(mainIntent, 0);
    }

    //重点在这里面
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        //重置上次颜色为Color.BLACK
        LinearLayout lLayout = (LinearLayout)view;
        lLayout.setBackgroundColor(Color.RED);
        
//        ImageView lImage = (ImageView)lLayout.getChildAt(0);
//        TextView lText = (TextView)lLayout.getChildAt(1);
        
//        lImage.setBackgroundResource(R.drawable.lightning_128x128);
//        lText.setTextColor(Color.RED);
        
        //保存最新的上次ID
    }
    

    public class AppsAdapter extends BaseAdapter {
        public AppsAdapter() {
        }

        public View getView(int position, View convertView, ViewGroup parent) {

        	// 从程序生成list里面的内容
//            LinearLayout layout = new LinearLayout(AppConfig.this);  
//            layout.setOrientation(LinearLayout.HORIZONTAL);  
//              
//            ResolveInfo info = mApps.get(position);
//            layout.addView(addTitleView(info.activityInfo.loadIcon(getPackageManager()),info.activityInfo.loadLabel(getPackageManager()).toString()));  
//              
//            return layout;

        	// 从layout文件生成list里面的内容
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
            LinearLayout layout = new LinearLayout(AppConfig.this);  
            layout.setOrientation(LinearLayout.HORIZONTAL);  
              
            ImageView iv = new ImageView(AppConfig.this);  
            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
          	iv.setLayoutParams(new GridView.LayoutParams(50, 50));
            iv.setImageDrawable(image);  
              
            layout.addView(iv);  
              
              
            TextView tv = new TextView(AppConfig.this);
//            tv.setTransformationMethod(SingleLineTransformationMethod.getInstance());  
//            tv.setSingleLine(true);
            tv.setText(title);  
              
            layout.addView(tv,  
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));  
              
            layout.setGravity(Gravity.CENTER);  
            return layout;  
        }  
          
    }

}
