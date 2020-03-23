package com.example.getparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import com.example.getparking.Helpers.AppData;
import com.example.getparking.Helpers.MyProgressDialog;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RentParkingActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {
    LinearLayout ll_ParkingLocation, llFromDate, llToDate, llCurrentLocation, llSearchLocation;
    TextView tvLocation, tvFromDate, tvToDate;
    PlacesClient placesClient;
    Button btnSubmit_SearchDialog;
    EditText etPhoneNumber;
    MyProgressDialog progressDialog;
    EditText  etPrice;
    Button btnFromDate , btnFromTime , btnToDate , btnToTime , btnFromSubmit , btnToSubmit;
    Button btnSubmit;
    String  place_name;
    Dialog datetime_dialog, searchPlaceDialog, locationOptionsDialog;
    Geocoder geocoder;
    List<Address> addresses;
    private LocationManager locationManager;
    FirebaseAuth auth;
    FirebaseUser user_fireBase;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //activity to handle parking rent.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_parking);

        Toolbar toolbar = findViewById(R.id.toolbar_rentparking);
        toolbar.setTitle("Rent your parking");
        setSupportActionBar(toolbar);

        ll_ParkingLocation = findViewById(R.id.ll_Location_RentActivity);
        llToDate = findViewById(R.id.ll_Totime_RentActivity);
        llFromDate = findViewById(R.id.ll_Fromdate_RentActivity);
        tvFromDate = findViewById(R.id.tv_From_Time_RentActivity);
        tvToDate = findViewById(R.id.tv_To_Time_RentActivity);
        etPrice = findViewById(R.id.et_Cash_RentActivity);
        tvLocation = findViewById(R.id.tvLocation_RentActivity);
        etPhoneNumber = findViewById(R.id.et_Phone_RentActivity);
        btnSubmit = findViewById(R.id.btnSubmit_ActivityRent);
        progressDialog = new MyProgressDialog(this);
        ll_ParkingLocation.setOnClickListener(this);
        llFromDate.setOnClickListener(this);
        llToDate.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        auth = FirebaseAuth.getInstance();
        user_fireBase = auth.getCurrentUser();

        if (user_fireBase == null)
        {
            AppData.connectedUser = null;
            startActivity(new Intent(RentParkingActivity.this, MainActivity.class));
        }
        if (AppData.connectedUser == null)
        {
            auth.signOut();
            startActivity(new Intent(RentParkingActivity.this, MainActivity.class));
        }
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        if (v == ll_ParkingLocation) {
            createLocationOptionsDialog();
        }
        if (v == llFromDate) {
            createFromTimeDialog();
        }
        if (v == llToDate) {
            createToTimeDialog();
        }

        if (v == btnSubmit)
        {
            if (formCompleted())
            {
                if (checkDateTimeIsValid()) {
                    addPostToDataBase();
                    progressDialog.show();
                }
                else
                {
                    Toast.makeText(RentParkingActivity.this , "The reminders between your date or hours are invalid, please check it." , Toast.LENGTH_LONG).show();
                }
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
                    tvLocation.setHint("Required *");
                    tvLocation.setHintTextColor(Color.RED);
                }

            }
        }
    }

    public void createsearchPlaceDialog() {
        //create dialog with auto complete search bar for search the parking location.
        try {
            searchPlaceDialog = new Dialog(this);
            searchPlaceDialog.setContentView(R.layout.activity_search_place);
            searchPlaceDialog.setCancelable(true);
            btnSubmit_SearchDialog =  searchPlaceDialog.findViewById(R.id.btnSubmit_SearchPlaceActv);
            btnSubmit_SearchDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (place_name != null)
                        if (place_name.length() > 1)
                        {
                            tvLocation.setText(place_name);
                            searchPlaceDialog.cancel();
                        }
                }
            });
            searchPlaceDialog.setCancelable(true);
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
        llSearchLocation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                createsearchPlaceDialog();
                locationOptionsDialog.cancel();
            }
        });
        llCurrentLocation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                currentLocation();
            }
        });
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
    public void addPostToDataBase()
    {
        //add the parking post to the database.
        if (!flag)
        {
            final ParkingPost newPost = new ParkingPost(
                    user_fireBase.getUid()
                    , tvLocation.getText().toString()
                    , etPhoneNumber.getText().toString()
                    , AppData.connectedUser.firstName + " " + AppData.connectedUser.lastName
                    , etPrice.getText().toString(), tvFromDate.getText().toString()
                    , tvToDate.getText().toString());

             AppData.PostsCollection.add(newPost)
                     .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                 @Override
                 public void onComplete(@NonNull Task<DocumentReference> task) {
                     if (task.isSuccessful())
                     {
                         final String postID = Objects.requireNonNull(task.getResult()).getId();
                         newPost.setPostId(postID);
                         task.getResult().set(newPost).addOnCompleteListener(new OnCompleteListener<Void>()
                         {
                             @Override
                             public void onComplete(@NonNull Task<Void> task)
                             {
                                 if (task.isSuccessful())
                                 {
                                     HashMap<String , String> postMap = new HashMap<>();
                                     postMap.put("id" , newPost.getPostId());
                                     AppData.UserCollection.document(AppData.connectedUser.uid).collection("Posts").document(newPost.getPostId()).set(postMap , SetOptions.merge() )
                                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                 @Override
                                                 public void onComplete(@NonNull Task<Void> task) {
                                                     if (task.isSuccessful())
                                                     {
                                                         Toast.makeText(RentParkingActivity.this, "Your parking uploaded successfully!" , Toast.LENGTH_LONG).show();
                                                         Intent intent = new Intent(RentParkingActivity.this , OptionsActivity.class);
                                                         startActivity(intent);
                                                     }
                                                     else
                                                     {
                                                         Toast.makeText(RentParkingActivity.this, "error with uploading your parking post", Toast.LENGTH_LONG).show();
                                                         AppData.PostsCollection.document(newPost.getPostId()).delete();
                                                     }
                                                 }
                                             });
                                 }
                                 else
                                 {
                                     Toast.makeText(RentParkingActivity.this, "error with uploading your parking post", Toast.LENGTH_LONG).show();
                                 }
                             }
                         });

                     }
                     else
                     {
                         Toast.makeText(RentParkingActivity.this, "error with uploading your parking post", Toast.LENGTH_LONG).show();
                     }
                 }
             });
        flag = true;
    }
    else
        {
            Toast.makeText(RentParkingActivity.this , "post already added!" , Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu , menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if (id == R.id.action_logout)
        {
            if (user_fireBase != null)
            {
                auth.signOut();
                AppData.connectedUser = null;
                startActivity(new Intent(RentParkingActivity.this , MainActivity.class));
            }
        }
        if (id == R.id.action_account_details)
        {
            Intent intent = new Intent (RentParkingActivity.this, AccountDetailsActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_posts)
        {
            Intent intent =new Intent(RentParkingActivity.this , PostManageActivity.class);
            startActivity(intent);
        }
        return true;
    }
    public boolean formCompleted()
    {
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
    public void createFromTimeDialog()
    {
        datetime_dialog = new Dialog(this);
        datetime_dialog.setContentView(R.layout.datetime_dialog);
        btnFromDate = datetime_dialog.findViewById(R.id.btn_Date_DatetimeDialog);
        btnFromTime = datetime_dialog.findViewById(R.id.btn_Time_DatetimeDialog);
        btnFromSubmit =  datetime_dialog.findViewById(R.id.btnSubmit_DateTimeDialog);

        final String[] mDate = new String[1];
        final String[] mTime = new String[1];

        btnFromTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(RentParkingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (minute >= 10 && hourOfDay >= 10)
                        {
                            mTime[0] = hourOfDay + ":" + minute;
                        }
                        else if (minute < 10 && hourOfDay >= 10)
                        {
                            mTime[0] = hourOfDay + ":" + "0" + minute;
                        }
                        else if (minute < 10)
                        {
                            mTime[0] = "0" + hourOfDay + ":" + "0" + minute;
                        }
                        else
                        {
                            mTime[0] = "0" + hourOfDay + ":" + minute;
                        }
                        btnFromTime.setText(mTime[0]);
                    }
                }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        btnFromDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(RentParkingActivity.this , new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mDate[0] = ((dayOfMonth < 10) ? "0" + String.valueOf(dayOfMonth) + "/" : String.valueOf(dayOfMonth) + "/")+ ((month + 1 < 10) ? "0" + String.valueOf(month + 1) + "/" : String.valueOf(month + 1) + "/") + year;
                        btnFromDate.setText(mDate[0]);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        btnFromSubmit.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v)
            {


                if (mDate[0] != null && mTime[0] != null)
                {
                    tvFromDate.setText(mDate[0] + " , " + mTime[0]);
                    datetime_dialog.dismiss();
                }
            }
        });
        datetime_dialog.show();


    }
    public void createToTimeDialog()
    {
        datetime_dialog = new Dialog(this);
        datetime_dialog.setContentView(R.layout.datetime_dialog);
        btnToDate = datetime_dialog.findViewById(R.id.btn_Date_DatetimeDialog);
        btnToTime = datetime_dialog.findViewById(R.id.btn_Time_DatetimeDialog);
        btnToSubmit =  datetime_dialog.findViewById(R.id.btnSubmit_DateTimeDialog);

        final String[] mDate = new String[1];
        final String[] mTime = new String[1];

        btnToTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(RentParkingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (minute >= 10 && hourOfDay >= 10)
                        {
                            mTime[0] = hourOfDay + ":" + minute;
                        }
                        else if (minute < 10 && hourOfDay >= 10)
                        {
                            mTime[0] = hourOfDay + ":" + "0" + minute;
                        }
                        else if (minute < 10)
                        {
                            mTime[0] = "0" + hourOfDay + ":" + "0" + minute;
                        }
                        else
                        {
                            mTime[0] = "0" + hourOfDay + ":" + minute;
                        }
                        btnToTime.setText(mTime[0]);
                    }
                }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        btnToDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(RentParkingActivity.this , new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mDate[0] = ((dayOfMonth < 10) ? "0" + String.valueOf(dayOfMonth) + "/" : String.valueOf(dayOfMonth) + "/")+ ((month + 1 < 10) ? "0" + String.valueOf(month + 1) + "/" : String.valueOf(month + 1) + "/") + year;
                        btnToDate.setText(mDate[0]);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        btnToSubmit.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v)
            {
                if (mDate[0] != null && mTime[0] != null)
                {
                    datetime_dialog.dismiss();
                    tvToDate.setText(mDate[0] + " , " + mTime[0]);
                }
            }
        });
    datetime_dialog.show();
    }
    public boolean checkDateTimeIsValid()
    {
        int toDay = Integer.parseInt(tvToDate.getText().toString().substring(0 , 2))
        ,toMonth =  Integer.parseInt(tvToDate.getText().toString().substring(3 , 5))
            ,toYear =  Integer.parseInt(tvToDate.getText().toString().substring(6 ,10))
        , fromDay =Integer.parseInt(tvFromDate.getText().toString().substring(0 ,2))
            , fromMonth = Integer.parseInt(tvFromDate.getText().toString().substring(3 ,5))
            , fromYear = Integer.parseInt(tvFromDate.getText().toString().substring(6 ,10))
                , fromHour = Integer.parseInt(tvFromDate.getText().toString().substring(13,15))
                , fromMinutes = Integer.parseInt(tvFromDate.getText().toString().substring(16,18))
                , toHour = Integer.parseInt(tvToDate.getText().toString().substring(13,15))
                , toMinutes = Integer.parseInt(tvToDate.getText().toString().substring(16,18));
        if (fromYear >  toYear) return false;
        else if(fromYear < toYear )return true;
        else if(fromMonth > toMonth) return false;
        else if (fromMonth < toMonth) return true;
        else if (fromDay > toDay) return false;
        else if(fromDay < toDay) return true;
        else if (fromHour > toHour) return false;
        else if (fromHour < toHour) return true;
        return fromMinutes < toMinutes;
    }
}

