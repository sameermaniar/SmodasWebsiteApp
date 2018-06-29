package in.co.tripin.smodaswebsiteapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.RemoteMessage;
import com.keiferstone.nonet.NoNet;

import java.text.SimpleDateFormat;

import dmax.dialog.SpotsDialog;
import in.co.tripin.smodaswebsiteapp.models.NotificationPojo;
import in.co.tripin.smodaswebsiteapp.models.UpdatesViewHolder;

public class NotificationsActivity extends AppCompatActivity {

    RecyclerView mUpdatesList;
    private DocumentReference mUserDocRef;
    private Query query;

    private FirestoreRecyclerOptions<NotificationPojo> options;
    private FirestoreRecyclerAdapter adapter;
    private AlertDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            startActivity(new Intent(NotificationsActivity.this,AuthLandingActivity.class));
        }

        mUpdatesList = findViewById(R.id.rv_updates);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
//        mLayoutManager.setReverseLayout(true);
//        mLayoutManager.setStackFromEnd(true);
        mUpdatesList.setLayoutManager(mLayoutManager);
        setTitle("Notifications");
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .setMessage("Loading")
                .build();

        NoNet.monitor(this)
                .poll()
                .snackbar();

//        For production only
        query = FirebaseFirestore.getInstance()
                .collection("notifications").orderBy("mTimeStamp", Query.Direction.DESCENDING).limit(30);


        options = new FirestoreRecyclerOptions.Builder<NotificationPojo>()
                .setQuery(query, NotificationPojo.class)
                .build();

        dialog.show();

        adapter = new FirestoreRecyclerAdapter<NotificationPojo, UpdatesViewHolder>(options) {


            @Override
            public UpdatesViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.item_notification_type_update, group, false);
                return new UpdatesViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                dialog.dismiss();
                mUpdatesList.smoothScrollToPosition(0);

            }

            @Override
            protected void onBindViewHolder(UpdatesViewHolder holder, int position, final NotificationPojo model) {

                holder.title.setText(model.getmTitle());
                holder.description.setText(model.getmMessage());
                holder.time.setText(new SimpleDateFormat("MMM-dd-yyyy").format(model.getmTimeStamp()));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        if(model.getmUrl()!=null){
                            if(!model.getmUrl().isEmpty()){
                                intent.putExtra("url",model.getmUrl());
                                setResult(Activity.RESULT_OK, intent);
                                finish();//finishing activity
                            }
                        }
                    }
                });


            }
        };

        mUpdatesList.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
