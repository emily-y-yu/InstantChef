package com.example.finalproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ValueEventListener {

    private EditText email;
    private EditText password;

    private String specifiedUser;

    private Button loginButton;
    private Button registerButton;

    private FirebaseAuth authref;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        loginButton = (Button) findViewById(R.id.signInButton);
        loginButton.setOnClickListener(this);

        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);

        // initializes FirebaseAuth instance
        authref = FirebaseAuth.getInstance();

        // initializes FirebaseDatabase instance
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users");
        databaseReference.addValueEventListener(this);
    }

    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = authref.getCurrentUser();

        // if user is already logged in
        if (currentUser != null) {
            Intent mapIntent = new Intent(MainActivity.this, Main2Activity.class);

            specifiedUser = currentUser.getEmail();

            // pass in username
            mapIntent.putExtra("email", specifiedUser);
            startActivity(mapIntent);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.signInButton:
                signIn();
                break;

            case R.id.registerButton:
                register();
                break;
        }
    }

    // helper method providing sign in functionality
    private void signIn() {
        String eInput = email.getText().toString();
        String pInput = password.getText().toString();

        // check if fields are filled in properly
        if (TextUtils.isEmpty(eInput)) {
            Toast.makeText(MainActivity.this, "Email field is empty", Toast.LENGTH_LONG).show();
            return;
        }

        if (eInput.indexOf("@") == -1) {
            Toast.makeText(MainActivity.this, "Invalid email address", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(pInput)) {
            Toast.makeText(MainActivity.this, "Password field is empty", Toast.LENGTH_LONG).show();
            return;
        }

        specifiedUser = eInput;

        // handles authentication of user
        authref.signInWithEmailAndPassword(eInput, pInput).addOnCompleteListener(
                this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent main2Intent = new Intent(MainActivity.this, Main2Activity.class);

                            FirebaseUser currentUser = authref.getInstance().getCurrentUser();

                            // pass in username
                            main2Intent.putExtra("email", currentUser.getEmail());
                            startActivity(main2Intent);
                        }

                        else {
                            Toast.makeText(MainActivity.this, "Sign in failed", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    // helper method providing registration functionality
    private void register() {
        final String eInput = email.getText().toString();
        String pInput = password.getText().toString();

        // check if fields are filled in properly
        if (TextUtils.isEmpty(eInput)) {
            Toast.makeText(MainActivity.this, "Email field is empty", Toast.LENGTH_LONG).show();
            return;
        }

        if (eInput.indexOf("@") == -1) {
            Toast.makeText(MainActivity.this, "Invalid email address", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(pInput)) {
            Toast.makeText(MainActivity.this, "Password field is empty", Toast.LENGTH_LONG).show();
            return;
        }

        if (pInput.length() < 6) {
            Toast.makeText(MainActivity.this, "Password should be at least 6 chars long", Toast.LENGTH_LONG).show();
            return;
        }

        final User tempUser = new User(eInput);

        // handles creation of user
        authref.createUserWithEmailAndPassword(eInput, pInput).addOnCompleteListener(
                this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String tempEmail = eInput;
                            tempEmail = tempEmail.replace(".", ",");

                            // create child in database
                            databaseReference.child(tempEmail).setValue(tempUser);

                            Toast.makeText(MainActivity.this, "Account created", Toast.LENGTH_LONG).show();
                        }

                        else {
                            Toast.makeText(MainActivity.this, "Registration failed", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
