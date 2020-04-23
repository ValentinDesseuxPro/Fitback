package com.fitback.fitback;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fitback.fitback.Class.Internet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private TextInputLayout etEmail;
    private TextInputLayout etPassword;
    private Button btnLogin;
    private LoginButton btnFbLogin;
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = (TextInputLayout) findViewById(R.id.etEmail);
        etPassword = (TextInputLayout) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnFbLogin = (LoginButton) findViewById(R.id.btnFbLogin);
        //btnFbLogin.setVisibility(View.VISIBLE);
        TextView tvSignup = (TextView) findViewById(R.id.tvSignup);
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        btnFbLogin.setReadPermissions("email", "public_profile");
        btnFbLogin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                btnFbLogin.setVisibility(View.GONE);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "fb:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "fb:onError", error);
            }
        });

    }

    private void handleFacebookAccessToken(AccessToken accessToken) {

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Connexion...");
        progressDialog.show();
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            startActivity(new Intent(getApplication(), MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Échec de connexion", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void login() {
        String email = etEmail.getEditText().getText().toString().trim();
        String password = etPassword.getEditText().getText().toString().trim();
        Internet internet = new Internet(getApplicationContext());
        if (!isValid(email, password) && internet.isConnected()) {
            btnLogin.setEnabled(true);
        } else {
            btnLogin.setEnabled(false);
            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Connexion...");
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success
                                progressDialog.dismiss();
                                startActivity(new Intent(getApplication(), MainActivity.class));
                                finish();
                            } else {
                                // If sign in fails
                                progressDialog.dismiss();
                                btnLogin.setEnabled(true);
                                Toast.makeText(LoginActivity.this, "Email ou mot de passe incorrect !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public boolean isValid(String email, String password) {
        boolean valid = true;
        if (email.isEmpty()) {
            etEmail.setError("champ vide");
            valid = false;
        } else {
            etEmail.setError(null);
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("email invalide");
            valid = false;
        } else {
            etEmail.setError(null);
        }
        if (password.isEmpty()) {
            etPassword.setError("champ vide");
            valid = false;
        } else {
            etPassword.setError(null);
        }
        if (password.length() < 6) {
            etPassword.setError("minimum 6 caractères");
            valid = false;
        } else {
            etPassword.setError(null);
        }
        return valid;
    }

}
