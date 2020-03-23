package com.example.getparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
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

import com.example.getparking.Helpers.AppData;
import com.example.getparking.Helpers.MyProgressDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

public class PostManageActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener
{
    ListView lv;
    PostAdapter postAdapter;
    MyProgressDialog progressDialog;
    FirebaseAuth auth;
    FirebaseUser user_FireBase;
    Button done;
    public ArrayList<ParkingPost> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //user can manage his own parking posts from this activity.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_manage);
        Toolbar toolbar = findViewById(R.id.toolbar_manageposts);
        toolbar.setTitle("parking posts");
        setSupportActionBar(toolbar);
        lv = findViewById(R.id.lv_findParkingActivity);
        progressDialog = new MyProgressDialog(this);
        done = findViewById(R.id.btnDone_PostManageActivity);
        done.setOnClickListener(this);
        auth = FirebaseAuth.getInstance();
        user_FireBase = auth.getCurrentUser();

        if (user_FireBase == null) {
            AppData.connectedUser = null;
            startActivity(new Intent(PostManageActivity.this, MainActivity.class));
        }
        if (AppData.connectedUser == null)
        {
            auth.signOut();
            startActivity(new Intent(PostManageActivity.this, MainActivity.class));
        }
        progressDialog.show();
        loadPosts();
        lv = findViewById(R.id.lv_managePostsActivity);
        lv.setOnItemClickListener(this);

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(PostManageActivity.this, 0, postList);
        lv.setAdapter(postAdapter);

    }

    public void loadPosts()
    {
        postList = new ArrayList<>();
        AppData.UserCollection.document(AppData.connectedUser.uid).collection("Posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    if (task.getResult() != null)
                    {
                        for (QueryDocumentSnapshot document : task.getResult())
                        {
                            AppData.PostsCollection.document(document.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        if (task.getResult() != null)
                                        {
                                            postList.add(task.getResult().toObject(ParkingPost.class));
                                            postAdapter.notifyDataSetChanged();
                                        }
                                        else
                                            {
                                            progressDialog.hide();
                                            Toast.makeText(PostManageActivity.this, "Error with loading parkings from the server!", Toast.LENGTH_LONG).show();
                                            }
                                    }
                                    else
                                    {
                                        progressDialog.hide();
                                        Toast.makeText(PostManageActivity.this, "Error with loading parkings from the server!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        progressDialog.hide();
                        postAdapter.notifyDataSetChanged();

                    }
                }
                else
                    {
                    progressDialog.hide();
                    Toast.makeText(PostManageActivity.this, "Error with loading parkings from the server!", Toast.LENGTH_LONG).show();
                    }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.findparking_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if (id == R.id.action_reload_findparking)
        {
            progressDialog.show();
            loadPosts();
        }
        if (id == R.id.action_logout)
        {
            if (user_FireBase != null)
            {
                auth.signOut();
                AppData.connectedUser = null;
                startActivity(new Intent(PostManageActivity.this, MainActivity.class));
            }
        }
        if (id == R.id.action_account_details)
        {
            Intent intent = new Intent(PostManageActivity.this, AccountDetailsActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_posts)
        {
            Intent intent = new Intent(PostManageActivity.this, PostManageActivity.class);
            startActivity(intent);
        }
        return true;
    }

    public void removePostFromDB(final ParkingPost post)
    {
        //if the user want to remove one of his posts from the database.
        AppData.PostsCollection.document(post.getPostId())
                .delete().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    AppData.UserCollection.document(AppData.connectedUser.uid).collection("Posts")
                            .document(post.getPostId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                postList.remove(post);
                                Toast.makeText(PostManageActivity.this, "Post deleted successfully!", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(PostManageActivity.this, "An error occurred", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(PostManageActivity.this, "An error occurred", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(PostManageActivity.this);
        builder.setTitle("Delete Post");
        builder.setMessage("Are You sure to delete?");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                ParkingPost post = (ParkingPost) parent.getItemAtPosition(position);
                removePostFromDB(post);
                loadPosts();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View v)
    {
        if (v == done)
        {
            Intent intent = new Intent(PostManageActivity.this, OptionsActivity.class);
            startActivity(intent);
        }
    }
}