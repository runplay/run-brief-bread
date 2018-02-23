package run.brief.news;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;

import run.brief.b.BRefreshable;
import run.brief.b.Bgo;
import run.brief.b.Device;
import run.brief.beans.RssItem;
import run.brief.util.Cal;
import run.brief.util.log.BLog;


public class NewsDialog extends Dialog {
	private Dialog thisDialog;
	private RssItem usenews;
	private Activity activity;
	public static boolean shouldRefresh=false;
	private BRefreshable refreshFragment;
	public RssItem getUseNews() {
		return usenews;
	}
	public NewsDialog(Activity activity,RssItem usenews, BRefreshable refreshFragment) {
		super(activity);
		this.refreshFragment=refreshFragment;
		
		this.usenews = usenews;
		
		this.activity=activity;

		/*
		this.setContentView(R.layout.news_dialog);
		this.setTitle(activity.getResources().getString(R.string.news_menu_options)); 
		Button bim = (Button) this.findViewById(R.id.dialog_cancel);
		bim.setOnClickListener(onCloseClick);
		Button del = (Button) this.findViewById(R.id.note_dialog_delete);
		del.setOnClickListener(onDeleteClick);
		Button email = (Button) this.findViewById(R.id.note_dialog_send_email);
		email.setOnClickListener(onSendEmailClick);
		thisDialog=this;
		this.setOnDismissListener(onDismiss);
		
		Button copy = (Button) this.findViewById(R.id.dialog_copy);
		copy.setOnClickListener(onCopyClick);
		*/
		//context.getMenuInflater().inflate(R.menu.notes_home_popup, popupMenu.getMenu());
	}
	public Button.OnClickListener onSendEmailClick = new Button.OnClickListener() {
		@Override
		public void onClick(View view) {
			

		}
	};
	public Button.OnClickListener onDeleteClick = new Button.OnClickListener() {
		@Override
		public void onClick(View view) {

			NewsItemsDb.remove(usenews);
			shouldRefresh=true;
			//BriefManager.setDirty(BriefManager.IS_DIRTY_NEWS);
			thisDialog.dismiss();
		}
	};
	public OnDismissListener onDismiss = new OnDismissListener() {
		@Override
		public void onDismiss(DialogInterface intf) {
			BLog.e("DISMISS", "Called");
			Bgo.refreshCurrentFragment(activity);
		}
	};
	public Button.OnClickListener onCloseClick = new Button.OnClickListener() {
		@Override
		public void onClick(View view) {
			shouldRefresh=false;
			thisDialog.dismiss();
		}
	};

	public Button.OnClickListener onCopyClick = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			
			StringBuilder sb = new StringBuilder((new Cal(usenews.getLong(RssItem.LONG_DATE)).getDatabaseDate()));
		    sb.append("\n");
		    sb.append(usenews.getString(RssItem.STRING_URL));
		    sb.append("\n\n");
		    sb.append(usenews.getString(RssItem.STRING_HEAD));
			sb.append("\n");
		    sb.append(usenews.getString(RssItem.STRING_TEXT));
		    sb.append("\n\n");
		    Device.copyToClipboard(activity, sb.toString());
			shouldRefresh=false;
		    thisDialog.dismiss();
		    
		}
		
	};
}
