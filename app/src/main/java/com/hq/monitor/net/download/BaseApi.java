package com.hq.monitor.net.download;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * @author LvQiSheng
 * @date 2019/1/30
 */
public interface BaseApi {

    /**
     * 下载文件
     */
    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Header("Range") String range, @Url String url);

}
