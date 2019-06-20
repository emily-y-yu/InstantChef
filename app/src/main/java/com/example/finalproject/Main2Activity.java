package com.example.finalproject;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class Main2Activity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnFragmentInteractionListener, ViewRecipesFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener,
        CreateRecipeFragment.OnFragmentInteractionListener, IndividualRecipeFragment.OnFragmentInteractionListener,
        SearchResultsFragment.OnFragmentInteractionListener {

    private BottomNavigationView navbar;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main2);

        // load home screen upon user sign in
        loadFragment(new HomeFragment());

        // retrieve intent and user email
        Intent intent = getIntent();
        userEmail = intent.getStringExtra("email");

        navbar = (BottomNavigationView) findViewById(R.id.navigation);
        navbar.setOnNavigationItemSelectedListener(this);
    }

    // helper method that will switch fragments
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment selectedFragment = null;

        switch (menuItem.getItemId()) {
            case R.id.homeItem:
                selectedFragment = new HomeFragment();
                break;
            case R.id.recipesItem:
                selectedFragment = new ViewRecipesFragment();
                break;
            case R.id.searchItem:
                selectedFragment = new SearchFragment();
                break;
            case R.id.profileItem:
                selectedFragment = new ProfileFragment();
                break;
        }

        return loadFragment(selectedFragment);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
