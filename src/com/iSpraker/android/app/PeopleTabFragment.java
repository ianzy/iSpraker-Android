package com.iSpraker.android.app;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.MenuItem.OnMenuItemClickListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iSpraker.android.R;
import com.iSpraker.android.dao.IUsersDAO;
import com.iSpraker.android.dao.impl.JsonUsersDAO;
import com.iSpraker.android.dos.Paging;
import com.iSpraker.android.dos.User;
import com.iSpraker.android.dos.UsersResponse;
import com.iSpraker.android.utils.NetworkHelper;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class PeopleTabFragment extends ListFragment {
	
//	private List<User> users;
	private PeopleListAdapter adapter;
//	private PhotoWallAdapter wallAdapter;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private PullToRefreshListView pairedListView;
//	private GridView gridview;
	
	private Paging pagingInfo;
	private double lat = Double.NaN;
	private double lng = Double.NaN;
	
	private int indexOfList;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.people_tab, container, false);
        return v;
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	super.onCreateOptionsMenu(menu, inflater);
    	menu.add("WallMode")
		.setIcon(R.drawable.ic_action_peoplewall)
		.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				IPeopleTabCallbacks callback = (IPeopleTabCallbacks)getActivity();
				callback.onListModeChange(adapter.mData);
				return true;
			}	
		})
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceSate) {
    	super.onActivityCreated(savedInstanceSate);
    	
    	if(null == adapter) {
    		IPeopleTabCallbacks callbacks = (IPeopleTabCallbacks)this.getActivity();
    		List<User> users = callbacks.getUsers();
    		if (users == null) {
    			adapter = new PeopleListAdapter();
    		} else {
    			adapter = new PeopleListAdapter(users);
    		}
    		
    		initializeUserLocation();
    	    
    	    if(!this.isOnline()) {
//    	          	Toast.makeText(PeopleTabActivity.this, "No internet connection, refresh later", Toast.LENGTH_LONG).show();
    	    	createNetworkDisabledAlert();
    	    } 
    	    if (!this.isGPSOn()){  
    	        createGpsDisabledAlert();
    	    }
    	}
    	
    	pairedListView = (PullToRefreshListView)this.getListView();
    	
    	// set show more list footer
    	View footerView = ((LayoutInflater)this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_footer, null, false);
    	pairedListView.addFooterView(footerView);
    	
        pairedListView.setAdapter(adapter);
        pairedListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				if (adapter.mData.size() == arg2 - 1) {
					Toast.makeText(PeopleTabFragment.this.getActivity(), "show more clicked!", Toast.LENGTH_LONG).show();
					if (lat != Double.NaN && lng != Double.NaN && adapter.mData != null && pagingInfo != null) {
						new UpdatePeopleListTask().execute(lat, lng);
					}
					return;
				}
				
				Intent intent = new Intent();
				User user = adapter.getItem(arg2-1);
				Bundle b = new Bundle();
				b.putParcelable("user", user);
				intent.setClassName("com.iSpraker.android", "com.iSpraker.android.app.PersonDetailActivity");
				intent.putExtras(b);
				startActivity(intent);
			}
		});
        
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
    
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
    
    public boolean isGPSOn() {
    	this.getActivity();
		LocationManager locManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
    	return locManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    
    private void createNetworkDisabledAlert() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());  
    	builder.setMessage("Your Network is disabled! Would you like to enable it?")  
    	     .setCancelable(false)  
    	     .setPositiveButton("Enable network",  
    	          new DialogInterface.OnClickListener(){  
    	          public void onClick(DialogInterface dialog, int id){  
    	        	  showNetworkOptions();  
    	          }  
    	     });  
    	     builder.setNegativeButton("Do nothing",  
    	          new DialogInterface.OnClickListener(){  
    	          public void onClick(DialogInterface dialog, int id){  
    	               dialog.cancel();  
    	          }  
    	     });  
    	AlertDialog alert = builder.create();  
    	alert.show();  
    }
    
    private void showNetworkOptions(){  
	    Intent networkOptionsIntent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);  
	    this.startActivity(networkOptionsIntent);  
	}  
    
    private void createGpsDisabledAlert(){  
    	AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());  
    	builder.setMessage("Your GPS is disabled! Would you like to enable it?")  
    	     .setCancelable(false)  
    	     .setPositiveButton("Enable GPS",  
    	          new DialogInterface.OnClickListener(){  
    	          public void onClick(DialogInterface dialog, int id){  
    	               showGpsOptions();  
    	          }  
    	     });  
    	     builder.setNegativeButton("Do nothing",  
    	          new DialogInterface.OnClickListener(){  
    	          public void onClick(DialogInterface dialog, int id){  
    	               dialog.cancel();  
    	          }  
    	     });  
    	AlertDialog alert = builder.create();  
    	alert.show();  
    }  
    	  
	private void showGpsOptions(){  
	    Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);  
	    this.startActivity(gpsOptionsIntent);  
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
		    	//do something in background with these
			    Log.i("---------------------------------------", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>here");
			    PeopleTabFragment.this.lat = lat;
			    PeopleTabFragment.this.lng = lng;
			  	locationManager.removeUpdates(this);
			  	new RefreshPeopleListTask().execute(lat, lng);
			        
			  }
			
			  public void onStatusChanged(String provider, int status, Bundle extras) {}
			
			  public void onProviderEnabled(String provider) {}
			
			  public void onProviderDisabled(String provider) {}
		};

	  // Register the listener with the Location Manager to receive location updates
	  locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

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
    
