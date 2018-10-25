package com.example.project.nobese.login;



import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.nobese.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResendVerificationDialog extends DialogFragment {


    private EditText mPassword;
    private EditText mEmail;


    Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.resendverification_dialog, container, false);

        mEmail = view.findViewById(R.id.dialog_email);
        mPassword = view.findViewById(R.id.dialog_password);
        mContext = getActivity();

        TextView cancelDialog = view.findViewById(R.id.dialog_cancel);
        TextView confirmDialog = view.findViewById(R.id.dialog_confirm);

        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        confirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isValid(mEmail.getText().toString()) && isValid(mPassword.getText().toString())) {
                    if (testEmail(mEmail.getText().toString()) && testPassword(mPassword.getText().toString())) {

                        authenticateAndResendEmail(mEmail.getText().toString(),
                                mPassword.getText().toString());

                    } else {
                        Toast.makeText(mContext, "inValid password / Email", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "Fill the fields properly", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;

    }

    private boolean isValid(String text) {
        return !TextUtils.isEmpty(text) && !text.contains(" ");
    }

    private boolean testEmail(String email) {
        return email.contains(".") && email.contains("@");
    }

    private boolean testPassword(String pass) {
        return pass.length() >= 6;
    }

    /*
    ---------------------------------------------FIREBASE -----------------------------------------
     */

    private void authenticateAndResendEmail(String email, String password) {

        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            getDialog().dismiss();

                            Toast.makeText(mContext, "Email verification Sent !!", Toast.LENGTH_SHORT).show();
                            sendUserVerification();
                            FirebaseAuth.getInstance().signOut();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(mContext, "Invalid credentials. Please try again ", Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
        });
    }

    private void sendUserVerification() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(mContext, "Verification ID sent !!", Toast.LENGTH_SHORT).show();
                                Log.e("zodea", "sent bro !!");
                            } else {
                                Toast.makeText(mContext, "Error.. please try again ", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }


    }


}

