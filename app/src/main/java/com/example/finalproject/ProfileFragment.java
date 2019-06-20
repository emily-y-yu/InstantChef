package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener, ValueEventListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ArrayList<String> ingredientList;
    private IngredientAdapter adapter;

    private ListView listview;
    private EditText ingredient;
    private Button addButton;
    private Button logoutButton;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private String userEmail;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View group = inflater.inflate(R.layout.fragment_profile, container, false);

        userEmail = getActivity().getIntent().getExtras().getString("email");
        userEmail = userEmail.replace(".", ",");

        // initializes FirebaseDatabase instance
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users");
        databaseReference.child(userEmail).child("ingredients").addValueEventListener(this);

        listview = (ListView) group.findViewById(R.id.profileIngredientsList);
        ingredient = (EditText) group.findViewById(R.id.ingredient);

        addButton = (Button) group.findViewById(R.id.addButton);
        addButton.setOnClickListener(this);

        logoutButton = (Button) group.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(this);

        ingredientList = new ArrayList<String>();
        adapter = new IngredientAdapter();

        listview.setAdapter(adapter);
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                databaseReference.child(userEmail).child("ingredients").child(ingredientList.remove(position)).removeValue();
                adapter.notifyDataSetChanged();
                return true;
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
        switch(v.getId()) {
            // logs a user out of the application
            case R.id.logoutButton:
                // sign out user once they hit button
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), MainActivity.class));
                break;

            // adds an ingredient if a valid string is entered
            case R.id.addButton:
                String text = ingredient.getText().toString();
                if (text != null && !text.equals("")) {
                    text = text.toLowerCase();
                    ingredientList.add(text);
                    ingredient.setText("");

                    // adds ingredient to database under user's email
                    databaseReference.child(userEmail).child("ingredients").child(text).setValue(text);
                }
                else
                    Toast.makeText(getActivity(), "Field cannot be empty!", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        ingredientList.clear(); // make list empty so that it's populated from scratch

        // get each entry one at a time
        for (DataSnapshot ing: dataSnapshot.getChildren())
            ingredientList.add(ing.getValue(String.class));

        // attach listview to adapter
        listview.setAdapter(adapter);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

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

    // adapter inner class used for displaying the list
    private class IngredientAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return ingredientList.size();
        }

        @Override
        public Object getItem(int position) {
            return ingredientList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) convertView;
            if (view == null) {
                view = new TextView(getActivity());
                view.setPadding(10, 10, 10, 10);
                view.setTextSize(16);
            }
            view.setText(ingredientList.get(position));
            return view;
        }
    }
}
