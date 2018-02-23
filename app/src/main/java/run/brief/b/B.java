
package run.brief.b;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Method;

import run.brief.beans.BriefSettings;
import run.brief.bread.R;
import run.brief.util.JSONUrlReader;
import run.brief.util.UrlStore;
import run.brief.util.json.JSONObject;
import run.brief.util.log.BLog;

public final class B {

    public static final B THEME = new B();

    public static final int ACTIVITY_RESULT_SHOW_IMAGE_SLIDER=324452;

    private Activity activity;

    public static final boolean LIVE_MODE = true;
    public static final boolean DEBUG = true;

    public static final String NAME="Brief";
    
    private static final FontSizes fontSizes = new FontSizes();

    public static double FONT_MEDIUM=1.5D;
    public static double FONT_LARGE=1.4D;
    public static double FONT_XLARGE=1.3D;
    public static double FONT_SMALL=2.3D;

    private Typeface typeface;
    private Typeface typefaceBold;
    private Bitmap resizedBitmap;
    private float defaultTextSize;

    public static void resetDefaultTextSize() {
        THEME.defaultTextSize=0;
    }

    private static RelativeLayout gotopView;
    private static GridView gotopList;
    private static Handler gotopHandler=new Handler();
    private static boolean isGotopOpen=false;

    public static final int APP_STAGE_FIRST_TIME=0;
    public static final int APP_STAGE_UNREGISTERED=1;
    public static final int APP_STAGE_REGISTERED=2;

    public static int getAppStage() {

        //File pemfile = new File(Files.HOME_PATH_APP+File.separator+)

        return APP_STAGE_FIRST_TIME;
    }

