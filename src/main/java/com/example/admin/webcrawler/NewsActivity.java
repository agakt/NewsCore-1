package com.example.admin.webcrawler;

import android.app.ProgressDialog;
import android.content.Entity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class NewsActivity extends ActionBarActivity implements View.OnTouchListener {
    private LinearLayout window;

    ArrayList<String> infos;
    String info;
    //ArrayList<NaverParser> nps = new ArrayList<NaverParser>();
    private NaverParser np;
    private ArrayList<NewsData> news;
    private ArrayList<ArrayList<NewsData>> news_list;
    private ArrayList<String> likes;
    private ArrayList<String> defaults;
    private TextView text;
    private ListView listView;
    private NewsCustomAdapter adapter;
    //private ProgressDialog dialog;
    URL url;
    private Thread thread;
    ViewFlipper flipper;

    /*터치 발생지점 저장*/
    float xAtDown;
    float xAtUp;
    int which=0;
    //private HttpURLConnection httpCon;
    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //LayoutInflater inflater = (LayoutInflater)getLayoutInflater();
            //View view = inflater.inflate(R.layout.news_list, viewFlipper, true);
            switch (msg.what) {
                case 0:
                    text = (TextView) findViewById(R.id.textView1);
                    text.setText(infos.get(0));

                    adapter = new NewsCustomAdapter(getApplicationContext(), R.layout.custom, news_list.get(0));
                    listView = (ListView) findViewById(R.id.listView1);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new ListView.OnItemClickListener() {


                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(news_list.get(0).get(arg2).link)));
                        }
                    });
                    break;
                case 1:
                    text = (TextView) findViewById(R.id.textView2);
                    text.setText(infos.get(1));

                    adapter = new NewsCustomAdapter(getApplicationContext(), R.layout.custom, news_list.get(1));
                    listView = (ListView) findViewById(R.id.listView2);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new ListView.OnItemClickListener() {


                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(news_list.get(1).get(arg2).link)));
                        }
                    });
                    break;
                case 2:
                    text = (TextView) findViewById(R.id.textView3);
                    text.setText(infos.get(2));

                    adapter = new NewsCustomAdapter(getApplicationContext(), R.layout.custom, news_list.get(2));
                    listView = (ListView) findViewById(R.id.listView3);

                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new ListView.OnItemClickListener() {


                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(news_list.get(2).get(arg2).link)));
                        }
                    });
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naversearch);
        defaults = new ArrayList<String>();
        defaults.add("Not Found");
        infos=new ArrayList<String>();
        likes = new ArrayList<String>();
        news_list = new ArrayList<ArrayList<NewsData>>();
        Intent intent = getIntent();
        // likes -> user's facebook like data
        likes = intent.getStringArrayListExtra("likes");
        flipper=(ViewFlipper)findViewById(R.id.viewFlipper);
        flipper.setOnTouchListener(this);
        // morph analyze with http post message
        for(int i=0;i<3;i++) {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            ArrayList<String> nouns = new ArrayList<String>();
            // url
            HttpPost httpPost = new HttpPost(
                    "http://nlp.kookmin.ac.kr/cgi-bin/index.cgi");
            // 파라메터 리스트
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            // 파라메터 설정
            // new BasicNameValuePair(키, 값)

            nvps.add(new BasicNameValuePair("Question",  likes.get(i)));

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nvps,"euc-kr"));
                // 응답
                HttpResponse response = httpclient.execute(httpPost);
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    // 콘텐츠를 읽어들임.
                    BufferedReader rd = new BufferedReader(new InputStreamReader(
                            entity.getContent(),"euc-kr"));

                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        // 콘텐츠 내용
                        if(line.matches(".*<pre>.*")) {
                            String[] arr = line.split("	");
                            nouns.add(arr[1]);
                            line = rd.readLine();
                            while (!line.matches(".*</pre>.*")) {
                                nouns.add(line.trim());
                                line = rd.readLine();
                            }
                        }
                    }
                    // nouns = {네이버, 개발자, 다음, 개발자, 아스날}
                    info="";
                    for(int t=0;t<nouns.size();t++)
                        info+=nouns.get(t)+ " ";
                    infos.add(info);
                    // info = 'naver engineer' or 'arsenal'
                    np = new NaverParser(infos.get(i));

                    thread = new Thread() {
                        @Override
                        public void run() {
                            super.run();

                           try {
                                Log.i("NET", "URL Loading");
                                url = new URL("http://openapi.naver.com/search?key=1612612e1bed0b0b469c748724b96a96&query=" + URLEncoder.encode(info, "UTF-8") +
                                            "&target=news&start=1&display=10");
                                Log.i("NET", "URL address complete");
                                news = np.getNewsData(url);
                                news_list.add(news);

                                handler.sendEmptyMessage(which++);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    };
                    thread.start();
                }
                /****************************************************
                 *  close 부분입니다. 중요하지 않은 부분입니다.
                 ****************************************************/
                /////////////////////////////////////////////////////
               if (entity == null) {
                    return;
                }
                if (entity.isStreaming()) {
                    final InputStream instream = entity.getContent();
                    if (instream != null) {
                        instream.close();
                    }
                }
                //////////////////////////////////////////////////////
            } catch(Exception ex) {} finally {
            // releaseConnection 삭제 -> Can not found method!!
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news, menu);
        return true;
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 터치 이벤트가 일어난 뷰가 ViewFlipper가 아니면 return
        if(v != flipper) return false;
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            xAtDown = event.getX(); // 터치 시작지점 x좌표 저장
        }
        else if(event.getAction() == MotionEvent.ACTION_UP){
            xAtUp = event.getX(); 	// 터치 끝난지점 x좌표 저장
            if( xAtUp < xAtDown ) {
                // 왼쪽 방향 에니메이션 지정
                flipper.setInAnimation(AnimationUtils.loadAnimation(this,
                        R.anim.push_left_in));
                flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
                        R.anim.push_left_out));
                // 다음 view 보여줌
                flipper.showNext();
            }
            else if (xAtUp > xAtDown){
                // 오른쪽 방향 에니메이션 지정
                flipper.setInAnimation(AnimationUtils.loadAnimation(this,
                        R.anim.push_right_in));
                flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
                        R.anim.push_right_out));
                // 전 view 보여줌
                flipper.showPrevious();
            }
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
