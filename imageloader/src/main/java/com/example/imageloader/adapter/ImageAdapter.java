package com.example.imageloader.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.imageloader.R;
import com.example.imageloader.activity.WebPicturesActivity;
import com.example.imageloader.bean.ImageBean;
import com.example.imageloader.utils.AppUtils;

import org.xutils.x;

import java.util.List;

/**
 * Created by nBB on 16/6/21.
 */
public class ImageAdapter extends BaseAdapter{
    private Context context;
    private List<ImageBean> list;

    public ImageAdapter(Context context) {
        this.context = context;
    }

    public List<ImageBean> getList() {
        return list;
    }

    public void setList(List<ImageBean> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return (list == null)?0:list.size();
    }

    @Override
    public Object getItem(int position) {
        return (list == null)? null :list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = View.inflate(context, R.layout.item_pictures,null);

            holder = new ViewHolder();

            holder.iv_item_pic = (ImageView) convertView.findViewById(R.id.iv_item_pic);
            holder.iv_item_checked = (ImageView) convertView.findViewById(R.id.iv_item_checked);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        ImageBean imageBean = list.get(position);
        x.image().bind(holder.iv_item_pic,imageBean.getUrl(), AppUtils.smallImageOptions);

        if(WebPicturesActivity.isEdit){
            holder.iv_item_checked.setVisibility(View.VISIBLE);
            if(imageBean.isChecked()) {
                holder.iv_item_checked.setImageResource(R.drawable.blue_selected);
            }else {
                holder.iv_item_checked.setImageResource(R.drawable.blue_unselected);
            }
        }else {
            holder.iv_item_checked.setVisibility(View.GONE);

        }
        return convertView;
    }

    public boolean getImageCheckedStatus(int position) {
        return  list.get(position).isChecked();
    }

    public void changeImageCheckedStatus(int position, boolean b) {
        list.get(position).setChecked(b);
    }

    public void changeAllImagesCheckedStatus(boolean isChecked) {
        for(int i = 0;i<list.size();i++){
            list.get(i).setChecked(isChecked);
        }
        this.notifyDataSetChanged();
    }


    static class ViewHolder{
        ImageView iv_item_checked;
        ImageView iv_item_pic;
    }
}
