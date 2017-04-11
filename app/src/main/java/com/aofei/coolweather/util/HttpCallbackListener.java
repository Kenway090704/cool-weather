package com.aofei.coolweather.util;

/**
 * Created by kenway on 17/4/11 18:28
 * Email : xiaokai090704@126.com
 */
public interface HttpCallbackListener {
    /**
     * 请求成功
     * @param response 返回的数据
     */
    void finish(String response);

    /**
     * 请求错误时调用
     * @param msg
     */
    void error(String msg);
}
