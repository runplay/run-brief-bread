package run.brief.beans;

import android.content.Context;

import java.util.ArrayList;


public class Brief extends BriefWith{

	public static final int TYPE_IN=0;
	public static final int TYPE_OUT=1;
	public static final int TYPE_ACTION=2;
    public static final int TYPE_MISSED=3;
	
	public static final int STATE_UNREAD=0;
	public static final int STATE_READ=1;
	public static final int STATE_SENDNG=-1;
	public static final int STATE_ERROR=4;
	public static final int STATE_ARCHIVED=3;
	
	private int WITH_;
	private long accountId;
	private int DBIndex;
	private String DBid;


	private int TYPE_;
	private long timestamp;
	private String message;
	private ArrayList<Brief> messageChain;
	public ArrayList<Brief> getMessageChain() {
		return messageChain;
	}
	public void setMessageChain(ArrayList<Brief> messageChain) {
		this.messageChain = messageChain;
	}
	private String subject;
	private String personId ="";
	private ArrayList<BriefOut> outs=new ArrayList<BriefOut>();
	private ArrayList<BriefObject> objects=new ArrayList<BriefObject>();
	private String ratingsIdentifier;
	private int state;
	private String threadId;
	
	public String getThreadId() {
		return threadId;
	}
	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}
	public int getState() {
		return state;
	}
	public void setState(int STATE_) {
		this.state=STATE_;
	}
	public String getDBid() {
		return DBid;
	}
	public void setDBid(String dBid) {
		DBid = dBid;
	}
	

	
	public String getPersonId() {
		return personId;
	}
	public void setPersonId(String personId) {
		this.personId = personId;
	}

	
	
	public long getAccountId() {
		return accountId;
	}
	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}
	public int getDBIndex() {
		return DBIndex;
	}
	public void setDBIndex(int dBIndex) {
		DBIndex = dBIndex;
	}
	
	public Brief() {
		TYPE_=TYPE_OUT;
	}
	public Brief(RssItem news, int itemDbIndex) {
		if(news!=null) {
			TYPE_=TYPE_IN;
			this.WITH_ = Brief.WITH_NEWS;
			this.DBid=news.getLong(RssItem.LONG_ID)+"";
			this.DBIndex =  itemDbIndex;
			this.message=news.getString(RssItem.STRING_TEXT);
			this.subject=news.getString(RssItem.STRING_HEAD);
			this.timestamp=news.getLong(RssItem.LONG_DATE);
			String source=news.getString(RssItem.STRING_PUBLISHER);
			BriefOut b = new BriefOut(BriefOut.WITH_NEWS,source,source);
			this.outs.add(b);
		}
	}
	public ArrayList<BriefObject> getBriefObjects() {
		if(objects==null)
			objects=new ArrayList<BriefObject>();
		return objects;
	}
	public ArrayList<BriefOut> getBriefOuts() {
		if(outs==null)
			outs=new ArrayList<BriefOut>();
		return outs;
	}
	public void setBriefOuts(ArrayList<BriefOut> outs) {
		this.outs = outs;
	}
	public void addBriefOut(BriefOut bout) {
		this.outs.add(bout);
	}
	public int getTYPE_() {
		return TYPE_;
	}
	public void setTYPE_(int tYPE_) {
		TYPE_ = tYPE_;
	}

	public int getWITH_() {
		return WITH_;
	}
	public void setWITH_(int wITH_) {
		WITH_ = wITH_;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
}
