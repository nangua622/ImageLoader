package com.example.imageloader.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.imageloader.R;
import com.example.imageloader.bean.WebLink;

import java.util.List;

/**
 * Created by nBB on 16/6/21.
 */
public class MainAdapter extends BaseAdapter{
    private Context context;
    private List<WebLink> list;

    public MainAdapter(Context context, List<WebLink> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public WebLink getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            convertView = View.inflate(context, R.layout.item_main,null);
            holder = new ViewHolder();
            holder.iv_item_icon = (ImageView) convertView.findViewById(R.id.iv_item_icon);
            holder.tv_item_name = (TextView) convertView.findViewById(R.id.tv_item_name);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        WebLink webLink = list.get(position);
        holder.iv_item_icon.setImageResource(webLink.getIcon());
        holder.tv_item_name.setText(webLink.getName());

        return convertView;
    }
    static class ViewHolder{
        ImageView iv_item_icon;
        TextView tv_item_name;
    }
}
