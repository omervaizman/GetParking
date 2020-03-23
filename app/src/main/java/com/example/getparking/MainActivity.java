package com.example.getparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.getparking.Helpers.AppData;
import com.example.getparking.Helpers.MyProgressDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin, btnRegister;
    FirebaseAuth mAuth ;
    FirebaseUser mUser;
    MyProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //first activity of the if the user isn't logged in.
        super.onCreate(savedInstanceState);
        AppData.initialize();
        setContentView(R.layout.activity_main);
        btnLogin = findViewById(R.id.btntoLogin);
        btnRegister =  findViewById(R.id.btntoRegister);
        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        progressDialog = new MyProgressDialog(this);


        if (mUser != null)
        {
            progressDialog.show();
            AppData.getUserDetails(mUser.getUid(), new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful())
                    {
                        progressDialog.dismiss();
                        startActivity(new Intent(MainActivity.this , OptionsActivity.class));
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "The was an error while trying to load your details." , Toast.LENGTH_LONG).show();
                        progressDialog.hide();
                    }
                }
            });
        }
        else
        {
            if (AppData.connectedUser != null)
            {
                AppData.connectedUser = null;
            }
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


}
