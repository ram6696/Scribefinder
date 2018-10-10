package com.ourapps.scribefinder.Needy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ourapps.scribefinder.Needy.AddressFragment;
import com.ourapps.scribefinder.Needy.BasicProfileFragment;

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
