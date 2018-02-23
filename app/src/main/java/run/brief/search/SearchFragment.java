package run.brief.search;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import run.brief.b.ActionBarManager;
import run.brief.b.ActionModeBack;
import run.brief.b.ActionModeCallback;
import run.brief.b.BCallback;
import run.brief.b.BFragment;
import run.brief.b.BRefreshable;
import run.brief.b.Bgo;
import run.brief.b.Device;
import run.brief.b.State;
import run.brief.b.StateObject;
import run.brief.beans.RssItem;
import run.brief.bread.R;
import run.brief.news.ViewNewsItemFragment;


public class SearchFragment extends BFragment implements BRefreshable {
	private View view;

	private AppCompatActivity activity=null;
	private EditText searchText;
	private static SearchAdapter adapter;
	private ListView list;
	View updating;
	View start;

    @Override
    public void onPause() {

		amb.done();

		super.onPause();
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		activity=(AppCompatActivity) getActivity();
		view=inflater.inflate(R.layout.search,container, false);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		State.setCurrentSection(State.SECTION_SEARCH);
		start = (View) view.findViewById(R.id.search_start);
		updating = (View) view.findViewById(R.id.search_updating);
		

        list=(ListView) activity.findViewById(R.id.search_list);
		amb = new ActionModeBack(activity, activity.getResources().getString(R.string.label_search)
				,R.menu.search
				, new ActionModeCallback() {
			@Override
			public void onActionMenuItem(ActionMode mode, MenuItem item) {

			}
		});
        if(android.os.Build.VERSION.SDK_INT>= 19) {

            ActionBarManager.setActionBarBackV19(activity, amb);
            //setActionBarBackV19();
        } else {
            ActionBarManager.setActionBarBackOnly(activity, activity.getResources().getString(R.string.label_search), R.menu.search,amb);
        }
		refresh();
		

	}
	public void refreshData() {
		
	}
	public void refresh() {


		adapter=new SearchAdapter(getActivity());
        list.setAdapter(adapter);
        list.setOnItemClickListener(newitemListner);
			
        run.brief.b.bButton searchnow=(run.brief.b.bButton) activity.findViewById(R.id.search_btn);
        searchnow.setClickable(true);
        searchnow.setOnClickListener(newSearchListener);
        
		searchText=(EditText) activity.findViewById(R.id.search_text);

		if(State.getStateObjectInt(State.SECTION_SEARCH, StateObject.INT_MODE)==0)
			Device.setKeyboard(activity, searchText, true);
		State.clearStateObject(State.SECTION_SEARCH, StateObject.INT_MODE);
	}
	private BCallback searchCallback = new BCallback() {
		@Override
		public void callback() {
			// do it
			updating.setVisibility(View.GONE);
			refresh();
		}
	};




	public OnClickListener newSearchListener = new OnClickListener() {
		@Override
		public void onClick(View view) {

			//Log.e("SEARCH","SEARCH FOR: "+searchText.getText().toString());
			start.setVisibility(View.GONE);
			updating.setVisibility(View.VISIBLE);
			Searcher.doSearch(activity, searchText.getText().toString(), searchCallback);

			SearchHistory.add(new SearchPacket(0, 0, searchText.getText().toString()));
			SearchHistory.Save();

			list.setAdapter(new SearchAdapter(getActivity()));
			Device.hideKeyboard(activity);
		}
	};
    public OnItemClickListener newitemListner = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			State.clearStateObjects(State.SECTION_NEWS_VIEW);
			RssItem t = (RssItem) adapter.getItem(position);
			//BLog.e("bhbh",""+t.getInt(RssItem.INT_ID));
			StateObject sob = new StateObject(StateObject.STRING_USE_DATABASE_ID,""+t.getLong(RssItem.LONG_ID));
			State.addToState(State.SECTION_NEWS_VIEW, sob);
			State.addToState(State.SECTION_SEARCH, new StateObject(StateObject.INT_MODE,1));
			Bgo.openFragmentBackStackAnimate(activity, new ViewNewsItemFragment());

		}
	};
			/*
            IndexerFile ind =Searcher.getResults().get(position);

            if(ind!=null) {
                File f = ind.getAsFileItem().getAbsoluteFile();
                if(f!=null && f.isFile()) {
					amb.done();
                    if(Files.isImage(f.getName())) {
						FileManagerList fml = new FileManagerList(Searcher.getResultsFileItems());
						fml.setStartAtPosition(position);
						State.addCachedFileManager(fml);
                        Bgo.openFragmentBackStack(activity, new ImagesSliderFragment());

                    } else if(Files.isTextFile(f.getName())) {

						State.addToState(State.SECTION_TEXT_FILE_VIEW,new StateObject(StateObject.STRING_FILE_PATH,f.getAbsolutePath()));
						Bgo.openFragmentBackStack(activity,new TextFileFragment());

					} else {
						//openOptions(f.getAbsolutePath());
						if(Files.removeBriefFileExtension(f.getName()).endsWith(".zip")) {

							FileManagerZip fmz = new FileManagerZip(f.getAbsolutePath());
							State.addCachedFileManager(fmz);
							Bgo.openFragmentBackStack(activity, new ZipExploreFragment());

						} else {
							Device.openAndroidFile(activity, f);
						}
					}
                }
            }
*/


	@Override
	public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {

		MenuInflater minflater = activity.getMenuInflater();
		minflater.inflate(R.menu.basic, menu);

		//searchView.setSearchableInfo(
		//		searchManager.getSearchableInfo(activity.getComponentName()));


	}
}
