package in.co.tripin.smodaswebsiteapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;
import im.delight.android.webview.AdvancedWebView;
import in.co.tripin.smodaswebsiteapp.models.UserPojo;

public class MainNavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,AdvancedWebView.Listener {

    private AdvancedWebView webView;
    private FirebaseAuth mAuth;
    LinearLayout mNotification, mLogOut;
    LinearLayout mTwitter, mFacebook, myoututbe, mInstagram, mRate;
    private TextView mUserName;
    private ImageView mCancelPanel;
    private ImageView mEditProfile;
    private AlertDialog dialog;
    DrawerLayout drawer;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .setMessage("Loading")
                .build();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainNavActivity.this, SplashActivity.class));
            finish();
        }

        getUserData();

        setupWebView();
        init();
        setListners();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void getUserData() {
       dialog.show();
       FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               dialog.dismiss();
               UserPojo userPojo = dataSnapshot.getValue(UserPojo.class);
               if(userPojo!=null){
                   if(!userPojo.getmUserFullName().isEmpty()){
                       mUserName.setText(userPojo.getmUserFullName());
                   }else {
                       mUserName.setText(userPojo.getmUserMobile());
                   }
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {
               dialog.dismiss();

           }
       });

    }

    private void init() {

        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

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



    }

    private void setListners() {


        mFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "https://www.facebook.com/SM-Interconnect-343886469465718/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
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
            }
        });

        mNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                startActivity(new Intent(MainNavActivity.this, NotificationsActivity.class));
            }
        });

        mLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                Toast.makeText(getApplicationContext(),"Edit User profile",Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void setupWebView() {
        webView = findViewById(R.id.webview);
        webView.setListener(this, this);
        webView.loadUrl("https://smodas.wooplr.com/");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

        }
        else if (id == R.id.nav_share) {

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBodyText = "Check it out. Smodas Android App!";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Subject here");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
            startActivity(Intent.createChooser(sharingIntent, "Shearing Option"));

        }
        else if (id == R.id.nav_rate) {

//            String url = "http://www.example.com";
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            i.setData(Uri.parse(url));
//            startActivity(i);

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(String url) {

    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {

    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {

    }
}
