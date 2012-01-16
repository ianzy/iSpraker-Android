package com.iSpraker.android.app;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

import com.iSpraker.android.R;
import com.iSpraker.android.dos.Paging;
import com.iSpraker.android.dos.User;

public class ISprakerAndroidClientActivity extends FragmentActivity implements IPeopleTabCallbacks, IPeopleWallCallbacks {
    private TabHost mTabHost;
    private TabManager mTabManager;
    
    private List<User> mData;
    private double lat = Double.POSITIVE_INFINITY;
    private double lng = Double.POSITIVE_INFINITY;
	private int indexOfList;
    private int indexOfGrid;
    private Paging pagingInfo;
    
    public enum WallMode {
    	LIST, WALL
    }
    private WallMode currentMode;
    private User currentUser;
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
//    	menu.add("ListMode")
//		.setIcon(R.drawable.ic_action_list)
//		.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//			@Override
//			public boolean onMenuItemClick(MenuItem item) {
//				mTabManager.replaceTabFragment("Friends", PeopleWallFragment.class);
//				return true;
//			}
//			
//		})
//		.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
//    	
//    	menu.add("WallMode")
//		.setIcon(R.drawable.ic_action_peoplewall)
//		.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//			@Override
//			public boolean onMenuItemClick(MenuItem item) {
//				mTabManager.replaceTabFragment("Friends", PeopleTabFragment.class);
//				return true;
//			}
//			
//		})
//		.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
    	
		return super.onCreateOptionsMenu(menu);
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//            	Toast.makeText(this, "wow this is not home", Toast.LENGTH_LONG).show();
                // app icon in Action Bar clicked; go home
//                Intent intent = new Intent(this, HomeActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
            	mTabHost.setCurrentTab(1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ispraker_home);
        
        Bundle b = this.getIntent().getExtras();
	    currentUser = (User)b.getParcelable("user");
        
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);

        View tabView = createTabView(mTabHost.getContext(), "Conversations", getResources().getDrawable(R.drawable.ic_tab_timeline));
        mTabManager.addTab(mTabHost.newTabSpec("Conversations").setIndicator(tabView),
        		HashTagsFragment.class, null);
        tabView = createTabView(mTabHost.getContext(), "Friends", getResources().getDrawable(R.drawable.ic_tab_friends));
        mTabManager.addTab(mTabHost.newTabSpec("Friends").setIndicator(tabView),
                PeopleTabFragment.class, null);
        tabView = createTabView(mTabHost.getContext(), "Events", getResources().getDrawable(R.drawable.ic_tab_events));
        mTabManager.addTab(mTabHost.newTabSpec("Events").setIndicator(tabView),
        		NearByFragment.class, null);
        tabView = createTabView(mTabHost.getContext(), "Me", getResources().getDrawable(R.drawable.ic_tab_me));
        mTabManager.addTab(mTabHost.newTabSpec("Me").setIndicator(tabView),
        		MeTabFragment.class, null);
   
        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
        
        mTabHost.setCurrentTab(1);
    }


    private View createTabView(final Context context, final String tag, final Drawable img) {
    	View view = LayoutInflater.from(context).inflate(R.layout.tab_view, null);
//    	TextView tv = (TextView) view.findViewById(R.id.tabText);
//    	tv.setText(tag);
    	ImageView imgv = (ImageView) view.findViewById(R.id.tabImage);
    	imgv.setImageDrawable(img);
    	return view;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", mTabHost.getCurrentTabTag());
    }

    /**
     * This is a helper class that implements a generic mechanism for
     * associating fragments with the tabs in a tab host.  It relies on a
     * trick.  Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show.  This is not sufficient for switching
     * between fragments.  So instead we make the content part of the tab host
     * 0dp high (it is not shown) and the TabManager supplies its own dummy
     * view to show as the tab content.  It listens to changes in tabs, and takes
     * care of switch to the correct fragment shown in a separate content area
     * whenever the selected tab changes.
     */
    public static class TabManager implements TabHost.OnTabChangeListener {
        private final FragmentActivity mActivity;
        private final TabHost mTabHost;
        private final int mContainerId;
        private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
        TabInfo mLastTab;

        static final class TabInfo {
            private final String tag;
            private Class<?> clss;
            private final Bundle args;
            private Fragment fragment;

            TabInfo(String _tag, Class<?> _class, Bundle _args) {
                tag = _tag;
                clss = _class;
                args = _args;
            }
        }

        static class DummyTabFactory implements TabHost.TabContentFactory {
            private final Context mContext;

            public DummyTabFactory(Context context) {
                mContext = context;
            }

            public View createTabContent(String tag) {
                View v = new View(mContext);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                return v;
            }
        }

        public TabManager(FragmentActivity activity, TabHost tabHost, int containerId) {
            mActivity = activity;
            mTabHost = tabHost;
            mContainerId = containerId;
            mTabHost.setOnTabChangedListener(this);
        }

        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
            tabSpec.setContent(new DummyTabFactory(mActivity));
            String tag = tabSpec.getTag();

            TabInfo info = new TabInfo(tag, clss, args);

            // Check to see if we already have a fragment for this tab, probably
            // from a previously saved state.  If so, deactivate it, because our
            // initial state is that a tab isn't shown.
            info.fragment = mActivity.getSupportFragmentManager().findFragmentByTag(tag);
            if (info.fragment != null && !info.fragment.isDetached()) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.detach(info.fragment);
                ft.commit();
            }

            mTabs.put(tag, info);
            mTabHost.addTab(tabSpec);
        }
        
