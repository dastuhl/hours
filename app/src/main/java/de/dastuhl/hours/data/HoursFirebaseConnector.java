package de.dastuhl.hours.data;

import android.content.Context;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by Martin on 04.10.2015.
 */
public class HoursFirebaseConnector {

    private static final String BASIC_REF = "https://dastuhl-hours.firebaseio.com/";
    private static final String SESSIONS_CHILD = "sessions/";
    private static final String SUMMARIES_WEEKLY = "summaries_weekly/";
    private static final String SUMMARIES_MONTHLY = "summaries_monthly/";
    private static final String SUMMARIES_YEARLY = "summaries_yearly/";
    private static final String USERS_CHILD = "users/";

    private String userID;
    private Context context;

    private boolean isListenerInitialized = false;

    private Firebase userSessionsRef;
    private Firebase userWeeklySummaries;
    private Firebase userMonthlySummaries;
    private Firebase userYearlySummaries;
    private Firebase userRef;

    public HoursFirebaseConnector (String userId, Context context) {
        this.userID = userId;
        this.context = context;
    }

    public static Firebase getBasicRef() {
        return new Firebase(BASIC_REF);
    }

    public Firebase getUserRef() {
        if (userRef == null) {
            userRef = new Firebase(BASIC_REF + USERS_CHILD + userID);
        }
        return userRef;
    }

    public Firebase getUserSessionsRef() {
        if (userSessionsRef == null) {
            userSessionsRef = new Firebase(BASIC_REF + SESSIONS_CHILD + userID);
            userSessionsRef.orderByPriority();
            userSessionsRef.keepSynced(true);
        }
        return userSessionsRef;
    }

    public void initSessionsListener() {
        if (isListenerInitialized) {
            return;
        }
        isListenerInitialized = true;
        userSessionsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot pDataSnapshot, String s) {
            }

            @Override
            public void onChildChanged(DataSnapshot pDataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot pDataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot pDataSnapshot, String s) {
            }

            @Override
            public void onCancelled(FirebaseError pFirebaseError) {
            }

        });
    }

    public Firebase getUserMonthlySummaries() {
        if (userMonthlySummaries == null) {
            userMonthlySummaries = new Firebase(BASIC_REF + SUMMARIES_MONTHLY + userID);
            userMonthlySummaries.orderByPriority();
            userMonthlySummaries.keepSynced(true);
        }
        return userMonthlySummaries;
    }

    public Firebase getUserWeeklySummaries() {
        if (userWeeklySummaries == null) {
            userWeeklySummaries = new Firebase(BASIC_REF + SUMMARIES_WEEKLY + userID);
            userWeeklySummaries.orderByPriority();
            userWeeklySummaries.keepSynced(true);
        }
        return userWeeklySummaries;
    }

    public Firebase getUserYearlySummaries() {
        if (userYearlySummaries == null) {
            userYearlySummaries = new Firebase(BASIC_REF + SUMMARIES_YEARLY + userID);
            userYearlySummaries.orderByKey();
            userYearlySummaries.keepSynced(true);
        }
        return userYearlySummaries;
    }

    public void saveSession(Session session) {
        if (session != null) {
            String key = session.createTimerangeString();
            final Firebase newSessionRef = getUserSessionsRef().child(key);
            newSessionRef.setValue(session, session.createPriority());
        }
    }

    private void updateWeekSummary(final Session addedSession) {
        final int week = addedSession.getWeekOfYear();
        final int year = addedSession.getYear();

        final String key = addedSession.createTimerangeString();

        Firebase ref = new Firebase(BASIC_REF).child(SUMMARIES_WEEKLY).child(userID).child(key);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot pDataSnapshot) {
                Session weekSummary = pDataSnapshot.getValue(Session.class);
                if (weekSummary == null) {
                    weekSummary = new Session(year, null, week, null,
                            addedSession.getSwimDuration(), addedSession.getCycleDuration(),
                            addedSession.getRunDuration(), addedSession.getAthleticDuration());
                } else {
                    int swimSum = weekSummary.getSwimDuration() + addedSession.getSwimDuration();
                    int cycleSum = weekSummary.getCycleDuration() + addedSession.getCycleDuration();
                    int runSum = weekSummary.getRunDuration() + addedSession.getRunDuration();
                    int athleticSum = weekSummary.getAthleticDuration() + addedSession.getAthleticDuration();
                    weekSummary.setAthleticDuration(athleticSum);
                    weekSummary.setCycleDuration(cycleSum);
                    weekSummary.setRunDuration(runSum);
                    weekSummary.setSwimDuration(swimSum);
                }
                new Firebase(BASIC_REF).child(SUMMARIES_WEEKLY).child(userID).child(key).setValue(weekSummary, weekSummary.createPriority());
            }

            @Override
            public void onCancelled(FirebaseError pFirebaseError) {

            }
        });
    }

}
