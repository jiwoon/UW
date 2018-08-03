package com.example.jimi.in_outstock.adpater;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewPager适配器
 */
public class TaskItemViewPagerAdapter extends FragmentStatePagerAdapter {
    // 更新的碎片列表
    private ArrayList<Fragment> mFragmentList;

    private List<String> titleList = new ArrayList<String>();

    public TaskItemViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments, List<String> titleList) {
        super(fm);
        updateData(fragments);
        this.titleList = titleList;
    }

    public void updateData(ArrayList<Fragment> fragments) {
        ArrayList<Fragment> myFragments = new ArrayList<>();
        for (int i = 0, size = fragments.size(); i < size; i++) {
            myFragments.add(fragments.get(i));
        }
        setFragmentList(myFragments);
    }

    private void setFragmentList(ArrayList<Fragment> fragmentList) {
        if(this.mFragmentList != null){
            mFragmentList.clear();
        }
        this.mFragmentList = fragmentList;
        notifyDataSetChanged();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return (titleList.size() > position) ? titleList.get(position) : "";
    }

    @Override
    public int getCount() {
        return this.mFragmentList.size();
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }
}
