package cc.kafuu.myhttp.helper;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Http {

    public static String getCookie(@NonNull Response response)
    {
        StringBuilder builder = new StringBuilder();
        for(String cookie : response.headers("Set-Cookie"))
        {
            builder.append(cookie);
            builder.append(';');
        }

        return builder.toString();
    }

    private static Request.Builder buildRequest(@NonNull String url, Map<String, String> head, String cookieString)
    {
        Request.Builder builder = new Request.Builder();

        builder.url(url);

        if(head != null)
        {
            for(Map.Entry<String, String> entry : head.entrySet())
                builder.addHeader(entry.getKey(), entry.getValue());
        }

        if(cookieString != null)
        {
            for(String cookie : cookieString.split(";"))
                builder.addHeader("cookie", cookie);
        }

        return builder;
    }

    /**
     * 根据头信息字符串构造一个Map
     * @param headText 头信息字符串
     * @return 构造结果
     */
    public static Map<String, String> makeHeadMap(String headText)
    {
        HashMap<String, String> item = new HashMap<>();

        for(String row : headText.split(";"))
        {
            String[] col = row.split("=");
            if(col.length == 2) item.put(col[0], col[1]);
        }

        return item;
    }

    public static Response get(@NonNull String url, Map<String, String> head, String cookieString)
    {
        Response response = null;

        try
        {
            Request request = buildRequest(url, head, cookieString).get().build();
            response = new OkHttpClient().newCall(request).execute();

        } catch (Exception e)
        {
            Log.d("Http.get", e.toString());
        }

        return response;
    }

    private static FormBody makeFormBody(String param)
    {
        FormBody.Builder builder = new FormBody.Builder();
        for(String row : param.split("&"))
        {
            String[] col = row.split("=");
            if(col.length == 2) builder.add(col[0], col[1]);
        }

        return builder.build();
    }


    public static Response post(@NonNull String url, String param, boolean isJson, Map<String, String> head, String cookieString)
    {
        Response response = null;

        try
        {
            RequestBody body;
            if(isJson)
                body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), param);
            else
                body = makeFormBody(param);

            Request request = buildRequest(url, head, cookieString).post(body).build();
            response = new OkHttpClient().newCall(request).execute();

        } catch (Exception e)
        {
            Log.d("Http.get", e.toString());
        }

        return response;
    }


}
