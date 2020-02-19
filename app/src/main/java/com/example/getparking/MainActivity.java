package com.example.getparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin, btnRegister;
    FirebaseAuth mAuth ;
    FirebaseUser mUser;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //first activity of the if the user isn't logged in.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogin = (Button) findViewById(R.id.btntoLogin);
        btnRegister = (Button) findViewById(R.id.btntoRegister);
        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser != null )
       {
           getUserFromDataBase();
       }
    }
    @Override
    public void onClick(View v) {
        if (v == btnLogin)
        {
            //if the user want to make login.
            Intent intent = new Intent (this, LoginActivity.class);
            startActivity(intent);
        }
        if (v == btnRegister)
        {
        //if the user want to register the app.
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }
    public void getUserFromDataBase()
    {
        //load the user details from database.
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Check your details...");
        progressDialog.show();
        final User user = new User();
        db.collection("Users").document(mUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    progressDialog.dismiss();
                    user.setUser(task.getResult().toObject(User.class));
                    Intent intent = new Intent(MainActivity.this, OptionsActivity.class);
                    intent.putExtra("user" , user);
                    startActivity(intent);
                }
            }
        });
    }

}
