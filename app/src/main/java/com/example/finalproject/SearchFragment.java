package com.example.finalproject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Button searchButton;
    private Spinner cuisineSearchSpinner;
    private Spinner categorySearchSpinner;

    private FirebaseDatabase database;
    private DatabaseReference userReference;
    private DatabaseReference databaseReference;
    private ArrayList<String> searchResults;
    private List<String> ingredientsList;

    private FragmentManager supportFragmentManager;

    private String userEmail;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View group = inflater.inflate(R.layout.fragment_search, container, false);

        userEmail = getActivity().getIntent().getExtras().getString("email");
        userEmail = userEmail.replace(".", ",");

        supportFragmentManager = getActivity().getSupportFragmentManager();

        // list that will hold user's recipes based on their selected filters
        searchResults = new ArrayList<String>(0);

        // list holding all of user's ingredients
        ingredientsList = new ArrayList<String>();

        // initializes FirebaseDatabase instance
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Recipes");

        int count = 0;
        userReference = database.getReference("Users").child(userEmail).child("ingredients");
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap: dataSnapshot.getChildren())
                    ingredientsList.add(snap.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cuisineSearchSpinner = (Spinner) group.findViewById(R.id.cuisineSearchSpinner);
        categorySearchSpinner = (Spinner) group.findViewById(R.id.categorySearchSpinner);

        searchButton = (Button) group.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);

        return group;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.searchButton:
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // outer loop to iterate through "Recipes" (e.g. "Banana Bread")
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            String recipeName = snapshot.getKey();
                            String recipeCategory = (String) snapshot.child("category").getValue();
                            String recipeCuisine = (String) snapshot.child("cuisine").getValue();

                            // get number of ingredients that the recipe requires
                            long ingredientCount = snapshot.child("ingredients").getChildrenCount();
                            long numInPantry = 0;

                            if (categorySearchSpinner.getSelectedItem().toString() != null && recipeCategory.equals(categorySearchSpinner.getSelectedItem().toString())
                                    && cuisineSearchSpinner.getSelectedItem().toString() != null && recipeCuisine.equals(cuisineSearchSpinner.getSelectedItem().toString())) {
                                // inner loop to iterate through ingredients
                                for (DataSnapshot secondSnapshot : snapshot.child("ingredients").getChildren()) {
                                    if (ingredientsList.contains(secondSnapshot.getValue().toString()))
                                        numInPantry++;
                                }
                            }

                            double result = (double) numInPantry / (double) ingredientCount;
                            if (result >= 0.8)
                                searchResults.add(recipeName);
                        }

                        // pass list of recipes to fragment
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("results", searchResults);
                        SearchResultsFragment fragment = new SearchResultsFragment();
                        fragment.setArguments(bundle);
                        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
