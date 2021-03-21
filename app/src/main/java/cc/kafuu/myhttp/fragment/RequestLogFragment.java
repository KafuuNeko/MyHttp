package cc.kafuu.myhttp.fragment;

import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.Objects;

import cc.kafuu.myhttp.R;
import cc.kafuu.myhttp.adapter.LogListAdapter;
import cc.kafuu.myhttp.helper.LogDatabase;

public class RequestLogFragment extends Fragment {

    private View mRootView = null;
    private SQLiteOpenHelper mDatabase = null;

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

        ListView logList = mRootView.findViewById(R.id.logList);
        Button logStatisticsButton = mRootView.findViewById(R.id.logStatisticsButton);
        Button clearLogButton = mRootView.findViewById(R.id.clearLogButton);
        Button refreshLogButton = mRootView.findViewById(R.id.refreshLogButton);

        logList.setAdapter(new LogListAdapter(getContext(), mDatabase));

    }
}
