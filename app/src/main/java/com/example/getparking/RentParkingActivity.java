package com.example.getparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RentParkingActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {
    LinearLayout ll_ParkingLocation, llFromDate, llToDate, llCurrentLocation, llSearchLocation;
    String location;
    TextView tvLocation, tvFromDate, tvToDate;
    PlacesClient placesClient;
    Button btnSubmit_SearchDialog;
    EditText etPhoneNumber;

    EditText etDate, etTime, etPrice;
    Button btnSubmitdialog, btnSubmit;
    String date, time, place_name;
    Dialog datetime_dialog, searchPlaceDialog, locationOptionsDialog;
    boolean dateFrom = true;
    Geocoder geocoder;
    List<Address> addresses;
    private LocationManager locationManager;
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user_fireuser;
    String uid , postID;
    ProgressDialog submit_progress , upload_progress;
    StorageReference storageRef;
    User user;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //activity to handle parking rent.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_parking);

        Toolbar toolbar = findViewById(R.id.toolbar_rentparking);
        toolbar.setTitle("Rent your parking");
        setSupportActionBar(toolbar);

        ll_ParkingLocation =  findViewById(R.id.ll_Location_RentActivity);
        llToDate =  findViewById(R.id.ll_Totime_RentActivity);
        llFromDate =  findViewById(R.id.ll_Fromdate_RentActivity);
        tvFromDate =  findViewById(R.id.tv_From_Time_RentActivity);
        tvToDate =  findViewById(R.id.tv_To_Time_RentActivity);
        etPrice =  findViewById(R.id.et_Cash_RentActivity);
        tvLocation =  findViewById(R.id.tvLocation_RentActivity);
        etPhoneNumber =  findViewById(R.id.et_Phone_RentActivity);
        btnSubmit =  findViewById(R.id.btnSubmit_ActivityRent);


        ll_ParkingLocation.setOnClickListener(this);
        llFromDate.setOnClickListener(this);
        llToDate.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        submit_progress = new ProgressDialog(this);
        upload_progress = new ProgressDialog(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user_fireuser = auth.getCurrentUser();
        assert user_fireuser != null;
        uid = user_fireuser.getUid();
        storageRef = FirebaseStorage.getInstance().getReference();

        try
        {
            Intent intent = getIntent();
            try
            {
                location = Objects.requireNonNull(intent.getExtras()).getString("location");
                tvLocation.setText(location);
            } catch (Exception ignored) { }

            try
            {
              user = (User) intent.getSerializableExtra("user");
            }catch (Exception ignored){}
        } catch (Exception ignored) {}
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        if (v == ll_ParkingLocation) {
            createLocationOptionsDialog();
        }
        if (v == llFromDate) {
            createDateTimeDialog('f');
        }
        if (v == llToDate) {
            createDateTimeDialog('t');
        }
        if (v == etDate) {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    etDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    date = etDate.getText().toString();
                }
            }, mYear, mMonth, mDay);
            datePickerDialog.show();


        }
        if (v == etTime) {
            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    if (minute >= 10 && hourOfDay >= 10) {
                        etTime.setText(hourOfDay + ":" + minute);
                    } else if (minute < 10 && hourOfDay >= 10){
                        etTime.setText(hourOfDay + ":" + "0" + minute);
                    }
                    else if (minute < 10)
                    {
                        etTime.setText("0" + hourOfDay + ":" + "0" + minute);
                    }
                    else
                    {
                        etTime.setText("0" + hourOfDay + ":" + minute);
                    }
                    time = etTime.getText().toString();
                }
            }, mHour, mMinute, false);
            timePickerDialog.show();

        }
        if (v == btnSubmitdialog) {
            if (!dateFrom) {
                tvToDate.setText(date + " , " + time);
            } else {
                tvFromDate.setText(date + " , " + time);
            }
            datetime_dialog.dismiss();
        }
        if (v == btnSubmit_SearchDialog)
        {
            if (place_name != null)
                if (place_name.length() > 1)
                {
                    tvLocation.setText(place_name);
                    searchPlaceDialog.cancel();
                }
        }
        if (v == llCurrentLocation)
        {
            currentLocation();
        }
        if (v == llSearchLocation)
        {
            createsearchPlaceDialog();
            locationOptionsDialog.cancel();
        }
        if (v == btnSubmit)
        {
            if (formCompleted())
            {
                addPostToDataBase(user.firstName + " " + user.lastName);
                submit_progress.setMessage("Waiting for connection....");
                submit_progress.show();
            }
            else
            {
                if (etPhoneNumber.getText().toString().equals(""))
                {
                    etPhoneNumber.setHint("Required *");
                    etPhoneNumber.setHintTextColor(Color.RED);
                }
                if (etPrice.getText().toString().equals(""))
                {
                    etPrice.setHint("Required *");
                    etPrice.setHintTextColor(Color.RED);
                }
                if (tvFromDate.getText().toString().equals(""))
                {
                    tvFromDate.setHint("Required *");
                    tvFromDate.setHintTextColor(Color.RED);
                }
                if (tvToDate.getText().toString().equals(""))
                {
                    tvToDate.setHint("Required *");
                    tvToDate.setHintTextColor(Color.RED);
                }
                if (tvLocation.getText().toString().equals(""))
                {
                    tvFromDate.setHint("Required *");
                    tvFromDate.setHintTextColor(Color.RED);
                }

            }
        }
    }
    public void createDateTimeDialog(char c)
    {
        //create dialog for choosing dates in the parking form.
        datetime_dialog = new Dialog(this);
        datetime_dialog.setContentView(R.layout.datetime_dialog);
        etDate =  datetime_dialog.findViewById(R.id.et_Date_DatetimeDialog);
        etTime =  datetime_dialog.findViewById(R.id.et_Time_DatetimeDialog);
        btnSubmitdialog =  datetime_dialog.findViewById(R.id.btnSubmit_DateTimeDialog);
        btnSubmitdialog.setOnClickListener(this);
        etDate.setOnClickListener(this);
        etTime.setOnClickListener(this);
        datetime_dialog.show();
        if (c == 't')
        {
            dateFrom = false;
        } else if (c == 'f')
        {
            dateFrom = true;
        }
    }
    public void createsearchPlaceDialog() {
        //create dialog with auto complete search bar for search the parking location.
        try {
            searchPlaceDialog = new Dialog(this);
            searchPlaceDialog.setContentView(R.layout.activity_search_place);
            searchPlaceDialog.setCancelable(true);
            btnSubmit_SearchDialog =  searchPlaceDialog.findViewById(R.id.btnSubmit_SearchPlaceActv);
            btnSubmit_SearchDialog.setOnClickListener(this);
            searchPlaceDialog.setCancelable(false);
            String api_key = "AIzaSyDYoQybddM6c-Daz0bHVe7h2tuyzxHmW1k";
            if (!Places.isInitialized()) {
                Places.initialize(getApplicationContext(), api_key);
            }
            placesClient = Places.createClient(this);
            final AutocompleteSupportFragment autocompleteSupportFragment =
                    (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
            assert autocompleteSupportFragment != null;
            autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
            autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place)
                {
                    final LatLng latLng = place.getLatLng();
                    place_name = place.getName();
                    assert latLng != null;
                    Log.i("PlacesApi", "onPlaceSelected: " + latLng.latitude + "\n" + latLng.longitude);
                }
                @Override
                public void onError(@NonNull Status status) {}
            });
            searchPlaceDialog.show();
        }catch (Exception ignored) {}
    }
    public void createLocationOptionsDialog()
    {
        //create dialog to choose between current location or search the parking location
        locationOptionsDialog = new Dialog(this);
        locationOptionsDialog.setCancelable(true);
        locationOptionsDialog.setContentView(R.layout.location_options_dialog);
        llCurrentLocation =  locationOptionsDialog.findViewById(R.id.ll_CurrentLocation_LocationOptionsDialog);
        llSearchLocation =  locationOptionsDialog.findViewById(R.id.ll_LocationSearch_LocationOptionsDialog);
        llSearchLocation.setOnClickListener(this);
        llCurrentLocation.setOnClickListener(this);
        locationOptionsDialog.show();
    }
    public void currentLocation()
    {
        //return the user's current location.
        if (ActivityCompat.checkSelfPermission(RentParkingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RentParkingActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(RentParkingActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else
        {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            assert location != null;
            onLocationChanged(location);
        }
    }
    @Override
    public void onLocationChanged(Location location)
    {
        //get the current location of the user.
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        fromCoordinates2Name(longitude, latitude);
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onProviderDisabled(String provider) {}

    public void fromCoordinates2Name(double longitude, double latitude)
    {
        //translate the location coordinates to named location.
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            tvLocation.setText(address);
            locationOptionsDialog.cancel();
        }catch (IOException e) {e.printStackTrace();}
    }
    public void addPostToDataBase(String fullname)
    {
        //add the parking post to the database.
        if (count == 0)
        {
            final ParkingPost newPost = new ParkingPost(user_fireuser.getUid(), tvLocation.getText().toString(),
                     etPhoneNumber.getText().toString(), fullname
                    , etPrice.getText().toString(), tvFromDate.getText().toString()
                    , tvToDate.getText().toString());
        db.collection("Posts")
                .add(newPost)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        postID = documentReference.getId();
                        newPost.setPostId(postID);
                        documentReference.set(newPost);

                        Toast.makeText(RentParkingActivity.this, "Your parking uploaded successfully!" , Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RentParkingActivity.this , OptionsActivity.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RentParkingActivity.this, "error with uploading your parking post", Toast.LENGTH_LONG).show();
            }
        });
        count ++;
    }
    else
        {
            Toast.makeText(RentParkingActivity.this , "post already added!" , Toast.LENGTH_LONG).show();
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
            if (user_fireuser != null)
            {
                auth.signOut();
                startActivity(new Intent(RentParkingActivity.this , MainActivity.class));
            }
        }
        if (id == R.id.action_account_details)
        {
            Intent intent = new Intent (RentParkingActivity.this, AccountDetailsActivity.class);
            intent.putExtra("user" , user);
            startActivity(intent);
        }
        if (id == R.id.action_posts)
        {
            Intent intent =new Intent(RentParkingActivity.this , PostManageActivity.class);
            intent.putExtra("user" , user);
            startActivity(intent);
        }
        return true;
    }
    public boolean formCompleted(){
        //check if all the parking form completed.
        if (etPhoneNumber.getText().toString().equals(""))
        {
            return false;
        }
        if (tvToDate.getText().toString().equals(""))
        {
            return false;
        }
        if ( etPrice.getText().toString().equals(""))
        {
            return false;
        }
        if (tvLocation.getText().toString().equals(""))
        {
            return false;
        }
        if (tvFromDate.getText().toString().equals(""))
        {
            return false;
        }
        return !tvFromDate.getText().toString().equals(tvToDate.getText().toString());


    }
}

