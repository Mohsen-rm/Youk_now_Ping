package com.ajiashi.youknowping;
import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CustomAdapterPing extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] maintitle;
    private final ArrayList<String> subtitle;
    private final Integer[] imgid;
    private final ArrayList<Integer> time;

    public CustomAdapterPing(Activity context, String[] maintitle, ArrayList<String> subtitle, Integer[] imgid, ArrayList<Integer> time) {
        super(context, R.layout.mylist_ping, maintitle);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.maintitle= maintitle;
        this.subtitle= subtitle;
        this.imgid= imgid;
        this.time = time;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist_ping, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.subtitle);

        titleText.setText(maintitle[position]);
        imageView.setImageResource(imgid[position]);

        try {

            if (time.get(position)==0){
                subtitleText.setText(".....");
                subtitleText.setTextColor(Color.GRAY);
            }else {
                subtitleText.setText(subtitle.get(position));
            }
        }catch (Exception e){
            subtitleText.setText("No find");
        }

        subtitleText.setTextColor(Color.GREEN);
        try {
            if(time.get(position)<150){
                subtitleText.setTextColor(Color.GREEN);
            }
            if(time.get(position)<350){
                subtitleText.setTextColor(Color.YELLOW);
            }
            if(time.get(position)==0){
                subtitleText.setTextColor(Color.GRAY);
            }if(time.get(position)>300){
                subtitleText.setTextColor(Color.RED);
            }
        }catch (Exception e){

        }
        return rowView;

    };
}