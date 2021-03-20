package cc.kafuu.myhttp.model;

import androidx.fragment.app.Fragment;

public class FragmentStruct {
    public String name;
    public Fragment fragment;

    public FragmentStruct(String name, Fragment fragment)
    {
        this.name = name;
        this.fragment = fragment;
    }
}
