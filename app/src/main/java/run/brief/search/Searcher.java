package run.brief.search;

import android.content.Context;

import java.util.ArrayList;

import run.brief.b.BCallback;
import run.brief.beans.RssItem;
import run.brief.beans.SearchResult;
import run.brief.news.NewsItemsDb;

public class Searcher {

	private static ArrayList<SearchResult> results=new ArrayList<SearchResult>();
	
	public static ArrayList<SearchResult> getResults() {
		return results;
	}
	public static SearchResult get(int index) {
		if(index>=0 && index<results.size()) {
			return results.get(index);
		}
		return null;
	}
	public static int size() {
		return results.size();
	}
	
	public static void doSearch(Context context, String term, BCallback callback) {
		ArrayList<SearchResult> res = new ArrayList<SearchResult>();
		
		if(term !=null) {
			
			ArrayList<String> st=getWords(term.toLowerCase());

            if(!res.isEmpty() && callback!=null)
                callback.callback();

			ArrayList<RssItem> results = NewsItemsDb.search(st);

			for(RssItem rss: results) {
				res.add(new SearchResult(rss.getBean()));
			}
/*
			if(NewsItemsDb.size()>0) {
				for(int i=0; i< NewsItemsDb.size(); i++) {
					RssItem rss= NewsItemsDb.get(i);
					String sme=rss.getString(RssItem.STRING_HEAD).toLowerCase();
					String smt=rss.getString(RssItem.STRING_TEXT).toLowerCase();
					for(String s: st) {
						int index=sme.indexOf(s);
						int indext=smt.indexOf(s);
						if(index!=-1) {
							res.add(new SearchResult(rss.getBean()));
						} else if(indext!=-1) {
							res.add(new SearchResult(rss.getBean()));
						}
						
					}
					
				}
			}
			*/

		
		}
		results=res;
		if(callback!=null)
			callback.callback();
	}
	private static String getResultText(int index, String term, String searchText) {
		StringBuilder sb=new StringBuilder();
		int st=index-10;
		if(st<0)
			st=0;
		int se=index+40;
		if(se>searchText.length()-1)
			se=searchText.length();
		sb.append(searchText.substring(st, index));
		sb.append(term);
		sb.append(searchText.substring(index+term.length(), se));
		return sb.toString();
		
	}
	private static ArrayList<String> getWords(String s) {
		ArrayList<String> fwords=new ArrayList<String>();
		//ArrayList<String> f=new ArrayList<String>();
		String[] tmp = s.split(",");
		if(tmp!=null) {
			for(int i=0; i<tmp.length; i++) {
				String[] etmp = tmp[i].split("\\s");
				if(etmp!=null) {
					for(int j=0; j<etmp.length; j++) {
						if(etmp[j].length()>1)
							fwords.add(etmp[j]);
					}
				}
			}
		}
		return fwords;
	}
}