//    public void onHomeClick(View v) {
//    	Context context = this.getActivity().getApplicationContext();
//    	CharSequence text = "Dude";
//    	int duration = Toast.LENGTH_SHORT;
//
//    	Toast toast = Toast.makeText(context, text, duration);
//    	toast.show();
//    }
    
    
	private class RefreshPeopleListTask extends AsyncTask<Double, Integer, UsersResponse> {
        protected UsersResponse doInBackground(Double... location) {
//        	String url = "http://ispraker.heroku.com//api/9b02756d6564a40dfa6436c3001a1441/users.json"; //PeopleTabFragment.this.getResources().getString(R.string.api_users);
        	String url = PeopleTabFragment.this.getResources().getString(R.string.api_users_local);
        	IUsersDAO userDAO = new JsonUsersDAO(url);
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
        	PullToRefreshListView lv = pairedListView;
        	if (lv != null) {
        		lv.onRefreshComplete();
        	}
        	super.onPostExecute(usersResponse);
        }
    }
	
	private class UpdatePeopleListTask extends AsyncTask<Double, Integer, UsersResponse> {
        protected UsersResponse doInBackground(Double... location) {
//        	String url = "http://ispraker.heroku.com//api/9b02756d6564a40dfa6436c3001a1441/users.json"; //PeopleTabFragment.this.getResources().getString(R.string.api_users);
        	String url = PeopleTabFragment.this.getResources().getString(R.string.api_users_local);
        	IUsersDAO userDAO = new JsonUsersDAO(url);
        	
        	indexOfList = pairedListView.getFirstVisiblePosition();
        	
        	return userDAO.getUsersDataByLocation(location[0], location[1], pagingInfo.getCurrentPage()+1);
        }

        protected void onPostExecute(UsersResponse usersResponse) {
//        	adapter.notifyDataSetChanged();
        	for(User e : usersResponse.getUsers()) {
        		adapter.addItem(e);
        	}
        	adapter.notifyDataSetChanged();
        	// update paging information
        	pagingInfo = usersResponse.getPaging();
        	
        	new DownloadImageTask().execute();
        	PullToRefreshListView lv = pairedListView;
        	if (lv != null) {
        		lv.onRefreshComplete();
        	}
        	
        	lv.setSelectionFromTop(indexOfList, 0);
        	super.onPostExecute(usersResponse);
        }
    }
    
    /**
     * adapter for the list view
     */
    private class PeopleListAdapter extends BaseAdapter {
		 
        private List<User> mData;
        private LayoutInflater mInflater;
 
        public PeopleListAdapter() {
            mInflater = (LayoutInflater)PeopleTabFragment.this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mData = new ArrayList<User>();
        }
        
        public PeopleListAdapter(List<User> users) {
            mInflater = (LayoutInflater)PeopleTabFragment.this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mData = users;
        }
 
        public void addItem(final User user) {
            mData.add(user);
//            notifyDataSetChanged();
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
        	ListViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.people_list_item, null);
                holder = new ListViewHolder();
                holder.name = (TextView)convertView.findViewById(R.id.name);
                holder.email = (TextView)convertView.findViewById(R.id.email);
                holder.profileImage = (ImageView)convertView.findViewById(R.id.profile_image);
                holder.description = (TextView)convertView.findViewById(R.id.description);
               
                convertView.setTag(holder);
            } else {
                holder = (ListViewHolder)convertView.getTag();
            }
            User e = mData.get(position);
            holder.name.setText(e.getScreenName());
            holder.description.setText(e.getDescription());
            holder.email.setText(e.getEmail());
            holder.profileImage.setImageBitmap(e.getProfileImage());
            return convertView;
        }
 
    }
 
    /**
     * view holder for list mode
     */
    public static class ListViewHolder {
        public TextView name;
        public TextView email;
        public TextView description;
        public ImageView profileImage;
    }
       
}
