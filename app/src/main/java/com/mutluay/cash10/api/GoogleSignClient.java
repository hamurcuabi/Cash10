package com.mutluay.cash10.api;

import android.content.Context;

import com.mutluay.cash10.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class GoogleSignClient {
    private static final String TAG = "GoogleSignClient";
    private static GoogleSignInOptions gso = null;
    private static GoogleSignInClient mGoogleSignInClient = null;

    public static GoogleSignInClient getClient(Context context) {
        if (gso == null) {
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
        }
        if (mGoogleSignInClient == null) {
            mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        }

        return mGoogleSignInClient;
    }
}

