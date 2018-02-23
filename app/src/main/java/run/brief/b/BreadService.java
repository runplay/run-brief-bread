package run.brief.b;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

import run.brief.beans.BriefSettings;
import run.brief.beans.RssUserFeed;
import run.brief.news.DoLoadForUserFeed;
import run.brief.news.NewsFeedsDb;
import run.brief.news.NewsHomeFragment;
import run.brief.news.NewsItemsDb;
import run.brief.secure.HomeFarm;
import run.brief.settings.SettingsDb;
import run.brief.util.Cal;
import run.brief.util.Files;
import run.brief.util.log.BLog;


public final class BreadService extends Service {
	
	private static BreadService SERVICE;//=new BrowseService();

    private SyncDataThread syncDataThread;
    private Handler syncDataHandler = new Handler();

    private static OnUserPresentReceiver ubreceiver;

    private static FileObserver fileObserver;
    private static final long MILLIS_SYNC_DATA = 120000; // every 5 mins

    private static boolean isAppStarted=false;

    public static void setIsAppStarted(boolean isStarted) {
        isAppStarted=isStarted;
    }
    public static boolean isAppStarted() {
        return isAppStarted;
    }

    public class LocalBinder extends Binder {
        public BreadService getService() {
            return BreadService.this;
        }
    }

    @Override
    public void onCreate() {
    	super.onCreate();
        //mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

    }
    public static void ensureStartups(Activity activity) {
        HomeFarm.init(activity);
        Bgo.setUseActivity(activity);
        Files.setAppHomePath(activity);
        SettingsDb.init();
        State.setSettings(SettingsDb.getSettings());
        Device.init(activity);
    }
    public static void ensureStartups(Context context) {
        HomeFarm.init(context);
        Files.setAppHomePath(context);
        SettingsDb.init();
        State.setSettings(SettingsDb.getSettings());
        Device.init(context);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
    	if(SERVICE==null) {
    		SERVICE=this;
    	//doFirstTimeCheck();
    		


            //BLog.e("SERVICE", "started");
            ensureStartups(getBaseContext());

            IntentFilter ubfilter = new IntentFilter(Intent.ACTION_USER_PRESENT);
            ubreceiver=new OnUserPresentReceiver();
            registerReceiver(ubreceiver, ubfilter);



            startRegularRefresh();

    	}

    	return START_STICKY;
    }


    
    @Override
    public void onDestroy() {
        super.onDestroy();
    	BLog.e("SERVICE", "Service stopped");


        unregisterReceiver(ubreceiver);
        //unregisterReceiver(ubackreceiver);

        fileObserver.stopWatching();
        SERVICE=null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //return mBinder;
        return null;
    }

