package de.dastuhl.hours;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.dastuhl.hours.data.HoursFirebaseConnector;
import de.dastuhl.hours.data.HoursFirebaseLoginHelper;
import de.dastuhl.hours.data.model.DailySessionsSummary;
import de.dastuhl.hours.data.model.MonthlySessionsSummary;
import de.dastuhl.hours.data.model.SessionsSummary;
import de.dastuhl.hours.data.model.WeeklySessionsSummary;
import de.dastuhl.hours.data.model.YearlySessionsSummary;


/**
 * A placeholder fragment containing a simple view.
 */
public class SessionListActivityFragment extends Fragment implements MainActivity.SessionListCallback {

    private static final String CUMULATION_INITIALIZED = "CumInitialized";

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";
    private static final int NEW_SESSION_REQUEST_CODE = 1;

    @Bind(R.id.listview_sessions)
    RecyclerView sessionListView;

    private SessionsViewAdapter adapter;

    private AuthData authUser;

    HoursFirebaseConnector firebaseConnector;

    public SessionListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_session_list, container, false);

        int section = (int) getArguments().get(ARG_SECTION_NUMBER);

        ButterKnife.bind(this, rootView);
        sessionListView.setHasFixedSize(true);
        sessionListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        authUser = HoursFirebaseLoginHelper.setupUserAuth(getActivity());
        if (authUser != null && authUser.getUid() != null) {
            firebaseConnector = new HoursFirebaseConnector(authUser.getUid(), getActivity());
            boolean doInit;
            if (savedInstanceState != null && savedInstanceState.containsKey(CUMULATION_INITIALIZED)) {
                doInit = !savedInstanceState.getBoolean(CUMULATION_INITIALIZED);
            } else {
                doInit = true;
            }
            firebaseConnector.initSessionsListener(doInit);
            initializeAdapter(section);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (firebaseConnector != null) {
            outState.putBoolean(CUMULATION_INITIALIZED, firebaseConnector.isSummariesInitialized());
        }
    }

    private void initializeAdapter(int section) {
        if (firebaseConnector == null) {
            return;
        }

        Firebase ref = null;
        Class clazz = null;
        switch (section) {
            case NavigationDrawerFragment.LIST_TYPE_DAYS:
                ref = firebaseConnector.getUserDailySummaries();
                clazz = DailySessionsSummary.class;
                break;
            case NavigationDrawerFragment.LIST_TYPE_WEEKS:
                ref = firebaseConnector.getUserWeeklySummaries();
                clazz = WeeklySessionsSummary.class;
                break;
            case NavigationDrawerFragment.LIST_TYPE_MONTHS:
                ref = firebaseConnector.getUserMonthlySummaries();
                clazz = MonthlySessionsSummary.class;
                break;
            case NavigationDrawerFragment.LIST_TYPE_YEARS:
                ref = firebaseConnector.getUserYearlySummaries();
                clazz = YearlySessionsSummary.class;
                break;
        }
        if(ref != null) {
            adapter = new SessionsViewAdapter(clazz, R.layout.list_item_session, SessionsViewAdapter.SessionListViewHolder.class, ref);
            sessionListView.setAdapter(adapter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.cleanup();
        }
        ButterKnife.unbind(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @OnClick(R.id.add_session_fab)
    void addSessionButtonClicked(){
        Intent newSessionIntent = new Intent(getActivity(), EditSessionActivity.class);
        startActivityForResult(newSessionIntent, NEW_SESSION_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case NEW_SESSION_REQUEST_CODE:
                    SessionsSummary newSessionsSummary = data.getParcelableExtra(EditSessionActivityFragment.RESULT_SESSION);
                    DailySessionsSummary dailySessionsSummary = DailySessionsSummary.fromSessionsSummary(newSessionsSummary);
                    firebaseConnector.saveDailySummary(dailySessionsSummary);
                    break;
            }
        }
    }

    @Override
    public void listTypeChanged(int listType) {
        initializeAdapter(listType);
    }
}
