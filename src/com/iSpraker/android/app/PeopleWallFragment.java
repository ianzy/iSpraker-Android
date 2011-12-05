package com.iSpraker.android.app;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.MenuItem.OnMenuItemClickListener;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.iSpraker.android.R;
import com.iSpraker.android.dao.IUsersDAO;
import com.iSpraker.android.dao.impl.JsonUsersDAO;
import com.iSpraker.android.dos.User;
import com.iSpraker.android.dos.UsersResponse;
import com.iSpraker.android.utils.NetworkHelper;

public class PeopleWallFragment extends Fragment {
	
	private PhotoWallAdapter adapter;
	private GridView gridview;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.people_wall_tab, container, false);
        return v;
    }
	
	@Override
    public void onActivityCreated(Bundle savedInstanceSate) {
		super.onActivityCreated(savedInstanceSate);
		if(null == adapter) {
			//use data from the activity and populate the adapter
	        IPeopleWallCallbacks callback = (IPeopleWallCallbacks)getActivity();
	        adapter = new PhotoWallAdapter(callback.getUsers());
		}
		
		gridview = (GridView) getActivity().findViewById(R.id.people_wall);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				Intent intent = new Intent();
				User user = adapter.getItem(arg2);
				Bundle b = new Bundle();
				b.putParcelable("user", user);
				intent.setClassName("com.iSpraker.android", "com.iSpraker.android.app.PersonDetailActivity");
				intent.putExtras(b);
				startActivity(intent);
			}
		});
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	menu.add("ListMode")
		.setIcon(R.drawable.ic_action_list)
		.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				IPeopleWallCallbacks callback = (IPeopleWallCallbacks)getActivity();
				callback.onWallModeChange(adapter.mData);
				return true;
			}
			
		})
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
    }
	
	private class DownloadImageTask extends AsyncTask<Void, Void, Void> {
	     protected Void doInBackground(Void... urls) {
	    	for(User e : adapter.mData) {
	        	if(!e.getProfileImageURL().equals("")) {
	            	Bitmap img = NetworkHelper.fetchImage(e.getProfileImageURL());
	            	if(img != null) {
	            		e.setProfileImage(img);
	            	}
	        	}
	        		
	        }
			return null;
	     }

	     protected void onPostExecute(Void result) {
	    	 adapter.notifyDataSetInvalidated();
	     }
	 }
   
   private class RefreshPeopleWallTask extends AsyncTask<Double, Integer, UsersResponse> {
       protected UsersResponse doInBackground(Double... location) {
       	//String url = "http://ispraker.heroku.com//api/9b02756d6564a40dfa6436c3001a1441/users.json"; //PeopleTabFragment.this.getResources().getString(R.string.api_users);
       	String url = getResources().getString(R.string.api_users_local);
       	IUsersDAO userDAO = new JsonUsersDAO(url, PeopleWallFragment.this.getActivity());
       	return userDAO.getUsersDataByLocation(location[0], location[1]);
       }

       protected void onPostExecute(UsersResponse usersResponse) {
       	adapter.mData = new ArrayList<User>();
       	adapter.notifyDataSetChanged();
       	for(User e : usersResponse.getUsers()) {
       		adapter.addItem(e);
       	}
       	
       	new DownloadImageTask().execute();
       	super.onPostExecute(usersResponse);
       }
   }
	
	public class PhotoWallAdapter extends BaseAdapter {
		private List<User> mData;
        private LayoutInflater mInflater;
 
        public PhotoWallAdapter() {
            mInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mData = new ArrayList<User>();
        }
        
        public PhotoWallAdapter(List<User> users) {
            mInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mData = users;
        }
 
        public void addItem(final User user) {
            mData.add(0,user);
            notifyDataSetChanged();
        }
 
        public int getCount() {
            return mData.size();
        }
 
        public User getItem(int position) {
            return mData.get(position);
        }
 
        public long getItemId(int position) {
            return position;
        }
 
        
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.people_wall_item, null);
                holder = new ViewHolder();
                holder.profileImage = (ImageView)convertView.findViewById(R.id.people_wall_profile_img);
                holder.name = (TextView)convertView.findViewById(R.id.people_wall_name);
               
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            User e = mData.get(position);
            holder.name.setText(e.getScreenName());
            holder.profileImage.setImageBitmap(e.getProfileImage());
            return convertView;
        }
 
    }
 
    public static class ViewHolder {
        public TextView name;
        public ImageView profileImage;
    }
}