    public static boolean isServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            //Log.e("SERV",service.service.getClassName());
            if (BreadService.class.getName().equals(service.service.getClassName())) {
            	//BLog.e("TESTSERVICE","BRIEF SERVICE TEST = IS NOT RUNNING");
                return true;
            }
        }
        return false;
    }
    private class OnUserPresentReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            synchronized(this) {
                //BLog.e("onReceive() ");
                BriefSettings settings = State.getSettings();
                if(intent.getAction().equals(Intent.ACTION_USER_BACKGROUND)) {
                    //BLog.e("ACTION_USER_BACKGROUND ");
                } else if(intent.getAction().equals(Intent.ACTION_USER_FOREGROUND)) {
                    //BLog.e("ACTION_USER_FOREGROUND ");
                } else {
                   // BLog.e("ACTION_USER_PRESENT ");

                }


            }

        }
    }

    public static void startRegularRefresh() {
        if(SERVICE.syncDataHandler!=null)
            SERVICE.syncDataHandler.removeCallbacks(SERVICE.syncDataThread);
        if(SERVICE.syncDataThread==null) {
            SERVICE.syncDataThread=SERVICE.new SyncDataThread();
            //SERVICE.syncDataThread.run();
        }
        if(!SERVICE.isrefreshing) {
            ensureStartups(SERVICE);
            BriefSettings bset = State.getSettings();

            if(!bset.getBoolean(BriefSettings.BOOL_NEWS_MANUAL_REFRESH)) {
                //BLog.e("SERVICE", "start syncDataHandler called");
                SERVICE.syncDataHandler.postDelayed(SERVICE.syncDataThread, 10000);
            } else {
                //BLog.e("SERVICE", "NOT-start syncDataHandler called");
            }

        }

    }


    private static void checkArchiveDelete() {
        BriefSettings bset = State.getSettings();
        long last = bset.getLong(BriefSettings.LONG_LAST_24HR_ARCHIVE_DELETE);
        if(last==0 || last + Cal.HOURS_24_IN_MILLIS<Cal.getUnixTime()) {
            long arcdate = bset.getLong(BriefSettings.INT_NEWS_DAYS_DELETE_IMAGES);
            arcdate = (arcdate*Cal.HOURS_24_IN_MILLIS);
            StringBuilder dir = new StringBuilder(Files.HOME_PATH_FILES);
            dir.append(File.separator);
            dir.append(Files.FOLDER_IMAGES);
            File[] images = Files.getFiles(dir.toString());
            if(images!=null && images.length>0) {
                for(File f: images) {
                    if(f.lastModified()<Cal.getUnixTime()-arcdate) {
                        BLog.e("deletefile: "+f.getName());
                        f.delete();
                    }
                }
            }
            long arcnews = bset.getLong(BriefSettings.INT_NEWS_DAYS_DELETE_STORIES);
            arcnews = (arcnews*Cal.HOURS_24_IN_MILLIS);
            NewsItemsDb.getItemsDatabase().deleteOlderThan(Cal.getUnixTime()-arcdate);
            bset.setLong(BriefSettings.LONG_LAST_24HR_ARCHIVE_DELETE,last+Cal.HOURS_24_IN_MILLIS);
            bset.save();
        }

    }

    private boolean isrefreshing=false;

    private class SyncDataThread  implements Runnable {
        @Override
        public void run() {
            synchronized (this) {
                if (!isrefreshing) {

                    isrefreshing = true;
                    new SyncDataTask().execute(false);
                }
            }
        }

    }
    private class SyncDataTask extends AsyncTask<Boolean, Void, Boolean> {

        //Twitter mTwitter = new TwitterFactory().getInstance();
        @Override
        protected Boolean doInBackground(Boolean... params) {
            ensureStartups(SERVICE);
            //BriefSettings bset = State.getSettings();
            refreshNews(getBaseContext(), false);
            checkArchiveDelete();
            return true;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            isrefreshing=false;
            SERVICE.syncDataHandler.removeCallbacks(SERVICE.syncDataThread);
            SERVICE.syncDataHandler.postDelayed(SERVICE.syncDataThread, MILLIS_SYNC_DATA);

        }


    }


    private boolean isNewsRefreshing=false;
    private static final String syncNewsOnID = "sync.new.str.82519809332";
    private Activity refreshNewNowActivity;
    private NewsNowTask newsNowTask;

    public static boolean isNewsRefreshing() {
        return SERVICE.isNewsRefreshing;
    }
    public static void refrehNewsNow(Activity activity, boolean force) {
        SERVICE.refreshNewNowActivity=activity;
        SERVICE.newsNowTask = SERVICE.new NewsNowTask(force);
        SERVICE.newsNowTask.execute(true);

    }
    private class NewsNowTask extends AsyncTask<Boolean, Void, Boolean> {
        private boolean force;
        public NewsNowTask(boolean force) {
            this.force=force;
        }
        @Override
        protected Boolean doInBackground(Boolean... params) {
            refreshNews(SERVICE.refreshNewNowActivity,force);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Bgo.refreshCurrentIfFragment(SERVICE.refreshNewNowActivity, NewsHomeFragment.class);
        }
    }
    private static void refreshNews(Activity context,boolean force) {

    }
    private static void refreshNews(Context context,boolean force) {
        //int totalNew=0;
        if(!SERVICE.isNewsRefreshing) {
            synchronized (syncNewsOnID) {
                SERVICE.isNewsRefreshing = true;
                NewsFeedsDb.init(context);
                NewsItemsDb.init(context);
                HashMap<String, RssUserFeed> feeds = NewsFeedsDb.getUserFeeds();
                Set<String> keys = feeds.keySet();

                boolean hasUpdates = false;
                for (String key : keys) {
                    RssUserFeed feed = feeds.get(key);
                    if (feed != null) {
                        long lastUpdate = feed.getLong(RssUserFeed.LONG_LAST_UPDATE);
                        int collect_ = feed.getInt(RssUserFeed.INT_COLLECT_);
                        int active = feed.getInt(RssUserFeed.INT_ACTIVE);
                        long now = Cal.getUnixTime();

                        if (active != 0) {
                            long refreshTime = refreshNewsCollectTime(collect_);

                            if ( (force &&  (lastUpdate + (Cal.MINUTES_1_IN_MILLIS*5)) < now)
                                    || (lastUpdate + refreshTime) < now) {
                                if(feed.getInt(RssUserFeed.INT_ERROR_COLLECT_COUNT)<5) {
                                    // ok refresh news feed
                                    //BLog.e("AUTO.REFRESH collect: " + lastUpdate + "," + refreshTime + "," + now + " -- for: " + feed.getString(RssUserFeed.STRING_URL));
                                    hasUpdates = true;
                                    feed.setLong(RssUserFeed.LONG_LAST_UPDATE, Cal.getUnixTime());
                                    NewsFeedsDb.updateUserFeed(feed);
                                    new DoLoadForUserFeed(context).execute(feed);
                                }
                                //BriefManager.setDirty(BriefManager.IS_DIRTY_NEWS);

                            }

                        }

                    }
                }
                SERVICE.isNewsRefreshing = false;
            }
        }
    }
    private static long refreshNewsCollectTime(int COLLECT_) {
        switch(COLLECT_) {
            case RssUserFeed.COLLECT_SLOW:
                return RssUserFeed.TIME_SLOW_MILLIS;
            case RssUserFeed.COLLECT_FAST:
                return RssUserFeed.TIME_FAST_MILLIS;
            default:
                return RssUserFeed.TIME_MEDIUM_MILLIS;
        }
    }
}
