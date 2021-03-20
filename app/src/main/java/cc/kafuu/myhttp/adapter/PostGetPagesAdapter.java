package cc.kafuu.myhttp.adapter;

import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import cc.kafuu.myhttp.fragment.AboutFragment;
import cc.kafuu.myhttp.fragment.RequestFragment;
import cc.kafuu.myhttp.fragment.RequestLogFragment;

public class PostGetPagesAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragments = new ArrayList<>();

    public PostGetPagesAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mFragments.add(new RequestFragment());
        mFragments.add(new RequestLogFragment());
        Log.d("PostGetFragment", "PostGetPagesAdapter");
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.d("PostGetFragment", "getItem");
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Nullable
    @Override
    public Parcelable saveState() {
        return null;
    }
}
