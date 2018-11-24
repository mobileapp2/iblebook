package in.oriange.iblebook.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.oriange.iblebook.fragments.Address_Fragment;
import in.oriange.iblebook.fragments.Bank_Fragment;
import in.oriange.iblebook.fragments.Contacts_Fragment;
import in.oriange.iblebook.fragments.Requests_Fragment;
import in.oriange.iblebook.fragments.Tax_Fragment;

public class BotNavViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private Fragment currentFragment;

    public BotNavViewPagerAdapter(FragmentManager fm) {
        super(fm);

        fragments.clear();
        fragments.add(new Address_Fragment());
        fragments.add(new Tax_Fragment());
        fragments.add(new Bank_Fragment());
        fragments.add(new Requests_Fragment());
        fragments.add(new Contacts_Fragment());
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