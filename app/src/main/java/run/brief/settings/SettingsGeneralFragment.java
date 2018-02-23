package run.brief.settings;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;

import run.brief.b.BRefreshable;
import run.brief.b.State;
import run.brief.beans.BriefSettings;
import run.brief.bread.R;
import run.brief.news.NewsItemsDb;
import run.brief.util.BriefActivityManager;
import run.brief.util.Files;


public class SettingsGeneralFragment extends Fragment implements BRefreshable {
	private View view;
	private View darkview;
	private View lightview;

	private NumberPicker npStories;
	private NumberPicker npImages;

	private long countStories;
	private long sizeondiskStories;
	private long countImages;
	private BriefSettings bset;

	//private RadioButton pasterename;
	//private RadioButton pasteover;

    //public static final int RESULTCODE_SMS = 0;
	//private CheckBox smscheck;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view=inflater.inflate(R.layout.settings,container, false);

		return view;

	}

	@Override
	public void onResume() {
		super.onResume();

		darkview=(View) view.findViewById(R.id.settings_theme_dark);
		darkview.setOnClickListener(goDarkTheme);
		lightview=(View) view.findViewById(R.id.settings_theme_light);
		lightview.setOnClickListener(goLightTheme);

		//pasterename = (RadioButton) view.findViewById(R.id.settings_paste_rename);
        //pasterename.setOnClickListener(onRadioClicked);
		//pasteover= (RadioButton) view.findViewById(R.id.settings_paste_overwrite);
        //pasteover.setOnClickListener(onRadioClicked);

		bset = State.getSettings();

		npStories=(NumberPicker) view.findViewById(R.id.numberPickerStories);

		npStories.setMaxValue(360);
		npStories.setMinValue(1);

		npStories.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				bset.setInt(BriefSettings.INT_NEWS_DAYS_DELETE_STORIES, newVal);
				//bset.save();

			}
		});
		npStories.setValue(bset.getInt(BriefSettings.INT_NEWS_DAYS_DELETE_STORIES));

		npImages = (NumberPicker) view.findViewById(R.id.numberPickerImages);
		npImages.setMaxValue(60);
		npImages.setMinValue(1);

		npImages.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

				bset.setInt(BriefSettings.INT_NEWS_DAYS_DELETE_IMAGES, newVal);
				//bset.save();

			}
		});
		npImages.setValue(bset.getInt(BriefSettings.INT_NEWS_DAYS_DELETE_IMAGES));

		new SyncDataTask().execute(true);
		refresh();

        //}
	}
	@Override
	public void onPause() {
		super.onPause();
		//BriefSettings bset = State.getSettings();
		bset.save();
	}
	@Override
	public void refresh() {
		TextView tstories = (TextView) view.findViewById(R.id.settings_stories_count);
		TextView timages = (TextView) view.findViewById(R.id.settings_images_count);
		DecimalFormat df = new DecimalFormat( "#0.00" );
		tstories.setText(getActivity().getString(R.string.settings_archive_stories_count)+" ("+countStories+") ~"+df.format((sizeondiskStories/1024D)/1024)+"MB");
		timages.setText(getActivity().getString(R.string.settings_archive_images_count) + " (" + countImages + ")");

		CheckBox disableJs=(CheckBox) view.findViewById(R.id.disable_javascript);
		disableJs.setOnClickListener(disableJsListner);

		if(State.getSettings().getBoolean(BriefSettings.BOOL_WEBVIEW_DISABLE_JAVASCRIPT))
			disableJs.setChecked(true);
		else
			disableJs.setChecked(false);
		//BLog.e("CALL","settings general");
		//ActionBarManager.setActionBarBackOnlyWithLogo(getActivity(), R.drawable.icon_settings, getActivity().getResources().getString(R.string.action_settings), R.menu.settings, R.color.actionbar_general);
	}	
	public void refreshData() {
		
	}

	private class SyncDataTask extends AsyncTask<Boolean, Void, Boolean> {

		//Twitter mTwitter = new TwitterFactory().getInstance();
		@Override
		protected Boolean doInBackground(Boolean... params) {

			countStories=NewsItemsDb.getRowsCount();
			//refreshNews(getBaseContext(),false);
			StringBuilder dir = new StringBuilder(Files.HOME_PATH_FILES);
			dir.append(File.separator);
			dir.append(Files.FOLDER_IMAGES);
			countImages= Files.countFilesInPath(dir.toString());

			sizeondiskStories=NewsItemsDb.getSizeOnDisk();
			return true;

		}

		@Override
		protected void onPostExecute(Boolean result) {
			if(getActivity()!=null) {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						refresh();
					}
				});
			}
		}


	}
	public OnClickListener goDarkTheme = new OnClickListener() {
		@Override
		public void onClick(View view) {
		    boolean restart=false;
		    BriefSettings settings = State.getSettings();
			if(settings.getBoolean(BriefSettings.BOOL_STYLE_DARK)==Boolean.FALSE) {
				settings.setBoolean(BriefSettings.BOOL_STYLE_DARK, Boolean.TRUE);
				restart=true;
			}
		    settings.save();
		    State.setSettings(settings);
			if(restart)
				BriefActivityManager.closeAndRestartBrief(getActivity());
		}
	};
	public OnClickListener goLightTheme = new OnClickListener() {
		@Override
		public void onClick(View view) {
			boolean restart=false;
			BriefSettings settings = State.getSettings();
			if(settings.getBoolean(BriefSettings.BOOL_STYLE_DARK)==Boolean.TRUE) {
				settings.setBoolean(BriefSettings.BOOL_STYLE_DARK, Boolean.FALSE);
				restart=true;
			}
			settings.save();
			State.setSettings(settings);
			if(restart)
				BriefActivityManager.closeAndRestartBrief(getActivity());

		}
	};
	public OnClickListener disableJsListner = new OnClickListener() {
		@Override
		public void onClick(View view) {
			CheckBox v = (CheckBox) view;
			//boolean restart=false;
			BriefSettings settings = State.getSettings();

			settings.setBoolean(BriefSettings.BOOL_WEBVIEW_DISABLE_JAVASCRIPT, v.isChecked());

			settings.save();
			State.setSettings(settings);


		}
	};
/*
	public OnClickListener onRadioClicked = new OnClickListener() {
		@Override
		public void onClick(View view) {
			boolean checked = ((RadioButton) view).isChecked();
			BriefSettings settings = State.getSettings();
			// Check which radio button was clicked
			switch(view.getId()) {
				case R.id.settings_paste_rename:
					if (checked) {
						//settings.setBoolean(BriefSettings.);
					}
					break;
				case R.id.settings_paste_overwrite:
					if (checked) {

					}
					break;
				default:
					if(checked) {

					}
					break;
			}
			settings.save();
			State.setSettings(settings);

		}
	};

*/


}
