package in.co.tripin.smodaswebsiteapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import in.co.tripin.smodaswebsiteapp.models.UserPojo;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignIn";
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;
    private Context activity;
    private int mVerificationState = 0;

    private TextView logIn;
    private EditText mPassword, mMobile, mCountryCode;
    private AwesomeValidation mAwesomeValidation;
    private FirebaseAuth mAuth;
    private Dialog otpDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        setTitle("SignIn");
        init();
        setUpValidation();
        setListners();
        setupPhoneVerificationCallback();
        createOTPDialog();
    }

    private void init() {
        activity = this;
        mCountryCode = findViewById(R.id.countrycode);
        mPassword = findViewById(R.id.password);
        mMobile = findViewById(R.id.mobile);
        logIn = findViewById(R.id.signin);
    }

    private void setUpValidation() {
        mAwesomeValidation = new AwesomeValidation(BASIC);
        mAwesomeValidation.addValidation(this, R.id.mobile, RegexTemplate.TELEPHONE, R.string.err_mobile);
        mAwesomeValidation.addValidation(this, R.id.mobile, RegexTemplate.NOT_EMPTY, R.string.err_mobile);
        mAwesomeValidation.addValidation(this, R.id.password, RegexTemplate.NOT_EMPTY, R.string.err_password);
        mAwesomeValidation.addValidation(this, R.id.countrycode, RegexTemplate.NOT_EMPTY, R.string.err_mobile);

    }

    private void setListners() {

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAwesomeValidation.validate()) {
                    if (mVerificationState == 0) {
                        //check Password from Firebase
                        final FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("users");
                        logIn.setText("Checking Details...");

                        myRef.child(mCountryCode.getText().toString().trim() + mMobile.getText().toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    UserPojo userPojo = dataSnapshot.getValue(UserPojo.class);
                                    if (userPojo.getmUserPassword().equals(mPassword.getText().toString().trim())) {
                                        //Proceed mobile verification
                                        startMobileVerification(mCountryCode.getText().toString().trim() + mMobile.getText().toString().trim());
                                        logIn.setText("Sending OTP... (Resend)");
                                        mVerificationState = 1;
                                    } else {
                                        //password incorrect
                                        Toast.makeText(getApplicationContext(), "Password Incorrect", Toast.LENGTH_LONG).show();
                                        logIn.setText("Sign In");

                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Sign In First!", Toast.LENGTH_LONG).show();
                                    logIn.setText("Sign In");

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                logIn.setText("Sign In");

                            }
                        });


                    } else {
                        resendOTP();
                        logIn.setText("Resending OTP");
                    }
                }
            }
        });
    }


    private void startMobileVerification(String s) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                s,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }


    private void resendOTP() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mCountryCode.getText().toString().trim() + mMobile.getText().toString().trim(),
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                mResendToken);             // ForceResendingToken from callbacks
    }


    private void setupPhoneVerificationCallback() {

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                Toast.makeText(getApplicationContext(), "Verification Failed", Toast.LENGTH_LONG).show();
                logIn.setText("Create Account");
                mVerificationState = 0;
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);
                logIn.setText("OTP Sent, Verifying...");
                otpDialog.show();

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // ...
            }
        };
    }

    private void createOTPDialog() {
        // custom dialog
        otpDialog = new Dialog(activity);
        otpDialog.setContentView(R.layout.enterotp_dialog);
        otpDialog.setTitle("OTP");


        TextView verifyButton = otpDialog.findViewById(R.id.verifyButton);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check if correct
                PinView pinView = otpDialog.findViewById(R.id.pinView);
                String code = pinView.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    pinView.setError("Cannot be empty.");
                    return;
                }
                verifyPhoneNumberWithCode(mVerificationId, code);

                otpDialog.dismiss();
            }
        });

    }

    private void verifyPhoneNumberWithCode(String mVerificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            startActivity(new Intent(SignInActivity.this, MainNavActivity.class));
                            finish();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

}
