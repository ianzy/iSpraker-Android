package com.iSpraker.android.app;

import com.iSpraker.android.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PersonDetailActivity extends FragmentActivity {
	/** Called when the activity is first created. */
	private String lv_arr[]={"Activities >","Friends >","Add Friend >"};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);   
	      setContentView(R.layout.person_detail);	
	      ListView actionsList = (ListView)findViewById(R.id.person_actions);
	    	// By using setAdpater method in listview we an add string array in list.
	      actionsList.setAdapter(new ArrayAdapter<String>(this, R.layout.me_actions_item , lv_arr));
	}
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Text")
			.setIcon(R.drawable.ic_action_list)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			
		return super.onCreateOptionsMenu(menu);
	}
}
