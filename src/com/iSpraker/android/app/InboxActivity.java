package com.iSpraker.android.app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import twitter4j.DirectMessage;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.MenuItem.OnMenuItemClickListener;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.iSpraker.android.R;
import com.iSpraker.android.dos.HashTag;
import com.iSpraker.android.utils.NetworkHelper;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class InboxActivity extends FragmentActivity {
	public static final String PREFS_NAME = "PrefsFile";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    setContentView(R.layout.inbox_fragment_layout);
	}
	
	public static class InboxFragment extends Fragment {
		private InboxAdapter adapter;
		private PullToRefreshListView pairedListView;
		private String hashTag;
		
		private final int COMPOSE_TWEET_SUCCESS = 0x1234;
		
		private Hashtable<Long, Bitmap> profileImages = new Hashtable<Long, Bitmap>();
		
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setHasOptionsMenu(true);
	        setRetainInstance(true);
	    }
		
		@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	        View v = inflater.inflate(R.layout.hash_tag_content_list, container, false);
	        return v;
	    }
	
		@Override
	    public void onActivityCreated(Bundle savedInstanceSate) {
	    	super.onActivityCreated(savedInstanceSate);
	        
//	        Bundle b = this.getActivity().getIntent().getExtras();
//		    hashTag = b.getString("hash_tag");
	        
	        if (this.adapter == null) {
	    		this.adapter = new InboxAdapter();
	    		new RefreshInboxTask().execute();
	    	}
	    	this.pairedListView = (PullToRefreshListView) this.getActivity().findViewById(R.id.hash_tag_content_list);
	    	
	    	pairedListView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					DirectMessage message = adapter.getItem(arg2-1);
					Intent intent = new Intent();
					intent.putExtra("recipient_id", message.getSenderId());
					intent.putExtra("recipient_screen_name", message.getSenderScreenName());
					intent.putExtra("message", message.getText());
					
					intent.setClassName("com.iSpraker.android", "com.iSpraker.android.app.SendMessageActivity");
					startActivity(intent);
				}
			});
	    	
	    	pairedListView.setAdapter(adapter);
	    	((PullToRefreshListView) pairedListView).setOnRefreshListener(new OnRefreshListener() {
	            public void onRefresh() {
	            	new RefreshInboxTask().execute();
	            }
	        });
	    	
	    	
	    }
		
	//	@Override
	//    public Object onRetainNonConfigurationInstance() {
	//        return this.adapter;
	//    }
		
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
		   if (requestCode == COMPOSE_TWEET_SUCCESS) {
		      if (resultCode == RESULT_OK) {
		    	  new RefreshInboxTask().execute();
		      }
		   }
		}
		
//		@Override
//	    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//			super.onCreateOptionsMenu(menu, inflater);
//	    	menu.add("Compose")
//				.setIcon(R.drawable.ic_action_compose)
//				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//					@Override
//					public boolean onMenuItemClick(MenuItem item) {
//						Intent intent = new Intent();
//						intent.setClassName("com.iSpraker.android", "com.iSpraker.android.app.ComposeActivity");
//						intent.putExtra("hash_tag", hashTag);
//					    startActivityForResult(intent, COMPOSE_TWEET_SUCCESS);
//						return true;
//					}
//			})
//			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
//	    }
		
		private class DownloadImageTask extends AsyncTask<Void, Void, Hashtable<Long, Bitmap>> {
		     protected Hashtable<Long, Bitmap> doInBackground(Void... urls) {
		    	 Hashtable<Long, Bitmap> profileImgs = new Hashtable<Long, Bitmap>();
		    	 for(DirectMessage e : adapter.mData) {
		    		 if(!e.getSender().getProfileImageURL().equals("") && profileImages.get(e.getId()) == null) {
		    			 Bitmap img = NetworkHelper.fetchImage(e.getSender().getProfileImageURL().toString());
		    			 if (img != null) {
		    				 profileImgs.put(e.getId(), img);
		    			 }
		        	 }	
		    	 }
		    	 
		    	 return profileImgs;
		     }
	
		     protected void onPostExecute(Hashtable<Long, Bitmap> profileImgs) {
		    	 for(DirectMessage e : adapter.mData) {
		    		 if(!e.getSender().getProfileImageURL().equals("") && profileImages.get(e.getId()) == null && profileImgs.get(e.getId()) != null) {
		    			 profileImages.put(e.getId(), profileImgs.get(e.getId()));
		    		 }
		    	 }
		    	 adapter.notifyDataSetInvalidated();
		     }
		}
		
		private class RefreshInboxTask extends AsyncTask<Void, Integer, ResponseList<DirectMessage>> {
	        protected ResponseList<DirectMessage> doInBackground(Void... params) {
	//        	String url = "http://ispraker.heroku.com//api/9b02756d6564a40dfa6436c3001a1441/hash_tags.json"; //PeopleTabFragment.this.getResources().getString(R.string.api_users);
	//        	String url = getResources().getString(R.string.api_hash_tags_local);
	        	Twitter twitter = new TwitterFactory().getInstance();
				SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
				// refactor this
				twitter.setOAuthConsumer("TCIaruKKUzfX2u8Xhg", "NAWrtBM7Vw8LZzeEkeyiLnEEY00fUjWYXmdX9tJkA");
				twitter.setOAuthAccessToken(new AccessToken(settings.getString("twitter_access_token", null), settings.getString("twitter_access_token_secret", null)) );
			    
	        	ResponseList<DirectMessage> messages = null;
				try {
					messages = twitter.getDirectMessages();
				} catch (TwitterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	        	return messages;

	        }
	
	        protected void onPostExecute(ResponseList<DirectMessage> messages) {
	        	adapter.mData = new ArrayList<DirectMessage>();
	        	profileImages = new Hashtable<Long, Bitmap>();
	        	for(DirectMessage e : messages) {
	        		adapter.addItem(e);
	        	}
	        	adapter.notifyDataSetChanged();
	        	
	        	new DownloadImageTask().execute();
	        	PullToRefreshListView lv = pairedListView;
	        	if (lv != null) {
	        		lv.onRefreshComplete();
	        	}
	        	super.onPostExecute(messages);
	        }
	    }
		
		/**
	     * adapter for the list view
	     */
	    private class InboxAdapter extends BaseAdapter {
			 
	        private List<DirectMessage> mData;
	        private LayoutInflater mInflater;
	 
	        public InboxAdapter() {
	            mInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            mData = new ArrayList<DirectMessage>();
	        }
	 
	        public void addItem(final DirectMessage tweet) {
	            mData.add(tweet);
	//            notifyDataSetChanged();
	        }
	 
	        public int getCount() {
	            return mData.size();
	        }
	 
	        public DirectMessage getItem(int position) {
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
	            DirectMessage e = mData.get(position);
	            if (profileImages.get(e.getId()) != null) {
	            	holder.hash_tag_content_profile.setImageBitmap(profileImages.get(e.getId()));
	            }
	            holder.hash_tag_content_name.setText("@"+e.getSenderScreenName());
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
}
