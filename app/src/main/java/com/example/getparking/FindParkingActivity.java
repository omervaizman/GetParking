package com.example.getparking;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.getparking.Helpers.AppData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Objects;

public class FindParkingActivity extends AppCompatActivity implements View.OnClickListener
{
    ListView lv ;
    PostAdapter postAdapter;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    FirebaseUser user_firebase;
    Button done;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //activity which show all the parking that can be leased.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_parking);
        Toolbar toolbar = findViewById(R.id.toolbar_findparking);
        toolbar.setTitle("parking posts");
        setSupportActionBar(toolbar);
        lv = findViewById(R.id.lv_findParkingActivity);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading the data from server...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        auth = FirebaseAuth.getInstance();
        done =  findViewById(R.id.btnDone_findParkingActivity);
        done.setOnClickListener(this);
        user_firebase = auth.getCurrentUser();
        if (user_firebase == null)
        {
            AppData.connectedUser = null;
            startActivity(new Intent(FindParkingActivity.this, MainActivity.class));
        }
        if (AppData.connectedUser == null)
        {
            auth.signOut();
            startActivity(new Intent(FindParkingActivity.this, MainActivity.class));
        }
        loadPosts();
    }

    public void loadPosts()
    {
        //Load all the post from the firebase Posts collection to an ArrayList.
        final ArrayList<ParkingPost> tempList = new ArrayList<>();
        AppData.PostsCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    progressDialog.dismiss();
                    for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult()))
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
       postAdapter = new PostAdapter(this, 0, postList);
       lv = findViewById(R.id.lv_findParkingActivity);
       lv.setAdapter(postAdapter);
   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
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
            if (user_firebase != null)
            {
                AppData.connectedUser = null;
                auth.signOut();
                startActivity(new Intent(FindParkingActivity.this , MainActivity.class));
            }
        }
        if (id == R.id.action_account_details)
        {
            Intent intent =new Intent (FindParkingActivity.this, AccountDetailsActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_posts)
        {
            Intent intent = new Intent(FindParkingActivity.this , PostManageActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onClick(View v)
    {
        if (v == done)
        {
            //if the user done with parking searching , he will transfer to options activity.
            Intent intent = new Intent(FindParkingActivity.this , OptionsActivity.class);
            startActivity(intent);
        }
    }
}