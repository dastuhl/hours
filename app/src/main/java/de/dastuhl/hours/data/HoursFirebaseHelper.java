package de.dastuhl.hours.data;

import android.content.Context;
import android.content.Intent;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import de.dastuhl.hours.LoginActivity;

/**
 * Created by Martin on 24.09.2015.
 */
public class HoursFirebaseHelper {

    public static final String BASIC_REF = "https://dastuhl-hours.firebaseio.com/";
    public static final String SPORT_TYPES_CHILD = "sporttypes/";
    public static final String SESSIONS_CHILD = "sessions";
    public static final String USERS_CHILD = "users";

    public static final String USER_ATTRIBUTE_EMAIL = "email";
    public static final String USER_ATTRIBUTE_DISPLAY_NAME = "displayName";

    public static final String AUTH_DATA_EMAIL = "email";
    public static final String AUTH_DATA_DISPLAY_NAME = "displayName";

    private HoursFirebaseHelper() {

    }

    public static AuthData setupUserAuth(Firebase ref, Context context) {

        if (ref != null && ref.getAuth() != null && ref.getAuth().getUid() != null) {
            // vorhandene login Daten verwenden
            return ref.getAuth();
        } else {
            // login aufrufen
            Intent loginIntent = new Intent(context, LoginActivity.class);
            context.startActivity(loginIntent);
        }
        return null;
    }
}
