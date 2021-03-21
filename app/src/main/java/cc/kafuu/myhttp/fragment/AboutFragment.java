package cc.kafuu.myhttp.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import cc.kafuu.myhttp.R;

public class AboutFragment extends Fragment {

    private View mRootView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView == null)
        {
            mRootView = inflater.inflate(R.layout.fragment_about, container, false);
            initView();
        }
        else
        {
            ViewGroup parent = (ViewGroup) container.getParent();
            parent.removeView(mRootView);
        }

        return mRootView;
    }

    private void initView()
    {
        LinearLayout submitFeedback = mRootView.findViewById(R.id.submitFeedback);
        LinearLayout personalWebsite = mRootView.findViewById(R.id.personalWebsite);
        LinearLayout openSourceLicense = mRootView.findViewById(R.id.openSourceLicense);
        LinearLayout codeWarehouse = mRootView.findViewById(R.id.codeWarehouse);

        submitFeedback.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/KafuuNeko/MyHttp/issues"))));
        personalWebsite.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://kafuu.cc"))));
        openSourceLicense.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/KafuuNeko/MyHttp/blob/master/LICENSE"))));
        codeWarehouse.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/KafuuNeko/MyHttp"))));

    }

}