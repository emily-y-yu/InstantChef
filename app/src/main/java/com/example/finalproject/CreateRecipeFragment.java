package com.example.finalproject;

import android.content.Context;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateRecipeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateRecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateRecipeFragment extends Fragment implements View.OnClickListener, ValueEventListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Button submitButton;
    private Button addIngredientButton;

    private ListView createListview;
    private EditText ingredientInsert;
    private EditText recipeName;
    private EditText instructions;
    private Spinner cuisineSpinner;
    private Spinner categorySpinner;

    private ArrayList<String> ingredientList;
    private IngredientAdapter adapter;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private String userEmail;

    public CreateRecipeFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateRecipeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateRecipeFragment newInstance(String param1, String param2) {
        CreateRecipeFragment fragment = new CreateRecipeFragment();
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
        View group = inflater.inflate(R.layout.fragment_create_recipe, container, false);

        userEmail = getActivity().getIntent().getExtras().getString("email");
        userEmail = userEmail.replace(".", ",");

        recipeName = (EditText) group.findViewById(R.id.recipeName);
        instructions = (EditText) group.findViewById(R.id.instructions);

        cuisineSpinner = (Spinner) group.findViewById(R.id.cuisineSpinner);
        categorySpinner = (Spinner) group.findViewById(R.id.categorySpinner);

        createListview = (ListView) group.findViewById(R.id.createRIngredients);
        ingredientInsert = (EditText) group.findViewById(R.id.ingredientInsert);

        ingredientList = new ArrayList<String>();
        adapter = new IngredientAdapter();

        addIngredientButton = (Button) group.findViewById(R.id.addIngredientButton);
        addIngredientButton.setOnClickListener(this);

        submitButton = (Button) group.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);

        createListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ingredientList.remove(position);
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        // initializes FirebaseDatabase instance
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Recipes");
        databaseReference.addValueEventListener(this);

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
            case R.id.submitButton:
                String name = recipeName.getText().toString();
                if (name != null & !name.equals("")) {
                    if (name.contains(".") || name.contains("$") || name.contains("[") || name.contains("]") || name.contains("#") || name.contains("/"))
                        Toast.makeText(getActivity(), "Recipe name cannot contain ., $, [, ], #, or /", Toast.LENGTH_LONG).show();

                    else {
                        // extract info entered by user
                        String cuisine = cuisineSpinner.getSelectedItem().toString();
                        String category = categorySpinner.getSelectedItem().toString();
                        String email = userEmail.replace(",", ".");
                        String instr = instructions.getText().toString();

                        // inserts full recipe into database
                        databaseReference.child(name).child("cuisine").setValue(cuisine);
                        databaseReference.child(name).child("author").setValue(email);
                        databaseReference.child(name).child("category").setValue(category);
                        databaseReference.child(name).child("ingredients").setValue(ingredientList);
                        databaseReference.child(name).child("instructions").setValue(instr);

                        // clear out everything else
                        cuisineSpinner.setSelection(0);
                        categorySpinner.setSelection(0);
                        recipeName.setText("");
                        instructions.setText("");
                        createListview.setAdapter(null);

                        Toast.makeText(getActivity(), "Recipe successfully added!", Toast.LENGTH_LONG).show();
                    }
                }
                else
                    Toast.makeText(getActivity(), "Recipe name cannot be empty!", Toast.LENGTH_LONG).show();
                break;
            case R.id.addIngredientButton:
                String text = ingredientInsert.getText().toString();
                if (text != null && !text.equals("")) {
                    if (text.contains(".") || text.contains("$") || text.contains("[") || text.contains("]") || text.contains("#") || text.contains("/"))
                        Toast.makeText(getActivity(), "Ingredient name cannot contain ., $, [, ], #, or /", Toast.LENGTH_LONG).show();
                    else {
                        text = text.toLowerCase();
                        ingredientList.add(text);
                        ingredientInsert.setText("");
                        createListview.setAdapter(adapter);
                    }
                }
                else
                    Toast.makeText(getActivity(), "Field cannot be empty!", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

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
