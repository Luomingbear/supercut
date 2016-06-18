package com.slwb.supercut;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.slwb.util.BaseJsonRequest;
import com.slwb.util.BaseRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jdesktop.application.ApplicationContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Login extends Activity {
    private SharedPreferences share;
    private SharedPreferences.Editor edit;
    private EditText username, password;
    private Button denglu;
    private CheckBox check;
    private String user, pass;
    private static final String TAG = "Login";

    String password_text;
    String username_text;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        share = super.getSharedPreferences("persondata", MODE_PRIVATE);//实例化
        //share=PreferenceManager.getDefaultSharedPreferences(this);//实例化SharedPreferences对象
        username = (EditText) findViewById(R.id.username_zhuce);
        password = (EditText) findViewById(R.id.password);
        denglu = (Button) findViewById(R.id.denglu);
        check = (CheckBox) findViewById(R.id.remember);//记住密码

        boolean judge = share.getBoolean("remember_password", false);//获取judge中的值，如果没有值则默认为false

        if (judge) {
            String userphone_text = share.getString("phone", "");
            String password_text = share.getString("password", "");
            username.setText(userphone_text);
            password.setText(password_text);
            check.setChecked(true);
        }
        denglu.setOnClickListener(new OnClickListener() {//登录时的数据处理
            public void onClick(View source) {
                username_text = username.getText().toString();
                password_text = password.getText().toString();
                user = share.getString("phone", "");
                pass = share.getString("password", "");


                //
                List<NameValuePair> paramList = new ArrayList<NameValuePair>();
                paramList.add(new BasicNameValuePair("UserPhone", username_text));
                paramList.add(new BasicNameValuePair("UserPass", password_text));
                //

                final LoginRequest loginRequest = new LoginRequest(paramList);

                loginRequest.setOnResponseListener(new BaseRequest.OnResponseListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        edit = share.edit();//实例化SharedPreferences的操作对象，使他可以操作数据的增删改查
                        if (check.isChecked()) {
                            edit.putBoolean("remember_password", true);//添加数据
                            edit.putString("phone", username_text);
                            edit.putString("password", password_text);
                        } else {
                            edit.clear();//如果checkbox没有打钩，则进行消除数据
                        }
                        edit.apply();
                        Toast.makeText(Login.this, "登陆成功", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Login.this, WorkStation.class);

                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        // 启动intent对应的Activity
                        startActivity(intent);


                    }

                    @Override
                    public void onFail(String message) {

                        Toast.makeText(Login.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                    }
                });

                loginRequest.request();


                //

            }
        });


        // 获取应用程序中的个人信息按钮
        Button bn_register = (Button) findViewById(R.id.button_register);
        // 为个人信息按钮绑定事件监听器
        bn_register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View source) {
                // 创建需要启动的Activity对应的Intent
                Intent intent = new Intent(Login.this, Register.class);
                // 启动intent对应的Activity
                startActivity(intent);
            }
        });
    }

    class LoginRequest extends BaseJsonRequest<Void> {

        List<NameValuePair> paramList;

        public LoginRequest(List<NameValuePair> paramList) {
            this.paramList = paramList;
        }

        @Override
        protected JSONObject json() throws JSONException {
            JSONObject json = new JSONObject();
            for (NameValuePair nameValuePair : paramList) {
                json.put(nameValuePair.getName(), nameValuePair.getValue());
            }
            return json;
        }

        @Override
        protected String methodName() {
            return "index/login";
        }

        @Override
        protected Void parseResponse(JSONObject response) throws JSONException {
            return null;

        }
    }


}