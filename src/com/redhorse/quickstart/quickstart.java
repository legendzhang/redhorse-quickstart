package com.redhorse.quickstart;

import com.redhorse.quickstart.AppGrid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class quickstart extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
		Intent setting = new Intent();
		setting.setClass(this, AppGrid.class);
		startActivity(setting);
    }
}