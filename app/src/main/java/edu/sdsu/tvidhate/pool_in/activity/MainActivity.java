package edu.sdsu.tvidhate.pool_in.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import edu.sdsu.tvidhate.pool_in.R;
import edu.sdsu.tvidhate.pool_in.fragment.AddTripFragment;
import edu.sdsu.tvidhate.pool_in.fragment.HomeFragment;
import edu.sdsu.tvidhate.pool_in.fragment.MyProfileFragment;
import edu.sdsu.tvidhate.pool_in.fragment.MyTripsFragment;
import edu.sdsu.tvidhate.pool_in.fragment.UpdateProfileFragment;
import edu.sdsu.tvidhate.pool_in.helper.SharedConstants;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener,
        AddTripFragment.OnFragmentInteractionListener,MyProfileFragment.OnFragmentInteractionListener,MyTripsFragment.OnFragmentInteractionListener,
        UpdateProfileFragment.OnFragmentInteractionListener,SharedConstants,NavigationView.OnNavigationItemSelectedListener,View.OnClickListener
{
    private FirebaseAuth auth;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private FloatingActionButton fab;
    private String[] activityTitles;
    private static int navItemIndex = 0;
    private static String CURRENT_TAG = TAG_HOME;
    private Handler mHandler;
    public static int height,width;
    private Uri mUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        String contact="";
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){
            contact = auth.getCurrentUser().getDisplayName();
        }

        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        Toolbar toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        fab = findViewById(R.id.fab);
        navigationView = findViewById(R.id.nav_view);

        setSupportActionBar(toolbar);
        mHandler = new Handler();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        View header = navigationView.getHeaderView(0);
        TextView navigation_header_caption = header.findViewById(R.id.nav_header_small_text);
        navigation_header_caption.setText(contact);

        fab.setOnClickListener(this);
   //     appInfoButton.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
    }


    private void loadHomeFragment() {

        Log.i("TPV-NOTE","in load fragment");
        selectNavMenu();

        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(activityTitles[navItemIndex]);
        }
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            toggleFab();
            return;
        }
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                Fragment fragment = getHomeFragment();
                Log.i("TPV-NOTE","current fragement"+fragment.toString()+CURRENT_TAG);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.screen_area, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        mHandler.post(mPendingRunnable);
        toggleFab();
        drawer.closeDrawers();
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                return new HomeFragment();
            case 1:
                AddTripFragment addTripFragment = new AddTripFragment();
                Log.i("TPV-NOTE","Creating Trip");
                return addTripFragment;
            case 2:
                return new MyTripsFragment();
    /*        case 3:
                return new MyProfileFragment();
            case 4:
                return new RequestsFragment();
*/
            default:
                return new HomeFragment();
        }
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        Log.d("TPV-NOTE","in back pressed");
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home

        // checking if user is on other navigation menu
        // rather than home
        if (navItemIndex != 0) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_app_info) {
            Intent appInfo = new Intent(MainActivity.this, AppInfoActivity.class);
            startActivity(appInfo);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        updateUI(user);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Log.d("TPV-NOTE","in onstart:"+user.getUid());
        } else {
            Log.d("TPV-NOTE","in onstart user is null:");
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri){

    }

    private void toggleFab() {
        if (navItemIndex == 0)
            fab.show();
        else
            fab.hide();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    navItemIndex = 0;
                    CURRENT_TAG = TAG_HOME;
                    break;
                case R.id.nav_add_trip:
                    navItemIndex = 1;
                    CURRENT_TAG = TAG_ADD_TRIP;
                    break;
                case R.id.nav_my_trips:
                    navItemIndex = 2;
                    CURRENT_TAG = TAG_MY_TRIPS;
                    break;
        /*        case R.id.nav_my_profile:
                    navItemIndex = 3;
                    CURRENT_TAG = TAG_MY_PROFILE;
                    break;
                case R.id.nav_requests:
                    navItemIndex = 4;
                    CURRENT_TAG = TAG_REQUESTS;
                    break;*/
                case R.id.nav_sign_out:
                    auth.signOut();
                    Intent login = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(login);
                    finish();
                    break;
                default:
                    navItemIndex = 0;
            }
            //Checking if the item is in checked state or not, if not make it in checked state
            if (menuItem.isChecked()) {
                menuItem.setChecked(false);
            } else {
                menuItem.setChecked(true);
            }
            menuItem.setChecked(true);
            loadHomeFragment();
            return true;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.fab:
                navItemIndex = 1;
                CURRENT_TAG = TAG_ADD_TRIP;
                loadHomeFragment();
                break;

        }

    }
}
