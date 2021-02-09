package com.gcoolservices.acrepair.address;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.google.android.libraries.places.api.Places;
//import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;

import com.seatgeek.placesautocomplete.DetailsCallback;
import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
import com.seatgeek.placesautocomplete.model.AddressComponent;
import com.seatgeek.placesautocomplete.model.AddressComponentType;
import com.seatgeek.placesautocomplete.model.Place;
import com.seatgeek.placesautocomplete.model.PlaceDetails;
import com.gcoolservices.acrepair.R;
import com.gcoolservices.acrepair.database.Task;
import com.gcoolservices.acrepair.utils.AppLocationService;
import com.gcoolservices.acrepair.utils.GPSTracker;
import com.gcoolservices.acrepair.utils.LocationAddress;
import com.gcoolservices.acrepair.utils.Toolbar_Set;

import static com.gcoolservices.acrepair.address.DeliveryOptionActivity.*;

public class AddressActivity extends AppCompatActivity {

    public static final String VENDOR_ID = "vendorid";
    private static final String TAG = AddressActivity.class.getSimpleName();
    private static final int AUTOCOMPLETE_REQUEST_CODE = 101;
    TextInputEditText txtHouse, txtPin, txtCity, txtArea;
    String txtLandmark;
    private PlacesClient placesClient;

    private ArrayList<Task> list;
    public static final String PRODUCT_LIST  = "product";
    public static final String AMOUNT  = "amount";
    public static final String HASHMAP  = "hashmap";
    private PlacesAutocompleteTextView placesAutocomplete;
    private String amount;
    private HashMap<String, String> hashMap;
    GPSTracker gpsTracker;
    private String vendorid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        Toolbar_Set.INSTANCE.setToolbar(this, "Add Address");

        list = (ArrayList<Task>) getIntent().getExtras().getSerializable(PRODUCT_LIST);
        vendorid = getIntent().getExtras().getString(VENDOR_ID);
        hashMap = (HashMap<String, String>) getIntent().getExtras().get(HASHMAP);
        System.out.println(hashMap);
        amount = getIntent().getExtras().getString(AMOUNT, "");
        txtArea = findViewById(R.id.txtArea);
        txtCity = findViewById(R.id.txtCity);
        txtHouse = findViewById(R.id.txtHouse);
        txtPin = findViewById(R.id.txtPincode);

        String apiKey = getString(R.string.location_api_key_id);

        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        // Create a new Places client instance.
        placesClient = Places.createClient(this);

        gpsTracker = new GPSTracker(this);
        gpsTracker.getLocation();

        placesAutocomplete = findViewById(R.id.places_autocomplete);
        placesAutocomplete.setOnPlaceSelectedListener(
                new OnPlaceSelectedListener() {
                    @Override
                    public void onPlaceSelected(final Place place) {
                        // do something awesome with the selected place
                        placesAutocomplete.getDetailsFor(place, new DetailsCallback() {
                            @Override
                            public void onSuccess(PlaceDetails details) {
                                Log.d("test", "details " + details);
//                                mStreet.setText(details.name);
                                for (AddressComponent component : details.address_components) {
                                    for (AddressComponentType type : component.types) {
                                        switch (type) {
                                            case STREET_NUMBER:
                                                break;
                                            case ROUTE:
                                                break;
                                            case NEIGHBORHOOD:
                                                break;
                                            case SUBLOCALITY_LEVEL_1:
                                                break;
                                            case SUBLOCALITY:
                                                txtArea.setText(component.long_name);
                                                break;
                                            case LOCALITY:
                                                txtCity.setText(component.long_name);
                                                break;
                                            case ADMINISTRATIVE_AREA_LEVEL_1:
//                                                txtArea.setText(component.short_name);
                                                break;
                                            case ADMINISTRATIVE_AREA_LEVEL_2:
                                                break;
                                            case COUNTRY:
                                                break;
                                            case POSTAL_CODE:
                                                txtPin.setText(component.long_name);
                                                break;
                                            case POLITICAL:
                                                break;
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Throwable throwable) {

                            }
                        });
                    }
                }
        );

        AppLocationService appLocationService = new AppLocationService(this);

        if (gpsTracker.canGetLocation()) {
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            LocationAddress locationAddress = new LocationAddress();
            locationAddress.getAddressFromLocation(latitude, longitude,
                    getApplicationContext(), new GeocoderHandler());
        } else {
            showSettingsAlert();
        }

    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        AddressActivity.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }


    public void next(View view) {

        if (validateform()) {
            Intent intent = new Intent(this, DeliveryOptionActivity.class);
            intent.putExtra(DeliveryOptionActivity.PRODUCT_LIST, list);
            intent.putExtra(DeliveryOptionActivity.VENDOR_ID, vendorid);

            intent.putExtra(ADDRESS,  txtHouse.getText().toString().trim() + " " +
                                            txtArea.getText().toString().trim() + " " +
                                            txtCity.getText().toString().trim());

            intent.putExtra(LANDMARK, placesAutocomplete.getText().toString());
            intent.putExtra(HASHMAP, hashMap);
            intent.putExtra(DeliveryOptionActivity.AMOUNT, amount);
            intent.putExtra(PINCODE, txtPin.getText().toString().trim());
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }

    private boolean validateform() {

        if (txtPin.getText().toString().isEmpty() || txtPin.getText().toString().trim().length()<6) {
            txtPin.setError("Enter 6 digit Pincode");
            return false;
        }
        if (txtCity.getText().toString().isEmpty()) {
            txtCity.setError("Enter City");
            return false;
        }
        if (txtArea.getText().toString().isEmpty()) {
            txtArea.setError("Enter Area");
            return false;
        }
        if (txtHouse.getText().toString().isEmpty() ) {
            txtHouse.setError("Enter House no./Building name");
            return false;
        }
        if (placesAutocomplete.getText().toString().isEmpty() ) {
            placesAutocomplete.setError("Enter Landmark");
            return false;
        }
        return true;
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
//            placesAutocomplete.setText(locationAddress);

            try {
                String[] strings = locationAddress.split(",");
                txtArea.setText(strings[0].trim());
                txtCity.setText(strings[1].trim());
                txtPin.setText(strings[2].trim());
            } catch (Exception ignored) {}
        }
    }

}