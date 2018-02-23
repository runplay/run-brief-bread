package run.brief.bread;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import run.brief.b.ActionBarManager;
import run.brief.b.Bgo;
import run.brief.b.BreadService;
import run.brief.b.Device;
import run.brief.b.State;
import run.brief.beans.BriefSettings;
import run.brief.search.SearchFragment;
import run.brief.search.SearchPacket;
import run.brief.util.Cal;


public class Main extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    public static final String INTENT_DATE_STACKTRACE="HASCRASH";

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;

    private     AppCompatActivity activity;
    private boolean isCreateStart=true;
    private boolean isRestart=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Thread.setDefaultUncaughtExceptionHandler(new CatchCrash(this));
        BreadService.ensureStartups(this);
        BriefSettings settings = State.getSettings();
        if(settings!=null && settings.getBoolean(BriefSettings.BOOL_STYLE_DARK)==Boolean.FALSE) {
            setTheme(R.style.AppThemeLight);
            //Log.e("THEME","Theme is LIGHT");
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        activity = this;

        //getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        State.loadState(savedInstanceState);

        Log.e("DEVICE", "SDK :" + android.os.Build.VERSION.SDK_INT);
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }


        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.icon);
        ab.setDisplayHomeAsUpEnabled(true);

        ActionBarManager.restart(this);

        //checkSdCard();


        Device.hideKeyboard(this);

        Bgo.clearBackStack(activity);

        if (!BreadService.isServiceRunning(activity)) {
            //Log.e("SERV", "Starting Browse service");
            Intent service = new Intent(activity, BreadService.class);
            activity.startService(service);

        }
        BreadService.setIsAppStarted(true);
    }


    @Override
    public void onRestart() {
        //BLog.e("SAVE", "RESTART instance state");
        super.onRestart();
        isRestart=true;

    }
    @Override
    public void onStop() {
        super.onStop();
        BreadService.setIsAppStarted(false);
    }
    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();
        //Uri data = intent.getData();
        Bundle data=intent.getBundleExtra(INTENT_DATE_STACKTRACE);
        if (data != null) {
            //BLog.e("COMPOSE", " -- " + data.toString());
            //String launchUri = data.toString();
            Toast.makeText(this,"App Caught crash",Toast.LENGTH_LONG);

        }





        String locale = getResources().getConfiguration().locale.getDisplayCountry();
        //Log.e("LOCALE", "Main.onResume() : " + locale);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        //this.getActionBar().getCustomView().setFitsSystemWindows(true);
        if(isCreateStart) {
            //Log.e("MAIN", "CREATE START tested as TRUE !!");
            //if(Device.isMediaMounted()) {
                Bgo.openCurrentState(this);
            //} else {
            //    checkSdCard();
            //}

        } else if(isRestart) {
            //Log.e("MAIN", "RESTART tested as TRUE !!");
            //throw new NullPointerException("Manually created exception");
            //Bgo.openCurrentState(this);
        }
        isCreateStart=false;
        isRestart=false;


    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        //BLog.e("SAVE", "RESTORE instance state");
        isCreateStart=true;
        Bgo.clearBackStack(activity);
        State.sectionsClearBackstack();
        State.loadState(savedInstanceState);
        //isRestart=true;
        Device.hideKeyboard(this);
        //Device.updateRotation(this);


    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        //BLog.e("SAVE", "SAVE instance state");
        outState.clear();
        State.saveState(outState);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        SearchPacket packet = null;
        String gofilepath=null;

        //ActionModeBack.isActionModeShowing=false;
        //Log.e("Nav", "Nav drawer selection : " + position);
        /*
        if(!NavigationDrawerFragment.items.isEmpty()) {

            NavigationDrawerFragment.Holder navitem = NavigationDrawerFragment.items.get(position);
            //Log.e("Nav","Nav drawer selection : "+navitem.Rid);
            switch (navitem.Rid) {
                case R.drawable.nav_btn_txt:
                    //packet = new SearchPacket(Files.CAT_TEXTFILE, R.drawable.nav_btn_txt, activity.getString(R.string.text_files));
                    //State.addToState(State.SECTION_SEARCH_SHORTCUT, new StateObject(StateObject.STRING_BJSON_OBJECT, packet.toString()));
                    //BLog.e("gofilepath: " + gofilepath);
                    break;
            }
        }
        */
        //if(packet!=null)
        //    Bgo.openFragmentBackStack(activity, new ShortcutSearchFragment());


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Log.e("SELECT","on create options called");
        if (!mNavigationDrawerFragment.isDrawerOpen()) {

            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //BLog.e("onOptionsItemSelected called: "+item.getItemId());
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                if(mNavigationDrawerFragment!=null)
                    mNavigationDrawerFragment.openDrawer();
                return true;
            case R.id.action_search:
                Bgo.clearBackStack(activity);
                Bgo.openFragmentBackStack(activity,new SearchFragment());
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private long closebackpressed;
    private Handler closehandler;
    private Runnable closerun = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(activity, "Press back again to exit\nOr click the arrow to go up folder", Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    public void onBackPressed() {
        ActionBarManager.setStopOptions(true);
        //Log.e("BACK PRESS", "item: " + State.getSectionsSize() + " -- " + (State.getCurrentSection() != State.SECTION_FILE_EXPLORE));
        if(State.getSectionsSize()<2) {
            //if(State.getCurrentSection()!=State.SECTION_FILE_EXPLORE)
            //    Bgo.openFragment(this, new FileExploreFragment());
            //else {
            if(Cal.getUnixTime()-closebackpressed<700) {

                Bgo.clearBackStack(this);

                State.clearStateAllObjects();
                if(closehandler!=null)
                    closehandler.removeCallbacks(closerun);
                //BLog.e("EXIT", "APP");
                //this.get
                super.onBackPressed();
            } else{
                closebackpressed=Cal.getUnixTime();
                closehandler=new Handler();
                closehandler.postDelayed(closerun, 750);

            }
            //}
        } else {
            Bgo.goPreviousFragment(this);
        }

    }


}
