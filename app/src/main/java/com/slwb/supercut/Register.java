package com.slwb.supercut;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.slwb.util.BaseJsonRequest;
import com.slwb.util.BaseRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends Activity {
    //private static final String fileName = "sharedfile";// 定义保存的文件的名称
    private TextView username = null;
    private TextView password = null;
    private TextView password_check = null;
    private TextView phone = null;
    private SharedPreferences.Editor edit;
    private SharedPreferences share;
    private String username_text, password_text, phone_text, passworldcheck_text;


    //注册网址接口
    private String url = "customer/customerCreate";

    //
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        username = (TextView) findViewById(R.id.username_zhuce);
        password = (TextView) findViewById(R.id.password_zhuce);
        password_check = (TextView) findViewById(R.id.passwordcheck_zhuce);
        phone = (TextView) findViewById(R.id.phone_zhuce);

        share = super.getSharedPreferences("persondata", MODE_PRIVATE);
        edit = share.edit();//实例化SharedPreferences的操作对象，使他可以操作数据的增删改查
        //

        Button bn_register = (Button) findViewById(R.id.zhuce);
        // 为个人信息按钮绑定事件监听器
        bn_register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View source) {
                username_text = username.getText().toString();
                password_text = password.getText().toString();
                passworldcheck_text = password_check.getText().toString();
                phone_text = phone.getText().toString();

                edit.putBoolean("remember_password", true);//添加数据
                edit.putBoolean("dengluguo", true);//添加数据
                edit.putString("username", username_text);
                edit.putString("password", password_text);
                edit.putString("phone", phone_text);
                edit.apply();    //提交数据保存
                if (!password_text.equalsIgnoreCase(passworldcheck_text)) {
                    Toast.makeText(Register.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                } else if (!isPhone(phone_text)) {
                    Toast.makeText(Register.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                } else {


                    List<NameValuePair> paramList = new ArrayList<NameValuePair>();
                    paramList.add(new BasicNameValuePair("UserPhone", phone_text));
                    paramList.add(new BasicNameValuePair("UserPass", password_text));
                    paramList.add(new BasicNameValuePair("UserNick", username_text));

                    RegisterRequest request = new RegisterRequest(paramList);
                    request.setOnResponseListener(new BaseRequest.OnResponseListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent intent = new Intent(Register.this, WorkStation.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }

                        @Override
                        public void onFail(String message) {
                            Toast.makeText(Register.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                    request.request();


                }
            }
        });
    }

    public static boolean isEmail(String email) {
        if (null == email && "".equals(email)) {
            return false;

        }
        return email.matches("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$");
    }

    public static boolean isPhone(String phone) {
        String ss = phone;
        String regExp = "^[1]([3-8][0-9]{1}|59|58|88|89)[0-9]{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(ss);
        return m.find();

    }

    class RegisterRequest extends BaseJsonRequest<Void> {

        List<NameValuePair> paramList;

        public RegisterRequest(List<NameValuePair> paramList) {
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
            return "customer/customerCreate";
        }

        @Override
        protected Void parseResponse(JSONObject response) throws JSONException {
            return null;
        }
    }


}
