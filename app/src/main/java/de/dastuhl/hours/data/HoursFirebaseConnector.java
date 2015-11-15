package de.dastuhl.hours.data;

import android.content.Context;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public HoursFirebaseConnector(String userId, Context context) {
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
                    // initial Loading done
                    SessionsSummary added = dataSnapshot.getValue(SessionsSummary.class);
                    Integer total = added.computeTotal();
                    if (total != null && total > 0) {
                        updateCumulations(added.getYear(), added.getYear());
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (summariesInitialized) {
                    SessionsSummary changed = dataSnapshot.getValue(SessionsSummary.class);
                    updateCumulations(changed.getYear(), changed.getYear());
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
                    updateCummulatedSummaries(dataSnapshot);
                    addMissingDailySummaries(dataSnapshot);
                }
                summariesInitialized = true;
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void updateCumulations(int startYear, int endYear) {
        getUserDailySummaries()
                .orderByChild(SessionsSummary.YEAR_PROPERTY)
                .startAt(startYear - 1)
                .endAt(endYear + 1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HoursFirebaseConnector.this.updateCummulatedSummaries(dataSnapshot);
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
        final Class<? extends SessionsSummary> clazz = getClassFromUrl(ref);
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

    private Class<? extends SessionsSummary> getClassFromUrl(String ref) {
        if (ref.contains(SUMMARIES_DAILY)) {
            return DailySessionsSummary.class;
        } else if (ref.contains(SUMMARIES_WEEKLY)) {
            return WeeklySessionsSummary.class;
        } else if (ref.contains(SUMMARIES_MONTHLY)) {
            return MonthlySessionsSummary.class;
        } else if (ref.contains(SUMMARIES_YEARLY)) {
            return YearlySessionsSummary.class;
        }
        return null;
    }

    public void saveDailySummary(final DailySessionsSummary sessionsSummary) {
        if (sessionsSummary != null) {
            saveNewSummary(sessionsSummary, getUserDailySummaries(),
                    new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            getUserDailySummaries().addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            addMissingDailySummaries(dataSnapshot);
                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {
                                        }
                                    });
                        }
                    });
        }
    }

    private void addMissingDailySummaries(DataSnapshot userDailySummaries) {
        Set<DailySessionsSummary> missingSummaries = Sets.newHashSet();

        List<DataSnapshot> list = ImmutableList.copyOf(userDailySummaries.getChildren());
        if (list.size() > 1) {
            Calendar cal = list.get(0).getValue(DailySessionsSummary.class).buildCalendar();

            for (DataSnapshot snapshot : list) {
                DailySessionsSummary summary = snapshot.getValue(DailySessionsSummary.class);

                DailySessionsSummary newSummary = new DailySessionsSummary(cal);
                while (newSummary.compareTo(summary) > 0) {
                    missingSummaries.add(newSummary);
                    cal.add(Calendar.DATE, -1);
                    newSummary = new DailySessionsSummary(cal);
                }
                cal.add(Calendar.DATE, -1);

            }

            if (!missingSummaries.isEmpty()) {
                int start = 9999;
                int end = 0;
                for (DailySessionsSummary ds : missingSummaries) {
                    saveNewSummary(ds, getUserDailySummaries(), null);
                    if (ds.getYear() > end) {
                        end = ds.getYear();
                    }
                    if (ds.getYear() < start) {
                        start = ds.getYear();
                    }
                }

                updateCumulations(start, end);
            }
        }
    }

    private void saveWeeklySummaries(Collection<WeeklySessionsSummary> summaries) {
        for (WeeklySessionsSummary summary : summaries) {
            saveNewSummary(summary, getUserWeeklySummaries(), null);
        }
    }

    private void saveMonthlySummaries(Collection<MonthlySessionsSummary> summaries) {
        for (MonthlySessionsSummary summary : summaries) {
            saveNewSummary(summary, getUserMonthlySummaries(), null);
        }
    }

    private void saveYearlySummaries(Collection<YearlySessionsSummary> summaries) {
        for (YearlySessionsSummary summary : summaries) {
            saveNewSummary(summary, getUserYearlySummaries(), null);
        }
    }

    private void saveNewSummary(SessionsSummary sessionsSummary, Firebase ref,
                                Firebase.CompletionListener listener) {
        if (sessionsSummary != null) {
            final Firebase newSummary = ref.child(sessionsSummary.createKey());
            if (listener != null) {
                newSummary.setValue(sessionsSummary, sessionsSummary.createPriority(), listener);
            } else {
                newSummary.setValue(sessionsSummary, sessionsSummary.createPriority());
            }
        }
    }

    private void updateCummulatedSummaries(final DataSnapshot dataSnapshot) {
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

    public interface SessionsSummaryLoader {
        void onDataLoaded(Collection<SessionsSummary> sessionsSummaries);

        void onLoadingFailed();
    }
}
