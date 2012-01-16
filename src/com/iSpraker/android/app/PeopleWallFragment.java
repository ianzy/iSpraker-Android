package com.iSpraker.android.app;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.MenuItem.OnMenuItemClickListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.iSpraker.android.R;
import com.iSpraker.android.dao.IUsersDAO;
import com.iSpraker.android.dao.impl.JsonUsersDAO;
import com.iSpraker.android.dos.Paging;
import com.iSpraker.android.dos.User;
import com.iSpraker.android.dos.UsersResponse;
import com.iSpraker.android.utils.NetworkHelper;

public class PeopleWallFragment extends Fragment implements OnScrollListener {
	
	private PhotoWallAdapter adapter;
	private GridView gridview;
	private Paging pagingInfo;
	private double lat = Double.POSITIVE_INFINITY;
	private double lng = Double.POSITIVE_INFINITY;
	private boolean updatingList = false;
	private int indexOfGrid = -1;
	
	private boolean enableListMode = true;
	
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
	        this.lat = callback.getLat();
	        this.lng = callback.getLng();
	        this.indexOfGrid = callback.getWallIndex();
	        this.pagingInfo = callback.getPaging();
		}
		
		gridview = (GridView) getActivity().findViewById(R.id.people_wall);
        gridview.setAdapter(adapter);
        
        gridview.setOnScrollListener(this);
        if(this.indexOfGrid != -1) {
        	gridview.setSelection(this.indexOfGrid);
        }
        
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
		MenuItem listModeItem = menu.add("ListMode")
		.setIcon(R.drawable.ic_action_list)
		.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				IPeopleWallCallbacks callback = (IPeopleWallCallbacks)getActivity();
				callback.onWallModeChange(adapter.mData, lat, lng, indexOfGrid, pagingInfo);
				return true;
			}
			
		});
		listModeItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		listModeItem.setEnabled(this.enableListMode);
    }
	
	private void setMenuItemEnable(boolean enable) {
		this.enableListMode = enable;
	    ((FragmentActivity)this.getActivity()).invalidateOptionsMenu();
	}
	
	private class DownloadImageTask extends AsyncTask<Void, Void, Hashtable<String, Bitmap>> {
	     protected Hashtable<String, Bitmap> doInBackground(Void... urls) {
	    	 Log.i("---------------------------------------", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>inside my head2");
	    	 Hashtable<String, Bitmap> profileImgs = new Hashtable<String, Bitmap>();
	    	 for(User e : adapter.mData) {
	    		 if(!e.getProfileImageURL().equals("") && e.getProfileImage() == null) {
	    			 Bitmap img = NetworkHelper.fetchImage(e.getProfileImageURL());
	    			 if (img != null) {
	    				 profileImgs.put(e.getUid(), img);
	    			 }
//		    			 if(img != null) {
//		    				 e.setProfileImage(img);
//		            	 }
	        	 }	
	    	 }
	    	 
	    	 return profileImgs;
	     }

	     protected void onPostExecute(Hashtable<String, Bitmap> profileImgs) {
	    	 for(User e : adapter.mData) {
	    		 if(!e.getProfileImageURL().equals("") && e.getProfileImage() == null && profileImgs.get(e.getUid()) != null) {
	    			 e.setProfileImage(profileImgs.get(e.getUid()));
	    		 }
	    	 }
	    	 adapter.notifyDataSetInvalidated();
	    	 setMenuItemEnable(true);
	    	 updatingList = false;
	     }
	}
   
   
	private class RefreshPeopleListTask extends AsyncTask<Double, Integer, UsersResponse> {
       protected UsersResponse doInBackground(Double... location) {
//       	String url = "http://ispraker.heroku.com//api/9b02756d6564a40dfa6436c3001a1441/users.json"; //PeopleTabFragment.this.getResources().getString(R.string.api_users);
       	String url = PeopleWallFragment.this.getResources().getString(R.string.api_users);
       	IUsersDAO userDAO = new JsonUsersDAO(url, PeopleWallFragment.this.getActivity());
       	return userDAO.getUsersDataByLocation(location[0], location[1]);
       }

       protected void onPostExecute(UsersResponse usersResponse) {
       	adapter.mData = new ArrayList<User>();
       	for(User e : usersResponse.getUsers()) {
       		adapter.addItem(e);
       	}
       	adapter.notifyDataSetChanged();
       	
       	// reset paging information
       	pagingInfo = usersResponse.getPaging();
       	
       	new DownloadImageTask().execute();
       	super.onPostExecute(usersResponse);
       }
   }
	
	private class UpdatePeopleListTask extends AsyncTask<Double, Integer, UsersResponse> {
       protected UsersResponse doInBackground(Double... location) {
//       	String url = "http://ispraker.heroku.com//api/9b02756d6564a40dfa6436c3001a1441/users.json"; //PeopleTabFragment.this.getResources().getString(R.string.api_users);
       	String url = PeopleWallFragment.this.getResources().getString(R.string.api_users);
       	IUsersDAO userDAO = new JsonUsersDAO(url, PeopleWallFragment.this.getActivity());
       	
       	return userDAO.getUsersDataByLocation(location[0], location[1], pagingInfo.getCurrentPage()+1);
       }

       protected void onPostExecute(UsersResponse usersResponse) {
       	
       	if(usersResponse.getUsers().size() != 0) {
	        	for(User e : usersResponse.getUsers()) {
	        		adapter.addItem(e);
	        	}
	        	adapter.notifyDataSetChanged();
	        	
	        	// update paging information
	        	pagingInfo = usersResponse.getPaging();
	        	
	        	new DownloadImageTask().execute();
	        	
	        	gridview.setSelection(indexOfGrid);
       	} else {
       		setMenuItemEnable(true);
       	}
       	
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
            mData.add(user);
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
            if (e.getProfileImage() != null ) {
            	holder.profileImage.setImageBitmap(e.getProfileImage());
            }
            return convertView;
        }
 
    }
 
    public static class ViewHolder {
        public TextView name;
        public ImageView profileImage;
    }

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
		
        if(pagingInfo != null && loadMore && pagingInfo.getCurrentPage() < pagingInfo.getNumPages()) {
        	if (lat != Double.POSITIVE_INFINITY && lng != Double.POSITIVE_INFINITY && adapter.mData != null && pagingInfo != null && this.updatingList == false) {
        		Log.i("---------------------------------------", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>inside my head");
        		this.updatingList = true;
        		this.indexOfGrid = gridview.getFirstVisiblePosition();
        		setMenuItemEnable(false);
				new UpdatePeopleListTask().execute(lat, lng);
			}
        }		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}
}
