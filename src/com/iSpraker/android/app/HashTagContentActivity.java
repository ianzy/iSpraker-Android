package com.iSpraker.android.app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.MenuItem.OnMenuItemClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iSpraker.android.R;
import com.iSpraker.android.utils.NetworkHelper;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class HashTagContentActivity extends FragmentActivity {
	
	private HashTagContentListAdapter adapter;
	private PullToRefreshListView pairedListView;
	private String hashTag;
	
	private final int COMPOSE_TWEET_SUCCESS = 0x1234;
	
	private Hashtable<Long, Bitmap> profileImages = new Hashtable<Long, Bitmap>();

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.hash_tag_content_list);
//        setRetainInstance(true);
        
        Bundle b = this.getIntent().getExtras();
	    hashTag = b.getString("hash_tag");
        
	    
	    adapter = (HashTagContentListAdapter)getLastNonConfigurationInstance();
        if (this.adapter == null) {
    		this.adapter = new HashTagContentListAdapter();
    		new RefreshHashTagContentListTask().execute();
    	}
    	this.pairedListView = (PullToRefreshListView) this.findViewById(R.id.hash_tag_content_list);
    	pairedListView.setAdapter(adapter);
    	((PullToRefreshListView) pairedListView).setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh() {
            	new RefreshHashTagContentListTask().execute();
            }
        });
    }
	
//	@Override
//    public Object onRetainNonConfigurationInstance() {
//        return this.adapter;
//    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	   if (requestCode == COMPOSE_TWEET_SUCCESS) {
	      if (resultCode == RESULT_OK) {
	    	  new RefreshHashTagContentListTask().execute();
	      }
	   }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add("Compose")
			.setIcon(R.drawable.ic_action_compose)
			.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					Intent intent = new Intent();
					intent.setClassName("com.iSpraker.android", "com.iSpraker.android.app.ComposeActivity");
					intent.putExtra("hash_tag", hashTag);
				    startActivityForResult(intent, COMPOSE_TWEET_SUCCESS);
					return true;
				}
		})
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		
		return super.onCreateOptionsMenu(menu);
    }
	
	private class DownloadImageTask extends AsyncTask<Void, Void, Hashtable<Long, Bitmap>> {
	     protected Hashtable<Long, Bitmap> doInBackground(Void... urls) {
	    	 Hashtable<Long, Bitmap> profileImgs = new Hashtable<Long, Bitmap>();
	    	 for(Tweet e : adapter.mData) {
	    		 if(!e.getProfileImageUrl().equals("") && profileImages.get(e.getId()) == null) {
	    			 Bitmap img = NetworkHelper.fetchImage(e.getProfileImageUrl());
	    			 profileImgs.put(e.getId(), img);
	        	 }	
	    	 }
	    	 
	    	 return profileImgs;
	     }

	     protected void onPostExecute(Hashtable<Long, Bitmap> profileImgs) {
	    	 for(Tweet e : adapter.mData) {
	    		 if(!e.getProfileImageUrl().equals("") && profileImages.get(e.getId()) == null) {
	    			 profileImages.put(e.getId(), profileImgs.get(e.getId()));
	    		 }
	    	 }
	    	 adapter.notifyDataSetInvalidated();
	     }
	}
	
	private class RefreshHashTagContentListTask extends AsyncTask<Void, Integer, List<Tweet>> {
        protected List<Tweet> doInBackground(Void... params) {
//        	String url = "http://ispraker.heroku.com//api/9b02756d6564a40dfa6436c3001a1441/hash_tags.json"; //PeopleTabFragment.this.getResources().getString(R.string.api_users);
//        	String url = getResources().getString(R.string.api_hash_tags_local);
        	Twitter twitter = new TwitterFactory().getInstance();
            Query query = new Query(hashTag);
            QueryResult result = null;
			try {
				result = twitter.search(query);
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	return result.getTweets();
        }

        protected void onPostExecute(List<Tweet> tweets) {
        	adapter.mData = new ArrayList<Tweet>();
        	profileImages = new Hashtable<Long, Bitmap>();
        	for(Tweet e : tweets) {
        		adapter.addItem(e);
        	}
        	adapter.notifyDataSetChanged();
        	
        	new DownloadImageTask().execute();
        	PullToRefreshListView lv = pairedListView;
        	if (lv != null) {
        		lv.onRefreshComplete();
        	}
        	super.onPostExecute(tweets);
        }
    }
	
	/**
     * adapter for the list view
     */
    private class HashTagContentListAdapter extends BaseAdapter {
		 
        private List<Tweet> mData;
        private LayoutInflater mInflater;
 
        public HashTagContentListAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mData = new ArrayList<Tweet>();
        }
 
        public void addItem(final Tweet tweet) {
            mData.add(tweet);
//            notifyDataSetChanged();
        }
 
        public int getCount() {
            return mData.size();
        }
 
        public Tweet getItem(int position) {
            return mData.get(position);
        }
 
        public long getItemId(int position) {
            return position;
        }
 
        
        public View getView(int position, View convertView, ViewGroup parent) {
        	ListViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.hash_tag_content_list_item, null);
                holder = new ListViewHolder();
                holder.hash_tag_content_profile = (ImageView)convertView.findViewById(R.id.hash_tag_content_profile);
                holder.hash_tag_content_name = (TextView)convertView.findViewById(R.id.hash_tag_content_name);
                holder.hash_tag_content_tweet = (TextView)convertView.findViewById(R.id.hash_tag_content_tweet);
                holder.hash_tag_content_time = (TextView)convertView.findViewById(R.id.hash_tag_content_time);
               
                convertView.setTag(holder);
            } else {
                holder = (ListViewHolder)convertView.getTag();
            }
            Tweet e = mData.get(position);
            if (profileImages.get(e.getId()) != null) {
            	holder.hash_tag_content_profile.setImageBitmap(profileImages.get(e.getId()));
            }
            holder.hash_tag_content_name.setText("@"+e.getFromUser());
            holder.hash_tag_content_time.setText(new SimpleDateFormat("h:mm a").format(e.getCreatedAt()));
            holder.hash_tag_content_tweet.setText(e.getText());
            return convertView;
        }
 
    }
 
    /**
     * view holder for list mode
     */
    public static class ListViewHolder {
    	public ImageView hash_tag_content_profile;
    	public TextView hash_tag_content_name;
    	public TextView hash_tag_content_tweet;
    	public TextView hash_tag_content_time;
    }
}
