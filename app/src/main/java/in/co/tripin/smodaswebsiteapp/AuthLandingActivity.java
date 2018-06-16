package in.co.tripin.smodaswebsiteapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AuthLandingActivity extends AppCompatActivity {

    private TextView mSignUp,mSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_landing);
        init();
        setListners();

    }

    private void setListners() {
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AuthLandingActivity.this,SignUpActivity.class));
            }
        });
        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AuthLandingActivity.this,SignInActivity.class));
            }
        });
    }

    private void init() {
        mSignIn = findViewById(R.id.signin);
        mSignUp = findViewById(R.id.signup);
    }
}
