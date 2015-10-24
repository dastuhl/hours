package de.dastuhl.hours;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.dastuhl.hours.data.HoursFirebaseConnector;
import de.dastuhl.hours.data.HoursFirebaseLoginHelper;


public class LoginActivity extends ActionBarActivity {

    @Bind(R.id.login_email_edittext)
    EditText email;
    @Bind(R.id.login_password_edittext)
    EditText password;

    private Firebase authRef;

    private ProgressDialog mAuthProgressDialog;

    private Firebase.AuthStateListener mAuthStateListener;

    private AuthData authenticatedUser;

    public static void start(Context context) {
        Intent loginIntent = new Intent(context, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(loginIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        authRef = HoursFirebaseConnector.getBasicRef();

        mAuthStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (mAuthProgressDialog != null) {
                    mAuthProgressDialog.hide();
                    mAuthProgressDialog.dismiss();
                }
                setAuthenticatedUser(authData);
            }
        };

        authRef.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
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

    @OnClick(R.id.login_button)
    void login() {
        if (!validateData()) {
            return;
        }

        showProgressDialog("Authenticating...");

        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();

        // try to login user
        authRef.authWithPassword(emailText, passwordText, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("provider", authData.getProvider());
                HoursFirebaseConnector connector = new HoursFirebaseConnector(authData.getUid(), LoginActivity.this);
                if(authData.getProviderData().containsKey(HoursFirebaseLoginHelper.AUTH_DATA_DISPLAY_NAME)) {
                    map.put(HoursFirebaseLoginHelper.USER_ATTRIBUTE_DISPLAY_NAME, authData.getProviderData().get(HoursFirebaseLoginHelper.AUTH_DATA_DISPLAY_NAME).toString());
                }
                if (authData.getProviderData().containsKey(HoursFirebaseLoginHelper.AUTH_DATA_EMAIL)) {
                    map.put(HoursFirebaseLoginHelper.USER_ATTRIBUTE_EMAIL, authData.getProviderData().get(HoursFirebaseLoginHelper.AUTH_DATA_EMAIL).toString());
                }
                connector.getUserRef().setValue(map);
            }

            @Override
            public void onAuthenticationError(FirebaseError error) {
                handleError(error);
            }
        });
    }

    private void handleError(FirebaseError error) {
        mAuthProgressDialog.hide();
        String errorMessage = error.getMessage();
        switch (error.getCode()) {
            case FirebaseError.USER_DOES_NOT_EXIST:
            case FirebaseError.INVALID_EMAIL:
            case FirebaseError.EMAIL_TAKEN:
                email.setError(errorMessage);
                break;
            case FirebaseError.INVALID_PASSWORD:
                password.setError(errorMessage);
            default:
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateData() {
        email.setError(null);
        password.setError(null);
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();

        if (!isEmailValid(emailText)) {
            email.setError(getString(R.string.invalid_email));
            return false;
        }

        if (!isPasswordValid(passwordText)) {
            password.setError(getString(R.string.invalid_password));
            return false;
        }

        return true;
    }

    @OnClick(R.id.register_button)
    void register() {
        if (!validateData()) {
            return;
        }

        showProgressDialog("Registering...");

        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();

        authRef.createUser(emailText, passwordText, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                mAuthProgressDialog.hide();
                login();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                mAuthProgressDialog.hide();
                handleError(firebaseError);
            }
        });

    }

    private void showProgressDialog(String message) {
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading");
        mAuthProgressDialog.setMessage(message);
        mAuthProgressDialog.setCancelable(false);
        mAuthProgressDialog.show();
    }

    private boolean isEmailValid(String email) {
        if (TextUtils.isEmpty(email.trim())) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return !TextUtils.isEmpty(password.trim()) && TextUtils.getTrimmedLength(password) >= 6;
    }

    public void setAuthenticatedUser(AuthData pAuthenticatedUser) {
        if (pAuthenticatedUser != null) {
            Intent sessionListIntent = new Intent(this, MainActivity.class);
            sessionListIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(sessionListIntent);
        }
        authenticatedUser = pAuthenticatedUser;
    }
}
