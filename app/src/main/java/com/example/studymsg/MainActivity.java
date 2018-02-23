package com.example.studymsg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {

    private EditText studentNumber; //账号
    private EditText passWord; //密码
    private EditText idCode;   //验证码
    private Bitmap bitmap;
    private ImageView IdcodeImage; //验证码图片
    private Button logIn;   //登录按钮
    private CheckBox rememeberPass;

    private Handler handler;
    private SharedPreferences pref;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String StudentNumber;
    String PassWord;
    String IdCode;

    String groupId="";
    String login="登录";

    //获取验证码图片的网址
    //String ur12="http://idas.uestc.edu.cn/authserver/custom/js/icheck.min.js";
    String ur12="http://jw.djtu.edu.cn/academic/getCaptcha.do";
    //提交登录信息的网址
    String ur13="http://idas.uestc.edu.cn/authserver/custom/js/jquery-1.7.1.min.js";

    HttpClient client;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化控件
        initEvent();

        //获取验证码
        getIdCode();
        //对我们的验证码绑定一个单击响应事件，这是为了去实现验证码看不清时再更新一张验证码而用
        IdcodeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIdCode();
            }
        });
        //对登录按钮绑定单击响应事件
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEvent();
                loginEvent();
            }
        });

    }



    public void initEvent(){

        client=new DefaultHttpClient();
        //保存账号与密码
        sharedPreferences=getSharedPreferences("params", Context.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象
        editor=sharedPreferences.edit();
        pref=PreferenceManager.getDefaultSharedPreferences(this);

        //引入控件布局
        studentNumber=(EditText)findViewById(R.id.studentNumber); //学号
        passWord=(EditText)findViewById(R.id.key); //密码
        IdcodeImage=(ImageView)findViewById(R.id.passImage); //验证码图片
        idCode=(EditText)findViewById(R.id.identifyingCode); //验证码
        logIn=(Button)findViewById(R.id.login); //登录按钮
        handler =new Handler();
        rememeberPass=(CheckBox)findViewById(R.id.remember_pass);
        boolean isRemember=pref.getBoolean("remember_password",false);
        if(isRemember){
            String getstudentNumber=pref.getString("StudentNumber","");
            String getPassWord=pref.getString("PassWord","");
            studentNumber.setText(getstudentNumber);
            passWord.setText(getPassWord);
            rememeberPass.setChecked(true);
        }

    }

    public void saveEvent()
    {
        //获取输入信息，并保存为做记住密码来铺垫
        StudentNumber =studentNumber.getText().toString();
        PassWord =passWord.getText().toString();
        IdCode = idCode.getText().toString();
        //这里写入StudentNumber和PassWord是为了做记住密码登录
        if(rememeberPass.isChecked()) {
            editor.putBoolean("rememeber_password",true);
            editor.putString("StudentNumber", StudentNumber);
            editor.putString("PassWord", PassWord);
        }else {
            editor.clear();
        }
        editor.apply();
    }

    public void getIdCode(){
        new Thread(new Runnable() {
            @Override
            public void run() {
               List<org.apache.http.cookie.Cookie> cookies1;
               HttpGet httpGet=new HttpGet(ur12);
                HttpResponse httpResponse = null;
                try {
                    //实例化HttpResponse
                    httpResponse = client.execute(httpGet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    try {
                        //使用输入流来接受数据
                        InputStream in = httpResponse.getEntity().getContent();
                        //bitmap来获取数据流中的图片信息
                        bitmap = BitmapFactory.decodeStream(in);
                        //关闭输入流
                        in.close();
                        String Cookies;
                        //获取Cookie
                        cookies1 = ((AbstractHttpClient) client).getCookieStore().getCookies();
                        Cookies = "JSESSIONID="+cookies1.get(0).getValue().toString();
                        //System.out.println(Cookies);
                        //在SharedPreferences中保存cookie
                        editor.putString("Cookies", Cookies);
                        //提交保存数据
                        editor.commit();
                        //通过handler.post方法在线程中更新主线程中的验证码图片信息
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (bitmap != null) {
                                    IdcodeImage.setImageBitmap(bitmap);
                                }
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
    public void loginEvent()
    {

        new Thread() {
            @Override
            public void run() {
                //提交数据用List<NameValuePair>的方式
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                //这里的名称不要有多余的符号，因为提交数据时httppost方法会帮你维护数据
                //这里表单的数据顺序要按照刚刚解析所显示的顺序排列
                params.add(new BasicNameValuePair("groupId", groupId));
                params.add(new BasicNameValuePair("j_username", StudentNumber));
                params.add(new BasicNameValuePair("login",login));
                params.add(new BasicNameValuePair("j_password", PassWord));
                params.add(new BasicNameValuePair("j_captcha", IdCode));
                System.out.println(params);
                try {
                    HttpPost httpPost = new HttpPost(ur13);
                    String Cookies;
                    //获取到刚刚在获取验证码时得到的Cookie
                    Cookies = sharedPreferences.getString("Cookies", null);
                    //System.out.println(Cookies);
                    //提交数据做准备
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                    //httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.name()));
                    //同步cookie
                    httpPost.setHeader("Cookie", Cookies);
                    //获取返回的信息
                    HttpResponse httpResponse = client.execute(httpPost);
                    String result = EntityUtils.toString(httpResponse.getEntity());
                    //System.out.println(result);
                    //这里我们不仅需要保证服务器正常响应，而且还要知道当我们登陆失败时是什么原因导致的
                    if(!result.contains("错误提示")&&httpResponse.getStatusLine().getStatusCode() == 200)
                    {
                        startActivity(new Intent(MainActivity.this, MyCourse.class));
                    }
                    else
                    {
                        if(result.contains("密码不匹配"))
                        {

                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    Toast.makeText(MainActivity.this, "密码不匹配或用户名错误!!!请重新输入", Toast.LENGTH_LONG).show();
                                    //当登陆失败时上一张验证码的图片已经失效因此需重新加载
                                    getIdCode();

                                }
                            });
                        }else if(result.contains("验证码错误")||result.contains("验证码不正确"))
                        {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "验证码错误!!!请重新输入", Toast.LENGTH_LONG).show();
                                    getIdCode();
                                }
                            });
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
}
