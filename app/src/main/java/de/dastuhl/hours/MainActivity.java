package de.dastuhl.hours;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import de.dastuhl.hours.data.HoursFirebaseLoginHelper;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String SESSION_FRAGMENT_TAG = "SFT";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mTitle = getTitle();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        if (position == NavigationDrawerFragment.POSITION_LOGOUT) {
            HoursFirebaseLoginHelper.logoutFirebase(this);
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag(SESSION_FRAGMENT_TAG);

            if (fragment == null) {
                fragment = new SessionsSummaryListActivityFragment();
                Bundle args = new Bundle();
                args.putInt(SessionsSummaryListActivityFragment.ARG_SECTION_NUMBER, position);
                fragment.setArguments(args);

                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment, SESSION_FRAGMENT_TAG)
                        .commit();
            } else {
                changeTitle(position);
                ((SessionListCallback) fragment).listTypeChanged(position);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public interface SessionListCallback {
        void listTypeChanged(int listType);
    }
}
