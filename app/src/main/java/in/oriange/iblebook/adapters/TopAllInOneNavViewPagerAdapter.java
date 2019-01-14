package in.oriange.iblebook.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.oriange.iblebook.fragments.My_AllInOne_Fragment;
import in.oriange.iblebook.fragments.Offline_AllInOne_Fragment;
import in.oriange.iblebook.fragments.Received_AllInOne_Fragment;

public class TopAllInOneNavViewPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private Fragment currentFragment;

    public TopAllInOneNavViewPagerAdapter(FragmentManager fm) {
        super(fm);

        fragments.clear();
        fragments.add(new My_AllInOne_Fragment());
        fragments.add(new Received_AllInOne_Fragment());
        fragments.add(new Offline_AllInOne_Fragment());
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            currentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }
}