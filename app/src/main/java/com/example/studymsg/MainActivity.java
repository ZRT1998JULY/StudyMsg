package com.example.studymsg;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private EditText studentNumber; //账号
    private EditText passWord; //密码
    private EditText idCode;   //验证码
    private Bitmap bitmap;
    private ImageView IdcodeImage; //验证码图片
    private Button logIn;   //登录按钮

    String StudengNumber;
    String PassWord;
    String IdCode;

    String groupId="";
    String login="登录";

    //获取验证码图片的网址
    String ur12="";
    //提交登录信息的网址
    String ur13="";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
