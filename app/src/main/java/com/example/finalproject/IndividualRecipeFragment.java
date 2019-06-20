package com.example.finalproject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
 * {@link IndividualRecipeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link IndividualRecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IndividualRecipeFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private DatabaseReference userref;

    private TextView recipeNameLabel;
    private TextView recipeAuthor;
    private TextView recipeCuisine;
    private TextView recipeCategory;
    private ListView recipeIngredients;
    private TextView recipeInstructions;

    private Button favoriteButton;
    private Button unfavoriteButton;
    private String userEmail;
    private String recipeName;

    public IndividualRecipeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IndividualRecipeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IndividualRecipeFragment newInstance(String param1, String param2) {
        IndividualRecipeFragment fragment = new IndividualRecipeFragment();
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
        View group = inflater.inflate(R.layout.fragment_individual_recipe, container, false);

        Bundle bundle = this.getArguments();
        recipeName = bundle.getString("recipeName");

        userEmail = getActivity().getIntent().getExtras().getString("email");
        userEmail = userEmail.replace(".", ",");

        // initializes FirebaseDatabase instance
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Recipes").child(recipeName);

        recipeNameLabel = (TextView) group.findViewById(R.id.recipeNameLabel);
        recipeNameLabel.setText(recipeName);

        recipeAuthor = (TextView) group.findViewById(R.id.recipeAuthor);
        recipeCuisine = (TextView) group.findViewById(R.id.recipeCuisine);
        recipeCategory = (TextView) group.findViewById(R.id.recipeCategory);
        recipeIngredients = (ListView) group.findViewById(R.id.recipeIngredients);
        recipeInstructions = (TextView) group.findViewById(R.id.recipeInstructions);

        databaseReference.child("author").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeAuthor.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("cuisine").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeCuisine.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeCategory.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("ingredients").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> ingredients = (List<String>) dataSnapshot.getValue();
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, ingredients);
                recipeIngredients.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("instructions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeInstructions.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        favoriteButton = (Button) group.findViewById(R.id.favoriteButton);
        favoriteButton.setOnClickListener(this);

        unfavoriteButton = (Button) group.findViewById(R.id.unfavoriteButton);
        unfavoriteButton.setOnClickListener(this);

        userref = database.getReference("Users").child(userEmail).child("favorites");
        userref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // only show unfavorite button if the recipe already exists in user's favorites
                if (dataSnapshot.hasChild(recipeName)) {
                    favoriteButton.setVisibility(View.GONE);
                    unfavoriteButton.setVisibility(View.VISIBLE);
                    return;
                }
                unfavoriteButton.setVisibility(View.GONE);
                favoriteButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
        switch (v.getId()) {
            case R.id.favoriteButton:
                userref.child(recipeName).setValue(recipeName);
                favoriteButton.setVisibility(View.GONE);
                unfavoriteButton.setVisibility(View.VISIBLE);

                Toast.makeText(getActivity(), "Added to Recipe Bank!", Toast.LENGTH_LONG).show();
                break;
            case R.id.unfavoriteButton:
                userref.child(recipeName).removeValue();
                favoriteButton.setVisibility(View.VISIBLE);
                unfavoriteButton.setVisibility(View.GONE);

                Toast.makeText(getActivity(), "Removed from Recipe Bank!", Toast.LENGTH_LONG).show();
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
