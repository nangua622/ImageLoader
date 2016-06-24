package com.example.imageloader.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imageloader.R;
import com.example.imageloader.adapter.ImageAdapter;
import com.example.imageloader.bean.HistoryUrl;
import com.example.imageloader.bean.ImageBean;
import com.example.imageloader.dao.HistoryDAO;
import com.example.imageloader.utils.AppUtils;
import com.example.imageloader.utils.Constants;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xutils.common.Callback;
import org.xutils.common.util.FileUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class WebPicturesActivity extends Activity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, CompoundButton.OnCheckedChangeListener {
    private TextView tv_pictures_info;//显示文本信息
    private CheckBox cb_pictures_selected; //勾选图片
    private ImageView iv_pictures_download; //下载或删除图片
    private Button btn_pictures_stop; //停止抓取图片
    private ProgressBar pb_pictures_loading; //深度抓取进度
    private GridView gv_pictures_pics; //抓取图片列表
    private ImageAdapter adapter;
    private List<ImageBean> imageBean = new ArrayList<ImageBean>();
    private HashSet<ImageBean> imageUrlSet = new HashSet<ImageBean>();
    private SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            String s = checkUrlPre(query);
            getHttpImages(s);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };
    private String html;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );
        setContentView(R.layout.activity_web_pictures);
        init();
    }

    private void init() {
        tv_pictures_info = (TextView) findViewById(R.id.tv_pictures_info);
        cb_pictures_selected = (CheckBox) findViewById(R.id.cb_pictures_selected);
        iv_pictures_download = (ImageView) findViewById(R.id.iv_pictures_download);
        btn_pictures_stop = (Button) findViewById(R.id.btn_pictures_stop);
        pb_pictures_loading = (ProgressBar) findViewById(R.id.pb_pictures_loading);
        gv_pictures_pics = (GridView) findViewById(R.id.gv_pictures_pics);

        adapter = new ImageAdapter(this);
        gv_pictures_pics.setAdapter(adapter);

        historyDAO = new HistoryDAO();
        historyUrls = historyDAO.getAll();

        Intent intent = getIntent();
        url = intent.getStringExtra("url");

        url = checkUrlPre(url);

        getHttpImages(url);

        gv_pictures_pics.setOnItemLongClickListener(this);

        gv_pictures_pics.setOnItemClickListener(this);

        cb_pictures_selected.setOnCheckedChangeListener(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        Constants.state = Constants.S_WEB;

    }

    private ProgressDialog dialog;

    private void getHttpImages(final String url) {

        addToHistory(url);

        isEdit = false;
        selectCount = 0;
        tv_pictures_info.setText("请在搜索框中输入网站搜索");
        cb_pictures_selected.setVisibility(View.GONE);
        iv_pictures_download.setVisibility(View.GONE);
        this.url = url;

        showProgressDialog("正在抓取" + url + "网站的图片", false);
        x.http().get(new RequestParams(url), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                imageBean.clear();
                imageUrlSet.clear();
                showImagesFromHtml(url, result);
                html = result;
                dialog.dismiss();

//                showDeepSeachDialog(url, result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(WebPicturesActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    private void addToHistory(String url) {
        HistoryUrl historyUrl = new HistoryUrl(-1,url);
        if(!historyUrls.contains(historyUrl)){
            historyDAO.add(historyUrl);
            historyUrls.add(historyUrl);
        }

    }

    private void showDeepSeachDialog(final String url, final String html) {
        new AlertDialog.Builder(this)
                .setTitle("请确认")
                .setMessage(url + "首页数据已抓取完毕，是否继续抓取？")
                .setPositiveButton("深度抓取", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deepSeach(html);
                    }
                })
                .setNegativeButton("下回吧", null)
                .show();
    }

    private void deepSeach(final String html) {
        pb_pictures_loading.setVisibility(View.VISIBLE);
        btn_pictures_stop.setVisibility(View.VISIBLE);

        btn_pictures_stop.setVisibility(View.VISIBLE);

        Document doc = Jsoup.parse(html);// 解析HTML页面

        // 获取页面中的所有连接
        Elements links = doc.select("a[href]");
        List<String> useLinks = getUseableLinks(links);// 过滤

        pb_pictures_loading.setMax(useLinks.size());
        for (int i = 0; i < useLinks.size(); i++) {
            final String useLink = useLinks.get(i);

            x.http().get(new RequestParams(useLink), new MyCacheCallback<String>() {
                @Override
                public void onSuccess(String result) {

                    if (stopDeepSeach) {
                        return;

                    }
                    showImagesFromHtml(useLink, html);
                    tv_pictures_info.setText("抓取到" + imageBean.size() + "张图片");
                    updateprogress("抓取完毕，总共抓取到" + imageBean.size() + "张图片");
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    if (stopDeepSeach) {
                        return;
                    }
                    updateprogress("部分抓取失败，总共抓取到" + imageBean.size() + "张图片");
                }
            });

        }
    }

    public boolean stopDeepSeach = false;

    public void stopSearch(View v) {
        stopDeepSeach = true;
        pb_pictures_loading.setVisibility(View.GONE);
        btn_pictures_stop.setVisibility(View.GONE);
        tv_pictures_info.setText("停止抓取，总共抓取到" + imageBean.size() + "张图片");
    }

    private List<String> getUseableLinks(Elements links) {
        //用于过滤重复url的集合
        HashSet<String> set = new HashSet<String>();
        //用于保存有效url的集合
        List<String> lstLinks = new ArrayList<String>();

        //遍历所有links,过滤,保存有效链接
        for (Element link : links) {
            String href = link.attr("href");// abs:href, "http://"
            //Log.i("spl","过滤前,链接:"+href);
            // 设置过滤条件
            if (href.equals("")) {
                continue;// 跳过
            }
            if (href.equals(url)) {
                continue;// 跳过
            }
            if (href.startsWith("javascript")) {
                continue;// 跳过
            }

            if (href.startsWith("/")) {
                href = url + href;
            }
            if (!set.contains(href)) {
                set.add(href);// 将有效链接保存至哈希表中
                lstLinks.add(href);
            }

            Log.i("spl", "有效链接:" + href);
        }
        return lstLinks;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if(!isEdit){
            isEdit=true;

            iv_pictures_download.setVisibility(View.VISIBLE);
            cb_pictures_selected.setVisibility(View.VISIBLE);

        }

        boolean isChecked = adapter.getImageCheckedStatus(position);
        selectCount += (isChecked)? -1 : +1;


        tv_pictures_info.setText(selectCount + "/" +imageBean.size());


        adapter.changeImageCheckedStatus(position,!isChecked);
        adapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(!isEdit) {
            Intent intent = new Intent(this,DragImageActivity.class);
            intent.putExtra("position",position);
            intent.putExtra("imagebeans",(ArrayList)imageBean);
            startActivity(intent);
        }else {
            boolean isChecked = adapter.getImageCheckedStatus(position);
            selectCount += (isChecked)? -1 : +1;


            tv_pictures_info.setText(selectCount + "/" +imageBean.size());


            adapter.changeImageCheckedStatus(position,!isChecked);
            adapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        adapter.changeAllImagesCheckedStatus(isChecked);

        if(isChecked) {
            selectCount = imageBean.size();
        }else {
            selectCount = 0;
        }
        tv_pictures_info.setText(selectCount+"/"+imageBean.size());
    }

    public class MyCacheCallback<ResultType> implements Callback.CacheCallback<ResultType> {

        @Override
        public boolean onCache(ResultType result) {
            return false;
        }

        @Override
        public void onSuccess(ResultType result) {

        }

        @Override
        public void onError(Throwable ex, boolean isOnCallback) {

        }

        @Override
        public void onCancelled(CancelledException cex) {

        }

        @Override
        public void onFinished() {

        }
    }


    private void updateprogress(String text) {
        pb_pictures_loading.incrementProgressBy(1);

        if (pb_pictures_loading.getProgress() == pb_pictures_loading.getMax()) {
            pb_pictures_loading.setVisibility(View.GONE);
            btn_pictures_stop.setVisibility(View.GONE);
            tv_pictures_info.setText(text);
        }
    }

    private void showImagesFromHtml(String url, String result) {
        List<ImageBean> list = parseHtml(url, result);
        imageBean.addAll(list);
        adapter.setList(imageBean);
        adapter.notifyDataSetChanged();
    }

    private List<ImageBean> parseHtml(String url, String result) {
        List<ImageBean> list = new ArrayList<>();
        Document doc = Jsoup.parse(result);
        List<Element> imgs = doc.getElementsByTag("img");
        for (Element img : imgs) {
            String src = img.attr("src");
            if (src.toLowerCase().endsWith("jpg") || src.toLowerCase().endsWith("png")) {
                src = checkSrc(url, src);
                ImageBean imageBean = new ImageBean(src);
                if (!imageUrlSet.contains(imageBean) && src.indexOf("/../") == -1) {
                    imageUrlSet.add(imageBean);
                    list.add(imageBean);
                }
            }
        }
        return list;

    }

    private String checkSrc(String url, String src) {
        if (src.startsWith("http")) {
            url = src;
        } else {
            if (src.startsWith("/")) {
                url = url + src;
            } else {
                url = url + "/" + src;
            }
        }
        return url;

    }

    public void showProgressDialog(String msg, boolean isHorizontal) {
        dialog = new ProgressDialog(this);
        if (isHorizontal) {
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        } else {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        dialog.setTitle("提示信息");
        dialog.setMessage(msg);
        dialog.show();
    }

    private String checkUrlPre(String url) {
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        return url;
    }

    public static boolean isEdit = false;
    private String url;
    private  int selectCount= 0;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isEdit = false;
    }

    private SearchView searchView;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu,menu);

        MenuItem item = menu.findItem(R.id.item_menu_search);
        searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("请输入网址");
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(listener);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(isEdit){
            isEdit = false;
            selectCount = 0;
            tv_pictures_info.setText("请在搜索框中输入网站网址");
            cb_pictures_selected.setChecked(false);
            cb_pictures_selected.setVisibility(View.GONE);
            iv_pictures_download.setVisibility(View.GONE);
            adapter.changeAllImagesCheckedStatus(false);
            adapter.notifyDataSetInvalidated();
        }else {
            super.onBackPressed();
        }
    }

    private HistoryDAO historyDAO;
    private List<HistoryUrl> historyUrls;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_menu_history :
                showHistory();
                break;

            case R.id.item_menu_local :
                showLocalDownloadImages();
                break;

            case android.R.id.home:
                finish();
                break;
            case R.id.deep:
                deepSeach(html);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLocalDownloadImages() {
        Constants.state = Constants.S_LOCAL;

        selectCount = 0;
        iv_pictures_download.setImageResource(R.drawable.op_del_press);
        iv_pictures_download.setVisibility(View.GONE);
        cb_pictures_selected.setChecked(false);
        isEdit = false;

        imageBean = getLocalImages(Constants.downloadPath);
        adapter.setList(imageBean);
        adapter.notifyDataSetChanged();

        tv_pictures_info.setText("本地一共搜索到"+imageBean.size()+"张");
    }

    private List<ImageBean> getLocalImages(String downloadPath) {
        List<ImageBean> list = new ArrayList<ImageBean>();

        File file = new File(Constants.downloadPath);
        File[] files = file.listFiles();
        if(files != null) {
            for (int i = 0 ; i<files.length;i++){
                ImageBean imageBean = new ImageBean(files[i].getAbsolutePath());
                list.add(imageBean);
            }

        }

        return list;
    }

    private void showHistory() {

        final String[] items = new String[historyUrls.size()];
        for(int i = 0;i<historyUrls.size();i++){
            items[i] = historyUrls.get(i).getUrl();
        }
        new AlertDialog.Builder(this)
                    .setTitle("显示历史记录")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getHttpImages(items[which]);
                        }
                    })
                    .show();
    }
    public void download(View v) {
        if(Constants.state == Constants.S_LOCAL) {
            for(int i=0;i<imageBean.size();i++){
                ImageBean imageBeans = imageBean.get(i);
                if(imageBeans.isChecked()){
                    File file = new File(imageBeans.getUrl());
                    if(file.exists()){
                        file.delete();
                    }
                    imageBean.remove(i);
                    i--;
                }

            }
            adapter.notifyDataSetChanged();
            cb_pictures_selected.setChecked(false);
            tv_pictures_info.setText("本地共有"+imageBean.size()+"张图片");

        }else if(Constants.state == Constants.S_WEB){
            showProgressDialog("正在下载中",true);
            dialog.setTitle("下载");
            dialog.setMax(selectCount);

            for(int i = 0; i < imageBean.size(); i++) {
                ImageBean imageBean = this.imageBean.get(i);
                if(imageBean.isChecked()){
                    downloadImages(imageBean.getUrl());

                    dialog.incrementProgressBy(1);
                    if(dialog.getProgress() == dialog.getMax()){
                        dialog.dismiss();
                    }
                }
            }
            Toast.makeText(WebPicturesActivity.this, "下载结束", Toast.LENGTH_SHORT).show();
            cb_pictures_selected.setChecked(false);
            tv_pictures_info.setText("请在搜索框中输入网址搜索");

        }

        isEdit = false;
        selectCount = 0;

        cb_pictures_selected.setVisibility(View.GONE);
        iv_pictures_download.setVisibility(View.GONE);
        adapter.changeAllImagesCheckedStatus(false);
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
                Toast.makeText(WebPicturesActivity.this, "下载失败"+url, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
