package com.example.getparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.text.method.PasswordTransformationMethod;
import android.text.method.HideReturnsTransformationMethod;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etFirstName , etLastName ,  etEmail, etPassword, etConfPassword;
    Button btnRegister;
    ImageView ivPasswd , ivConfPasswd;
    int et_password_counter = 0 , et_password_confirm_counter = 0;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    String firstName, lastName , email , password, confPassword;
    FirebaseFirestore db;
    FirebaseUser user;






    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //user can create his account from this activity.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etConfPassword = (EditText) findViewById(R.id.et_Register_password_confirm);
        etFirstName = (EditText) findViewById(R.id.et_Register_firstname);
        etEmail = (EditText) findViewById(R.id.et_Register_email);
        etLastName = (EditText) findViewById(R.id.et_Register_lastname);
        etPassword = (EditText) findViewById(R.id.et_Register_password);
        btnRegister = (Button) findViewById(R.id.btnregister_regActivity);
        ivConfPasswd = (ImageView) findViewById(R.id.ivEye_confirm_register);
        ivPasswd= (ImageView) findViewById(R.id.ivEye_register);
        btnRegister.setOnClickListener(this);
        ivPasswd.setOnClickListener(this);
        ivConfPasswd.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        db = FirebaseFirestore.getInstance();
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


            if(Password_Confirm_Check(password , confPassword))
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
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }


    public void Register(final String email , String password)
    {
        //create new account in the fire-base authentications database.
        progressDialog.setMessage("Create your account, Please wait...");
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            user = mAuth.getCurrentUser();
                            addUserDetailstoDB();
                            Toast.makeText(RegisterActivity.this, "Authentication succeed.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.putExtra("email" , email);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    public boolean Password_Confirm_Check(String p , String c)
    {
        //checks if the user confirm the password.

       return p.equals(c);
    }
    public void addUserDetailstoDB()
    {
        //create user form in the regular database extra details about the user.
        User user_temp = new User(user.getUid() , firstName , lastName, email);
        db.collection("Users")
                .document(user.getUid()).set(user_temp);

    }


}
