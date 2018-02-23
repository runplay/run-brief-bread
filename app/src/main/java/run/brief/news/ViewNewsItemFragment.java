package run.brief.news;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import run.brief.b.ActionBarManager;
import run.brief.b.ActionModeBack;
import run.brief.b.ActionModeCallback;
import run.brief.b.B;
import run.brief.b.BCallbackInt;
import run.brief.b.BFragment;
import run.brief.b.BRefreshable;
import run.brief.b.Bgo;
import run.brief.b.Device;
import run.brief.b.State;
import run.brief.b.StateObject;
import run.brief.b.bButton;
import run.brief.beans.RssItem;
import run.brief.bread.R;
import run.brief.util.BriefActivityManager;
import run.brief.util.Cal;
import run.brief.util.Functions;
import run.brief.util.Sf;
import run.brief.util.UrlImage;

//import run.brief.BriefManager;


public class ViewNewsItemFragment extends BFragment implements BRefreshable,BCallbackInt {
	
	private View view;
	private static AppCompatActivity activity;
	private static RssItem newsitem;
	
	private TextView url;
	private TextView head;
	private TextView text;
	private UrlImage urlImage;
    private TextView mediaLink;
	private TextView publisher;
	private ProgressBar pb;

	private NewsWebView webview;
	private int loadedpct;
	private boolean stopCallback=false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		activity=(AppCompatActivity) getActivity();

		NewsItemsDb.init(activity);
		NewsFeedsDb.init(activity);

		view=inflater.inflate(R.layout.news_view_item,container, false);

