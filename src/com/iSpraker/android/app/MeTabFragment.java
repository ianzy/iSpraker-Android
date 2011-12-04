package com.iSpraker.android.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.iSpraker.android.R;

public class MeTabFragment extends Fragment {
	private String lv_arr[]={"Activities >","Friends >","Add Friend >"};
	
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
//    	ListView actionsList = (ListView)this.getActivity().findViewById(R.id.me_actions);
    	// By using setAdpater method in listview we an add string array in list.
//    	actionsList.setAdapter(new ArrayAdapter<String>(this.getActivity(), R.layout.me_actions_item , lv_arr));
    }
    
}
