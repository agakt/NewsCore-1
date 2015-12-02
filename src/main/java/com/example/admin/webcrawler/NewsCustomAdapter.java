package com.example.admin.webcrawler;

/**
 * Created by admin on 2015-10-16.
 */
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by syh on 2015-09-28.
 */
public class NewsCustomAdapter extends BaseAdapter {
    private ArrayList<NewsData> n_listItem;
    Context context;
    LayoutInflater Inflater;
    private int layout;

    public NewsCustomAdapter(Context context, int listViewResourceId, ArrayList<NewsData> objects)
    {
        this.n_listItem = objects;
        this.context = context;
        this.layout = listViewResourceId;
        Inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return n_listItem.size();
    }

    @Override
    public Object getItem(int position) {
        return n_listItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if( convertView == null) {
            //LayoutInflater vi = (LayoutInflater)this.context.getSystemService(this.context.LAYOUT_INFLATER_SERVICE);
            convertView = Inflater.inflate(R.layout.custom,parent,false);
        }

        //위젯 찾기
        //Button b = (Button)convertView.findViewById(R.id.button2);
        TextView titleTxt = (TextView)convertView.findViewById(R.id.textView4);
        //TextView oriTxt = (TextView)convertView.findViewById(R.id.textView3);
        //TextView linkTxt = (TextView)convertView.findViewById(R.id.textView5);
        //TextView desTxt = (TextView)convertView.findViewById(R.id.textView6);

        //위젯에 데이터를 넣기
        NewsData item = n_listItem.get(position);

        titleTxt.setText(item.title);
        //oriTxt.setText(item.originallink);
        //linkTxt.setText(item.link);
        //desTxt.setText(item.description);

        return convertView;
    }
}