		return view;
	}
	public void refreshData() {
		
	}
    @Override
    public void onPause() {
		super.onPause();
		stopCallback=true;

    }
	@Override
	public void refresh() {
		loadedpct=0;
		//super.refresh();
		//BriefManager.clearController(activity);
        if(State.hasStateObject(State.SECTION_NEWS_VIEW, StateObject.STRING_USE_DATABASE_ID)) {

            String id = State.getStateObjectString(State.SECTION_NEWS_VIEW, StateObject.STRING_USE_DATABASE_ID);
            //BLog.e("open","id: "+id);
            newsitem = NewsItemsDb.getById(Sf.toLong(id));

        } else if(State.hasStateObject(State.SECTION_NEWS_VIEW, StateObject.INT_USE_SELECTED_INDEX)) {
			int index = State.getStateObjectInt(State.SECTION_NEWS_VIEW, StateObject.INT_USE_SELECTED_INDEX);
			newsitem = NewsItemsDb.get(index);
			
		}
		view.setEnabled(true);
		

		
		String title=null;
		if(newsitem!=null)
			title=newsitem.getString(RssItem.STRING_PUBLISHER);
		else 
			title=activity.getResources().getString(R.string.news_title);

		amb = new ActionModeBack(activity, title
				,R.menu.basic
				, new ActionModeCallback() {
			@Override
			public void onActionMenuItem(ActionMode mode, MenuItem item) {
				onOptionsItemSelected(item);
			}
		});
		if(android.os.Build.VERSION.SDK_INT>= 19) {

			ActionBarManager.setActionBarBackV19(activity, amb);
			//setActionBarBackV19();
		} else {
			ActionBarManager.setActionBarBackOnly(activity, title, R.menu.basic,amb);
		}

		//ActionBarManager.setActionBarBackOnly(activity, title,R.menu.basic, R.color.brand);
		//Log.e("FRAG REFRESH","NewsHomeFragment");
		//ActionBarManager.setActionBarMenu(activity, R.menu.news, R.color.actionbar_news);
		bButton ba = (bButton) view.findViewById(R.id.news_btn_open);
		ba.setOnClickListener(openListner);
		bButton bas = (bButton) view.findViewById(R.id.news_btn_open_external);
		bas.setOnClickListener(openExternalListner);

        ImageView image = (ImageView) view.findViewById(R.id.news_item_image);
        head = (TextView)view.findViewById(R.id.news_item_head);
        head.setOnClickListener(copyListner);
        text = (TextView)view.findViewById(R.id.news_item_text);
        text.setOnClickListener(copyListner);
        TextView date = (TextView)view.findViewById(R.id.news_item_date);
        publisher = (TextView)view.findViewById(R.id.news_item_pub);
        url = (TextView)view.findViewById(R.id.news_item_url);
        url.setOnClickListener(copyUrlListner);

        mediaLink=(TextView) view.findViewById(R.id.news_media_url);

        //ImageView img = (ImageView) vi.findViewById(R.id.news_item_image);

        bButton emailbtn = (bButton) view.findViewById(R.id.news_btn_email);
        emailbtn.setOnClickListener(openEmailListner);


        B.addStyle(new TextView[]{text, date, publisher, url});
        B.addStyleBold(head, B.FONT_LARGE);
        //final RssItem t = NewsItemsDb.get(position);
 
        // Setting all values in listview
        if(newsitem!=null) {

			if(newsitem.getInt(RssItem.INT_READ)==0) {
				newsitem.setInt(RssItem.INT_READ,1);
				NewsItemsDb.getItemsDatabase().updateFavouriteRead(newsitem);
			}

        	//BLog.e("VNF", "is not NULL");
        	head.setText(newsitem.getString(RssItem.STRING_HEAD));
        	text.setText(newsitem.getString(RssItem.STRING_TEXT));
        	String imgurl=newsitem.getString(RssItem.STRING_IMG_URL);
        	publisher.setText(newsitem.getString(RssItem.STRING_PUBLISHER));
            String newsurl=newsitem.getString(RssItem.STRING_URL);
        	url.setText(newsurl);
        	String dte= Cal.getCal(new Date(newsitem.getLong(RssItem.LONG_DATE))).friendlyReadDate();
        	date.setText(dte);
        	if(!imgurl.isEmpty()) {
        		urlImage= new UrlImage();
        		Bitmap b  =urlImage.get(activity,imgurl,this.getClass());
        		if(b!=null) {
                    image.setVisibility(View.VISIBLE);
                    int h=140;
                    int w=200;
                    if(b.getWidth()>200) {

                        h= Double.valueOf(b.getHeight() / (b.getWidth() / 200)).intValue();

                        if(h<140)
                            h=140;
                        else if(h>200)
                            h=200;
                    }
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(b, w, h, false);
                    image.setImageBitmap(resizedBitmap);
        		} else {
                    image.setVisibility(View.GONE);
        			//text.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        			//img.setImageResource(R.drawable.content_picture);
        		}
        	} else {
                image.setVisibility(View.GONE);
        		//text.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        	}
            String mediaUrl=newsitem.getString(RssItem.STRING_MEDIA_URL);
            if(mediaUrl!=null && !mediaUrl.equals(newsurl)) {
                mediaLink.setText(mediaUrl);
                mediaLink.setOnClickListener(mediaListner);
                mediaLink.setVisibility(View.VISIBLE);
            } else {
                mediaLink.setVisibility(View.GONE);
            }

			String wvurl=State.getStateObjectString(State.SECTION_NEWS_VIEW, StateObject.STRING_VALUE);
			if(wvurl==null) {
				//if(!wvurl.equals(newsurl)) {
					webview = NewsWebViewManager.getWebView(activity, newsurl);
					if (!webview.isLoadedAlready()) {
						pb.setVisibility(View.VISIBLE);
						webview.loadUrl(newsurl, this);
					} else
						callback(100);

				//}
			}
			State.clearStateObject(State.SECTION_NEWS_VIEW, StateObject.STRING_VALUE);
        }

		State.clearStateObjects(State.SECTION_NEWS_VIEW);
	}

	public void callback(int value) {
		if(!stopCallback) {
			if (webview.isLoadedAlready() || value > 86) {
				State.addToState(State.SECTION_NEWS_WEBVIEW, new StateObject(StateObject.STRING_VALUE, url.getText().toString()));
				State.addToState(State.SECTION_NEWS_WEBVIEW, new StateObject(StateObject.STRING_ID, publisher.getText().toString()));
				Bgo.openFragmentBackStackAnimate(activity, new ViewNewsItemWebFragment());
				//RelativeLayout listtouch = (RelativeLayout) view.findViewById(R.id.list_touch);
				//listtouch.addView(webview);
			} else if (value == 0) {

			} else {
				pb.setVisibility(View.VISIBLE);
				pb.setProgress(value);
			}
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		stopCallback=false;
		pb = (ProgressBar) view.findViewById(R.id.story_load_progress);
		pb.setVisibility(View.GONE);
		State.setCurrentSection(State.SECTION_NEWS_VIEW);
		
		refresh();
	}

	protected OnClickListener openListner = new OnClickListener() {
		@Override
		public void onClick(View view) {
			if(newsitem!=null) {
				State.addToState(State.SECTION_NEWS_WEBVIEW,new StateObject(StateObject.STRING_VALUE,url.getText().toString()));
				State.addToState(State.SECTION_NEWS_WEBVIEW,new StateObject(StateObject.STRING_ID,publisher.getText().toString()));
				Bgo.openFragmentBackStackAnimate(activity,new ViewNewsItemWebFragment());
			}

		}
	};
	protected OnClickListener openExternalListner = new OnClickListener() {
		@Override
		public void onClick(View view) {
			if(newsitem!=null)
				BriefActivityManager.openAndroidBrowserUrl(activity, newsitem.getString(RssItem.STRING_URL));
		}
	};
    protected OnClickListener mediaListner = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if(newsitem!=null)
                BriefActivityManager.openAndroidBrowserUrl(activity, newsitem.getString(RssItem.STRING_MEDIA_URL));
        }
    };
    protected OnClickListener openEmailListner = new OnClickListener() {
        @Override
        public void onClick(View view) {
			if(newsitem!=null)
				BriefActivityManager.shareExternal(activity, newsitem.getString(RssItem.STRING_HEAD)+"\n"+newsitem.getString(RssItem.STRING_TEXT)+"\n\n"+newsitem.getString(RssItem.STRING_URL));
        }
    };

    protected OnClickListener copyListner = new OnClickListener() {
		@Override
		public void onClick(View view) {
			StringBuilder sb = new StringBuilder(head.getText().toString());
			sb.append("\n\n");
			sb.append(text.getText().toString());
			sb.append("\n\n");
			sb.append(newsitem.getString(RssItem.STRING_URL));
			Functions.copyToClipFlashView(activity, head);
			Functions.copyToClipFlashView(activity, text);
			Functions.copyToClipFlashView(activity, url);
			
			Device.copyToClipboard(activity, sb.toString());
			Toast.makeText(activity, R.string.copied_to_clip, Toast.LENGTH_SHORT).show();
		}
	};	
	protected OnClickListener copyUrlListner = new OnClickListener() {
		@Override
		public void onClick(View view) {
			Device.copyToClipboardFlashView(activity, view, url.getText().toString());
			Toast.makeText(activity, R.string.copied_to_clip, Toast.LENGTH_SHORT).show();
		}
	};	
}
