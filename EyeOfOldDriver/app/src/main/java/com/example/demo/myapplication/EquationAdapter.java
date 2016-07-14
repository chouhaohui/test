package com.example.demo.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import javax.net.ssl.HandshakeCompletedListener;

/**
 * Created by HeWenjie on 2016/7/13.
 */
public class EquationAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<String> contentList;
    private Callback callback;

    public interface Callback {
        public void click(View v);
    }

    public EquationAdapter(Context context, List<String> contentList, Callback callback) {
        this.contentList = contentList;
        this.inflater = LayoutInflater.from(context);
        this.callback = callback;
    }

    public class ViewHolder {
        public TextView textView;
        public ImageButton button;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.textView = (TextView)convertView.findViewById(R.id.equation);
            holder.button = (ImageButton)convertView.findViewById(R.id.deleteButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        String equation = (String)this.getItem(position);
        holder.textView.setText(equation);
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.click(v);
            }
        });
        holder.button.setTag(position);
        return convertView;
    }

    public int getCount() {
        return contentList.size();
    }

    public Object getItem(int position) {
        return contentList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
}
