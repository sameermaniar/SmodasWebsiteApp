package in.co.tripin.smodaswebsiteapp;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.keiferstone.nonet.NoNet;

import in.co.tripin.smodaswebsiteapp.models.UserPojo;

public class EditProfileActivity extends AppCompatActivity {

    EditText mFullname;
    TextView mSaveChanges;
    String phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setTitle("Edit Profile");
        mFullname = findViewById(R.id.fullname);
        mSaveChanges = findViewById(R.id.saveprofile);

        NoNet.monitor(this)
                .poll()
                .snackbar();

        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            finish();
        }

        phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        FirebaseDatabase.getInstance().getReference().child("users").child(phone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    UserPojo userPojo = dataSnapshot.getValue(UserPojo.class);
                    if(userPojo!=null){
                        mFullname.setText(userPojo.getmUserFullName());
                        mSaveChanges.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.main_rounded_selector));
                        Toast.makeText(getApplicationContext(),"Profile Updated",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"User Dont Exist",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phone!=null){
                    FirebaseDatabase.getInstance().getReference().child("users")
                            .child(phone)
                            .child("mUserFullName")
                            .setValue(mFullname.getText().toString().trim());
                }
            }
        });

        mFullname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSaveChanges.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.main_rounded_red_selector));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
}
