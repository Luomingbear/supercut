package com.slwb.util;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 所有网络操作的基础类
 *
 * @author 王鑫
 *         Created by 王鑫 on 2015/9/16.
 */
public abstract class
BaseRequest<T> {

    /**
     * 网络请求的域名
     */

    public static final String BASE_URL = "http://ssssssss";//

    /**
     * 网络请求的返回结果监听器
     */
    private OnResponseListener<T> mOnResponseListener;

    /**
     * 子类通过实现此方法指定网络请求的具体地址，如 return {@link #BASE_URL} + "userLogin";
     *
     * @return 网络请求的具体地址。
     */
    protected abstract String methodName();

    /**
     * 子类通过实现此方法对网络请求的返回内容进行解析。
     *
     * @param response 网络请求内包含的具体内容，类型为JSONObject。
     *                 注意：这里传入的值为返回数据的最外层封装，"data"字段的数据需自行提取。
     * @return 解析完成后的类型由子类继承父类时指定的泛型决定.
     * @throws JSONException
     */
    protected abstract T parseResponse(JSONObject response) throws JSONException;

    /**
     * 当服务器返回异常数据时（注意：此时服务器正常连接并返回数据，不属于服务器连接异常），
     * 会调用此方法解析异常情况对应的状态码，状态码及其对应的异常信息请查看接口文档。
     * 注意：此方法的返回异常信息描述将直接显示在用户界面上。
     *
     * @param resultCode 异常情况对应的状态码，具体情况请查看接口文档。
     * @return 异常信息描述。
     */
    protected String parseErrorMessage(int resultCode) {
        switch (resultCode) {
            case -2: // 服务器异常，服务器出现bug，如空指针
                return "服务器异常";
            case -1: // 未收到参数或参数类型有误
                return "参数有误";
            case 1:  // 收到的参数不合法，如用户密码错误
                return "参数不合法";
            case 2:  // 未登录
                return "登录信息失效请重新登录";
            case 3:  // sessionId过期
                return "登录信息失效请重新登录";
            case 4:  // 帐号被挤下线
                return "帐号被挤下线";
            case 5:  // 未查询到数据
                return "未查询到数据";
            default:
                return parseErrorMessageWithCode(resultCode);
        }
    }

    /**
     * 当服务器返回异常数据时（注意：此时服务器正常连接并返回数据，不属于服务器连接异常），
     * 会调用{@link #parseErrorMessage(int)}进行异常情况解析，当对应的{@code code}不在
     * {@link #parseErrorMessage(int)}的解析范围内时会调用此方法进行解析。子类可以通过重写
     * 此方法对特定的状态码进行解析。默认情况下返回null。若返回null则会自动调用返回数据中的"resultCode"字
     * 段对应的异常信息，将其作为异常信息描述，用于提示用户。
     *
     * @param code 异常信息状态码，具体情况请查看接口文档。
     * @return 异常信息的描述。
     */
    @SuppressWarnings("unused")
    protected String parseErrorMessageWithCode(int code) {
        return null;
    }

    /**
     * 开启线程，异步进行网络请求。<br/>
     * 默认情况下会将网络请求加入应用的网络请求线程池{@link NetworkThreadPoolExecutor}中。
     */
    public void request() {
        new RequestTask().executeOnExecutor(NetworkThreadPoolExecutor.getInstance());
    }

    /**
     * 每个子类重写此方法实现具体的联网操作，并返回解析后的数据<br/>
     * 注意：此方法是在异步执行！
     */
    public abstract Response<T> getResponse();

    /**
     * 添加网络请求的监听器<br/>
     * 请务必在执行{@link #request()}之前调用此方法
     *
     * @param listener listener
     */
    public void setOnResponseListener(OnResponseListener<T> listener) {
        mOnResponseListener = listener;
    }


    public interface OnResponseListener<Response> {
        /**
         * 当网络请求成功时会调用该方法
         *
         * @param response 返回数据
         */
        void onSuccess(Response response);

        /**
         * 当网络请求失败后会调用该方法
         *
         * @param message 失败信息
         */
        void onFail(String message);
    }

    /**
     * 返回数据解析后的统一格式
     *
     * @param <T>
     */
    public static class Response<T> {
        /**
         * 用于表征网络请求成功，并返回数据（注意：未查询到数据不包括在内）。默认为失败
         */
        public boolean isSuccess;
        /**
         * 连接失败是的提示信息，若连接成功则为null
         */
        public String errorMessage;
        /**
         * 连接成功后的返回信息
         */
        public T data;
    }

    /**
     * 用于异步进行网络请求：<br/>
     * 检查url、请求数据<br/>
     * 发送数据<br/>
     * 解析返回数据<br/>
     * 执行回调方法
     */
    private final class RequestTask extends AsyncTask<Void, Void, Response<T>> {
        /**
         * 调用子类的网络连接方法进行联网获取数据
         */
        @Override
        protected Response<T> doInBackground(Void... params) {
            if (DEBUG) {
                Response<T> response = new Response<T>();
                response.isSuccess = true;
                response.data = getDebugData();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return response;
            }

            return getResponse();
        }

        @Override
        protected void onPostExecute(Response<T> response) {

            //AppLog.e("Response", "onPostExecute", response.errorMessage);
            if (mOnResponseListener != null) {
                if (response.isSuccess) {
                    mOnResponseListener.onSuccess(response.data);
                } else {
                    mOnResponseListener.onFail(response.errorMessage);
                }
            }
        }
    }

    // 在DEBUG模式下任何操作都会成功
    public static boolean DEBUG = false;//TODO 卧槽一定要记得关(false)啊，不然接口都通了啊！！！


    protected T getDebugData() {
        return null;
    }
}

