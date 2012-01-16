package com.iSpraker.android.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iSpraker.android.R;
import com.iSpraker.android.dos.HashTag;
import com.iSpraker.android.dos.User;
import com.iSpraker.android.utils.NetworkHelper;

public class PersonDetailActivity extends FragmentActivity {
	
	//	private String lv_arr[]={"Activities >","Friends >","Add Friend >"};
	//private String phoneNumber;
	private User user;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);   
	      setContentView(R.layout.person_detail);	
//	      ListView actionsList = (ListView)findViewById(R.id.person_actions);
//	      actionsList.setAdapter(new ArrayAdapter<String>(this, R.layout.me_actions_item , lv_arr));
	      
	      // get the user detail
	      Bundle b = this.getIntent().getExtras();
	      user = (User)b.getParcelable("user");
	      
	      ((TextView)this.findViewById(R.id.person_name)).setText(user.getScreenName());
	      ((TextView)this.findViewById(R.id.person_email)).setText(user.getEmail());
	      ((TextView)this.findViewById(R.id.person_description)).setText(user.getDescription());
	      ((ImageView)this.findViewById(R.id.person_profile_img)).setImageDrawable(this.getResources().getDrawable(R.drawable.default_profile_3_normal));
	      //this.phoneNumber = user.getPhoneNumber();
	      
	      new DownloadImageTask().execute(user.getProfileImageURL());
	}
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Text")
			.setIcon(R.drawable.ic_action_list)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			
		return super.onCreateOptionsMenu(menu);
	}
	
	public void onCallClick(View v){
	      // get the user detail
		if(user.getPhoneNumber()!=null){
			try {
				startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + user.getPhoneNumber())));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			Context context = getApplicationContext();
			CharSequence text = user.getScreenName()+"does not have phone number.";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			
		}
		
	}
	
	public void onSayHiClicked(View v) {
		Intent intent = new Intent();
		intent.putExtra("recipient_id", user.getUid());
		intent.putExtra("recipient_screen_name", "");
		intent.putExtra("message", "");
		
		intent.setClassName("com.iSpraker.android", "com.iSpraker.android.app.SendMessageActivity");
		startActivity(intent);
	}
	
	public void onDirectMessagesClicked(View v) {
		Intent intent = new Intent();
		
		intent.setClassName("com.iSpraker.android", "com.iSpraker.android.app.InboxActivity");
		startActivity(intent);
	}
	
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    protected Bitmap doInBackground(String... urls) {
	    	if (urls[0] == null) {
	    		return null;
	    	}
	    	Bitmap img = NetworkHelper.fetchImage(urls[0]);
			return img;
	    }

	    protected void onPostExecute(Bitmap result) {
	    	if (result != null) {
	    		((ImageView)findViewById(R.id.person_profile_img)).setImageBitmap(result);
	    	}
	    }
	 }
}
