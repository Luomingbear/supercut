package com.slwb.util;

import android.text.TextUtils;
import android.util.Log;

import com.slwb.supercut.BuildConfig;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class OkHttpInvoker {
    private static final String TAG = OkHttpInvoker.class.getSimpleName();

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static final int READ_TIMEOUT = 30 * 1000;

    public static final int CONNECT_TIMEOUT = 30 * 1000;

    private static OkHttpClient mOkHttpClient;
    public static final int WHITE_TIMEOUT = 30 * 1000;

    static {
        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setReadTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);
        mOkHttpClient.setWriteTimeout(WHITE_TIMEOUT, TimeUnit.MILLISECONDS);
        mOkHttpClient.setConnectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public static String postJson(String url, String json) throws IOException {

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "#postJson: url=" + url + " json=" + json);
        }

        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = mOkHttpClient.newCall(request).execute();

        String strResponse = response.body().string();

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "postJson#get a response=" + strResponse);
        }

        return strResponse;
    }

    public static String postFiles(String url, String json, String voiceFileName)
            throws IOException {

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "#postFiles: url=" + url + " json=" + json);
        }

        MultipartBuilder multipartBuilder = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"data\""),
                        RequestBody.create(MediaType.parse("application/octet-stream"), json));

        if (!TextUtils.isEmpty(voiceFileName)) {
            multipartBuilder.addPart(
                    Headers.of("Content-Disposition", "form-data; name=\"voice\""),
                    RequestBody.create(MediaType.parse("application/octet-stream"), new File(voiceFileName)));
        }

        RequestBody body = multipartBuilder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = mOkHttpClient.newCall(request).execute();
        return response.body().string();
    }

    public static String postFiles(String url, String json, byte[] voiceByteArray)
            throws IOException {

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "#postFiles: url=" + url + " json=" + json);
        }

        MultipartBuilder multipartBuilder = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"data\""),
                        RequestBody.create(MediaType.parse("application/octet-stream"), json));

        if (voiceByteArray != null) {
            multipartBuilder.addPart(
                    Headers.of("Content-Disposition",
                            "form-data; name=\"voice\"; filename=\"voice.pcm\""),
                    RequestBody.create(
                            MediaType.parse("application/octet-stream"),
                            voiceByteArray));
        }

        RequestBody body = multipartBuilder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = mOkHttpClient.newCall(request).execute();
        return response.body().string();
    }

    //    public static byte[] getByteArray(String url) throws IOException {
//        Request request = new Request.Builder()
//                .url(url)
//                .build();
//
//        Response response = mOkHttpClient.newCall(request).execute();
//
//        if (response.isSuccessful()) {
//            AppLog.d(TAG, "#getByteArray: Content-Length=" + response.body().contentLength());
//            return response.body().bytes();
//        }
//        return null;
//    }

//    public static byte[] getByteArray(String url) throws IOException {
//
//        Log.d(TAG, "#getByteArray get bytes from url: " + url);
//
//        URL httpUrl = new URL(url);
//        HttpURLConnection httpURLConnection = (HttpURLConnection) httpUrl.openConnection();
//
//        // 定制链接方式
//        httpURLConnection.setDoInput(true); // 设置允许输入
//        httpURLConnection.setDoOutput(true); // 设置允许输出
//        httpURLConnection.setUseCaches(false); // 设置不可缓存
//        httpURLConnection.setRequestMethod("POST"); // 设置请求方式
//        httpURLConnection.setConnectTimeout(CONNECT_TIMEOUT); // 设置联网超时时间
//        httpURLConnection.setReadTimeout(READ_TIMEOUT); // 设置读取输入流的超时时间
//
//        if (httpURLConnection.getResponseCode() == 200) { // 网络请求成功
//            InputStream inputStream = httpURLConnection.getInputStream();
//            return inputStreamToString(inputStream);
//        }
//        return null;
//    }

    public static byte[] getByteArray(String url) throws IOException {
        //AppLog.d(TAG, "getByteArray", "url=" + url);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept-Encoding", "identity")
                .build();

        Response response = mOkHttpClient.newCall(request).execute();

        if (response.isSuccessful()) {
            return streamToBytes(response.body().byteStream());
        } else {
            //AppLog.d(TAG, "getByteArray", "Content-Length=" + response.body().contentLength());
        }
        return null;
    }

    public static byte[] streamToBytes(InputStream is) throws IOException {
        if (is == null) return null;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            //AppLog.d(TAG, "streamToBytes", "#streamToBytes read end");
        } finally {
            is.close();
            outputStream.flush();
            outputStream.close();
            //AppLog.d(TAG, "streamToBytes", "stream closed");
        }
        return outputStream.toByteArray();
    }


    public static byte[] inputStreamToString(InputStream is) {
        if (is == null) {
            return null;
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int i;
            while ((i = is.read()) != -1) {
                baos.write(i);
            }
            //AppLog.d(TAG, "inputStreamToString", "#inputStreamToString read end");

            is.close();
            baos.flush();
            baos.close();
            return baos.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    /**
     * get string from input stream
     *
     * @param inputStream input stream
     * @return string from input stream
     * @throws IOException
     */
    public static String StreamToString(InputStream inputStream) throws IOException {
        String data;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        outputStream.close();
        data = outputStream.toString();
        return data;
    }

    /**
     * 下载文件到指定目录
     *
     * @param url      文件下载地址
     * @param fileName 文件下载路径
     * @return 文件路径，若下载失败返回null
     * @throws IOException 网络连接错误
     */
    public static File getFile(String url, String fileName) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = mOkHttpClient.newCall(request).execute();

        if (response.isSuccessful()) {
            InputStream inputStream = response.body().byteStream();
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            return new File(fileName);
        }
        return null;
    }
}
