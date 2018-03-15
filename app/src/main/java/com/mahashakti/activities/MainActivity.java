package com.mahashakti.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mahashakti.AppConstants;
import com.mahashakti.R;
import com.mahashakti.baseactivity.BaseActivity;
import com.mahashakti.response.CreateSocialUser.CreateSocialSuccess;
import com.mahashakti.sharedPreferences.UserDataUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener{


    @BindView(R.id.btnLoginIn)
    Button btnLoginIn;
    @BindView(R.id.btnSignup)
    Button btnSignup;

    @BindView(R.id.loginFacebookButton)
    Button loginFacebookButton;
    @BindView(R.id.loginGoogleButton)
    Button loginGoogleButton;

    private CallbackManager callbackManager;
    private Context context;

    // A magic number we will use to know that our sign-in error
    // resolution activity has completed.
    private static final int OUR_REQUEST_CODE = 49404;

    // The core Google Play Services client.
    private GoogleApiClient mGoogleApiClient;

    // A progress dialog to display when the user is connecting in
    // case there is a delay in any of the dialogs being ready.
    private ProgressDialog mConnectionProgressDialog;
    private static final String TAG = MainActivity.class.getSimpleName();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.new_main_activity);
        callbackManager = CallbackManager.Factory.create();


        context = this;

        // First we need to configure the Google Sign In API to ensure we are retrieving
        // the server authentication code as well as authenticating the client locally.
        String serverClientId = getString(R.string.clientid);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(serverClientId)
                .requestEmail()
                .build();

        // We pass through three "this" arguments to the builder, specifying the:
        // 1. Context
        // 2. Object to use for resolving connection errors
        // 3. Object to call onConnectionFailed on
        // We also add the Google Sign in API we previously created.
        mGoogleApiClient = new GoogleApiClient.Builder(this /* Context */)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Configure the ProgressDialog that will be shown if there is a
        // delay in presenting the user with the next sign in step.
        mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setMessage("Signing in...");


    }

    @OnClick({R.id.btnLoginIn, R.id.btnSignup,R.id.loginFacebookButton,R.id.loginGoogleButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnLoginIn:


                navigateToNextActivity(LoginActivity.class);


                break;
            case R.id.btnSignup:


                navigateToNextActivity(SignUpActivity.class);

                break;



            case R.id.loginFacebookButton:

                LoginManager.getInstance().logInWithReadPermissions(
                        this,
                        Arrays.asList("user_friends", "email", "public_profile"));

                LoginManager.getInstance().registerCallback(callbackManager,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {

                                setFacebookData(loginResult);
                            }

                            @Override
                            public void onCancel() {
                            }

                            @Override
                            public void onError(FacebookException exception) {
                            }
                        });

                break;

            case R.id.loginGoogleButton:


                // mGoogle.performSignIn(this);
                Log.v(TAG, "Tapped sign in");
                // Show the dialog as we are now signing in.
                mConnectionProgressDialog.show();

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, OUR_REQUEST_CODE);


                break;


        }
    }


    private void setFacebookData(final LoginResult loginResult)
    {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        try {
                            Log.i("Response",response.toString());

                            String email = response.getJSONObject().getString("email");
                            String firstName = response.getJSONObject().getString("first_name");
                            String lastName = response.getJSONObject().getString("last_name");
                            String gender = response.getJSONObject().getString("gender");



                            Profile profile = Profile.getCurrentProfile();
                            String id = profile.getId();
                            String link = profile.getLinkUri().toString();
                            Log.i("Link",link);
                            if (Profile.getCurrentProfile()!=null)
                            {
                                Log.i("Login", "ProfilePic" + Profile.getCurrentProfile().getProfilePictureUri(200, 200));
                                sharedPrefsHelper.put(AppConstants.PROFILE_PIC, String.valueOf(Profile.getCurrentProfile().getProfilePictureUri(200, 200)));

                            }


                            sharedPrefsHelper.put(AppConstants.USER_NAME,firstName+" "+lastName);
                            sharedPrefsHelper.put(AppConstants.EMAIL,email);
                            sharedPrefsHelper.put(AppConstants.USER_SEX,gender);


                            Log.i("Login" + "Email", email);
                            Log.i("Login"+ "FirstName", firstName);
                            Log.i("Login" + "LastName", lastName);
                            Log.i("Login" + "Gender", gender);


                            String fullName = firstName+" "+lastName;

                            compositeDisposable.add(apiService.createusersocial(fullName,email,"facebook")
                                    .subscribeOn(Schedulers.computation())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<CreateSocialSuccess>() {
                                        @Override
                                        public void accept(CreateSocialSuccess createSocialSuccess) throws Exception {



                                            if(createSocialSuccess.getSuccess()){


                                                sharedPrefsHelper.put(AppConstants.USER_ID,createSocialSuccess.getPayload().getId());

                                                showSuccessDialog(createSocialSuccess.getMessage());



                                                // Snackbar.make(mainLoginLayout, "Login Success", Snackbar.LENGTH_SHORT).show();
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {


                                                        UserDataUtility.setLogin(true,MainActivity.this);
                                                        Intent intent = new Intent(context, DashboardActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);


                                                    }
                                                }, 1000);

                                            }
                                            else {

                                                sharedPrefsHelper.put(AppConstants.USER_ID,createSocialSuccess.getPayload().getId());

                                                showSuccessDialog(createSocialSuccess.getMessage());

                                                // Snackbar.make(mainLoginLayout, createSocialSuccess.getMessage(), Snackbar.LENGTH_SHORT).show();
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {



                                                        UserDataUtility.setLogin(true,MainActivity.this);
                                                        Intent intent = new Intent(context, DashboardActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);


                                                    }
                                                }, 1000);

                                            }


                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {

                                            compositeDisposable.dispose();
                                            throw new RuntimeException("I'm a cool exception and I crashed the main thread!");

                                        }
                                    }));







                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        // mFacebook.onActivityResult(requestCode, resultCode, data);


        if (requestCode == OUR_REQUEST_CODE) {
            // Hide the progress dialog if its showing.
            mConnectionProgressDialog.dismiss();

            // Resolve the intent into a GoogleSignInResult we can process.
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }


    /**
     * Helper method to trigger retrieving the server auth code if we've signed in.
     */
    private void handleSignInResult(GoogleSignInResult result ) {


        if (result.isSuccess()) {

            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.e(TAG, "display name: " + acct.getDisplayName());

            String personName = acct.getDisplayName();
            //String personPhotoUrl = acct.getPhotoUrl().toString();
            String email = acct.getEmail();

           /* Log.e(TAG, "Name: " + personName + ", email: " + email
                    + ", Image: " + personPhotoUrl);
*/


            //  sharedPrefsHelper.put(AppConstants.PROFILE_PIC, personPhotoUrl);
            sharedPrefsHelper.put(AppConstants.USER_NAME,personName);
            sharedPrefsHelper.put(AppConstants.EMAIL,email);



            compositeDisposable.add(apiService.createusersocial(personName,email,"google")
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<CreateSocialSuccess>() {
                        @Override
                        public void accept(CreateSocialSuccess createSocialSuccess) throws Exception {


                            if(createSocialSuccess.getSuccess()){

                                sharedPrefsHelper.put(AppConstants.USER_ID,createSocialSuccess.getPayload().getId());



                                showSuccessDialog(createSocialSuccess.getMessage());


                                //Snackbar.make(mainLoginLayout, "Login Success", Snackbar.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        UserDataUtility.setLogin(true,MainActivity.this);
                                        Intent intent = new Intent(context, DashboardActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);


                                    }
                                }, 1000);

                            }
                            else {


                                sharedPrefsHelper.put(AppConstants.USER_ID,createSocialSuccess.getPayload().getId());

                                showSuccessDialog(createSocialSuccess.getMessage());


                                //Snackbar.make(mainLoginLayout, createSocialSuccess.getMessage(), Snackbar.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        UserDataUtility.setLogin(true,MainActivity.this);
                                        Intent intent = new Intent(context, DashboardActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);


                                    }
                                }, 1000);
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            compositeDisposable.dispose();
                            throw new RuntimeException("I'm a cool exception and I crashed the main thread!");
                        }
                    }));

        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.d(TAG, "onConnectionFailed:" + connectionResult);


    }
}
