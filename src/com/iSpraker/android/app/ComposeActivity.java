package com.iSpraker.android.app;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.iSpraker.android.R;

public class ComposeActivity extends FragmentActivity {
	// refactor this
	public static final String PREFS_NAME = "PrefsFile";

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.compose);
        
        String hashTag = this.getIntent().getStringExtra("hash_tag");
        ((TextView)this.findViewById(R.id.componse_content)).setText("#"+hashTag);
	}
	
	public void onTweetClicked(View v) {
		((Button)this.findViewById(R.id.tweet)).setEnabled(false);
		String tweetContent = ((TextView)this.findViewById(R.id.componse_content)).getText().toString();
		
		Twitter twitter = new TwitterFactory().getInstance();
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		// refactor this
		twitter.setOAuthConsumer("AOMdfPY2WZ2vFNeOjVGQdw", "kBPBoZCRFOOktU8BHOt6isDDOCDgIGy64VbzrzaKo");
		twitter.setOAuthAccessToken(new AccessToken(settings.getString("twitter_access_token", null), settings.getString("twitter_access_token_secret", null)) );
	    Status status;
		try {
			status = twitter.updateStatus(tweetContent.trim());
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			Toast.makeText(this, "Unable to tweet... Please try again", Toast.LENGTH_LONG).show();
			((Button)this.findViewById(R.id.tweet)).setEnabled(true);
			e.printStackTrace();
		}
		this.setResult(RESULT_OK);
		this.finish();
	}
}