//        public Fragment getCurrentFragment() {
//        	return mLastTab.fragment;
//        }
        // seems this only work when the tabId is the current tab
        public void replaceTabFragment(String tabId, Class<?> fragmentClass) {
        	TabInfo existingTab = mTabs.get(tabId);
        	FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        	if (existingTab != null) {
                if (existingTab.fragment != null) {
                    ft.detach(existingTab.fragment);
                }
                existingTab.fragment = Fragment.instantiate(mActivity,
                		fragmentClass.getName(), null);
                existingTab.clss = fragmentClass;
                ft.add(mContainerId, existingTab.fragment, tabId);
            }
        	ft.commit();
            mActivity.getSupportFragmentManager().executePendingTransactions();
        	
        }

        public void onTabChanged(String tabId) {
            TabInfo newTab = mTabs.get(tabId);
            if (mLastTab != newTab) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                if (mLastTab != null) {
                    if (mLastTab.fragment != null) {
                        ft.detach(mLastTab.fragment);
                    }
                }
                if (newTab != null) {
                    if (newTab.fragment == null) {
                        newTab.fragment = Fragment.instantiate(mActivity,
                                newTab.clss.getName(), newTab.args);
                        ft.add(mContainerId, newTab.fragment, newTab.tag);
                    } else {
                        ft.attach(newTab.fragment);
                    }
                }

                mLastTab = newTab;
                ft.commit();
                mActivity.getSupportFragmentManager().executePendingTransactions();
                mActivity.getSupportActionBar().setTitle(newTab.tag);
            }
        }
    }

	public void onListModeChange(List<User> users, double lat, double lng, int listIndex, Paging pagingInfo) {
		this.mData = users;
		this.lat = lat;
		this.lng = lng;
		this.indexOfList = listIndex;
		this.pagingInfo = pagingInfo;
		mTabManager.replaceTabFragment("Friends", PeopleWallFragment.class);
	}

	public List<User> getUsers() {
		return this.mData;
	}

	public void onWallModeChange(List<User> users, double lat, double lng, int wallIndex, Paging pagingInfo) {
		this.mData = users;
		this.lat = lat;
		this.lng = lng;
		this.indexOfGrid = wallIndex;
		this.pagingInfo = pagingInfo;
		mTabManager.replaceTabFragment("Friends", PeopleTabFragment.class);
	}
	
	public User getCurrentUser() {
		return this.currentUser;
	}

	@Override
	public int getWallIndex() {
		return this.indexOfGrid;
	}

	@Override
	public int getListIndex() {
		return this.indexOfList;
	}

	@Override
	public double getLat() {
		return this.lat;
	}

	@Override
	public double getLng() {
		return this.lng;
	}
	
	public void setLat(double lat) {
		this.lat = lat;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	@Override
	public Paging getPaging() {
		// TODO Auto-generated method stub
		return pagingInfo;
	}
    
}