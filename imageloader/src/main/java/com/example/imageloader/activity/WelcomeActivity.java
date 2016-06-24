package com.example.imageloader.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;

import com.example.imageloader.R;

public class WelcomeActivity extends Activity {
    private RelativeLayout rl_welcome;
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what) {
                case  1:
                    startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                    finish();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        rl_welcome = (RelativeLayout)findViewById(R.id.rl_welcome);

        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(2000);
        rl_welcome.setAnimation(animation);

        handler.sendEmptyMessageDelayed(1,2000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
