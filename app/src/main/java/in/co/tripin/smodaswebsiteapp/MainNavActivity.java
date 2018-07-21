package in.co.tripin.smodaswebsiteapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebView;
import com.keiferstone.nonet.NoNet;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;
import in.co.tripin.smodaswebsiteapp.models.UserPojo;

public class MainNavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private LinearLayout mLin;
    LinearLayout mNotification, mLogOut, mRedeem, mHelp, mReturn;
    LinearLayout mTwitter, mFacebook, myoututbe, mInstagram, mRate;
    private TextView mUserName;
    private ImageView mCancelPanel;
    private ImageView mEditProfile;
    private AlertDialog dialog;
    private DrawerLayout drawer;
    private String NotifUrl = "";
    private String mCurrentURL = "";
    private Context mContext;
    private ValueEventListener valueEventListener;
    private AgentWeb.PreAgentWeb mPreAgentWeb;
    private AgentWeb mAgentWeb;
    private WebViewClient mWebViewClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav);

        NoNet.monitor(this)
                .poll()
                .snackbar();

        mContext = this;
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(true)
                .setMessage("Loading")
                .build();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainNavActivity.this, SplashActivity.class));
            finish();
        }

        getUserData();

        init();
        buildWebView();

        if(getIntent().getExtras()!=null){
            if(getIntent().getExtras().getString("url")!=null){
                if(!getIntent().getExtras().getString("url").isEmpty()){
                    setupWebView(getIntent().getExtras().getString("url"));
                    NotifUrl = getIntent().getExtras().getString("url");
                }else {
                    setupWebView("https://smodas.wooplr.com/");
                }
            }else {
                setupWebView("https://smodas.wooplr.com/");
            }
        }else {
            setupWebView("https://smodas.wooplr.com/");
        }

        setListners();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void buildWebView(){

        mWebViewClient = new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                dialog.show();
                mCurrentURL = url;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                dialog.dismiss();
            }
        };

        mPreAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(mLin, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .setWebViewClient (mWebViewClient)
                .createAgentWeb()
                .ready();


    }

    private void setupWebView(String url) {
      mAgentWeb = mPreAgentWeb.go(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }

        if(!NotifUrl.isEmpty()&&!NotifUrl.equals("https://smodas.wooplr.com/")){
            setupWebView("https://smodas.wooplr.com/");
            return true;
        }

        if(mCurrentURL.equals("https://smodas.wooplr.com/")){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainNavActivity.this);
            builder.setTitle(R.string.app_name);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setMessage("Do you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }
    @Override
    public void onDestroy() {
        mAgentWeb . getWebLifeCycle () . onDestroy ();
        super . onDestroy ();
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    private void getUserData() {
        dialog.show();
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dialog.dismiss();

                if(dataSnapshot.exists()){
                    UserPojo userPojo = dataSnapshot.getValue(UserPojo.class);
                    if (userPojo != null) {
                        if (userPojo.getmUserFullName() != null) {
                            if (!userPojo.getmUserFullName().isEmpty()) {
                                mUserName.setText(userPojo.getmUserFullName());
                            } else {
                                mUserName.setText(userPojo.getmUserMobile());
                            }
                        }
                    }
                }else {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).removeEventListener(valueEventListener);
                    Toast.makeText(getApplicationContext(),"User Not Found, Sign UP!",Toast.LENGTH_LONG).show();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainNavActivity.this, AuthLandingActivity.class));
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();

            }
        };

        FirebaseDatabase.getInstance()
                .getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .addValueEventListener(valueEventListener);

    }

    private void init() {

        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);

        drawer = findViewById(R.id.drawer_layout);

        mFacebook = header.findViewById(R.id.facebook);
        mInstagram = header.findViewById(R.id.instagram);
        mTwitter = header.findViewById(R.id.twitter);
        myoututbe = header.findViewById(R.id.youtube);
        mRate = header.findViewById(R.id.rateonplay);

        mUserName = header.findViewById(R.id.username);
        mCancelPanel = header.findViewById(R.id.cancelpanel);
        mEditProfile = header.findViewById(R.id.editprofile);
        mNotification = header.findViewById(R.id.notif);
        mLogOut = header.findViewById(R.id.logoutt);
        mRedeem = header.findViewById(R.id.redeem);
        mHelp = header.findViewById(R.id.help);
        mReturn = header.findViewById(R.id.returnn);
        mLin = findViewById(R.id.llmain);

    }

    private void setListners() {


        mFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //getOpenFacebookIntent(getApplicationContext());
                try {
                    Intent intent;
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + "221550015100537"));
                    startActivity(intent);
                } catch (Exception e) {
                    Intent intent;
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + "221550015100537"));
                    startActivity(intent);
                }

            }
        });

        mTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        myoututbe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.youtube.com/channel/UCCrAI6da_Tjcqyq_yPEU0PA";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        mInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "https://www.instagram.com/smodassmi/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        mRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                rateApp();
            }
        });

        mNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                startActivityForResult(new Intent(MainNavActivity.this, NotificationsActivity.class), 1);

            }
        });

        mLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).removeEventListener(valueEventListener);

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainNavActivity.this, AuthLandingActivity.class));
                finish();

            }
        });
        mCancelPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                startActivity(new Intent(MainNavActivity.this, EditProfileActivity.class));
            }
        });

        mRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                setupWebView("http://tiny.cc/SMODASbookmyshowoffer");
            }
        });

        mHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                setupWebView("https://smodas.wooplr.com/faq");
            }
        });

        mReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                setupWebView("https://smodas.wooplr.com/check-order-status");
            }
        });

    }



    @Override
    protected void onStop() {
        super.onStop();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notifications) {
            startActivity(new Intent(MainNavActivity.this, NotificationsActivity.class));
        } else if (id == R.id.nav_logout) {

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainNavActivity.this, AuthLandingActivity.class));
            finish();

        } else if (id == R.id.nav_youtube) {

//            String url = "http://www.example.com";
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            i.setData(Uri.parse(url));
//            startActivity(i);

        } else if (id == R.id.nav_instagram) {

            String url = "https://www.instagram.com/smodassmi/";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);

        } else if (id == R.id.nav_twitter) {

//            String url = "http://www.example.com";
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            i.setData(Uri.parse(url));
//            startActivity(i);

        } else if (id == R.id.nav_share) {

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBodyText = "Check it out. Smodas Android App!";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject here");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
            startActivity(Intent.createChooser(sharingIntent, "Shearing Option"));

        } else if (id == R.id.nav_rate) {

//            String url = "http://www.example.com";
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            i.setData(Uri.parse(url));
//            startActivity(i);

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + mContext.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + mContext.getPackageName())));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        Log.v("OnActResult: ", "On Activity Result : result code: " + resultCode + " request code:" + requestCode);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                Toast.makeText(getApplicationContext(), "Notification Redirect!", Toast.LENGTH_SHORT).show();
                String url = intent.getStringExtra("url");
                Log.v("onActResult: ",url);

                setupWebView(url);

            }
        }


    }



}
