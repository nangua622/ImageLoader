package com.example.imageloader.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.imageloader.R;
import com.example.imageloader.utils.AppUtils;

import org.xutils.x;

/**
 * Created by nBB on 16/6/23.
 */
public class ImageFragment extends Fragment {

    private String imagePath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ImageView view = (ImageView) View.inflate(getActivity(), R.layout.item_drag_image, null);
        x.image().bind(view, imagePath, AppUtils.bigImageOptions);
        return view;
    }

    public static Fragment getInstance(String imagePath) {
        ImageFragment fragment = new ImageFragment();
        fragment.imagePath = imagePath;
        return fragment;
    }
}
