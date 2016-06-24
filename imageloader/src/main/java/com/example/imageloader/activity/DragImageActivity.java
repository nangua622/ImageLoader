package com.example.imageloader.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imageloader.R;
import com.example.imageloader.bean.ImageBean;
import com.example.imageloader.fragment.ImageFragment;
import com.example.imageloader.listener.MyCacheCallback;
import com.example.imageloader.utils.AppUtils;
import com.example.imageloader.utils.Constants;

import org.xutils.common.util.FileUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DragImageActivity extends FragmentActivity {
    //region 一脸迷茫
    private TextView tv_drag_url;
    private TextView tv_drag_pageno;
    private ViewPager vp_drag;
    private ImageView iv_drag_download;
    private ImageView iv_drag_share;
    private PictureSlidePagerAdapyer adpter;

    private int position;
    private List<ImageBean> imageBeans;
    private ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            DragImageActivity.this.position = position;
            tv_drag_url.setText(imageBeans.get(position).getUrl());
            tv_drag_pageno.setText((position+1)+"/"+imageBeans.size());
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_image);
        tv_drag_url = (TextView)findViewById(R.id.tv_drag_url);
        tv_drag_pageno = (TextView)findViewById(R.id.tv_drag_pageno);
        vp_drag = (ViewPager)findViewById(R.id.vp_drag);
        iv_drag_download = (ImageView)findViewById(R.id.iv_drag_download);
        iv_drag_share = (ImageView)findViewById(R.id.iv_drag_share);

        getActionBar().hide();

        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);
        imageBeans = (List<ImageBean>) intent.getSerializableExtra("imagebeans");

        tv_drag_url.setText(imageBeans.get(position).getUrl());
        tv_drag_pageno.setText((position+1)+"/"+imageBeans.size());
        
        if(Constants.state == Constants.S_WEB) {
            iv_drag_download.setImageResource(R.drawable.icon_s_download_press);
            iv_drag_share.setVisibility(View.GONE);
        }else if(Constants.state == Constants.S_LOCAL) {
            iv_drag_download.setImageResource(R.drawable.garbage_media_cache);
            iv_drag_share.setVisibility(View.VISIBLE);
        }
        adpter = new PictureSlidePagerAdapyer(this.getSupportFragmentManager());
        vp_drag.setAdapter(adpter);
        vp_drag.setCurrentItem(position);
        vp_drag.addOnPageChangeListener(listener);
    }

    class PictureSlidePagerAdapyer extends FragmentStatePagerAdapter{

        public PictureSlidePagerAdapyer(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            String imagePath = imageBeans.get(position).getUrl();
            return ImageFragment.getInstance(imagePath);
        }

        @Override
        public int getCount() {
            return imageBeans.size();
        }
    }
    public void downloadImage(View v) {
        if(Constants.state == Constants.S_WEB){
            String url = imageBeans.get(position).getUrl();
            downloadImages(url);
            Toast.makeText(DragImageActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
        }else if (Constants.state == Constants.S_LOCAL){
            Bitmap bitmap = BitmapFactory.decodeFile(imageBeans.get(position).getUrl());
            try {
                setWallpaper(bitmap);
                Toast.makeText(DragImageActivity.this, "设置壁纸成功", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(DragImageActivity.this, "设置壁纸失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void shareImage(View v) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);

        File file = new File(imageBeans.get(position).getUrl());
        intent.setDataAndType(Uri.fromFile(file),"image/*");
        startActivity(intent);
    }

    private void downloadImages(final String url) {

        File fileDir = new File(Constants.downloadPath);
        if(!fileDir.exists()){
            fileDir.mkdirs();
        }

        final String filePath = Constants.downloadPath +"/" + System.currentTimeMillis() + AppUtils.getImageName(url);
        RequestParams params = new RequestParams(url);
        params.setConnectTimeout(5000);
        x.http().get(params,new MyCacheCallback<File>(){
            @Override
            public boolean onCache(File result) {
                FileUtil.copy(result.getAbsolutePath(),filePath);

                return true;
            }

            @Override
            public void onSuccess(File result) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(DragImageActivity.this, "下载失败"+url, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //endregion
}