    public static void addGoTopTracker(Activity activity, GridView list) {
        addGoTopTracker(activity,list,R.drawable.gt_brief);
    }
    public static void addGoTopTracker(Activity activity, GridView list, int Rdrawable) {
        THEME.activity=activity;
        //int listYPos=list.getY
        gotopList=list;
        gotopView=(RelativeLayout) activity.findViewById(R.id.main_gotop);
        if(gotopView!=null) {
            gotopView.setGravity(Gravity.CENTER);
            gotopView.setBackgroundColor(activity.getResources().getColor(R.color.white_alpha));
            View topbtn = activity.findViewById(R.id.main_gotop_btn);
            topbtn.setBackground(activity.getResources().getDrawable(Rdrawable));
            topbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gotopList.setSelection(0);
                    isGotopOpen=false;
                    Animation animation = AnimationUtils.loadAnimation(THEME.activity, R.anim.slide_out_to_top);
                    gotopView.setAnimation(animation);
                    gotopView.startAnimation(animation);
                }
            });
            //gotopView.addView(topbtn);
            gotopView.setVisibility(View.GONE);
            gotopHandler.postDelayed(gotopRunner, 50);
            //list.addHeaderView(activity.getLayoutInflater().inflate(R.layout.wait, null));
        }
    }
    public static void removeGoTopTracker() {
        gotopHandler.removeCallbacks(gotopRunner);
        if(isGotopOpen) {
            isGotopOpen=false;
            //Animation animation = AnimationUtils.loadAnimation(BM.activity, R.anim.slide_out_to_top);
            gotopView.setVisibility(View.GONE);
            //gotopView.startAnimation(animation);
        }
    }
    private static Runnable gotopRunner = new Runnable() {
        @Override
        public void run() {

            int pos = gotopList.getFirstVisiblePosition();
            if(pos>3) {
                //BLog.e("GT-TRACK","should be open");
                if(!isGotopOpen) {
                    isGotopOpen=true;
                    gotopView.setVisibility(View.VISIBLE);
                    gotopView.bringToFront();
                    Animation animation = AnimationUtils.loadAnimation(THEME.activity, R.anim.slide_in_from_top);
                    gotopView.setAnimation(animation);
                    gotopView.startAnimation(animation);
                    //BLog.e("GT-TRACK", "------ opened");
                }
            } else {
                //BLog.e("GT-TRACK","should be closed: "+pos);
                if(isGotopOpen) {
                    isGotopOpen=false;
                    Animation animation = AnimationUtils.loadAnimation(THEME.activity, R.anim.slide_out_to_top);
                    gotopView.setAnimation(animation);
                    gotopView.startAnimation(animation);
                } else {
                    gotopView.setVisibility(View.GONE);
                }
            }
            gotopHandler.postDelayed(gotopRunner,300);
        }
    };

    public static AlphaAnimation animateAlphaFlash() {
        AlphaAnimation animation1 = new AlphaAnimation(0.2f, 1.0f);
        animation1.setDuration(200);
        animation1.setStartOffset(50);
        animation1.setFillAfter(true);
        return animation1;
    }


    public static Method getMethod(Class<?> classObject, String methodName) {
        try {
            return classObject.getMethod(methodName, boolean.class);
        } catch (NoSuchMethodException e) {
            //Log.i(B.LOG_TAG, "Can't get method " +
            //      classObject.toString() + "." + methodName);
        } catch (Exception e) {
            //BLog.add("B() Error while using reflection to get method " + classObject.toString() + "." + methodName, e);
        }
        return null;
    }

    public static FontSizes getFontSizes() {
        return fontSizes;
    }
    public static void fixDrawableLevels(TextView textview) {
    	// Fix level of existing drawables
    	Drawable[] drawables = textview.getCompoundDrawables();
    	for (Drawable d : drawables) if (d != null && d instanceof ScaleDrawable) d.setLevel(1);
    	textview.setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
    }
    public static void addStyleBold(EditText editText) {
        if(editText!=null) {
            editText.setTypeface(THEME.typefaceBold);
            //textView.setTextAppearance();
        }
    }
    public static float getSetDefaultTextSize(TextView refrenceTextView) {
        if(THEME.defaultTextSize==0) {
            BriefSettings set = State.getSettings();
            if(!set.has(BriefSettings.STRING_FLOAT_DEF_FONT_SIZE)) {
                set.setString(BriefSettings.STRING_FLOAT_DEF_FONT_SIZE,Float.toString(refrenceTextView.getTextSize()));
                set.save();

            }
            THEME.defaultTextSize= Float.valueOf(set.getString(BriefSettings.STRING_FLOAT_DEF_FONT_SIZE));

        }
        return THEME.defaultTextSize;
    }
    public static boolean forceTryConnection(Context context) {
        //boolean tryConnect=false;
        JSONObject test=null;
        Device.CheckInternet(context);
        //if(Device.getCONNECTION_TYPE()==Device.CONNECTION_TYPE_NONE) {
        test = JSONUrlReader.readJsonFromUrlPlainText(UrlStore.URL_CHECK_INTERNET);

        if (test != null) {
            BLog.e("JSONtest", Device.getCONNECTION_TYPE() + "," + Device.getCONNECTION_STATE() + " - " + test.toString());
            return true;
        } else {
            //BLog.e("JSONtest", Device.getCONNECTION_TYPE() + " - FAILED" );
            return false;
        }
        //}
        //return true;
    }
    public static int getTheme() {
        String fontSizePref = State.getSettings().getString(BriefSettings.STRING_STYLE_FONT_SIZE); //settings.getString("FONT_SIZE", "Medium");
        int themeID = R.style.FontSizeMedium;
        if (fontSizePref == BriefSettings.FONT_SIZE_SMALL) {
            themeID = R.style.FontSizeSmall;
        }
        else if (fontSizePref == BriefSettings.FONT_SIZE_LARGE) {
            themeID = R.style.FontSizeLarge;
        }
        return themeID;
    }
    public static void addStyleBold(TextView textView,double FONT_) {
        if(textView!=null) {
            textView.setTypeface(THEME.typefaceBold);
            //BLog.e("SCX","scale x: "+textView.getTextSize());
            textView.setTextSize(Double.valueOf(Float.valueOf(getSetDefaultTextSize(textView)).intValue()/FONT_).floatValue());
            //textView.setTextAppearance();
        }
    }
    public static void addStyleBold(TextView textView) {
        if(textView!=null) {
            textView.setTypeface(THEME.typefaceBold);
            //textView.setTextAppearance();
        }
    }
    public static void addStyleBold(TextView[] textViews) {
        if(textViews!=null) {
            for(TextView textView: textViews) {
                textView.setTypeface(THEME.typefaceBold);
            }
        }
    }


    public static void addStyle(EditText editText) {
        if(editText!=null) {
            editText.setTypeface(THEME.typeface);
            //textView.setTextAppearance();
        }
    }
    public static void addStyle(TextView textView) {
        if(textView!=null) {
            textView.setTypeface(THEME.typeface);
            //textView.setTextAppearance();
        }
    }
    public static void addStyle(TextView textView,double FONT_) {
        if(textView!=null) {
            textView.setTypeface(THEME.typefaceBold);
            //BLog.e("SCX","scale x: "+textView.getTextSize());
            textView.setTextSize(Double.valueOf(Float.valueOf(getSetDefaultTextSize(textView)).intValue() / FONT_).floatValue());
            //textView.setTextAppearance();
        }
    }
    public static void addStyle(TextView[] textViews) {
        if(textViews!=null) {
            for(TextView textView: textViews) {
                textView.setTypeface(THEME.typeface);
            }
        }
    }
    public static void addStyle(EditText[] textViews) {
        if(textViews!=null) {
            for(TextView textView: textViews) {
                textView.setTypeface(THEME.typeface);
            }
        }
    }
    @SuppressWarnings("deprecation")
    public static Drawable getDrawable(Context context, int Rdrawable) {
        if(android.os.Build.VERSION.SDK_INT>= 21) {
            return context.getDrawable(Rdrawable);
        } else {
            return context.getResources().getDrawable(Rdrawable);
        }
    }
}
