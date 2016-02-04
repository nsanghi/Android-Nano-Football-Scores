package barqsoft.footballscores;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yehya khaled on 2/27/2015.
 */
public class PagerFragment extends Fragment
{
    public static final int NUM_PAGES = 5;
    private static final String LOG_TAG = PagerFragment.class.getSimpleName();
    public ViewPager mPagerHandler;
    private myPageAdapter mPagerAdapter;
    private MainScreenFragment[] viewFragments = new MainScreenFragment[5];
    @Override
    @SuppressLint("NewApi")
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.pager_fragment, container, false);
        mPagerHandler = (ViewPager) rootView.findViewById(R.id.pager);
        mPagerAdapter = new myPageAdapter(getChildFragmentManager());
        boolean useRtl = false;

        //Get current locale and check for the direction.
        Locale current = getResources().getConfiguration().locale;
        int direction = TextUtils.getLayoutDirectionFromLocale(current);
        //Added to handle RTL for pager
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if( currentapiVersion >= Build.VERSION_CODES.JELLY_BEAN && direction == View.LAYOUT_DIRECTION_RTL) {
            useRtl = true;
            //Log.d(LOG_TAG,"Doing RTL for ViewPager data");
        }

        int position;

        for (int i = 0;i < NUM_PAGES;i++)
        {
            if (useRtl) {
                //for RTL invert the position of data with most recent being at i=0
                position = Utilies.inversePositionForRTL(i, NUM_PAGES);  //thanks to Udacity student josen (Jose) for this suggestion
            } else {
                position = i;
            }
            Date fragmentdate = new Date(System.currentTimeMillis()+((i-2)*86400000));
            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
            viewFragments[position] = new MainScreenFragment();
            viewFragments[position].setFragmentDate(mformat.format(fragmentdate));
        }
        mPagerHandler.setAdapter(mPagerAdapter);
        mPagerHandler.setCurrentItem(MainActivity.current_fragment);
        return rootView;
    }
    private class myPageAdapter extends FragmentStatePagerAdapter
    {
        @Override
        public Fragment getItem(int i)
        {
            return viewFragments[i];
        }

        @Override
        public int getCount()
        {
            return NUM_PAGES;
        }

        public myPageAdapter(FragmentManager fm)
        {
            super(fm);
        }
        // Returns the page title for the top indicator
        @Override
        @SuppressLint("NewApi")
        public CharSequence getPageTitle(int position)
        {
            Locale current = getResources().getConfiguration().locale;
            int direction = TextUtils.getLayoutDirectionFromLocale(current);

            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if( currentapiVersion >= Build.VERSION_CODES.JELLY_BEAN && direction == View.LAYOUT_DIRECTION_RTL) {
                //Log.d(LOG_TAG,"doing RTL for Page Title");
                position = Utilies.inversePositionForRTL(position, getCount());  //thanks to Udacity student josen (Jose) for this suggestion
            }
            return getDayName(getActivity(),System.currentTimeMillis()+((position-2)*86400000));
        }
        public String getDayName(Context context, long dateInMillis) {
            // If the date is today, return the localized version of "Today" instead of the actual
            // day name.

            Time t = new Time();
            t.setToNow();
            int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
            int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
            if (julianDay == currentJulianDay) {
                return context.getString(R.string.today);
            } else if ( julianDay == currentJulianDay +1 ) {
                return context.getString(R.string.tomorrow);
            }
             else if ( julianDay == currentJulianDay -1)
            {
                return context.getString(R.string.yesterday);
            }
            else
            {
                Time time = new Time();
                time.setToNow();
                // Otherwise, the format is just the day of the week (e.g "Wednesday".
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
                return dayFormat.format(dateInMillis);
            }
        }
    }
}
