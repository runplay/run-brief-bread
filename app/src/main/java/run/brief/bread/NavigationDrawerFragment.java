package run.brief.bread;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import run.brief.b.B;
import run.brief.b.Bgo;
import run.brief.b.State;
import run.brief.b.StateObject;
import run.brief.beans.RssUserFeed;
import run.brief.news.AdapterListNews;
import run.brief.news.NewsChooseFeedsFragment;
import run.brief.news.NewsFeedsDb;
import run.brief.news.NewsHomeFragment;
import run.brief.settings.SettingsHomeTabbedFragment;
import run.brief.util.BitmapFunctions;
import run.brief.util.Cal;
import run.brief.util.UrlImage;
import run.brief.util.UrlStore;


/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment implements OnScrollListener  {
    private NavigationDrawerCallbacks mCallbacks;
    public ActionBarDrawerToggle mDrawerToggle;
    private Activity activity;

    public DrawerLayout mDrawerLayout;
    private View mDrawerView;
    //private GridView mDrawerListView;

    private View mFragmentContainerView;
    private ImageView btnFilters;
    private ImageView btnSettings;


    private ListView newsSources;
    private NewsSourcesAdapter newsAdapter;
    private List<RssUserFeed> userfeeds;


    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity=getActivity();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDrawerView = inflater.inflate(R.layout.navigation_drawer, container, false);



        return mDrawerView;
    }

    public void closeDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }
    public void openDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }
    }


    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }
    private View.OnClickListener toFeedsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            closeDrawer();
            State.clearStateObject(State.SECTION_NEWS_CHOOSE, StateObject.INT_VALUE);
            Bgo.openFragmentBackStackAnimate(getActivity(), new NewsChooseFeedsFragment());
            //Bgo.openFragment(activity,new NewsChooseFeedsFragment());
            //showView(showFilters);
        }
    };
    private View.OnClickListener toFiltersListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            closeDrawer();
            State.addToState(State.SECTION_NEWS_CHOOSE, new StateObject(StateObject.INT_VALUE, 1));
            Bgo.openFragmentBackStackAnimate(getActivity(), new NewsChooseFeedsFragment());
            //Bgo.openFragment(activity,new NewsChooseFeedsFragment());
            //showView(showFilters);
        }
    };
    private View.OnClickListener toSettingsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            closeDrawer();
            Bgo.openFragmentBackStackAnimate(getActivity(), new SettingsHomeTabbedFragment());
            //Bgo.openFragment(activity,new NewsChooseFeedsFragment());
            //showView(showFilters);
        }
    };
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;


        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener



        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //Fab.showHideNavClose();
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //Fab.hide();
                if (!isAdded()) {
                    return;
                }

                //mDrawerView.setVisibility(View.GONE);
                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()

                reloadLastItems();
            }
        };

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            Log.e("Callback","callback navigation");
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            //Log.e("Callback","callback navigation - attached");
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            //inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Log.e("TOOLBAR", "onOptionsItemSelected called");

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if(mDrawerLayout!=null)
            mDrawerLayout.bringToFront();
        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        //ActionBar actionBar = getActionBar();
        //actionBar.setDisplayShowTitleEnabled(true);
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        //actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {

        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }


    public class Holder {
        public Holder(int Rid,String txt) {this.Rid=Rid; this.txt=txt;}
        public int Rid;
        public String txt;

    }




    private void reloadLastItems() {

        userfeeds = NewsFeedsDb.getUserFeedsArray();
//BLog.e("feeds size: "+userfeeds.size());

        newsSources = (ListView) mDrawerView.findViewById(R.id.list_sources);
        newsAdapter = new NewsSourcesAdapter(activity);
        newsSources.setAdapter(newsAdapter);


        btnFilters = (ImageView) mDrawerLayout.findViewById(R.id.btn_content_filter);
        btnFilters.setOnClickListener(toFiltersListener);

        TextView btnFeeds = (TextView) mDrawerLayout.findViewById(R.id.btn_content_feeds);
        btnFeeds.setOnClickListener(toFeedsListener);


        btnSettings = (ImageView) mDrawerLayout.findViewById(R.id.btn_settings);
        btnSettings.setOnClickListener(toSettingsListener);


    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if(newsAdapter!=null)
            newsAdapter.notifyDataSetChanged();

    }
    private long lastscroll;
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        long now= Cal.getUnixTime();

        //lastfilesadapter.notifyDataSetChanged();

        //BLog.e("SCR SPEED: " + (now-lastscroll));
        lastscroll=now;

    }

    public class NewsSourcesAdapter extends BaseAdapter {
        private Activity activity;


        public NewsSourcesAdapter(Activity c) {
            this.activity = c;


        }

        public int getCount() {
            return userfeeds.size()+2;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {

            RelativeLayout lay;
            if (convertView == null) {
                lay=(RelativeLayout) activity.getLayoutInflater().inflate(R.layout.navigation_drawer_item,null);

            } else {
                lay= (RelativeLayout) convertView;
            }

            ImageView image = (ImageView) lay.findViewById(R.id.btn_nav_item_img);
            TextView text = (TextView) lay.findViewById(R.id.btn_nav_item_txt);

            RssUserFeed feed = null;
            if(position<2) {
                if(position==0) {
                    text.setText(activity.getString(R.string.latest_news));

                    lay.setTag("all");
                    image.setImageDrawable(B.getDrawable(activity, R.drawable.content_feeds));
                } else {
                    text.setText(activity.getString(R.string.starred_news));

                    lay.setTag("star");
                    image.setImageDrawable(B.getDrawable(activity, R.drawable.rate_positive));
                }
            } else {
                feed=userfeeds.get(position-2);


                text.setText(feed.getString(RssUserFeed.STRING_PUBLISHER));

                lay.setTag(feed.getString(RssUserFeed.STRING_PUBLISHER));
                String pubimage = feed.getString(RssUserFeed.STRING_PUBLISHER_IMAGE);

                //BLog.e(pubimage);
                if(pubimage!=null && pubimage.length()>5) {
                    image.setTag(pubimage);
                    Bitmap b = new UrlImage().getRefresh(activity, UrlStore.URL_NEWS_PUBLISHER_IMAGES+pubimage , image);
                    if (b != null) {


                        //Drawable d = new BitmapDrawable(b);
                        int h = 100;
                        int w = 100;
                        /*
                        if (b.getWidth() > 60) {

                            h = Double.valueOf(b.getHeight() / (b.getWidth() / 60)).intValue();

                            if (h < 40)
                                h = 40;
                            else if (h > 80)
                                h = 80;
                        }
                        */
                        Bitmap resizedBitmap = BitmapFunctions.resizeToFitMax(b, w, h);
                        image.setImageDrawable(null);
                        image.setImageBitmap(resizedBitmap);
                        //d.setBounds(0,0,150,h);
                        //text.setCompoundDrawables(null, null, d, null);

                    } else {
                        image.setVisibility(View.GONE);
                        //text.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        //img.setImageResource(R.drawable.content_picture);
                    }
                } else {
                    if(feed.getString(RssUserFeed.STRING_URL).contains("youtube.")) {
                        image.setImageBitmap(null);
                        image.setImageDrawable(B.getDrawable(activity,R.drawable.logo_youtube));
                    }
                }
            }


            //if(feed.has(RssUserFeed.))
            //image.setVisibility(View.GONE);


            lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tag=(String) v.getTag();
                    if(tag!=null) {
                        if(tag.equals("all")) {
                            AdapterListNews.setShowPublisher(null);

                        } else if(tag.equals("star")) {
                            AdapterListNews.setShowPublisher("star");

                        } else {
                            AdapterListNews.setShowPublisher(tag);
                        }
                        Bgo.openFragment(activity, NewsHomeFragment.class);
                        closeDrawer();
                    }
                }
            });

            return lay;
        }



    }


}
