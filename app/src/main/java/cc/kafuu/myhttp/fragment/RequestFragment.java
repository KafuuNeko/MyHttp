package cc.kafuu.myhttp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cc.kafuu.myhttp.dialog.EncodingSelectorDialog;
import cc.kafuu.myhttp.R;
import cc.kafuu.myhttp.helper.Http;
import cc.kafuu.myhttp.helper.LogDatabase;
import okhttp3.Response;

public class RequestFragment extends Fragment {
    private Handler mHandler = null;
    private View mRootView = null;
    private EditText mRequestUrl = null;
    private Button mRequestButton = null;
    private EditText mResultText = null;
    private EditText mRequestCookieText = null;
    private EditText mRequestHeadText = null;
    private EditText mResponseHeadersText = null;
    private RadioButton mIsGetRequest = null;
    private EditText mRequestParamText = null;
    private LinearLayout mRequestParamLayout = null;
    private RadioButton mRequestParamIsJson = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView == null)
        {
            mRootView = inflater.inflate(R.layout.fragment_request, container, false);
            initView();
        } else {
            ViewGroup parent = (ViewGroup) container.getParent();
            parent.removeView(mRootView);
        }

        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRootView = null;
    }

    /**
     * 初始化视图
     */
    private void initView()
    {
        mHandler = new Handler(Looper.getMainLooper());

        mRequestUrl = mRootView.findViewById(R.id.requestUrl);
        mRequestButton = mRootView.findViewById(R.id.requestButton);
        mResultText = mRootView.findViewById(R.id.resultText);
        mRequestHeadText = mRootView.findViewById(R.id.requestHeadText);
        mResponseHeadersText = mRootView.findViewById(R.id.responseHeadersText);
        mRequestCookieText = mRootView.findViewById(R.id.requestCookieText);
        mIsGetRequest = mRootView.findViewById(R.id.isGetRequest);
        mRequestParamText = mRootView.findViewById(R.id.requestParamText);
        mRequestParamLayout = mRootView.findViewById(R.id.requestParamLayout);
        mRequestParamIsJson = mRootView.findViewById(R.id.requestParamIsJson);

        Button urlEncodingButton = mRootView.findViewById(R.id.urlEncodingButton);
        //短按默认使用UTF8
        urlEncodingButton.setOnClickListener(v -> onUrlEncodingClick(false));
        //长按用户选择编码方式
        urlEncodingButton.setOnLongClickListener((View.OnLongClickListener) v -> onUrlEncodingClick(true));

        //用户单击请求按钮事件
        mRequestButton.setOnClickListener(v -> request());

        //用户是否选择使用GET方式提交，否则则为POST提交，且显示POST参数输入
        mIsGetRequest.setOnCheckedChangeListener((buttonView, isChecked) -> mRequestParamLayout.setVisibility(isChecked?View.GONE:View.VISIBLE));
    }


    /**
     * UrlEncoding按钮被点击事件
     * @param longClick 是否为长按触发
     */
    private boolean onUrlEncodingClick(boolean longClick)
    {
        if(!longClick)
            urlEncoding("UTF-8");
        else
            new EncodingSelectorDialog(getContext()).setListener((dialog, charset) -> urlEncoding(charset.name())).show();

        //此处返回值为长按事件监听器返回值
        return true;
    }

    /**
     * 使用指定编码对URL输入框内容进行URL编码
     * @param name 指定的编码名称
     */
    private void urlEncoding(String name)
    {
        try {
            mRequestUrl.setText(URLEncoder.encode(mRequestUrl.getText().toString(), name));
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(getContext(), "Coding failure", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 提交请求
     * 用户点击请求后将触发此函数，根据用户输入提交HTTP请求
     */
    private void request()
    {
        if(mRequestUrl.getText().toString().length() == 0)
        {
            Toast.makeText(getContext(), "Url is null", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            mHandler.post(() -> mRequestButton.setEnabled(false));

            final String url = mRequestUrl.getText().toString();
            final boolean isGet = mIsGetRequest.isChecked();
            final String requestHeadText = mRequestHeadText.getText().toString();
            final String requestCookieText = mRequestCookieText.getText().toString();
            final String requestParam = mRequestParamText.getText().toString();
            final boolean requestParamIsJson = mRequestParamIsJson.isChecked();

            String responseResult = null;
            String responseHeaders = null;
            try {
                Response response = null;
                if(isGet)
                    response = Http.get(url, Http.makeHeadMap(requestHeadText), requestCookieText);
                else
                    response = Http.post(url, requestParam, requestParamIsJson, Http.makeHeadMap(requestHeadText), requestCookieText);

                responseResult = Objects.requireNonNull(response.body()).string();
                final String result = responseResult;
                mHandler.post(() -> mResultText.setText(result));

                responseHeaders = response.headers().toString();
                final  String headers = responseHeaders;
                mHandler.post(() -> mResponseHeadersText.setText(headers));

            } catch (Exception e) {
                responseResult = e.getMessage();
                final String result = responseResult;
                mHandler.post(() -> mResultText.setText(result));
            }

            mHandler.post(() -> mRequestButton.setEnabled(true));

            //将本次请求信息写入数据库
            LogDatabase database = new LogDatabase(Objects.requireNonNull(getContext()));
            database.addNewPostGetLog(url, isGet, requestParam, requestParamIsJson, requestHeadText, requestCookieText, responseResult, responseHeaders);
            database.close();

            //发送请求完成广播，以便日志动态更新
            mHandler.post(()-> LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent("Broadcast.PostGet.Request.Complete")));

        }).start();
    }

}