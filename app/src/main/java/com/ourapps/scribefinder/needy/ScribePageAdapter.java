package com.ourapps.scribefinder.needy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ScribePageAdapter extends FragmentStatePagerAdapter {

    private int noOfTabs;
    String id;
    Bundle bundle = new Bundle();

    public ScribePageAdapter(FragmentManager fm, int noOfTabs, String id){
        super(fm);
        this.noOfTabs = noOfTabs;
        this.id = id;
        bundle.putString("id", id);
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                BasicProfileFragment bpf = new BasicProfileFragment();
                bpf.setArguments(bundle);
                return bpf;
            case 1:
                AddressFragment adf = new AddressFragment();
                adf.setArguments(bundle);
                return adf;
            default:
                    return null;

        }
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return noOfTabs;
    }
}
