package com.example.getparking.Helpers;

import androidx.annotation.NonNull;

import com.example.getparking.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AppData {
    private static AppData instance;

    public static CollectionReference UserCollection;
    public static CollectionReference PostsCollection;
    public static User connectedUser;

    private AppData()
    {
        UserCollection = FirebaseFirestore.getInstance().collection("Users");
        PostsCollection = FirebaseFirestore.getInstance().collection("Posts");
    }

    public static void initialize()
    {
        if (instance == null)
        {
            instance = new AppData();
        }
    }
    public static void getUserDetails(String uid , final OnCompleteListener listener)
    {
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            AppData.UserCollection.document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task)
                {
                    if (task.isSuccessful())
                    {
                        if (task.getResult() != null)
                        {
                            connectedUser = task.getResult().toObject(User.class);
                        }
                        listener.onComplete(task);
                    }
                }
            });
        }
    }
}
