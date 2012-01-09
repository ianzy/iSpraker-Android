package com.iSpraker.android.app;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iSpraker.android.R;
import com.iSpraker.android.dao.IHashTagsDAO;
import com.iSpraker.android.dao.impl.JsonHashTagsDAO;
import com.iSpraker.android.dos.HashTag;
import com.iSpraker.android.dos.HashTagsResponse;
import com.iSpraker.android.tasks.UpdateLocationTask;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class HashTagsFragment extends ListFragment {
	
	private LocationManager locationManager;
	private LocationListener locationListener;
	
	private HashTagsListAdapter adapter;
	private PullToRefreshListView pairedListView;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.hashtags_tab, container, false);
        return v;
    }
	
	@Override
    public void onActivityCreated(Bundle savedInstanceSate) {
    	super.onActivityCreated(savedInstanceSate);
    	
    	double lat = ((ISprakerAndroidClientActivity)this.getActivity()).getLat();
    	double lng = ((ISprakerAndroidClientActivity)this.getActivity()).getLng();
    	if ( lat != Double.POSITIVE_INFINITY && lng != Double.POSITIVE_INFINITY) {
    		this.adapter = new HashTagsListAdapter();
    		new RefreshHashTagsListTask().execute(lat, lng);
    	} else {
	    	if (this.adapter == null) {
	    		this.adapter = new HashTagsListAdapter();
	    		initializeUserLocation();
	    	}
    	}
    	
    	this.pairedListView = (PullToRefreshListView)this.getListView();
    	pairedListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				Intent intent = new Intent();
				HashTag hashTag = adapter.getItem(arg2-1);
				Bundle b = new Bundle();
				b.putString("hash_tag", hashTag.getHash_tag());
				intent.setClassName("com.iSpraker.android", "com.iSpraker.android.app.HashTagContentActivity");
				intent.putExtras(b);
				startActivity(intent);
			}
		});
    	
    	pairedListView.setAdapter(adapter);
    	((PullToRefreshListView) pairedListView).setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh() {
                // Do work to refresh the list here.
            	if( locationManager != null && locationListener != null) {
            		locationManager.removeUpdates(locationListener);
            	}
            	initializeUserLocation();
            }
        });
    	
	}
	
	//fetch user location information
	private void initializeUserLocation() {
		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
		    	// Called when a new location is found by the network location provider.
		    	
		    	double lat = location.getLatitude();
		    	double lng = location.getLongitude();
		    	
		    	((ISprakerAndroidClientActivity)HashTagsFragment.this.getActivity()).setLat(lat);
			    ((ISprakerAndroidClientActivity)HashTagsFragment.this.getActivity()).setLng(lng);
			    
			  	locationManager.removeUpdates(this);
			  	new RefreshHashTagsListTask().execute(lat, lng);
			  	new UpdateLocationTask(HashTagsFragment.this.getActivity(), 
			  			((ISprakerAndroidClientActivity)HashTagsFragment.this.getActivity()).getCurrentUser()).execute(lat, lng);
			  	
			        
			  }
			
			  public void onStatusChanged(String provider, int status, Bundle extras) {}
			
			  public void onProviderEnabled(String provider) {}
			
			  public void onProviderDisabled(String provider) {}
		};

	  // Register the listener with the Location Manager to receive location updates
	  locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

	}
	
	private class RefreshHashTagsListTask extends AsyncTask<Double, Integer, HashTagsResponse> {
        protected HashTagsResponse doInBackground(Double... location) {
//        	String url = "http://ispraker.heroku.com//api/9b02756d6564a40dfa6436c3001a1441/hash_tags.json"; //PeopleTabFragment.this.getResources().getString(R.string.api_users);
        	String url = HashTagsFragment.this.getResources().getString(R.string.api_hash_tags_local);
        	IHashTagsDAO hashTagsDAO = new JsonHashTagsDAO(url, HashTagsFragment.this.getActivity());
        	return hashTagsDAO.getHashTagsByLocation(location[0], location[1]);
        }

        protected void onPostExecute(HashTagsResponse hashTagsResponse) {
        	adapter.mData = new ArrayList<HashTag>();
        	for(HashTag e : hashTagsResponse.getHashTags()) {
        		adapter.addItem(e);
        	}
        	adapter.notifyDataSetChanged();
        	
        	PullToRefreshListView lv = pairedListView;
        	if (lv != null) {
        		lv.onRefreshComplete();
        	}
        	super.onPostExecute(hashTagsResponse);
        }
    }
	
	/**
     * adapter for the list view
     */
    private class HashTagsListAdapter extends BaseAdapter {
		 
        private List<HashTag> mData;
        private LayoutInflater mInflater;
 
        public HashTagsListAdapter() {
            mInflater = (LayoutInflater)HashTagsFragment.this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mData = new ArrayList<HashTag>();
        }
 
        public void addItem(final HashTag hashTag) {
            mData.add(hashTag);
//            notifyDataSetChanged();
        }
 
        public int getCount() {
            return mData.size();
        }
 
        public HashTag getItem(int position) {
            return mData.get(position);
        }
 
        public long getItemId(int position) {
            return position;
        }
 
        
        public View getView(int position, View convertView, ViewGroup parent) {
        	ListViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.hash_tags_list_item, null);
                holder = new ListViewHolder();
                holder.hash_tag = (TextView)convertView.findViewById(R.id.hash_tag);
               
                convertView.setTag(holder);
            } else {
                holder = (ListViewHolder)convertView.getTag();
            }
            HashTag e = mData.get(position);
            holder.hash_tag.setText("# "+e.getHash_tag());
            return convertView;
        }
 
    }
 
    /**
     * view holder for list mode
     */
    public static class ListViewHolder {
        public TextView hash_tag;
    }
}
