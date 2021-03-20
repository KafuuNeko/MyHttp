package cc.kafuu.myhttp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cc.kafuu.myhttp.R;
import cc.kafuu.myhttp.helper.Http;
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

        //用户单击请求按钮事件
        mRequestButton.setOnClickListener(v -> request());

        //用户是否选择使用GET方式提交，否则则为POST提交，且显示POST参数输入
        mIsGetRequest.setOnCheckedChangeListener((buttonView, isChecked) -> mRequestParamLayout.setVisibility(isChecked?View.GONE:View.VISIBLE));
    }

    private Map<String, String> makeHeadMap()
    {
        HashMap<String, String> item = new HashMap<>();

        for(String row : mRequestHeadText.getText().toString().split(";"))
        {
            String[] col = row.split("=");
            if(col.length == 2) item.put(col[0], col[1]);
        }

        return item;
    }

    private void request()
    {
        new Thread(() -> {
            mHandler.post(() -> mRequestButton.setEnabled(false));

            try {
                Response response = null;
                if(mIsGetRequest.isChecked())
                    response = Http.get(mRequestUrl.getText().toString(), makeHeadMap(), mRequestCookieText.getText().toString());
                else
                    response = Http.post(mRequestUrl.getText().toString(), mRequestParamText.getText().toString(), mRequestParamIsJson.isChecked(), makeHeadMap(), mRequestCookieText.getText().toString());

                final String result = Objects.requireNonNull(response.body()).string();
                mHandler.post(() -> mResultText.setText(result));

                final  String headers = response.headers().toString();
                mHandler.post(() -> mResponseHeadersText.setText(headers));

            } catch (Exception e) {
                final String result = e.getMessage();
                mHandler.post(() -> mResultText.setText(result));
            }

            mHandler.post(() -> mRequestButton.setEnabled(true));

        }).start();
    }

}