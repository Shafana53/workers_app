package com.kvsn.builds.cap1;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.Status;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;


import android.location.Location;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class GetLocation extends AppCompatActivity {
	private CardView currLocation;
	private Button otherLocation;
	private int PLACE_PICKER_REQUEST = 1;

	private FusedLocationProviderClient fusedLocationProviderClient;
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;
	private Location lastLocation;
	private String selectedAddress;
	private String selectedCity;


	public void goPlacePicker(View view) {
		Places.initialize(getApplicationContext(), "AIzaSyBZ0udaqnclJx1ZKPR2HF6luNf4uV5cCIQ"); // Replace "YOUR_API_KEY" with your actual API key
		List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
		Intent intent=new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fields).build(this);
		startActivityForResult(intent,PLACE_PICKER_REQUEST);
	//public void goPlacePicker(View view) {
		//PlacePicker.IntentBulder builder=new PlacePicker.IntentBuilder();
	//	try {
		//	startActivityForResult(builder.build(GetLocation.this), PLACE_PICKER_REQUEST);
		//} catch (GooglePlayServicesRepairableException e) {
		//	e.printStackTrace();
	//	}
		//catch(GooglePlayServicesNotAvailableException e){
		//	e.printStackTrace();
		//}
	}

	@Override


	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
			if (data != null) {
				Place place = Autocomplete.getPlaceFromIntent(data);
				Geocoder geocoder = new Geocoder(this, Locale.getDefault());
				try {
					List<Address> addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
					if (addresses.size() > 0) {
						Address address = addresses.get(0);
						selectedAddress = address.getAddressLine(0);
						selectedCity = address.getLocality();
						editor.putString("City", selectedCity);
						editor.putString("Address", selectedAddress);
						editor.apply();
						Intent intent = new Intent(getApplicationContext(), AvailableWorkers.class);
						startActivity(intent);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
			Status status = Autocomplete.getStatusFromIntent(data);
			// Handle the error
		} else if (resultCode == RESULT_CANCELED) {
			// The user canceled the operation
		}
	}

	//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
//			Place place = PlacePicker.getPlace(data,this);
//			Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//			try {
//				List<Address> addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
//				if (addresses.size() > 0) {
//					Address address = addresses.get(0);
//					selectedAddress = address.getAddressLine(0);
//					selectedCity = address.getLocality();
//					editor.putString("City", selectedCity);
//					editor.putString("Address", selectedAddress);
//					editor.apply();
//					Intent intent = new Intent(getApplicationContext(), AvailableWorkers.class);
//					startActivity(intent);
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_location);
		currLocation = findViewById(R.id.curr_Location);
		otherLocation = findViewById(R.id.other_Location);
		fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
		sharedPreferences = getSharedPreferences("Categories", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		new CheckInternetConnection(this).checkConnection();
	}

	public void doThis(View v) {
		getLocation();
	}

	private void getLocation() {
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
		} else {
			fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
				@Override
				public void onSuccess(Location location) {
					if (location != null) {
						lastLocation = location;
						double latitude = lastLocation.getLatitude();
						double longitude = lastLocation.getLongitude();

						Geocoder geocoder = new Geocoder(GetLocation.this, Locale.getDefault());

						try {
							List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
							if (addresses.size() > 0) {
								Address address = addresses.get(0);
								selectedAddress = address.getAddressLine(0);
								selectedCity = address.getLocality();
								editor.putString("City", selectedCity);
								editor.putString("Address", selectedAddress);
								editor.apply();
								startActivity(new Intent(GetLocation.this, AvailableWorkers.class));
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				getLocation();
			} else {
				Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
			}
		}
	}
}