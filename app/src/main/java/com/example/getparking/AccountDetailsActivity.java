package com.example.getparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.accounts.Account;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.text.method.PasswordTransformationMethod;
import android.text.method.HideReturnsTransformationMethod;

import com.example.getparking.Helpers.AppData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Objects;

public class AccountDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth mAuth;
    FirebaseUser user_firebase;
    Dialog dialog_password , dialog_email , dialog_name;
    Button confirm , submit_passwordChange , submit_emailChange , submit_nameChange;
    EditText etFirstName , etLastName , etEmail , etCurrentPass , etNewPass , etConfirmNew , etConfirmPassword_ChangeEmail;
    LinearLayout changePassword , changeEmail , changeName;
    ImageView eyePassEmail , eyeCurrentPass, eyeNewPass, eyePassConfirm;
    int countPassEmail =0, countCurrentPass =0 ,  countNewPass =0 , countPassConfirm =0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Activity for give the user the option to edit his details.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        Toolbar toolbar = findViewById(R.id.toolbar_accountDetails);
        toolbar.setTitle("account details");
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        user_firebase= mAuth.getCurrentUser();
        if (user_firebase == null)
        {
            AppData.connectedUser = null;
            startActivity(new Intent(AccountDetailsActivity.this, MainActivity.class));
        }
        if (AppData.connectedUser == null)
        {
            mAuth.signOut();
            startActivity(new Intent(AccountDetailsActivity.this, MainActivity.class));
        }

        confirm = findViewById(R.id.btnConfirm_AccountDetails);
        changePassword = findViewById(R.id.llChangePassword_AccountDetails);
        changeEmail = findViewById(R.id.llChangeEmail_AccountDetails);
        changeName = findViewById(R.id.llChangeName_AccountDetails);

        confirm.setOnClickListener(this);
        changeEmail.setOnClickListener(this);
        changePassword.setOnClickListener(this);
        changeName.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        if (v == confirm)
        {
            //if the user done with the details editing , back to the options select activity.
            Intent intent = new Intent(AccountDetailsActivity.this , OptionsActivity.class);
            startActivity(intent);
        }
        if (v == changePassword)
        {
            /*if the user wants to change the password,
             create password change dialog.*/
            createChangePasswordDialog();
        }
        if (v == changeEmail)
        {
            /*if the user wants to change the email ,
             create email change dialog */
            createChangeEmailDialog();
        }
        if (v == submit_passwordChange)
        {
            /*
            on the password change dialog - if the user click submit ,
             yes/no dialog will open to make sure the user really want
             to change the password, if yes is clicked - the password will change
             if no it isn't.
            */
            final AlertDialog.Builder builder = new AlertDialog.Builder(AccountDetailsActivity.this);
            builder.setTitle("Password Change");
            builder.setMessage("change the password?");
            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    changeThePassword();
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
        if (v == submit_emailChange)
        {
            //change the email function when press submit on email change dialog.
          changeTheEmail();
        }
        /*eye buttons use to hide and show the password on the screen*/
        if (v == eyeCurrentPass)
        {

            if (countCurrentPass % 2 == 0 )
            {
                etCurrentPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                countCurrentPass ++;
            }
            else
            {
                etCurrentPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                countCurrentPass ++;
            }
        }
        if (v == eyeNewPass)
        {
            if (countNewPass % 2 == 0)
            {
                etNewPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                countNewPass ++;
            }
            else
            {
                etNewPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                countNewPass ++;
            }
        }
        if (v == eyePassConfirm)
        {
            if (countPassConfirm % 2 == 0)
            {
                etConfirmNew.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                countPassConfirm ++;
            }
            else
            {
                etConfirmNew.setTransformationMethod(PasswordTransformationMethod.getInstance());
                countPassConfirm ++;
            }
        }
        if (v == eyePassEmail)
        {
            if (countPassEmail % 2 == 0)
            {
                etConfirmPassword_ChangeEmail.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                countPassEmail ++;
            }
            else
            {
                etConfirmPassword_ChangeEmail.setTransformationMethod(PasswordTransformationMethod.getInstance());
                countPassEmail ++;
            }
        }
        if (v == changeName)
        {
            //create name change dialog.
            createChangeNameDialog();
        }
        if (v == submit_nameChange)
        {
            //if the user submit the name changing, it will change in the database with the following function
            changeTheName();
        }
    }
    public boolean newPasswordsCheck()
    {
        //check if the new password is equals to the confirm field of the new password.
        return etNewPass.getText().toString().equals(etConfirmNew.getText().toString());
    }
    public boolean isEmailChanged(User user)
    {
        //check if the user make any changes in the email.
        return !(user.email.equals(etEmail.getText().toString()));
    }
    public boolean isPasswordChanged()
    {
        //check if the current password before change equals the new password.
        return (!etConfirmNew.getText().toString().equals("")) || (!etNewPass.getText().toString().equals("")) || (!etCurrentPass.getText().toString().equals(""));
    }
    public boolean passwordChangesCheck()
    {
        //check if all the fields ssword change dialog are valid.
        String newP = etNewPass.getText().toString() , currentP = etCurrentPass.getText().toString()
                , checkP = etConfirmNew.getText().toString();
        if ((!currentP.equals("")) && (checkP.equals("") || newP.equals("")))
        {
            return false;
        }
        else if ((!newP.equals("")) && (checkP.equals("") || currentP.equals("")))
        {

            return false;
        }
        else if (!checkP.equals("") && newP.equals(""))
        {

            return false;
        }

        return newPasswordsCheck();

    }
    public void changeThePassword()
    {
        //function to change the password on the fire-base authentication database.
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("change your password...");
        pd.setCancelable(false);
        pd.show();
        if (isPasswordChanged()) {
            if (!passwordChangesCheck()) {
                Toast.makeText(AccountDetailsActivity.this, "One of your password details is wrong!", Toast.LENGTH_LONG).show();
                pd.dismiss();
            }
            else
            {
                AuthCredential credential = EmailAuthProvider
                        .getCredential(AppData.connectedUser.email , etCurrentPass.getText().toString());
                user_firebase.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            user_firebase.updatePassword(etNewPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        pd.dismiss();
                                        Toast.makeText(AccountDetailsActivity.this, "Password changed successfully!" , Toast.LENGTH_LONG).show();
                                        dialog_password.dismiss();
                                    }
                                    else{
                                        Toast.makeText(AccountDetailsActivity.this, "An Error occurd!" , Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }
                        else
                        {
                            Toast.makeText(AccountDetailsActivity.this , "Current Password typed is wrong!" , Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
        else
        {
            pd.dismiss();
        }
    }
    public void createChangePasswordDialog()
    {
        //create the password change dialog
        dialog_password = new Dialog(this);
        dialog_password.setContentView(R.layout.change_password_dialog);
        dialog_password.setCancelable(true);
        etCurrentPass =  dialog_password.findViewById(R.id.etCurrentPassword_ChangePassDialog);
        etNewPass = dialog_password.findViewById(R.id.etNewPassword_ChangePassDialog);
        etConfirmNew =  dialog_password.findViewById(R.id.etConfirmPassword_ChangePassDialog);
        submit_passwordChange =  dialog_password.findViewById(R.id.btnSubmit_ChangePasswordDialog);
        submit_passwordChange.setOnClickListener(this);
        eyeCurrentPass = dialog_password.findViewById(R.id.ivEyeCurrentPass_changePassDialog);
        eyeNewPass = dialog_password.findViewById(R.id.ivEyeNewPass_changePassDialog);
        eyePassConfirm = dialog_password.findViewById(R.id.ivEyeConfirmPass_changePassDialog);

        eyePassConfirm.setOnClickListener(this);
        eyeNewPass.setOnClickListener(this);
        eyeCurrentPass.setOnClickListener(this);
        dialog_password.show();

    }
    public void createChangeEmailDialog()
    {
        //create the email change dialog.
        dialog_email = new Dialog(this);
        dialog_email.setContentView(R.layout.change_email_dialog);
        dialog_email.setCancelable(true);
        etEmail = dialog_email.findViewById(R.id.etNewEmail_ChangeEmailDialog);
        etConfirmPassword_ChangeEmail =  dialog_email.findViewById(R.id.etPassConfirm_ChangeEmailDialog);
        submit_emailChange = dialog_email.findViewById(R.id.btnSubmit_ChangeEmailDialog);
        submit_emailChange.setOnClickListener(this);
        eyePassEmail =  dialog_email.findViewById(R.id.ivEye_changeEmailDialog);
        eyePassEmail.setOnClickListener(this);
        dialog_email.show();

    }
    public void createChangeNameDialog()
    {
        //create the name change dialog.
        dialog_name = new Dialog(this);
        dialog_name.setContentView(R.layout.change_name_dialog);
        dialog_name.setCancelable(true);
        etFirstName = dialog_name.findViewById(R.id.etFirstName_ChangeNameDialog);
        etLastName =  dialog_name.findViewById(R.id.etLastName_ChangeNameDialog);
        etFirstName.setText(AppData.connectedUser.firstName);
        etLastName.setText(AppData.connectedUser.lastName);
        submit_nameChange = dialog_name.findViewById(R.id.btnConfirm_ChangeNameDialog);
        submit_nameChange.setOnClickListener(this);
        dialog_name.show();
    }
    public void changeTheEmail()
    {
        //change the email in the fire store cloud and in the authentication database.
        final String passwordAuth = etConfirmPassword_ChangeEmail.getText().toString();
        if (isEmailChanged(AppData.connectedUser))
        {
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("change your email...");
            pd.setCancelable(false);
            pd.show();
            //create yes/no dialog for make sure the user want to execute the email change.
            final AlertDialog.Builder builder = new AlertDialog.Builder(AccountDetailsActivity.this);
            builder.setTitle("Email Change");
            builder.setMessage("change the email?");
            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(AppData.connectedUser.email , passwordAuth);
                    user_firebase.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                user_firebase.updateEmail(etEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            AppData.connectedUser.email = etEmail.getText().toString();
                                            AppData.UserCollection.document(AppData.connectedUser.uid).set(AppData.connectedUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful())
                                                    {
                                                        Toast.makeText(AccountDetailsActivity.this , "Email added successfully!" , Toast.LENGTH_LONG).show();
                                                        dialog_email.dismiss();
                                                        pd.dismiss();
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(AccountDetailsActivity.this, "An error occurred.", Toast.LENGTH_LONG).show();
                                                        pd.dismiss();
                                                    }
                                                }
                                            });
                                        }
                                        else
                                        {

                                            pd.dismiss();
                                            Toast.makeText(AccountDetailsActivity.this , Objects.requireNonNull(task.getException()).toString() , Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(AccountDetailsActivity.this, "Password was wrong!" , Toast.LENGTH_LONG).show();
                            }
                        }
                    });


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
        else
        {
            Toast.makeText(AccountDetailsActivity.this , "Check your new email details." , Toast.LENGTH_LONG).show();
        }
    }
    public void changeTheName()
    {
        //change the user's full name in the database.
        final ProgressDialog nameProgress = new ProgressDialog(this);
        nameProgress.setMessage("Change your details...");
        nameProgress.show();
        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();

        AppData.connectedUser.firstName = firstName;
        AppData.connectedUser.lastName = lastName;
        //create yes/no dialog to make sure the user want to execute the name change.
        final AlertDialog.Builder builder = new AlertDialog.Builder(AccountDetailsActivity.this);
        builder.setTitle("Full Name Change");
        builder.setMessage("change the name?");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                AppData.UserCollection.document(AppData.connectedUser.uid).set(AppData.connectedUser)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    nameProgress.dismiss();
                                    dialog_name.dismiss();
                                    Toast.makeText(AccountDetailsActivity.this , "Details changed successfully!", Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    Toast.makeText(AccountDetailsActivity.this , "An error Occurred.", Toast.LENGTH_LONG).show();

                                }
                            }
                        });

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
    public boolean onCreateOptionsMenu(Menu menu) {
        //create the menu.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //set the menu options selections listener.
        super.onOptionsItemSelected(item);
        int id = item.getItemId();

        if (id == R.id.action_logout)
        {
            if (user_firebase != null)
            {
                mAuth.signOut();
                AppData.connectedUser = null;
                startActivity(new Intent(AccountDetailsActivity.this, MainActivity.class));
            }
        }
        if (id == R.id.action_account_details) {
            Intent intent = new Intent(AccountDetailsActivity.this, AccountDetailsActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_posts) {
            Intent intent = new Intent(AccountDetailsActivity.this, PostManageActivity.class);
            startActivity(intent);
        }
        return true;
    }

}
