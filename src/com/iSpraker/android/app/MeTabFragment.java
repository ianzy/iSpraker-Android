package com.iSpraker.android.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iSpraker.android.R;
import com.iSpraker.android.dos.User;
import com.iSpraker.android.utils.NetworkHelper;

public class MeTabFragment extends Fragment {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
       
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.me_tab, container, false);
        return v;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceSate) {
    	super.onActivityCreated(savedInstanceSate);
    	
    	User user = ((ISprakerAndroidClientActivity)this.getActivity()).getCurrentUser();
    	((TextView)this.getActivity().findViewById(R.id.me_name)).setText(user.getScreenName());
    	((TextView)this.getActivity().findViewById(R.id.tvAddress1_content)).setText(user.getEmail());
    	((TextView)this.getActivity().findViewById(R.id.tvAccount_introduce_content)).setText(user.getDescription());
    	
    	new DownloadImageTask().execute(user.getProfileImageURL());
    }
    
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    protected Bitmap doInBackground(String... urls) {
	    	if (urls[0] == null) {
	    		return null;
	    	}
	    	Bitmap img = NetworkHelper.fetchImage(urls[0]);
			return img;
	    }

	    protected void onPostExecute(Bitmap result) {
	    	if (result != null) {
	    		Activity container = MeTabFragment.this.getActivity();
	    		if (container != null) {
	    			((ImageView)container.findViewById(R.id.ivPortrait)).setImageBitmap(result);
	    		}
	    	}
	    }
	 }
    
}
