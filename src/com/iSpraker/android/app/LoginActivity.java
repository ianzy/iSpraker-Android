package com.iSpraker.android.app;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.WebView;

import com.iSpraker.android.R;

public class LoginActivity extends FragmentActivity {
	
	public static final String PREFS_NAME = "PrefsFile";
	
	private static final String CONSUMER_KEY="AOMdfPY2WZ2vFNeOjVGQdw";
	private static final String CONSUMER_SECRET="kBPBoZCRFOOktU8BHOt6isDDOCDgIGy64VbzrzaKo";
	private static final String CALLBACK_URL="ispraker:///";
	
	private Twitter mTwitter;
	private RequestToken requestToken;
	
    @Override
    public void onNewIntent(Intent intent) {
    	super.onNewIntent(intent);
    	
    	Uri uri=intent.getData();
    	if(uri != null && uri.toString().startsWith(CALLBACK_URL)) {
    		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    		SharedPreferences.Editor editor = settings.edit();
    		
    		String oauthVerifier = uri.getQueryParameter("oauth_verifier");
    		try {
				AccessToken accessToken = mTwitter.getOAuthAccessToken(requestToken, oauthVerifier);
				editor.putString("twitter_access_token", accessToken.getToken());
				editor.putString("twitter_access_token_secret", accessToken.getTokenSecret());
				editor.commit();
				
//				AccessToken accessToken2 = new AccessToken(settings.getString("twitter_access_token", null), settings.getString("twitter_access_token_secret", null));
//				mTwitter.setOAuthAccessToken(accessToken);
				User user = mTwitter.showUser(accessToken.getUserId());
				user.getId();
//				setContentView(R.layout.main);
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);   
	      setContentView(R.layout.login);
	}
	
	public void onLoginClicked(View v) {
		mTwitter = new TwitterFactory().getInstance();
        
        mTwitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        
        try {
			requestToken= mTwitter.getOAuthRequestToken(CALLBACK_URL);
			WebView twitterSite = new WebView(this);
			twitterSite.loadUrl(requestToken.getAuthenticationURL());
			setContentView(twitterSite);
			
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}
	
	private void isUserExist(AccessToken accessToken) {
		
	}
}
