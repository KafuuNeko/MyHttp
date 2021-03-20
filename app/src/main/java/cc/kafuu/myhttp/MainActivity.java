package cc.kafuu.myhttp;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cc.kafuu.myhttp.fragment.AboutFragment;
import cc.kafuu.myhttp.fragment.PostGetFragment;
import cc.kafuu.myhttp.model.FragmentStruct;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout = null;
    private Toolbar mToolbar = null;
    private final Map<Integer, FragmentStruct> mFragment = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    /**
     * 初始化View
     */
    void initView()
    {
        mDrawerLayout = findViewById(R.id.drawerLayout);
        mToolbar = findViewById(R.id.toolbar);

        //将DrawerLayout与Toolbar关联
        new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open_drawer_content_desc, R.string.close_drawer_content_desc).syncState();

        initFragment();

        //侧边栏项目被选中，切换页面
        NavigationView navigation = findViewById(R.id.navigation);
        navigation.setNavigationItemSelectedListener(item -> {
            changeFragment(item.getItemId());
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
                mDrawerLayout.closeDrawer(GravityCompat.START);
            return false;
        });
    }

    /**
     * 初始化Fragment
     * 1.创建Fragment对象
     * 2.将创建好的Fragment对象添加到FragmentManager
     */
    void initFragment()
    {
        mFragment.put(R.id.navPostGet, new FragmentStruct("Post/Get", new PostGetFragment()));
        mFragment.put(R.id.navAbout, new FragmentStruct("About", new AboutFragment()));

        //添加Fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for(Integer key : mFragment.keySet())
            transaction.add(R.id.fragment, Objects.requireNonNull(mFragment.get(key)).fragment);
        transaction.commit();

        changeFragment(R.id.navPostGet);
    }

    /**
     * 改变显示的Fragment
     * @param key 要显示的Fragment的键值
     */
    void changeFragment(int key)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        //先隐藏所有Fragment
        for(Integer k : mFragment.keySet())
            transaction.hide(Objects.requireNonNull(mFragment.get(k)).fragment);
        //接着显示指定的Fragment
        transaction.show(Objects.requireNonNull(mFragment.get(key)).fragment);

        transaction.commit();

        //更新Toolbar的标题
        mToolbar.setTitle(Objects.requireNonNull(mFragment.get(key)).name);
    }


}