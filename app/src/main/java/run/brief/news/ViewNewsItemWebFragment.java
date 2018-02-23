package run.brief.news;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import run.brief.b.ActionBarManager;
import run.brief.b.ActionModeBack;
import run.brief.b.ActionModeCallback;
import run.brief.b.BFragment;
import run.brief.b.BRefreshable;
import run.brief.b.Bgo;
import run.brief.b.State;
import run.brief.b.StateObject;
import run.brief.bread.R;
import run.brief.util.log.BLog;

//import run.brief.BriefManager;


public class ViewNewsItemWebFragment extends BFragment implements BRefreshable {
	
	private View view;
	private static AppCompatActivity activity;

	private RelativeLayout listtouch;
	private NewsWebView webview;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		activity=(AppCompatActivity) getActivity();

		view=inflater.inflate(R.layout.news_view_item_web,container, false);

		return view;
	}
	public void refreshData() {
		
	}
    @Override
    public void onPause() {
        super.onPause();
		if(webview!=null)
			webview.onPause();
		listtouch.removeAllViews();
		if(activity.getSupportActionBar()!=null && !activity.getSupportActionBar().isShowing())
			activity.getSupportActionBar().show();
    }
	@Override
	public void refresh() {
		BLog.e("webview frag on refresh()");


	}

	@Override
	public void onResume() {
		super.onResume();
//BLog.e("webview frag on resume()");
		//activity.getSupportActionBar().hide();
		amb = new ActionModeBack(activity, State.getStateObjectString(State.SECTION_NEWS_WEBVIEW,StateObject.STRING_ID)
				,R.menu.news_web
				, new ActionModeCallback() {
			@Override
			public void onActionMenuItem(ActionMode mode, MenuItem item) {
				if(webview!=null) {
					webview.setIsClosed(true);;
					webview.loadData("<html><body style=\"background:#fff;padding-top:40px;text-align:center;font-size:150%\">" + activity.getString(R.string.label_working) + "</body>", "text/html", "UTF-8");
				}
				onOptionsItemSelected(item);

			}
		});
		if(android.os.Build.VERSION.SDK_INT>= 19) {

			ActionBarManager.setActionBarBackV19(activity, amb);
			//setActionBarBackV19();
		} else {
			ActionBarManager.setActionBarBackOnly(activity, State.getStateObjectString(State.SECTION_NEWS_WEBVIEW, StateObject.STRING_ID), R.menu.news_web,amb);
		}

		State.setCurrentSection(State.SECTION_NEWS_WEBVIEW);

		String url = State.getStateObjectString(State.SECTION_NEWS_WEBVIEW, StateObject.STRING_VALUE);

		webview = NewsWebViewManager.getWebView(activity, url);
		if(webview.isLoadedAlready()) {
			//BLog.e("configure webv");
			webview.configure();

		}
		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
		webview.setLayoutParams(rlp);

		listtouch = (RelativeLayout) view;
		listtouch.removeAllViews();
		listtouch.addView(webview);

		RelativeLayout frame = new RelativeLayout(activity);
		frame.setVisibility(View.GONE);
		frame.setLayoutParams(rlp);
		frame.setId(R.id.customViewContainer);



		listtouch.addView(frame);
		webview.setCustomViewContainer(activity,frame);

		State.addToState(State.SECTION_NEWS_VIEW, new StateObject(StateObject.STRING_VALUE, "opened"));
		refresh();
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		boolean intercept=false;
		switch(item.getItemId()) {
			case R.id.action_back_back:
				//State.addCachedFileManager(fm);
				//callrefresh=false;
				Bgo.clearBackStack(activity);
				Bgo.openFragment(activity,NewsHomeFragment.class);
				//Bgo.openFragmentBackStack(activity, new SearchFragment());
				intercept=true;
				break;


		}

		return intercept;
	}

}
