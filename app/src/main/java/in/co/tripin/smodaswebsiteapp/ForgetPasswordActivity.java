package in.co.tripin.smodaswebsiteapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ForgetPasswordActivity extends AppCompatActivity {

    TextView change;
    EditText et1,et2;
    String mobile = "";
    AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        setTitle("Change Password");
        init();
        setListners();
        addValidations();
        mobile = getIntent().getExtras().getString("mobile");
        if(mobile.isEmpty()){
            finish();
        }




    }

    private void addValidations() {
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this, R.id.newpass1, R.id.newpass2, R.string.err_password_confirmation);
        awesomeValidation.addValidation(this, R.id.newpass1, RegexTemplate.NOT_EMPTY, R.string.err_mobile);
        awesomeValidation.addValidation(this, R.id.newpass2, RegexTemplate.NOT_EMPTY, R.string.err_mobile);
    }

    private void setListners() {
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(awesomeValidation.validate()){
                    change.setText("Applying Changes...");
                    FirebaseDatabase.getInstance().
                            getReference()
                            .child("users")
                            .child(mobile)
                            .child("mUserPassword")
                            .setValue(et1.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(),"Password Changed!",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(ForgetPasswordActivity.this,MainNavActivity.class));
                            finish();
                        }
                    });
                }

            }
        });
    }

    private void init() {
        change = findViewById(R.id.changepass);
        et1 = findViewById(R.id.newpass1);
        et2 = findViewById(R.id.newpass2);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseAuth.getInstance().signOut();
        finish();
    }
}
