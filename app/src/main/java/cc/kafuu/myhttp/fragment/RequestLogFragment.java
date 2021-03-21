package cc.kafuu.myhttp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.PersistableBundle;
import android.service.restrictions.RestrictionsReceiver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.Objects;

import cc.kafuu.myhttp.R;
import cc.kafuu.myhttp.adapter.LogListAdapter;
import cc.kafuu.myhttp.helper.LogDatabase;

public class RequestLogFragment extends Fragment {

    private View mRootView = null;
    private SQLiteOpenHelper mDatabase = null;
    private ListView mLogList = null;
    private BaseAdapter mLogListAdapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(mRootView == null)
        {
            mRootView = inflater.inflate(R.layout.fragment_rquest_log, container, false);
            initView();
        }
        else
        {
            ViewGroup parent = (ViewGroup) container.getParent();
            parent.removeView(mRootView);
        }

        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRootView = null;
        mDatabase.close();
    }

    private void initView()
    {
        mDatabase = new LogDatabase(Objects.requireNonNull(getContext()));

        mLogList = mRootView.findViewById(R.id.logList);

        mLogListAdapter = new LogListAdapter(getContext(), mDatabase);
        mLogList.setAdapter(mLogListAdapter);

        initBroadcast();
    }

    private BroadcastReceiver mBroadcastReceiver = null;

    private void initBroadcast()
    {
        if(mBroadcastReceiver != null)
        {
            return;
        }

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mLogListAdapter.notifyDataSetChanged();
            }
        };

        IntentFilter filter = new IntentFilter("Broadcast.PostGet.Request.Complete");
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getContext())).registerReceiver(mBroadcastReceiver, filter);
    }
}
