package com.example.abdulrahman.serverside;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.abdulrahman.serverside.Common.Common;
import com.example.abdulrahman.serverside.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import info.hoang8f.widget.FButton;

public class SignIn extends AppCompatActivity {
    EditText edtPhone,edtPassword;
    Button btnSignIn;
    FirebaseDatabase database;
    DatabaseReference users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        edtPassword=(MaterialEditText)findViewById(R.id.edtPassword);
        edtPhone=(MaterialEditText)findViewById(R.id.edtPhone);
        btnSignIn=(FButton)findViewById(R.id.btnSignIn);

        //firebase
        database=FirebaseDatabase.getInstance();
        users=database.getReference("User");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser(edtPhone.getText().toString(),edtPassword.getText().toString());
            }
        });


    }

    private void signInUser(String phone,String password) {
        final ProgressDialog dialog =new ProgressDialog(SignIn.this);
        dialog.setMessage("Please Waiting");
        dialog.show();
        final  String localPhone=phone;
        final  String localPassword = password;
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(localPhone).exists())
                {
                    dialog.dismiss();
                    User user =dataSnapshot.child(localPhone).getValue(User.class);
                    user.setPhone(localPhone);
                    if (Boolean.parseBoolean(user.getIsStaff())) //if is staff ==true
                    {
                        if (user.getPassword().equals(localPassword))
                        {
                            //login ok
                            Intent intent =new Intent(SignIn.this,Home.class);
                            Common.currentuser=user;
                            startActivity(intent);
                            finish();
                        }
                        else
                            Toast.makeText(SignIn.this,"Wrong password",Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(SignIn.this,"Please login with Staff account",Toast.LENGTH_SHORT);
                }
                else
                {
                  dialog.dismiss();
                    Toast.makeText(SignIn.this,"User not exist in Database",Toast.LENGTH_SHORT).show();

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
