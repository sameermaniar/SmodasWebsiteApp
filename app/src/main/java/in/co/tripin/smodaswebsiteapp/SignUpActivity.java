package in.co.tripin.smodaswebsiteapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
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
import java.util.logging.Logger;

import dmax.dialog.SpotsDialog;
import in.co.tripin.smodaswebsiteapp.models.UserPojo;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class SignUpActivity extends AppCompatActivity {

    private String TAG = "SignUp Activity";

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;
    private Context activity;
    private int mVerificationState = 0;

    private TextView createAccount;
    private EditText mEmail, mPassword, mMobile, mCountryCode, mFullName;
    private AwesomeValidation mAwesomeValidation;
    private FirebaseAuth mAuth;
    private Dialog otpDialog;
    private AlertDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        FirebaseApp.initializeApp(this);

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .setMessage("Signing Up")
                .build();


        mAuth = FirebaseAuth.getInstance();

        setTitle("SignUp");
        init();
        setUpValidation();
        setListners();
        setupPhoneVerificationCallback();
        createOTPDialog();


    }

    private void init() {
        activity = this;
        mCountryCode = findViewById(R.id.countrycode);
        mEmail = findViewById(R.id.email);
        mFullName = findViewById(R.id.fullname);
        mPassword = findViewById(R.id.password);
        mMobile = findViewById(R.id.mobile);
        createAccount = findViewById(R.id.create);
    }

    private void setUpValidation() {
        mAwesomeValidation = new AwesomeValidation(BASIC);
        mAwesomeValidation.addValidation(this, R.id.mobile, RegexTemplate.TELEPHONE, R.string.err_mobile);
        mAwesomeValidation.addValidation(this, R.id.email, android.util.Patterns.EMAIL_ADDRESS, R.string.err_email);
        mAwesomeValidation.addValidation(this, R.id.mobile, RegexTemplate.NOT_EMPTY, R.string.err_mobile);
        mAwesomeValidation.addValidation(this, R.id.countrycode, RegexTemplate.NOT_EMPTY, R.string.err_mobile);
        mAwesomeValidation.addValidation(this, R.id.email, RegexTemplate.NOT_EMPTY, R.string.err_email);
        mAwesomeValidation.addValidation(this, R.id.password, RegexTemplate.NOT_EMPTY, R.string.err_password);


    }


    private void setListners() {

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAwesomeValidation.validate()) {
                    if (mVerificationState == 0) {
                        dialog.show();

                        startMobileVerification(mCountryCode.getText().toString().trim() + mMobile.getText().toString().trim());
                        mVerificationState = 1;
                    } else {
                        resendOTP();
                    }
                }
            }
        });
    }


    private void startMobileVerification(final String s) {

        FirebaseDatabase.getInstance().getReference().child("users").child(s).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    dialog.dismiss();
                    createAccount.setText("Sign Up");
                    Toast.makeText(getApplicationContext(),"Mobile Already Registered, Sign In!",Toast.LENGTH_LONG).show();
                }else {
                   verifyy(s);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void verifyy(String s) {

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
                dialog.dismiss();
                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                dialog.dismiss();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                Toast.makeText(getApplicationContext(), "Verification Failed", Toast.LENGTH_LONG).show();
                createAccount.setText("Create Account");
                mVerificationState = 0;
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);
                createAccount.setText("OTP Sent, Verifying...");
                dialog.dismiss();
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
        otpDialog.setTitle("Sending OTP");



        TextView verifyButton =  otpDialog.findViewById(R.id.verifyButton);
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
                            otpDialog.dismiss();
                            setupFirebaseDatabase();


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

    private void setupFirebaseDatabase() {
        dialog.show();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");

        //Create User Object
        UserPojo userPojo = new UserPojo(mFullName.getText().toString().trim(),
                mEmail.getText().toString().trim(),
                mCountryCode.getText().toString().trim() + mMobile.getText().toString().trim(),
                mPassword.getText().toString().trim());
        myRef.child(userPojo.getmUserMobile()).setValue(userPojo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dialog.dismiss();
                startActivity(new Intent(SignUpActivity.this,MainNavActivity.class));
                finish();
            }
        });

    }


    public void redirectToSignIn(View view) {
        Intent intent = new Intent(SignUpActivity.this,SignInActivity.class);
        if(!mMobile.getText().toString().trim().isEmpty()){
            intent.putExtra("mobile",mMobile.getText().toString().trim());
        }
        startActivity(intent);
        finish();
    }
}


