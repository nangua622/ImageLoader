package com.example.imageloader.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.imageloader.R;

import org.xutils.image.ImageOptions;

/**
 * Created by nBB on 16/6/21.
 */
public class AppUtils {
    public static Context context;
    public static void setContext(Context context){
        AppUtils.context = context;
    }

    public static ImageOptions smallImageOptions;
    public static ImageOptions bigImageOptions;
    static {
        smallImageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.FIT_CENTER) //等比例放大/缩小到充满长/宽居中显示
                .setLoadingDrawableId(R.drawable.default_image)
                .setFailureDrawableId(R.drawable.default_image)
                //.setConfig(Bitmap.Config.RGB_565)
                .build();

        bigImageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_INSIDE)//等比例缩小到充满长/宽居中显示, 或原样显示
                .setLoadingDrawableId(R.drawable.default_image)
                .setFailureDrawableId(R.drawable.default_image)
                .setConfig(Bitmap.Config.ARGB_8888)
                .build();
    }

    public static String getImageName(String url) {

        int i = url.lastIndexOf("/") + 1;
        return url.substring(i);


    }
}
