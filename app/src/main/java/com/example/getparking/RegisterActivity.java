package com.example.getparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.text.method.PasswordTransformationMethod;
import android.text.method.HideReturnsTransformationMethod;

import com.example.getparking.Helpers.AppData;
import com.example.getparking.Helpers.MyProgressDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etFirstName , etLastName ,  etEmail, etPassword, etConfPassword;
    Button btnRegister;
    ImageView ivPasswd , ivConfPasswd;
    int et_password_counter = 0 , et_password_confirm_counter = 0;
    private FirebaseAuth mAuth;
    String firstName, lastName , email , password, confPassword;
    FirebaseUser user;
    MyProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etConfPassword = findViewById(R.id.et_Register_password_confirm);
        etFirstName =  findViewById(R.id.et_Register_firstname);
        etEmail =  findViewById(R.id.et_Register_email);
        etLastName = findViewById(R.id.et_Register_lastname);
        etPassword = findViewById(R.id.et_Register_password);
        btnRegister = findViewById(R.id.btnregister_regActivity);
        ivConfPasswd =  findViewById(R.id.ivEye_confirm_register);
        ivPasswd=  findViewById(R.id.ivEye_register);
        btnRegister.setOnClickListener(this);
        ivPasswd.setOnClickListener(this);
        ivConfPasswd.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new MyProgressDialog(this);
    }


    @Override
    public void onClick(View v)
    {
        if (v == btnRegister)
        {
            //if the user finish the create account form.
            email = etEmail.getText().toString();
            password = etPassword.getText().toString();
            confPassword = etConfPassword.getText().toString();
            firstName = etFirstName.getText().toString();
            lastName = etLastName.getText().toString();


            if(password.equals(confPassword))
            {
                Register(email, password);
            }


        }
        if (v == ivPasswd)
        {

            if (et_password_counter % 2 == 0) {
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                et_password_counter ++;
            }
            else {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                et_password_counter ++;
            }


        }
        if (v == ivConfPasswd)
        {
            if(et_password_confirm_counter % 2 == 0)
            {
                etConfPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                et_password_confirm_counter ++;
            }
            else
            {
                etConfPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                et_password_confirm_counter ++;
            }

        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    public void Register(final String email , String password)
    {
        //create new account in the fire-base authentications database.
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            user = mAuth.getCurrentUser();
                            if (user != null) {
                                final User user_temp = new User(user.getUid(), firstName, lastName, email);
                                AppData.UserCollection.document(user.getUid()).set(user_temp)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if (task.isSuccessful())
                                                {
                                                    Toast.makeText(RegisterActivity.this, "Authentication succeed.",
                                                            Toast.LENGTH_SHORT).show();
                                                    AppData.connectedUser = user_temp;
                                                    Intent intent = new Intent(RegisterActivity.this, OptionsActivity.class);
                                                    startActivity(intent);

                                                } else {
                                                    Toast.makeText(RegisterActivity.this, "An error occurred.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                            else
                            {
                                Toast.makeText(RegisterActivity.this, "An error occurred.", Toast.LENGTH_SHORT).show();
                            }

                        }
                        else
                        {
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.hide();
                    }
                });
    }





}
