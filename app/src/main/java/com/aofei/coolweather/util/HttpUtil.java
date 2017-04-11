package com.aofei.coolweather.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by kenway on 17/4/11 18:27
 * Email : xiaokai090704@126.com
 */

public class HttpUtil {
    /**
     * 发送请求
     * @param address  地址
     * @param listener 接口
     */
    public static void sendHttpRequest(final String address, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    URL url = new URL(address);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(8000);
                    conn.setConnectTimeout(8000);
                    InputStream stream = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuffer buffer= new StringBuffer();
                    String line="";
                    while ((line=reader.readLine())!=null){
                        buffer.append(line);
                    }
                    if (listener!=null){
                        //回调onFinish()方法
                        listener.finish(buffer.toString());
                    }
                } catch (IOException e) {
                    if (listener!=null){
                        //回调onError()
                        listener.error(e.toString());
                    }
                    e.printStackTrace();
                }finally {
                    if (conn!=null){
                        conn.disconnect();
                    }
                }
            }
        }).start();
    }
}
