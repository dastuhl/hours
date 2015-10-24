package de.dastuhl.hours;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

import de.dastuhl.hours.data.HoursFirebaseLoginHelper;
import de.dastuhl.hours.navigation.NavigationDrawerFragment;
import de.dastuhl.hours.Settings.SettingsActivity;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String SUMMARY_LIST_FRAGMENT_TAG = "SFT";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private boolean stackedBarChart;
    private List<Float> maxValuesForCharts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stackedBarChart = Utility.getPreferredBarChartStyle(this);
        maxValuesForCharts = getMaxValuesFromPreferences();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mTitle = getTitle();
    }

    @NonNull
    private ArrayList<Float> getMaxValuesFromPreferences() {
        return Lists.newArrayList(
                Utility.getPreferredMaxValueDays(this),
                Utility.getPreferredMaxValueWeeks(this),
                Utility.getPreferredMaxValueMonths(this),
                Utility.getPreferredMaxValueYears(this)
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean styleFromPreferences = Utility.getPreferredBarChartStyle(this);
        List<Float> valuesFromPreferences = getMaxValuesFromPreferences();
        if (styleFromPreferences != stackedBarChart
                || !valuesFromPreferences.equals(maxValuesForCharts)) {
            Fragment fragment = findSessionsSummaryListActivityFragment();
            if (fragment != null) {
                ((SessionsSummaryListCallback) fragment).chartTypeChanged();
            }
            stackedBarChart = styleFromPreferences;
            maxValuesForCharts = valuesFromPreferences;
        }
    }

    private Fragment findSessionsSummaryListActivityFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        return fragmentManager.findFragmentByTag(SUMMARY_LIST_FRAGMENT_TAG);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        if (position == NavigationDrawerFragment.POSITION_LOGOUT) {
            HoursFirebaseLoginHelper.logoutFirebase(this);
        } else {
            Fragment fragment = findSessionsSummaryListActivityFragment();

            if (fragment == null) {
                fragment = new SessionsSummaryListActivityFragment();
                Bundle args = new Bundle();
                args.putInt(SessionsSummaryListActivityFragment.ARG_SECTION_NUMBER, position);
                fragment.setArguments(args);

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment, SUMMARY_LIST_FRAGMENT_TAG)
                        .commit();
            } else {
                changeTitle(position);
                ((SessionsSummaryListCallback) fragment).listTypeChanged(position);
            }
        }
    }

    public void onSectionAttached(int number) {
        changeTitle(number);
    }

    private void changeTitle(int number) {
        switch (number) {
            case NavigationDrawerFragment.LIST_TYPE_DAYS:
                mTitle = getString(R.string.title_section1);
                break;
            case NavigationDrawerFragment.LIST_TYPE_WEEKS:
                mTitle = getString(R.string.title_section2);
                break;
            case NavigationDrawerFragment.LIST_TYPE_MONTHS:
                mTitle = getString(R.string.title_section3);
                break;
            case NavigationDrawerFragment.LIST_TYPE_YEARS:
                mTitle = getString(R.string.title_section4);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mTitle);
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setUserIdentification(String userIdentification) {
        if (mNavigationDrawerFragment != null) {
            mNavigationDrawerFragment.setmUserIdentificationText(userIdentification);
        }
    }

    public interface SessionsSummaryListCallback {
        void listTypeChanged(int listType);
        void chartTypeChanged();
    }
}
