package com.example.personalmoney;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

public class myFragAdapter extends FragmentPagerAdapter {
    FragmentManager fragmentManager;
    List<Fragment> list;

    private ViewGroup container;
    private Object object;

    public myFragAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.fragmentManager = fm;
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        this.container = container;
        this.object = object;
        /**position==2,不销毁**/
        if (position != 2)
            super.destroyItem(container, position, object);
    }
}
