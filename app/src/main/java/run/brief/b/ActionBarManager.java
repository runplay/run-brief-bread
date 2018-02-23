package run.brief.b;


import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import run.brief.bread.R;




public class ActionBarManager {
	private static final ActionBarManager ACT=new ActionBarManager();

	private static Menu menuTabs;


    private View actionbarTitle;
    private TextView actionbarTitleText;
    Activity activity;
    private boolean stopOtions=false;

    public static boolean stopOptions() {
        if(ACT.stopOtions) {
            ACT.stopOtions=false;
            return true;
        }
        return false;
    }
    public static void setStopOptions(boolean stop) {
        ACT.stopOtions=stop;
    }

    public static void restart(Activity activity) {
        ACT.activity=activity;
        ACT.actionbarTitle=null;
        ACT.actionbarTitleText=null;
    }

    private static ColorDrawable getBackground(Activity activity,int Rcolor) {
        ColorDrawable cd= new ColorDrawable(activity.getResources().getColor(Rcolor));
        return cd;

    }



	public static Menu getMenu() {
		return menuTabs;
	}
	
	public class bMenuItem {
		int rMenu;
		String title;
		int STATE_SECTION_;
		Drawable img;
		String subtext;
		public bMenuItem(int rMenu, int STATE_SECTION_, String title, Drawable img, String subtext) {
			this.rMenu=rMenu; this.title=title; this.STATE_SECTION_=STATE_SECTION_; this.img=img; this.subtext=subtext;
		}
	}

	
	private int CURRENT=-1;
	
	public static int getCurrent() {
		return ACT.CURRENT;
	}


	public static void setActionBarBackOnly(Activity activity, String title, int R_MENU_ ,ActionModeBack amb) {
		setActionBarBackOnly(activity, title, R_MENU_, R.color.actionbar_basic,amb);
	}


    public static void setActionBarBackOnly(Activity activity, String title, int R_MENU_, int Rcolor,ActionModeBack amb) {
        setActionBarBackOnly(activity, title, R_MENU_,getBackground(activity, Rcolor),amb);
    }

    public static void hide(AppCompatActivity activity) {
        activity.getSupportActionBar().hide();
    }
    public static void show(AppCompatActivity activity) {
        activity.getSupportActionBar().show();
    }
    public static void setActionBarBackV19(AppCompatActivity activity,ActionModeBack amb) {
        ActionBar ab = activity.getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(false);
        ab.show();
    }


	private static void setActionBarBackOnly(Activity activity, String title, int R_MENU_, ColorDrawable color,ActionModeBack amb ) {
        ActionBar ab = ((AppCompatActivity) activity).getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(false);
        ab.show();

	}



	public static void setActionBarBackOnlyWithLogo(final Activity activity,int Rdrawable, String title, int R_MENU_, int Rcolor) {
		ACT.CURRENT= R_MENU_;
		final AppCompatActivity apact = (AppCompatActivity) activity;

		ActionBar actionBar = apact.getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        actionBar.setDisplayShowCustomEnabled(false);

        actionBar.setDisplayShowTitleEnabled(true);

        actionBar.setTitle(title);
        actionBar.setLogo(null);
        apact.supportInvalidateOptionsMenu();
        actionBar.invalidateOptionsMenu();
		actionBar.show();

	}


}
