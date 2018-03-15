package com.mahashakti.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.mahashakti.AppConstants;
import com.mahashakti.R;
import com.mahashakti.baseactivity.BaseActivity;
import com.mahashakti.facebook.FacebookHelper;
import com.mahashakti.facebook.FacebookListener;
import com.mahashakti.google.GoogleHelper;
import com.mahashakti.google.GoogleListener;
import com.mahashakti.presenter.LoginPresenter;
import com.mahashakti.response.CreateSocialUser.CreateSocialSuccess;
import com.mahashakti.sharedPreferences.UserDataUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class LoginActivity extends BaseActivity implements LoginPresenter.View {


    private static final String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.edPsw)
    EditText edPsw;
    @BindView(R.id.tvForgetPsw)
    TextView tvForgetPsw;
    @BindView(R.id.btnSignIn)
    Button btnSignIn;

    @BindView(R.id.edEmailId)
    EditText edEmailId;
    @BindView(R.id.mainLoginLayout)
    RelativeLayout mainLoginLayout;
    private Context context;






    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_new_activity);

        ButterKnife.bind(this);
        context = this;






    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);

    }

    @OnClick({R.id.imgBack, R.id.tvForgetPsw, R.id.btnSignIn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.imgBack:

                finish();

                break;
            case R.id.tvForgetPsw:

                startActivity(new Intent(context, ForgetPasswordActivity.class));

                break;
            case R.id.btnSignIn:

                if (TextUtils.isEmpty(edEmailId.getText().toString().trim()) || TextUtils.isEmpty(edPsw.getText().toString().trim()))

                    if (TextUtils.isEmpty(edEmailId.getText().toString().trim())) {
                        Snackbar.make(mainLoginLayout, "Email can't be empty", Snackbar.LENGTH_SHORT).show();

                    }
                    else {
                        Snackbar.make(mainLoginLayout, "Password can't be empty", Snackbar.LENGTH_SHORT).show();

                    }


                else {

                    flipProgress();


                    View view  = this.getCurrentFocus();
                    if(view !=null){

                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
                    }


                    new LoginPresenter(LoginActivity.this,apiService,sharedPrefsHelper,edEmailId.getText().toString().trim(),edPsw.getText().toString().trim());

                    fpd.dismiss();
                }

                break;






        }
    }




    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }




    @Override
    public void loginSuccessful(String message) {



        showSuccessDialog(message);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fpd.dismiss();
                UserDataUtility.setLogin(true,LoginActivity.this);
                Intent intent = new Intent(context, DashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }
        }, 1000);


    }

    @Override
    public void loginFailed(Throwable t) {

        fpd.dismiss();
        showErrorDialog(t.getMessage());

    }



}



