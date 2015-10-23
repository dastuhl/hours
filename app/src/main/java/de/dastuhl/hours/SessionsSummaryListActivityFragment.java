package de.dastuhl.hours;

import android.app.Activity;
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
import de.dastuhl.hours.data.model.WeeklySessionsSummary;
import de.dastuhl.hours.data.model.YearlySessionsSummary;
import de.dastuhl.hours.navigation.NavigationDrawerFragment;


/**
 * A placeholder fragment containing a simple view.
 */
public class SessionsSummaryListActivityFragment extends Fragment
        implements MainActivity.SessionsSummaryListCallback, SessionsSummaryViewAdapter.SummarySelectionListener {

    private static final String CUMULATION_INITIALIZED = "CumInitialized";
    private static final String LIST_TYPE = "ListType";

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";

    @Bind(R.id.listview_sessions)
    RecyclerView sessionListView;

    private SessionsSummaryViewAdapter adapter;

    private AuthData authUser;

    HoursFirebaseConnector firebaseConnector;
    // Days, Weeks, ...
    private int selectedListType;

    // are the cumulated views (week, months, years) initialized
    private boolean doInit;


    public SessionsSummaryListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sessions_summaries_list, container, false);

        ButterKnife.bind(this, rootView);
        sessionListView.setHasFixedSize(true);
        sessionListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(LIST_TYPE)) {
            selectedListType = savedInstanceState.getInt(LIST_TYPE);
        } else {
            selectedListType = (int) getArguments().get(ARG_SECTION_NUMBER);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(CUMULATION_INITIALIZED)) {
            doInit = !savedInstanceState.getBoolean(CUMULATION_INITIALIZED);
        } else {
            doInit = true;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (firebaseConnector == null) {
            setup();
        }
    }

    private void setup() {
        authUser = HoursFirebaseLoginHelper.setupUserAuth(getActivity());
        if (authUser != null && authUser.getUid() != null) {
            firebaseConnector = new HoursFirebaseConnector(authUser.getUid(), getActivity());
            firebaseConnector.initSessionsListener(doInit);
            initializeAdapter();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (firebaseConnector != null) {
            outState.putBoolean(CUMULATION_INITIALIZED, firebaseConnector.isSummariesInitialized());
            if (adapter != null) {
                outState.putInt(LIST_TYPE, selectedListType);
            }
        }
    }

    private void initializeAdapter() {
        if (firebaseConnector == null) {
            return;
        }

        Firebase ref = null;
        Class clazz = null;
        switch (selectedListType) {
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
            adapter = new SessionsSummaryViewAdapter(clazz, R.layout.list_item_sessions_summary_chart,
                    SessionsSummaryViewAdapter.SessionListViewHolder.class, ref, getActivity(), this);
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

    @OnClick(R.id.add_dailysummary_fab)
    void addSessionButtonClicked(){
        DetailSessionsSummaryActivity.start(getActivity(), authUser.getUid(), null);
    }

    @Override
    public void listTypeChanged(int listType) {
        selectedListType = listType;
        initializeAdapter();
    }

    @Override
    public void chartTypeChanged() {
        initializeAdapter();
    }

    @Override
    public void summarySelected(String url) {
        DetailSessionsSummaryActivity.start(getActivity(), authUser.getUid(), url);
    }
}
