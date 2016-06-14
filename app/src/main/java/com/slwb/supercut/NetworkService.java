package com.slwb.supercut;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by bear on 16-6-13.
 */
public class NetworkService {

    private static String TAG = "NetworkService";

    //private static String url_ip = ServerUrl.SERVER_ADRESS+"UserInfoServlet?";
    private static String url_ip = "http://115.159.110.165:8002/";

    /**
     * 释放资源
     */
    public static void cancel() {
        Log.i(TAG, "cancel!");
        // if(conn != null) {
        // conn.cancel();
        // }
    }

    //无参数传递的
    public static String getPostResult(String url) {

        url = url_ip + url;
        //创建http请求对象
        HttpPost post = new HttpPost(url);

        //创建HttpParams以用来设置HTTP参数
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 10 * 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 10 * 1000);

        //创建网络访问处理对象
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        try {
            //执行请求参数?
            HttpResponse response = httpClient.execute(post);
            //判断是否请求成功
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                //获得响应信息
                String content = EntityUtils.toString(response.getEntity());
                return content;
            } else {
                //网连接失败，使用Toast显示提示信息

            }

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":405,\"resultMsg\":\"网络超时！\"}";
        } finally {
            //释放网络连接资源
            httpClient.getConnectionManager().shutdown();
        }
        return "{\"status\":405,\"resultMsg\":\"网络超时！\"}";

    }

    //有参数传递的
    public static String getPostResult(String url, List<NameValuePair> paramList) {

        url = url_ip + url;


        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(paramList, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        //创建http请求对象
        HttpPost post = new HttpPost(url);

        BasicHttpParams httpParams = new BasicHttpParams();

        HttpConnectionParams.setConnectionTimeout(httpParams, 10 * 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 10 * 1000);
        post.setEntity(entity);
        //创建网络访问处理对象
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        try {
            //执行请求参数?
            HttpResponse response = httpClient.execute(post);
            //判断是否请求成功
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                //获得响应信息
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                return content;
            } else {
                Log.e("NetworkService:", "网络链接失败");

            }

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":405,\"resultMsg\":\"网络超时！\"}";
        } finally {
            //释放网络连接资源
            httpClient.getConnectionManager().shutdown();
        }
        return "{\"status\":405,\"resultMsg\":\"网络超时！\"}";

    }
}


