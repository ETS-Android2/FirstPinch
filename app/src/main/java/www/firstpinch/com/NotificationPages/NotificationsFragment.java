package www.firstpinch.com.firstpinch2.NotificationPages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import www.firstpinch.com.firstpinch2.ErrorLogs.Error_GlobalVariables;
import www.firstpinch.com.firstpinch2.R;

/**
 * Created by Rianaa Admin on 14-09-2016.
 */

//notification fragment from Home.java
public class NotificationsFragment extends Fragment {

    ViewPager viewPager;
    TabLayout tabLayout;
    Context c;
    ViewPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.notifications, container, false);
        c = getActivity();
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        /*mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String notif_id = intent.getStringExtra("id");
                if (data_notif_id.contains(notif_id)) {

                } else {
                    data_notif_id.add(notif_id);
                    beginTask(intent);
                    if (active)
                        ((NotificationsFragment) adapter.mFragmentList.get(1)).refresh();
                }

            }
        };*/

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        changeTabsFont();
        return view;
    }

    //to refresh notification fragments data.
    public void refresh() {
        try {
            ((YourNotificationsFragment) adapter.mFragmentList.get(0)).refresh();
            ((HouseNotificationsFragment) adapter.mFragmentList.get(1)).refresh();
        } catch (Exception e) {
            Log.e("NotificationsFrgmnt", "refresh function" + e.toString());
            Error_GlobalVariables error_global = new Error_GlobalVariables();
            error_global.initializeVar(getActivity(), e.toString());
        }
    }

    //load fragments in notification viewPager
    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new YourNotificationsFragment(), "Your Notifications");
        adapter.addFragment(new HouseNotificationsFragment(), "Club Notifications");
        viewPager.setAdapter(adapter);
    }

    private void changeTabsFont() {
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Cabin-Regular.ttf");
        Typeface custom_font1 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Regular.ttf");

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(custom_font);
                }
            }
        }
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            /*if(position==0){
                ll.setVisibility(View.VISIBLE);
            }
            else
            ll.setVisibility(View.GONE);*/
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}