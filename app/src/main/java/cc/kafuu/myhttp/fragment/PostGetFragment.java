package cc.kafuu.myhttp.fragment;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import cc.kafuu.myhttp.R;
import cc.kafuu.myhttp.adapter.PostGetPagesAdapter;


public class PostGetFragment extends Fragment {
    private View mRootView = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(mRootView == null)
        {
            mRootView = inflater.inflate(R.layout.fragment_post_get, container, false);
            initView();
            Log.d("PostGetFragment", "initView");
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
        mRootView = null;
        super.onDestroyView();
    }

    void initView()
    {
        final ViewPager viewPager = mRootView.findViewById(R.id.viewPager);
        final BottomNavigationView bottomNavigationView = mRootView.findViewById(R.id.bottomNavigationView);

        assert getFragmentManager() != null;
        viewPager.setAdapter(new PostGetPagesAdapter(getChildFragmentManager()));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            final int item_id = item.getItemId();

            if (item_id == R.id.navRequest)
                viewPager.setCurrentItem(0);
            else if (item_id == R.id.navLog)
                viewPager.setCurrentItem(1);

            return true;
        });

        viewPager.setCurrentItem(0);
    }


}