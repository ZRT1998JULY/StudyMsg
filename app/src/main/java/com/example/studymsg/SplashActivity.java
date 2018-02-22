package com.example.studymsg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Machenike on 2018/2/22.
 */

public class SplashActivity extends AppCompatActivity {
    private static final int TIME=3000;
    private static final int GO_MAIN=1;
    private static final int GO_GUIDE=2;
    private static boolean isFirstIn=false;

    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case GO_MAIN:
                    goMain();
                    break;
                case GO_GUIDE:
                    goGuide();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        init();
    }

    private void init(){
        SharedPreferences sf=getSharedPreferences("data",MODE_PRIVATE);
        isFirstIn=sf.getBoolean("isFirstIn",true);
        if(isFirstIn){
            mhandler.sendEmptyMessageDelayed(GO_GUIDE,TIME);
            SharedPreferences.Editor editor=sf.edit();
            editor.putBoolean("isFirstIn",false);
            editor.commit();

        }else {
            mhandler.sendEmptyMessageDelayed(GO_MAIN,TIME);
        }

    }
    private void goMain(){
        Intent intent=new Intent(SplashActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void goGuide(){
        Intent intent=new Intent(SplashActivity.this,GuideActivity.class);
        startActivity(intent);
        finish();

    }
}
