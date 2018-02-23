package run.brief.beans;

import java.util.ArrayList;
import java.util.List;

import run.brief.settings.SettingsDb;
import run.brief.util.Cal;
import run.brief.util.Sf;
import run.brief.util.json.JSONObject;

public final class BriefSettings extends BJSONBean {

	//public static final String SET_="";
	public static final String INT_COUNT_LAUNCH="launched";
	public static final String BOOL_STYLE_DARK="style";
	//public static final String LONG_LAST_INDEX_QUICK="indq";
	//public static final String LONG_LAST_INDEX_FULL="indf";
	
	public static final String BOOL_USE_EMOTICONS="emo";
	public static final String BOOL_NOTIFY_NEWS_SOUND="not_news_s";
	public static final String BOOL_NOTIFY_NEWS_VIBRATE="not_news_v";
	public static final String BOOL_NOTIFY_REPEAT_VIBRATE="not_repeat_v";
	public static final String STRING_ALERT_TIMES="altme";
	public static final String INT_NEWS_DAYS_DELETE_STORIES="stodel";
	public static final String INT_NEWS_DAYS_DELETE_IMAGES="picdel";
	public static final String BOOL_NEWS_MANUAL_REFRESH="newsman";
	public static final String LONG_LAST_24HR_ARCHIVE_DELETE="lardel";

	public static final String BOOL_WEBVIEW_DISABLE_JAVASCRIPT="jsdis";


	public static final String FONT_SIZE_SMALL="Small";
	public static final String FONT_SIZE_MEDIUM="Medium";
	public static final String FONT_SIZE_LARGE="Large";
	public static final String FONT_SIZE_XLARGE="XLarge";

	public static final String FONT_FACE_DEFAULT="Default";
	public static final String FONT_FACE_CAVIAR="Caviar";
	public static final String FONT_FACE_COMIC="Comic";

	public static final String STRING_FLOAT_DEF_FONT_SIZE="df_size";
	public static final String STRING_STYLE_FONT_SIZE="st_font_size";
	public static final String STRING_STYLE_FONT_FACE="st_font_face";



	public BriefSettings() {
		bean=new JSONObject();
		bean.put(BOOL_USE_EMOTICONS, Boolean.TRUE);
		bean.put(INT_COUNT_LAUNCH, 0);
		bean.put(BOOL_STYLE_DARK, Boolean.TRUE);

		bean.put(BOOL_NOTIFY_NEWS_SOUND, Boolean.TRUE);
		bean.put(BOOL_NOTIFY_NEWS_VIBRATE, Boolean.TRUE);
		bean.put(BOOL_NOTIFY_REPEAT_VIBRATE, Boolean.TRUE);

		bean.put(INT_NEWS_DAYS_DELETE_STORIES, 60);
		bean.put(INT_NEWS_DAYS_DELETE_IMAGES, 3);

		bean.put(STRING_ALERT_TIMES,deftimes);
		bean.put(BOOL_NEWS_MANUAL_REFRESH,false);
		bean.put(LONG_LAST_24HR_ARCHIVE_DELETE, Cal.getUnixTime());
	}
	public BriefSettings(JSONObject obj) {

        this.bean=obj;


	}
	public void save() {
	    SettingsDb.Update(this);
	    SettingsDb.Save();
	}
	public static final         String deftimes = "0,0,0,0,0,0,0,0,0,0,0,0,"
			+ "0,0,1,1,2,2,2,2,2,2,2,2,"
			+ "2,2,2,2,2,2,2,2,2,2,2,2,"
			+ "2,2,2,2,2,2,2,2,1,1,1,1";
	public static final List<String> tztimes = new ArrayList<String>();
	static {
		tztimes.add("00:00"); tztimes.add("00:30"); tztimes.add("01:00"); tztimes.add("01:30"); tztimes.add("02:00"); tztimes.add("02:30");
		tztimes.add("03:00"); tztimes.add("03:30"); tztimes.add("04:00"); tztimes.add("04:30"); tztimes.add("05:00"); tztimes.add("05:30");
		tztimes.add("06:00"); tztimes.add("06:30"); tztimes.add("07:00"); tztimes.add("07:30"); tztimes.add("08:00"); tztimes.add("08:30");
		tztimes.add("09:00"); tztimes.add("09:30"); tztimes.add("10:00"); tztimes.add("10:30"); tztimes.add("11:00"); tztimes.add("11:30");
		tztimes.add("12:00"); tztimes.add("12:30"); tztimes.add("13:00"); tztimes.add("13:30"); tztimes.add("14:00"); tztimes.add("14:30");
		tztimes.add("15:00"); tztimes.add("15:30"); tztimes.add("16:00"); tztimes.add("16:30"); tztimes.add("17:00"); tztimes.add("17:30");
		tztimes.add("18:00"); tztimes.add("18:30"); tztimes.add("19:00"); tztimes.add("19:30"); tztimes.add("20:00"); tztimes.add("20:30");
		tztimes.add("21:00"); tztimes.add("21:30"); tztimes.add("22:00"); tztimes.add("22:30"); tztimes.add("23:00"); tztimes.add("23:30");
	}
	public Integer getTimeSlotSetting(String timeSlotHHMM) {
		int useindex=0;
		for(int i=0; i<tztimes.size(); i++) {
			String str= tztimes.get(i);
			if(str.equals(timeSlotHHMM)) {
				useindex=i;
			}

		}
		String[] splits = this.getString(STRING_ALERT_TIMES).split(",");
		return Sf.toInt(splits[useindex]);
	}

}
