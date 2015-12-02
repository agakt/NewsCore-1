package com.example.admin.webcrawler;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by syh on 2015-09-27.
 */
public class NaverParser {
    private String key;
    ArrayList<NewsData> data;

    NaverParser(String info)
    {
        this.key = info;
    }

    public ArrayList<NewsData> getNewsData(URL url) throws MalformedURLException {
        data = new ArrayList<NewsData>();
        NewsData item = null;
        boolean flag = false;

        try {

            Log.i("NET","Call parser");
            XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();

            parser.setInput(url.openStream(),null);
            Log.i("NET", "Parsing");
            int parseEvent = parser.getEventType();
            while(parseEvent != XmlPullParser.END_DOCUMENT){

                switch(parseEvent){
                    case XmlPullParser.START_TAG:
                        String tag = parser.getName();

                        if(tag.compareTo("item")==0){
                            item = new NewsData();
                            //parser.nextText();
                            flag = true;
                            Log.i("NET", "Start...");
                        }
                        if(flag) {
                            if (tag.compareTo("title") == 0) {

                                //item = new NewsData();
                                String titleSrc = parser.nextText();
                                item.title = titleSrc;
                                Log.i("NET", "Start...");
                            }

                            if (tag.compareTo("originallink") == 0) {
                                String originallinkSrc = parser.nextText();
                                item.originallink = originallinkSrc;
                            }

                            if (tag.compareTo("link") == 0) {
                                String linkSrc = parser.nextText();
                                item.link = linkSrc;
                            }

                            if (tag.compareTo("description") == 0) {
                                String desSrc = parser.nextText();
                                item.description = desSrc;
                                data.add(item);
                                flag = false;
                            }
                        }
                        break;
                }
                parseEvent = parser.next();
            }

            Log.i("NET", "End...");
        } catch (Exception e1) {
            Log.i("NET", "Parsing fail...");
            e1.printStackTrace();
        }

        return data;
    }


}

class NewsData {
    String title;
    String originallink;
    String link;
    String description;
    String pubDate;

    NewsData(){}

    NewsData(String title,String originallink,String link,String description,String pubDate)
    {
        this.title=title;
        this.originallink=originallink;
        this.link=link;
        this.description=description;
        this.pubDate=pubDate;
    }
}