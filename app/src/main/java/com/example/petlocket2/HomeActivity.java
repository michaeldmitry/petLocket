package com.example.petlocket2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity  implements ContactFragment.OnFragmentInteractionListener, EditProfile.OnFragmentInteractionListener,
        SendReport.OnFragmentInteractionListener{



    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();


        drawerLayout = findViewById(R.id.drawer_layout);
       toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
//

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView drawer = findViewById(R.id.drawer);
        drawer.setNavigationItemSelectedListener(drawerListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.messages,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_mesg: startActivity(new Intent(this,ChatsActivity.class));
            break;
        }

        if(toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFrag= null;
            switch (menuItem.getItemId()){
                case R.id.nav_profile:
                    selectedFrag= new ProfileFragment();
                    break;
                case R.id.nav_services:
                    selectedFrag= new ServicesFragment();
                    break;
                case R.id.nav_petshops:
                    selectedFrag= new LocationFragment();
                    break;
                case R.id.nav_newsfeed:
                    selectedFrag= new NewsfeedFragment();
                    break;

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFrag).commit();
            return true;
        }


    };

    private NavigationView.OnNavigationItemSelectedListener drawerListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFrag= null;
            switch (menuItem.getItemId()){
                case R.id.contact_us:
                    selectedFrag= new ContactFragment();
                    break;
                case R.id.send_report:
                    selectedFrag= new SendReport();
                    break;
                case R.id.saved_posts:
                    selectedFrag= new SavedPosts();
                    break;
                case R.id.saved_pets:
                    selectedFrag= new SavedPets();
                    break;
                case R.id.logout:
                    SharedPreferences sharedPreferences =HomeActivity.this.getSharedPreferences("com.example.petlocket2",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor= sharedPreferences.edit();
                    editor.putInt(("com.example.petlocket2.rememberme"), 0).apply();

                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                    break;

            }
            drawerLayout.closeDrawers();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFrag).commit();
            return true;
        }
    };


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
