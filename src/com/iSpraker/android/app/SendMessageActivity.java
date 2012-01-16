package com.iSpraker.android.app;

import twitter4j.DirectMessage;
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

public class SendMessageActivity extends FragmentActivity {
		// refactor this
		public static final String PREFS_NAME = "PrefsFile";
		
		private long recipientId;

		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        this.setContentView(R.layout.send_message);
	        
	        recipientId = this.getIntent().getLongExtra("recipient_id", -1);
	        String recipientScreenName = this.getIntent().getStringExtra("recipient_screen_name");
	        String message = this.getIntent().getStringExtra("message");
	        if ( !message.equals("")) {
	        	((TextView)this.findViewById(R.id.message_sender_name)).setText("@"+ recipientScreenName + ":");
	        } else {
	        	((TextView)this.findViewById(R.id.message_sender_name)).setText("");
	        }
	        ((TextView)this.findViewById(R.id.message_content)).setText(message);
		}
		
		public void onSubmitClicked(View v) {
			((Button)this.findViewById(R.id.message_send_button)).setEnabled(false);
			String tweetContent = ((TextView)this.findViewById(R.id.send_message_content)).getText().toString();
			
			if (tweetContent.trim().equals("")) {
				Toast.makeText(this, "Please provide some content...", Toast.LENGTH_LONG).show();
				return;
			}
			
			Twitter twitter = new TwitterFactory().getInstance();
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			// refactor this
			twitter.setOAuthConsumer("TCIaruKKUzfX2u8Xhg", "NAWrtBM7Vw8LZzeEkeyiLnEEY00fUjWYXmdX9tJkA");
			twitter.setOAuthAccessToken(new AccessToken(settings.getString("twitter_access_token", null), settings.getString("twitter_access_token_secret", null)) );
		    
			try {
				DirectMessage message = twitter.sendDirectMessage(recipientId, tweetContent);
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				Toast.makeText(this, "Unable to send... Please try again", Toast.LENGTH_LONG).show();
				((Button)this.findViewById(R.id.message_send_button)).setEnabled(true);
				e.printStackTrace();
			}
			Toast.makeText(this, "Message sent...", Toast.LENGTH_LONG).show();
			this.setResult(RESULT_OK);
			this.finish();
		}
		
		public void onCancelClicked(View v) {
			this.setResult(RESULT_CANCELED);
			this.finish();
		}
}
