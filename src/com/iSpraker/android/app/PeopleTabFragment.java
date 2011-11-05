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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iSpraker.android.R;
import com.iSpraker.android.dao.IUsersDAO;
import com.iSpraker.android.dao.impl.JsonUsersDAO;
import com.iSpraker.android.dos.User;
import com.iSpraker.android.utils.NetworkHelper;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class PeopleTabFragment extends ListFragment {
	
//	private List<User> users;
	private PeopleListAdapter adapter;
	private LocationManager locationManager;
	private LocationListener locationListener;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
       
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.people_tab, container, false);
        return v;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceSate) {
    	super.onActivityCreated(savedInstanceSate);
//      setContentView(R.layout.people_tab);
        
//      adapter = (PeopleListAdapter)getLastNonConfigurationInstance();
    	if(null == adapter) {
    		adapter = new PeopleListAdapter();
    		initializeUserLocation();
    	    
    	    if(!this.isOnline()) {
//    	          	Toast.makeText(PeopleTabActivity.this, "No internet connection, refresh later", Toast.LENGTH_LONG).show();
    	    	createNetworkDisabledAlert();
    	    } 
    	    if (!this.isGPSOn()){  
    	        createGpsDisabledAlert();
    	    }
    	}
    	
    	ListView pairedListView = this.getListView();
        pairedListView.setAdapter(adapter);
        pairedListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
//				Log.i(">>>>>>>>>>>>>>>>>>>>>>>>>", String.valueOf(arg2));
//				Intent intent = new Intent();
//				User e = adapter.getItem(arg2);
//				Bundle b = new Bundle();
//				b.putParcelable("event", e);
//				intent.setClassName("exercise.app","exercise.app.EventDetails");
//				intent.putExtras(b);
//				startActivity(intent);
				
				Intent intent = new Intent();
				intent.setClassName("com.iSpraker.android", "com.iSpraker.android.app.PersonDetailActivity");
				startActivity(intent);
			}
		});
        
        ((PullToRefreshListView) pairedListView).setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh() {
                // Do work to refresh the list here.
            	
            	if( locationManager != null && locationListener != null) {
//            		adapter.mData.clear();
            		locationManager.removeUpdates(locationListener);
            	}
            	initializeUserLocation();
            }
        });
    }
    
//    @Override
//    public Object onRetainNonConfigurationInstance() {
//        return this.adapter;
//    }
    
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


//	public void onRefreshClick(View v) {
//		// trigger off background sync
//    	findViewById(R.id.btn_title_refresh).setVisibility(
//                View.GONE );
//        findViewById(R.id.title_refresh_progress).setVisibility(
//                View.VISIBLE);
//        initializeUserLocation();
//	}
	
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
		    	
		  	locationManager.removeUpdates(this);
		  	new UpdatePeopleListTask().execute(lat, lng);
		        
		  }
		
		  public void onStatusChanged(String provider, int status, Bundle extras) {}
		
		  public void onProviderEnabled(String provider) {}
		
		  public void onProviderDisabled(String provider) {}
};

	  // Register the listener with the Location Manager to receive location updates
//	  locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

	}
	
//	private Handler handler = new Handler() {
//        public void  handleMessage(Message msg) {
//            //update UI here
//        	switch(msg.what) {
//    		case Updater.NODATAFOUND:
//    			Toast.makeText(EventViewer.this, "Unable to fetch events on this zipcode...", Toast.LENGTH_LONG).show();
//    			break;
//    		case Updater.SUCCESS:
//    			adapter.mData = new ArrayList<Event>();
//            	adapter.notifyDataSetChanged();
//            	for(Event e : events) {
//            		adapter.addItem(e);
//            	}
//            	new DownloadImageTask().execute();
//    			break;
//        	}
//        	((ImageButton)EventViewer.this.findViewById(R.id.button_refresh)).setEnabled(true);
//        }
//	};
	
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
    
    public void onHomeClick(View v) {
    	Context context = this.getActivity().getApplicationContext();
    	CharSequence text = "Dude";
    	int duration = Toast.LENGTH_SHORT;

    	Toast toast = Toast.makeText(context, text, duration);
    	toast.show();
    }
    
    
    private class UpdatePeopleListTask extends AsyncTask<Double, Integer, List<User>> {
        protected List<User> doInBackground(Double... location) {
        	String url = "http://10.0.2.2:3000/api/9b02756d6564a40dfa6436c3001a1441/users.json";
        	IUsersDAO userDAO = new JsonUsersDAO(url);
        	return userDAO.getUsersByLocation(location[0], location[1]);
        }

        protected void onPostExecute(List<User> users) {
        	adapter.mData = new ArrayList<User>();
        	adapter.notifyDataSetChanged();
        	for(User e : users) {
        		adapter.addItem(e);
        	}
        	new DownloadImageTask().execute();
        	PullToRefreshListView lv = (PullToRefreshListView)getListView();
        	if (lv != null) {
        		lv.onRefreshComplete();
        	}
        	super.onPostExecute(users);
        }
    }
    
    
    private class PeopleListAdapter extends BaseAdapter {
		 
        private List<User> mData;
        private LayoutInflater mInflater;
 
        public PeopleListAdapter() {
            mInflater = (LayoutInflater)PeopleTabFragment.this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mData = new ArrayList<User>();
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
                convertView = mInflater.inflate(R.layout.people_list_item, null);
                holder = new ViewHolder();
                holder.name = (TextView)convertView.findViewById(R.id.name);
                holder.email = (TextView)convertView.findViewById(R.id.email);
                holder.profileImage = (ImageView)convertView.findViewById(R.id.profile_image);
                holder.description = (TextView)convertView.findViewById(R.id.description);
               
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            User e = mData.get(position);
            holder.name.setText(e.getScreenName());
            holder.description.setText(e.getDescription());
            holder.email.setText(e.getEmail());
            holder.profileImage.setImageBitmap(e.getProfileImage());
            return convertView;
        }
 
    }
 
    public static class ViewHolder {
        public TextView name;
        public TextView email;
        public TextView description;
        public ImageView profileImage;
    }
}
