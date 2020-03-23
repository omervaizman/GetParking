package com.example.getparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.text.method.HideReturnsTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.getparking.Helpers.AppData;
import com.example.getparking.Helpers.MyProgressDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etEmail , etPassword;
    int eye_Password_counter = 0;
    ImageView ivPasswd;
    Button btnLogin;
    TextView forgotPass;
    private FirebaseAuth mAuth;
    MyProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //activity to handle user login.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmail =  findViewById(R.id.et_login_email);
        etPassword = findViewById(R.id.et_login_password);
        ivPasswd =  findViewById(R.id.ivEye_login);
        ivPasswd.setOnClickListener(this);
        btnLogin = findViewById(R.id.btnLogin_ActivityLogin);
        btnLogin.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new MyProgressDialog(this);
        forgotPass = findViewById(R.id.tv_ForgotPassword_LoginActivity);
        forgotPass.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (v == btnLogin)
        {
            //if the user click login button it will execute login function.
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            login(email , password);

        }
        //handle user requests for hide and show the password on the screen.
        if(v == ivPasswd)
        {
            if (eye_Password_counter % 2 == 0)
            {
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                eye_Password_counter ++;
            }
            else
            {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                eye_Password_counter ++;
            }
        }
        if (v == forgotPass)
        {
            startActivity(new Intent(LoginActivity.this , RestorePasswordActivity.class));
        }
    }

    public void login(String email ,String password)
    {
        progressDialog.setMessage("Try to login, Please wait...");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            FirebaseUser mUser = mAuth.getCurrentUser();
                            assert mUser != null;
                            AppData.UserCollection.document(mUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        if (task.getResult() != null)
                                        {
                                            AppData.connectedUser = task.getResult().toObject(User.class);
                                            Intent intent = new Intent(LoginActivity.this , OptionsActivity.class);
                                            startActivity(intent);
                                            Toast.makeText(LoginActivity.this, "Login successfully!" , Toast.LENGTH_LONG).show();
                                        }
                                        else
                                        {
                                            Toast.makeText(LoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(LoginActivity.this, "Internet error.",Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.hide();

                    }
                });

    }
    @Override
    public void onStart() {
        super.onStart();

    }
}
