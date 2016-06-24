package com.example.imageloader.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.imageloader.R;
import com.example.imageloader.adapter.MainAdapter;
import com.example.imageloader.bean.WebLink;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private GridView gv_main;
    private MainAdapter adapter;
    private List<WebLink> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gv_main = (GridView)findViewById(R.id.gv_main);
        initData();
        adapter = new MainAdapter(this,data);
        gv_main.setAdapter(adapter);

        gv_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,WebPicturesActivity.class);
                intent.putExtra("url",data.get(position).getUrl());

                startActivity(intent);

            }
        });
    }

    private void initData() {
        data = new ArrayList<>();
        data = new ArrayList<WebLink>();
        data.add(new WebLink("图片天堂", R.drawable.i1, "www.ivsky.com/"));
        data.add(new WebLink("硅谷教育", R.drawable.i2, "www.atguigu.com/"));
        data.add(new WebLink("新闻图库", R.drawable.i3, "www.cnsphoto.com/"));

        data.add(new WebLink("MOKO美空", R.drawable.i4,"www.moko.cc/"));
        data.add(new WebLink("114啦", R.drawable.i5, "www.114la.com/mm/"));
        data.add(new WebLink("动漫之家", R.drawable.i6, "www.donghua.dmzj.com/"));

        data.add(new WebLink("7k7k", R.drawable.i7, "www.7k7k.com/"));
        data.add(new WebLink("嘻嘻哈哈", R.drawable.i8, "www.xxhh.com/"));
        data.add(new WebLink("有意思吧", R.drawable.i9, "www.u148.net/"));

    }

    private long lastTime;
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(System.currentTimeMillis()-lastTime>2000) {
                Toast.makeText(MainActivity.this, "再点击一次 退出程序", Toast.LENGTH_SHORT).show();
                lastTime = System.currentTimeMillis();
                return true;
            }else {
                x.image().clearMemCache();
//                x.image().clearCacheFiles();

            }   
        }
        return super.onKeyUp(keyCode, event);
    }
}
