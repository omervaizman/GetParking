package com.example.getparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OptionsActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout llRent , llSearch ;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //give the user the option to choose between rent his parking or find parking for leasing.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_options);
        toolbar.setTitle("Action Options");
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        user =(User) intent.getSerializableExtra("user");
        mAuth = FirebaseAuth.getInstance();
        llRent = (LinearLayout) findViewById(R.id.llRent);
        llSearch = (LinearLayout) findViewById(R.id.llSearch);
        llRent.setOnClickListener(this);
        llSearch.setOnClickListener(this);

       mUser = mAuth.getCurrentUser();
    }

    @Override
    public void onClick(View v) {
        if (v == llRent)
        {
            //handle user who want to rent his parking.
            Intent intent = new Intent (OptionsActivity.this , RentParkingActivity.class);
            intent.putExtra("user" , user);
            startActivity(intent);

        }
        if(v == llSearch)
        {
            //handle user who want to search parking to lease.
            Intent intent = new Intent(OptionsActivity.this , FindParkingActivity.class);
            intent.putExtra("user",  user);
            startActivity(intent);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu , menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if (id == R.id.action_logout)
        {
            if (mUser != null)
            {
                mAuth.signOut();
                startActivity(new Intent(OptionsActivity.this , MainActivity.class));
            }
        }
        if (id == R.id.action_account_details)
        {
            Intent intent = new Intent (OptionsActivity.this, AccountDetailsActivity.class);
            intent.putExtra("user",  user);
            startActivity(intent);
        }
        if (id == R.id.action_posts)
        {
            Intent intent = new Intent(OptionsActivity.this , PostManageActivity.class);
            intent.putExtra("user" , user);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
    }
}
