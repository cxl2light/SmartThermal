package com.hq.monitor.net.interceptors;

import com.hq.base.consts.GlobalConst;
import com.hq.base.util.Logger;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created on 2020/11/22
 * author :
 * desc :
 */
public class LogInterceptor implements Interceptor {
    private static final String TAG = "LogInterceptor";

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        /*return new Response.Builder()
                .request(chain.request())
                .message("Ok")
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .body(ResponseBody.create("{\n" +
                        "    \"softVer\" : \"1.0.00 Nov 16 2020 17:27:22\",\n" +
                        "    \"hardware\" : \"ZS-T400\",\n" +
                        "    \"devName\" : \"ZS-T400-00\"\n" +
                        "}", MediaType.parse("text/plain")))
                .build();*/
        final Response originalResponse = chain.proceed(chain.request());
        if (GlobalConst.isDebug()) {
            Logger.d(TAG, originalResponse.request().url().toString());
            final ResponseBody responseBody = originalResponse.body();
            if (responseBody != null) {
                final String data = responseBody.string();
                Logger.d(TAG, data);
                return originalResponse.newBuilder()
                        .body(ResponseBody.create(data, responseBody.contentType())).build();
            }
            Logger.d(TAG, "响应码：" + originalResponse.code());
            return originalResponse;
        }
        return originalResponse;
    }
}
