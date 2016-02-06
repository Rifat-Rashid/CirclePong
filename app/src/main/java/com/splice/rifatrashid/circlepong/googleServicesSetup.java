package com.splice.rifatrashid.circlepong;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;

public class googleServicesSetup extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Button b1;
    private TextView t1, t2, t3;
    private ProgressBar spinner;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 1;
    private static final int PROFILE_PIC_SIZE = 400;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Games.SCOPE_GAMES)
                .build();


        b1 = (Button) findViewById(R.id.button);
        Typeface lato = Typeface.createFromAsset(getAssets(), "fonts/gt-walsheim-web.ttf");
        Typeface lato2 = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        Typeface lato1 = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        t1 = (TextView) findViewById(R.id.textView);
        t2 = (TextView) findViewById(R.id.textView2);
        t3 = (TextView) findViewById(R.id.textView3);
        t3.setTypeface(lato1);
        t2.setTypeface(lato2);
        t1.setTypeface(lato1);
        b1.setTypeface(lato);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGplus();
            }
        });
        t2.setVisibility(View.INVISIBLE);
        t3.setVisibility(View.INVISIBLE);
        b1.setVisibility(View.INVISIBLE);


        new CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {


                YoYo.with(Techniques.FadeIn)
                        .delay(0)
                        .playOn(findViewById(R.id.button));
                b1.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeIn)
                        .delay(0)
                        .playOn(findViewById(R.id.textView2));
                t2.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeIn)
                        .delay(0)
                        .playOn(findViewById(R.id.textView3));
                t3.setVisibility(View.VISIBLE);
            }
        }.start();
    }
    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    /**
     * Method to resolve any signin errors
     * */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
        //Intent i = new Intent(this.getApplication(), MainActivity.class);
        //startActivity(i);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = connectionResult;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    public void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    public void onStop(){
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
}
