package cc.kafuu.myhttp.fragment;

import android.content.Context;
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
import android.widget.TextView;
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

        if (mRootView == null) {
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
     * ???????????????
     */
    private void initView() {
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
        //??????????????????UTF8
        urlEncodingButton.setOnClickListener(v -> onUrlEncodingClick(false));
        //??????????????????????????????
        urlEncodingButton.setOnLongClickListener((View.OnLongClickListener) v -> onUrlEncodingClick(true));

        //??????????????????????????????
        mRequestButton.setOnClickListener(v -> request());

        //????????????????????????GET???????????????????????????POST??????????????????POST????????????
        mIsGetRequest.setOnCheckedChangeListener((buttonView, isChecked) -> mRequestParamLayout.setVisibility(isChecked ? View.GONE : View.VISIBLE));
    }


    /**
     * UrlEncoding?????????????????????
     *
     * @param longClick ?????????????????????
     */
    private boolean onUrlEncodingClick(boolean longClick) {
        if (!longClick)
            urlEncoding("UTF-8");
        else
            new EncodingSelectorDialog(getContext()).setListener((dialog, charset) -> urlEncoding(charset.name())).show();

        //????????????????????????????????????????????????
        return true;
    }

    /**
     * ?????????????????????URL?????????????????????URL??????
     *
     * @param name ?????????????????????
     */
    private void urlEncoding(String name) {
        try {
            mRequestUrl.setText(URLEncoder.encode(mRequestUrl.getText().toString(), name));
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(getContext(), "Coding failure", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * ????????????
     * ??????????????????????????????????????????????????????????????????HTTP??????
     */
    private void request() {
        if (mRequestUrl.getText().toString().length() == 0) {
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
                if (isGet)
                    response = Http.get(url, Http.makeHeadMap(requestHeadText), requestCookieText);
                else
                    response = Http.post(url, requestParam, requestParamIsJson, Http.makeHeadMap(requestHeadText), requestCookieText);

                responseResult = Objects.requireNonNull(response.body()).string();
                final String result = responseResult;
                mHandler.post(() -> mResultText.setText(result));

                responseHeaders = response.headers().toString();
                final String headers = responseHeaders;
                mHandler.post(() -> mResponseHeadersText.setText(headers));

            } catch (Exception e) {
                responseResult = e.getMessage();
                final String result = responseResult;
                mHandler.post(() -> mResultText.setText(result));
            }

            mHandler.post(() -> mRequestButton.setEnabled(true));

            //????????????????????????????????????
            LogDatabase database = new LogDatabase(Objects.requireNonNull(getContext()));
            database.addNewPostGetLog(url, isGet, requestParam, requestParamIsJson, requestHeadText, requestCookieText, responseResult, responseHeaders);
            database.close();

            //???????????????????????????????????????????????????
            mHandler.post(() -> LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent("Broadcast.PostGet.Request.Complete")));

        }).start();
    }
    
}