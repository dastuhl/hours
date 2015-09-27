package de.dastuhl.hours;

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
import de.dastuhl.hours.data.HoursFirebaseHelper;
import de.dastuhl.hours.data.Session;


/**
 * A placeholder fragment containing a simple view.
 */
public class SessionListActivityFragment extends Fragment {

    @Bind(R.id.listview_sessions)
    RecyclerView sessionListView;

    private SessionsViewAdapter adapter;

    private AuthData authUser;

    Firebase sessionsRef;

    public SessionListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_session_list, container, false);

        ButterKnife.bind(this, rootView);
        sessionListView.setHasFixedSize(true);
        sessionListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        sessionsRef = new Firebase(HoursFirebaseHelper.BASIC_REF).child(HoursFirebaseHelper.SESSIONS_CHILD);

        authUser = HoursFirebaseHelper.setupUserAuth(sessionsRef, getActivity());
        if (authUser != null) {
            adapter = new SessionsViewAdapter(Session.class, R.layout.list_item_session, SessionsViewAdapter.SessionListViewHolder.class, sessionsRef);
            sessionListView.setAdapter(adapter);
        }


        //Session session = new Session("martin", 2015, 9, "2015/09/01", 100, 110, 120, 130);
        //Firebase newSessionRef = sessionsRef.push();
        //newSessionRef.setValue(session);
        //
        //Session session2 = new Session("martin", 2015, 9, "2015/09/02", 100, 110, 120, 130);
        //newSessionRef = sessionsRef.push();
        //newSessionRef.setValue(session2);
        //
        //Session session3 = new Session("martin", 2015, 9, "2015/09/03", 100, 110, 120, 130);
        //newSessionRef = sessionsRef.push();
        //newSessionRef.setValue(session3);

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.cleanup();
        }
        ButterKnife.unbind(this);
    }

}
