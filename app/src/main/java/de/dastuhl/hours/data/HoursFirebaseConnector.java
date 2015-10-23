package de.dastuhl.hours.data;

import android.content.Context;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import de.dastuhl.hours.data.model.DailySessionsSummary;
import de.dastuhl.hours.data.model.MonthlySessionsSummary;
import de.dastuhl.hours.data.model.SessionsSummary;
import de.dastuhl.hours.data.model.WeeklySessionsSummary;
import de.dastuhl.hours.data.model.YearlySessionsSummary;

/**
 * Created by Martin on 04.10.2015.
 */
public class HoursFirebaseConnector {

    private static final String BASIC_REF = "https://dastuhl-hours.firebaseio.com/";
    private static final String SUMMARIES_DAILY = "summaries_daily/";
    private static final String SUMMARIES_WEEKLY = "summaries_weekly/";
    private static final String SUMMARIES_MONTHLY = "summaries_monthly/";
    private static final String SUMMARIES_YEARLY = "summaries_yearly/";
    private static final String USERS_CHILD = "users/";

    private String userID;
    private Context context;

    private boolean summariesInitialized = false;

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

    public Firebase getUserDailySummaries() {
        if (userSessionsRef == null) {
            userSessionsRef = new Firebase(BASIC_REF + SUMMARIES_DAILY + userID);
            userSessionsRef.orderByPriority();
            userSessionsRef.keepSynced(true);
        }
        return userSessionsRef;
    }

    public void initSessionsListener(final boolean initCumulatedSummaries) {
        // for incremental operations on the date
        getUserDailySummaries().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (summariesInitialized) {
                    updateSummaries(dataSnapshot.getValue(SessionsSummary.class));
                }
            }

            private void updateSummaries(SessionsSummary newOrUpdatedSummary) {
                Integer year = newOrUpdatedSummary.getYear();
                getUserDailySummaries()
                        .orderByChild(SessionsSummary.YEAR_PROPERTY)
                        .startAt(year)
                        .endAt(year)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                HoursFirebaseConnector.this.updateSummaries(dataSnapshot);
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (summariesInitialized) {
                    updateSummaries(dataSnapshot.getValue(SessionsSummary.class));
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        // initial Loading and updating the summaries
        getUserDailySummaries().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (initCumulatedSummaries) {
                    updateSummaries(dataSnapshot);
                }
                summariesInitialized = true;
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

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

    public void loadSessionsSummary(String ref, final SessionsSummaryLoader listener) {
        Firebase summaryRef = new Firebase(ref);
        final Class<? extends SessionsSummary> clazz = getClassFromRef(ref);
        if (clazz != null && summaryRef.getAuth() != null && userID.equals(summaryRef.getAuth().getUid())) {
            summaryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    SessionsSummary summary = dataSnapshot.getValue(clazz);
                    if (summary == null) {
                        listener.onLoadingFailed();
                    } else {
                        listener.onDataLoaded(Collections.singleton(summary));
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    listener.onLoadingFailed();
                }
            });
        } else {
            listener.onLoadingFailed();
        }
    }

    private Class<? extends SessionsSummary> getClassFromRef(String ref) {
        if (ref.contains(SUMMARIES_DAILY)) {
            return DailySessionsSummary.class;
        } else if (ref.contains(SUMMARIES_WEEKLY)) {
            return WeeklySessionsSummary.class;
        } else if(ref.contains(SUMMARIES_MONTHLY)) {
            return MonthlySessionsSummary.class;
        } else if (ref.contains(SUMMARIES_YEARLY)) {
            return YearlySessionsSummary.class;
        }
        return null;
    }

    public void saveDailySummary(DailySessionsSummary sessionsSummary) {
        if (sessionsSummary != null) {
            saveSession(sessionsSummary, getUserDailySummaries());
        }
    }

    private void saveWeeklySummaries(Collection<WeeklySessionsSummary> summaries) {
        for (WeeklySessionsSummary summary : summaries) {
            saveSession(summary, getUserWeeklySummaries());
        }
    }

    private void saveMonthlySummaries(Collection<MonthlySessionsSummary> summaries) {
        for (MonthlySessionsSummary summary : summaries) {
            saveSession(summary, getUserMonthlySummaries());
        }
    }

    private void saveYearlySummaries(Collection<YearlySessionsSummary> summaries) {
        for (YearlySessionsSummary summary : summaries) {
            saveSession(summary, getUserYearlySummaries());
        }
    }

    private void saveSession(SessionsSummary sessionsSummary, Firebase ref) {
        if (sessionsSummary != null) {
            String key = sessionsSummary.createPeriodString();
            final Firebase newSessionRef = ref.child(key);
            newSessionRef.setValue(sessionsSummary, sessionsSummary.createPriority());
        }
    }

    private void updateSummaries(final DataSnapshot dataSnapshot) {
        Map<String, WeeklySessionsSummary> weeklySummaries = Maps.newHashMap();
        Map<String, MonthlySessionsSummary> monthlySummaries = Maps.newHashMap();
        Map<String, YearlySessionsSummary> yearlySummaries = Maps.newHashMap();

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            SessionsSummary summary = snapshot.getValue(SessionsSummary.class);
            DailySessionsSummary dailySummary = DailySessionsSummary.newInstanceFromSessionsSummary(summary);
            String yearlyKey = dailySummary.createKeyForYearlySummary();
            String monthlyKey = dailySummary.createKeyForMonthlySummary();
            String weeklyKey = dailySummary.createKeyForWeeklySummary();

            if (weeklySummaries.containsKey(weeklyKey)) {
                weeklySummaries.get(weeklyKey).addSessionDurations(dailySummary);
            } else {
                WeeklySessionsSummary weeklySummary = WeeklySessionsSummary.fromDailySessionsSummary(dailySummary);
                weeklySummaries.put(weeklyKey, weeklySummary);
            }

            if (monthlySummaries.containsKey(monthlyKey)) {
                monthlySummaries.get(monthlyKey).addSessionDurations(dailySummary);
            } else {
                MonthlySessionsSummary monthlySummary = MonthlySessionsSummary.fromDailySessionsSummary(dailySummary);
                monthlySummaries.put(monthlyKey, monthlySummary);
            }

            if (yearlySummaries.containsKey(yearlyKey)) {
                yearlySummaries.get(yearlyKey).addSessionDurations(dailySummary);
            } else {
                YearlySessionsSummary yearlySummary = YearlySessionsSummary.fromDailySessionsSummary(dailySummary);
                yearlySummaries.put(yearlyKey, yearlySummary);
            }
        }

        saveWeeklySummaries(weeklySummaries.values());
        saveMonthlySummaries(monthlySummaries.values());
        saveYearlySummaries(yearlySummaries.values());
    }

    public boolean isSummariesInitialized() {
        return summariesInitialized;
    }

    public static interface SessionsSummaryLoader {
        void onDataLoaded(Collection<SessionsSummary> sessionsSummaries);
        void onLoadingFailed();
    }
}
