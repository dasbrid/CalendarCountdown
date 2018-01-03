package uk.me.asbridge.calendarcountdown;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by AsbridgeD on 03-Jan-18.
 */

public class CalendarsAdapter extends BaseAdapter {

    private ArrayList<Calendar> albums;
    private LayoutInflater inflater;

    // Constructor
    public CalendarsAdapter(Context c, ArrayList<Calendar> theAlbums){
        albums=theAlbums;
        inflater =LayoutInflater.from(c);
    }


    @Override
    public int getCount() {
        return albums.size();
    }

    @Override
    public Object getItem(int index) {
        return albums.get(index);
    }

    public Calendar getAlbum(int position) {
        return albums.get(position);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get album using position
        Calendar currAlbum = albums.get(position);

        // using standard android layout, but could copy anc customise, but must be a checked text view...
        CheckedTextView layout = (CheckedTextView) inflater.inflate(android.R.layout.simple_list_item_multiple_choice/*R.layout.bucket_in_list_android*/, parent, false);
        TextView tvBucketName = (TextView)layout.findViewById(android.R.id.text1);
        tvBucketName.setText(currAlbum.getName());

        //set position as tag
        layout.setTag(position);
        return layout;
    }

}
