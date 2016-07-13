package com.example.demo.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by HeWenjie on 2016/7/13.
 */
public class EquationAdapter extends ArrayAdapter<String> {
    private int resources;
    private LayoutInflater inflater;

    public EquationAdapter(Context context, int resources, List<String> equationList) {
        super(context, resources, equationList);
        this.resources = resources;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(resources, null);
        String equation = (String)this.getItem(position);

        if(equation == null) return null;

        TextView nameView = (TextView)convertView.findViewById(R.id.equation);
        nameView.setText(equation);

        return convertView;
    }
}
