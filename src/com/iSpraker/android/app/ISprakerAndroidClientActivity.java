package com.iSpraker.android.app;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

import com.iSpraker.android.R;

public class ISprakerAndroidClientActivity extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.setupTabs();
    }
    
    private void setupTabs() {
    	Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = this.getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, PeopleTabActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("Events").setIndicator("",
                          res.getDrawable(R.drawable.ic_launcher))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, PeopleTabActivity.class);
        spec = tabHost.newTabSpec("Users").setIndicator("",
                          res.getDrawable(R.drawable.ic_launcher))
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, PeopleTabActivity.class);
        spec = tabHost.newTabSpec("Me").setIndicator("",
                          res.getDrawable(R.drawable.ic_launcher))
                      .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(1);
    }
    
}