package de.dastuhl.hours.data;

import android.content.Context;
import android.content.Intent;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import de.dastuhl.hours.LoginActivity;

/**
 * Created by Martin on 24.09.2015.
 */
public class HoursFirebaseLoginHelper {

    public static final String USER_ATTRIBUTE_EMAIL = "email";
    public static final String USER_ATTRIBUTE_DISPLAY_NAME = "displayName";

    public static final String AUTH_DATA_EMAIL = "email";
    public static final String AUTH_DATA_DISPLAY_NAME = "displayName";

    private HoursFirebaseLoginHelper() {

    }

    public static AuthData setupUserAuth(Context context) {

        Firebase ref = HoursFirebaseConnector.getBasicRef();
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

    public static void logoutFirebase(Context context) {
        HoursFirebaseConnector.getBasicRef().unauth();
        setupUserAuth(context);
    }
}
