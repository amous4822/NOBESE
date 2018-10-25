package com.example.project.nobese.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import android.support.annotation.NonNull;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.project.nobese.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class RegisterActivity extends AppCompatActivity {

    EditText mUsername;
    EditText mPassword;
    EditText mConfirmPassword;
    EditText mEmail;
    Button mLogin;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mEmail = (EditText) findViewById(R.id.email_login);
        mUsername = (EditText) findViewById(R.id.username_login);
        mPassword = (EditText) findViewById(R.id.password_login);
        mConfirmPassword = (EditText) findViewById(R.id.cnfr_password_login);

        mLogin = (Button) findViewById(R.id.login_button);
        final ProgressDialog dialog = new ProgressDialog(this);


        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideSoftKeyboard();
                //check username validity
                if (isValid(mUsername.getText().toString()) && isValid(mPassword.getText().toString())
                        && isValid(mConfirmPassword.getText().toString()) && isValid(mEmail.getText().toString())) {

                    //check Email validity
                    if (testEmail(mEmail.getText().toString())) {

                        //check if password has 6 characters
                        if (testPassword(mPassword.getText().toString())) {

                            //check if passwords are same
                            if (mPassword.getText().toString().equals(mConfirmPassword.getText().toString())) {

                                //register if everything is OK
                                registerNewEmail(mEmail.getText().toString(), mConfirmPassword.getText().toString());

                            } else {
                                Toast.makeText(RegisterActivity.this, "Passwords don't match ", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
                        }
                    } else
                        Toast.makeText(RegisterActivity.this, "Enter valid Email address", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "You must fill all the fields properly !", Toast.LENGTH_SHORT).show();
                }

            }

        });

    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private boolean testEmail(String email) {
        return email.contains(".") && email.contains("@");
    }

    private boolean isValid(String text) {
        return !TextUtils.isEmpty(text) && !text.contains(" ");
    }

    private boolean testPassword(String pass) {
        return pass.length() >= 6;
    }

    /*
 --------------------------------------------------------------------------FIREBASE SETUP ----------------------------------------------------------------
 */


    private void registerNewEmail(String email, String password) {

        final ProgressDialog dialog = ProgressDialog.show(this, "Loading", "Please wait...", true);

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                           @Override
                                           public void onComplete(@NonNull Task<AuthResult> task) {

                                               if (task.isSuccessful()) {


                                                   sendUserVerification();
                                                   Toast.makeText(RegisterActivity.this, "Registration complete: " + FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                                                   FirebaseAuth.getInstance().signOut();

                                                   dialog.dismiss();
                                                   startActivity(new Intent(RegisterActivity.this, LoginActivity.class));

                                               } else {

                                                   dialog.dismiss();
                                                   Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                               }
                                           }
                                       }
                );
    }

    private void sendUserVerification() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "Verification ID sent !!", Toast.LENGTH_SHORT).show();
                                Log.e("zodea","sent bro !!");
                            }else {
                                Toast.makeText(RegisterActivity.this, "Error.. please try again ", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }


    }

    private void showProgress() {
        ProgressDialog dialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
        dialog.dismiss();
    }


}
