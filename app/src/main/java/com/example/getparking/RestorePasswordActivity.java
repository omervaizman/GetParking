package com.example.getparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.getparking.Helpers.MyProgressDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RestorePasswordActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth mAuth ;
    EditText etEmail;
    Button btnRestore;
    MyProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_password);
        mAuth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.et_RestoreEmail);
        btnRestore = findViewById(R.id.btnRestore_RestoreActivity);
        btnRestore.setOnClickListener(this);
        dialog = new MyProgressDialog(this);

    }

    @Override
    public void onClick(View v) {
        if (v == btnRestore)
        {
            dialog.show();
            String email = etEmail.getText().toString().trim();
            if (!email.equals("") && email.indexOf("@") > 0)
            {
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(RestorePasswordActivity.this  ,"Password Restore sent successfully , check your Email." , Toast.LENGTH_LONG).show();
                            finish();
                        }
                        else
                        {
                            Toast.makeText(RestorePasswordActivity.this  , "An error occurred , try again." , Toast.LENGTH_LONG).show();
                        }
                        dialog.hide();
                    }
                });

            }
        }
    }
}
