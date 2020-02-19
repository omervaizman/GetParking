package com.example.getparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;

public class FindParkingActivity extends AppCompatActivity implements View.OnClickListener {

    ListView lv ;
    PostAdapter postAdapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    FirebaseUser user_fireuser;
    User user;
    Button done;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //activity which show all the parking that can be leased.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_parking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_findparking);
        toolbar.setTitle("parking posts");
        setSupportActionBar(toolbar);
        lv = (ListView) findViewById(R.id.lv_findParkingActivity);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading the data from server...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        done = (Button) findViewById(R.id.btnDone_findParkingActivity);
        done.setOnClickListener(this);
        user_fireuser = auth.getCurrentUser();
        loadPosts();
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");



    }
    public void loadPosts()
    {
        //Load all the post from the firebase Posts collection to an ArrayList.
        final ArrayList<ParkingPost> tempList = new ArrayList<ParkingPost>();
        db.collection("Posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    progressDialog.dismiss();
                    for(QueryDocumentSnapshot document : task.getResult())
                    {

                        ParkingPost temp = document.toObject(ParkingPost.class);
                        tempList.add(temp);

                    }
                    if (task.isComplete())
                    {
                        setLv(tempList);
                    }
                }
                else
                {
                    Toast.makeText(FindParkingActivity.this, "Error with loading parkings from the server!" , Toast.LENGTH_LONG).show();

                }
            }
        });
    }
   public void setLv(ArrayList<ParkingPost> postList)
   {

       //set the ListView with the PostAdapter.
       postAdapter = new PostAdapter(this, 0, 0, postList);
       //Toast.makeText(FindParkingActivity.this , postList.get(0).getName() , Toast.LENGTH_LONG).show();
       lv = (ListView) findViewById(R.id.lv_findParkingActivity);

       lv.setAdapter(postAdapter);
   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.findparking_menu , menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //set the menu options selections listener.
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if (id == R.id.action_reload_findparking)
        {
            progressDialog.show();
            loadPosts();
        }
        if (id == R.id.action_logout)
        {
            if (user_fireuser != null)
            {
                auth.signOut();
                startActivity(new Intent(FindParkingActivity.this , MainActivity.class));
            }
        }
        if (id == R.id.action_account_details)
        {
            Intent intent =new Intent (FindParkingActivity.this, AccountDetailsActivity.class);
            intent.putExtra("user" , user);
            startActivity(intent);
        }
        if (id == R.id.action_posts)
        {
            Intent intent = new Intent(FindParkingActivity.this , PostManageActivity.class);
            intent.putExtra("user" , user);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == done)
        {
            //if the user done with parking searching , he will transfer to options activity.
            Intent intent = new Intent(FindParkingActivity.this , OptionsActivity.class);
            intent.putExtra("user" , user);
            startActivity(intent);
        }
    }
}
