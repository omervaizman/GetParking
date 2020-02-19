package com.example.getparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PostManageActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    ListView lv;
    PostAdapter postAdapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    FirebaseUser user_fireuser;
    ArrayList<String> postIds;
    Button done;
    boolean loadFinished;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //user can manage his own parking posts from this activity.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_manage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_manageposts);
        toolbar.setTitle("parking posts");
        setSupportActionBar(toolbar);
        lv = (ListView) findViewById(R.id.lv_findParkingActivity);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading the data from server...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        done = (Button) findViewById(R.id.btnDone_PostManageActivity);
        done.setOnClickListener(this);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user_fireuser = auth.getCurrentUser();
        loadFinished = false;
        postIds = new ArrayList<String>();
        loadPosts();
        lv = (ListView) findViewById(R.id.lv_managePostsActivity);
        lv.setOnItemClickListener(this);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
    }

    public void loadPosts() {
        //Load all the post from the firebase Posts collection to an ArrayList.
        final ArrayList<ParkingPost> tempList = new ArrayList<ParkingPost>();
        db.collection("Posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        ParkingPost temp = document.toObject(ParkingPost.class);
                        if (temp.getUid().equals(user_fireuser.getUid())) {
                            tempList.add(temp);
                            postIds.add(document.getId());
                        }

                    }
                    if (task.isComplete()) {
                        setLv(tempList);
                    }
                } else {
                    Toast.makeText(PostManageActivity.this, "Error with loading parkings from the server!", Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    public void setLv(ArrayList<ParkingPost> postList)
    {

        //set the ListView with the PostAdapter.
        postAdapter = new PostAdapter(this, 0, 0, postList);
        lv.setAdapter(postAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.findparking_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //set the menu options selections listener.
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if (id == R.id.action_reload_findparking) {
            progressDialog.show();
            loadPosts();
        }
        if (id == R.id.action_logout) {
            if (user_fireuser != null) {
                auth.signOut();
                startActivity(new Intent(PostManageActivity.this, MainActivity.class));
            }
        }
        if (id == R.id.action_account_details) {
            Intent intent = new Intent(PostManageActivity.this, AccountDetailsActivity.class);
            intent.putExtra("user" , user);
            startActivity(intent);
        }
        if (id == R.id.action_posts) {
            Intent intent = new Intent(PostManageActivity.this, PostManageActivity.class);
            intent.putExtra("user" , user);
            startActivity(intent);
        }
        return true;
    }

    public void removePostFromDB(ParkingPost post)
    {
        //if the user want to remove one of his posts from the database.
        db.collection("Posts").document(post.getPostId())
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(PostManageActivity.this , "Post deleted successfully!" , Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(PostManageActivity.this);
        builder.setTitle("Delete Post");
        builder.setMessage("Are You sure to delete?");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ParkingPost post = (ParkingPost) parent.getItemAtPosition(position);
                removePostFromDB(post);
                loadPosts();
            }});
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        if (v == done)
        {
            Intent intent = new Intent (PostManageActivity.this, OptionsActivity.class);
            intent.putExtra("user",  user);
            startActivity(intent);
        }
    }
}


