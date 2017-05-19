package projetocaronas.tcc.ifsp.br.projetocarona;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import projetocaronas.tcc.ifsp.br.projetocarona.controllers.NotificationController;
import projetocaronas.tcc.ifsp.br.projetocarona.entities.User;
import projetocaronas.tcc.ifsp.br.projetocarona.session.ManageUserSession;
import projetocaronas.tcc.ifsp.br.projetocarona.tasks.ConnectionReceiveJSONTask;
import projetocaronas.tcc.ifsp.br.projetocarona.tasks.ConnectionSendLoginJSONTask;
import projetocaronas.tcc.ifsp.br.projetocarona.utils.AndroidUtilsCaronas;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ConnectionReceiveJSONTask.OnJsonTransmitionCompleted, LocationListener {
    private LatLng latLng = null;
    private GoogleMap mMap;
    private User userToRegister = null;
    private JSONArray usersData = null;
    private String pendingRides;
    private JSONArray confirmedRides = null;
    private boolean gotUserPosition = false; //FIXME Precisa ser estático? Em quais situações será instanciada uma nova classe?
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private Map<Marker, User> mapAllUsersMarkers = new HashMap<>();
    // Cria o limite
    private LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtains user from UserRegisterActivity
        Intent intent = getIntent();
        userToRegister = (User) intent.getSerializableExtra("newUser");
        // Reads pending rides information
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        this.pendingRides = sharedPref.getString(getString(R.string.pending_rides_to_me), "");

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        // Forces map to MyLocation
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        if (bestProvider != null){
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {
                onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);
        }else{
            Toast.makeText(MapsActivity.this, "Impossivel obter localizaçao", Toast.LENGTH_LONG).show();//TODO  Call activity Confirma carona
        }

        //FIXME Caso nao consiga obter localizaçao, sera necessario omitir o botao?
        // Click event of get current location
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        mMap.clear(); // Clear All Markers and other stuff

                        double longitude = cameraPosition.target.longitude;
                        double latitide = cameraPosition.target.latitude;

                        latLng = new LatLng(latitide, longitude);
                        mMap.addMarker(new MarkerOptions().position(latLng).title("Seu local"));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        mMap.setOnCameraChangeListener(null);//disable
                        fillAddress(latitide, longitude);
                        // Fill markers
                        populateMapWithUsers(usersData);
                    }
                });

                return false;
            }
        });
        // Fill map with data of users
        new ConnectionReceiveJSONTask(this, MapsActivity.this, "/getAllUsersAndPools").execute();

        // Event of click on Info Window each marker (User or Ride)
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (! marker.getSnippet().equals(getString(R.string.maps_waiting_confirm_ride))) {
                    NotificationController notificationController = new NotificationController(MapsActivity.this);
                    String toOffer = getString(R.string.to_offer);
                    String toRequest = getString(R.string.to_request);
                    String toCancel = getString(R.string.maps_cancel_ride);

                    User origin = ManageUserSession.getSessionUser();
                    User destination = mapAllUsersMarkers.get(marker);

                    if (marker.getSnippet().toLowerCase().contains(toOffer)) {
                        notificationController.sendRideOffer(origin, destination);
                    } else if (marker.getSnippet().toLowerCase().contains(toRequest)) {
                        notificationController.sendRideRequest(origin, destination);
                    } else if (marker.getSnippet().equals(toCancel)){
                        Toast.makeText(MapsActivity.this, "Cancelar carona ainda não permitido.", Toast.LENGTH_SHORT).show();
                        return ;
                    }
                    changeMarkerToPending(marker);
                    Toast.makeText(MapsActivity.this, "Mensagem de : " + origin.getName() + " para " + destination.getName() + " foi enviada! ", Toast.LENGTH_SHORT).show();//TODO  Call activity Confirma carona
                }else{
                    Toast.makeText(MapsActivity.this, "Aguarde a confirmação da carona!", Toast.LENGTH_SHORT).show();//TODO  Call activity Confirma carona
                }
            }
        });

    }

    public void onSearch(View view) {
        hideKeyboardFromUtils(view);

        EditText locationEditText = (EditText) findViewById(R.id.editTextSearch);
        String location = locationEditText.getText().toString();

        mMap.clear(); // Clear All Markers and other stuff

        if (location != null && !location.isEmpty()) {

            Geocoder geocoder = new Geocoder(this);
            try {
                List<Address> addressList = geocoder.getFromLocationName(location, 1);
                if (addressList.size() > 0) {
                    Address address = addressList.get(0);

                    latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Seu local"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                } else {
                    Toast.makeText(MapsActivity.this, "O endereço informado não foi encontrado", Toast.LENGTH_SHORT).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void hideKeyboardFromUtils(View view) {
        AndroidUtilsCaronas.hideKeyboard(this);
    }

    public void onShowMyRides(View view){
        this.mMap.clear();
        populateMapWithUsers(this.confirmedRides);
    }

    public void onShowAllRides(View view){
        this.mMap.clear();
        populateMapWithUsers(this.usersData);
    }

    public void onSaveLocation(View view) {
        if (latLng != null) {

            JSONObject postParameters = this.userToRegister.toJSONObject();

            try {
                postParameters.put("latitude", latLng.latitude);
                postParameters.put("longitude", latLng.longitude);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Toast.makeText(this, "Cadastro realizado, localização : Latitude: " + latLng.latitude + " Longitude: " + latLng.longitude, Toast.LENGTH_LONG).show();
            // Envia os dados, chamando a prócima activity de cadastro de caronas - FIXME voltar quando implementar os detalhes da carona
            //new ConnectionSendLoginJSONTask(MapsActivity.this, new RegisterRidesActivity(), "/register_user_and_coordinates").execute(postParameters);
        } else {
            Toast.makeText(this, "Cannot read location!", Toast.LENGTH_LONG).show();
        }
    }

    public void onBackButton(View view) {
        Intent intent = new Intent(this, UserRegisterActivity.class);
        intent.putExtra("userToRegister", userToRegister);
        startActivity(intent);
    }

    @Override
    public void onTrasmitionCompleted(JSONArray jsonArray) {
        // Prevenir null pointer exception
        if (jsonArray == null) jsonArray = new JSONArray();
        // Fill with location markers
        populateMapWithUsers(jsonArray);
        Toast.makeText(this, "Total de usuarios enontrados: " + jsonArray.length() , Toast.LENGTH_LONG).show();
        usersData = jsonArray;
    }

    public void populateMapWithUsers(JSONArray usersData) {
        boolean canUserGiveRide = ManageUserSession.canCurrentUserGiveRides();

        for (int i = 0; i < usersData.length(); i++) {
            try {
                JSONObject userJson = (JSONObject) usersData.get(i);
                User user = User.createUserFromJSON(userJson);
                if (ManageUserSession.isThisUserLogged(user)) {
                    // Sets rides from current user
                    this.confirmedRides = userJson.getJSONArray("confirmedRides");
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }


        for (int i = 0; i < usersData.length(); i++) {
            try {
                JSONObject userJson = (JSONObject) usersData.get(i);

                User user = User.createUserFromJSON(userJson);
                if (ManageUserSession.isThisUserLogged(user)) {
                    // Don't draw marker for own user
                    double selfLatitude = user.getLocation().getLatitude();
                    double selfLongitude = user.getLocation().getLongitude();
                    this.boundsBuilder.include(new LatLng(selfLatitude, selfLongitude));
                    continue;
                }
                boolean giveRide = (Boolean) userJson.get("canGiveRide");

                LatLng userLatLng = new LatLng((Double) userJson.getJSONObject("location").get("latitude"), (Double) userJson.getJSONObject("location").get("longitude"));
                MarkerOptions markerOpt = null;
                if (giveRide) {
                    markerOpt = new MarkerOptions().position(userLatLng).title(userJson.get("name").toString()).snippet(getString(R.string.maps_ask_ride)).icon(BitmapDescriptorFactory.fromResource(R.drawable.car_vacancy_marker));
                } else {
                    markerOpt = new MarkerOptions().position(userLatLng).title(userJson.get("name").toString()).snippet(canUserGiveRide ? getString(R.string.maps_offer_ride) : getString(R.string.maps_see_profile)).icon(BitmapDescriptorFactory.fromResource(R.drawable.man_marker));
                }

                Marker marker = mMap.addMarker(markerOpt);
                this.mapAllUsersMarkers.put(marker, user);
                // Verify confirmed ride
                if (this.confirmedRides != null && this.confirmedRides.length() > 0) {
                    if (!changeMarkerToConfirmed(user, marker)) {
                        // Verify pendding - Needs mapAllUsersMarkers filled
                        if (this.pendingRides.contains(user.getRecord())) {
                            changeMarkerToPending(marker);
                        }
                    }
                }

                this.boundsBuilder.include(userLatLng);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Move camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100));
    }

    private boolean changeMarkerToConfirmed(User user, Marker marker) throws JSONException {
        // User está no confirmedRides?
        for (int i = 0 ; i < this.confirmedRides.length() ; i++){
            JSONObject rideJson = this.confirmedRides.getJSONObject(i);
            User rideUser = User.createUserFromJSON(this.confirmedRides.getJSONObject(i).getJSONObject("user"));
            if (user.getRecord().equals(rideUser.getRecord())){
                // Refers to 'amIDriver' condition
                if(rideJson.getBoolean("driver")){
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.man_marker_confirmed));
                }else{
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.car_marker_confirmed));
                }
                marker.setSnippet(getString(R.string.maps_cancel_ride));
                return true;
            }
        }
        return false;
        // se sim, muda a cor e o snippet (remove do preferences tbm)
    }

    private void changeMarkerToPending(Marker marker) {
        if (marker.getSnippet().equals(getString(R.string.maps_ask_ride))){
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.car_marker_pending_ride));
        }else{
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.man_marker_pending_ride));
        }
        // Persists on preferences
        savePendingRide(marker);
        marker.setSnippet(getString(R.string.maps_waiting_confirm_ride));
    }

    private void savePendingRide(Marker marker){
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String prefContent = sharedPref.getString(getString(R.string.pending_rides_to_me), "");
        prefContent += (prefContent.length() == 0 ? "" : ", ") + this.mapAllUsersMarkers.get(marker).getRecord();
        editor.putString(getString(R.string.pending_rides_to_me), prefContent );
        editor.commit();
    }

    @Override
    public void onProviderEnabled(String s) {
        // To use LocationListener - Don't remove!
    }

    @Override
    public void onProviderDisabled(String s) {
        // To use LocationListener - Don't remove!
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        // To use LocationListener - Don't remove!
    }

    /**
     * Get the current user's current position
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        if(this.gotUserPosition == false) {
            this.gotUserPosition = true; // Run once
            // Sets marker on current postion
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            this.mMap.addMarker(new MarkerOptions().position(latLng)); //TODO Refactor this. Put in fillAddress Method too

            this.mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            this.mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            fillAddress(latitude, longitude);

        }
    }

    private void fillAddress(double latitude, double longitude) {
        try {
            // Fills the address information
            Geocoder geocoder = new Geocoder(this);
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            Address address = addressList.get(0);
            Toast.makeText(MapsActivity.this, "Current address - Street: " + address.getAddressLine(0), Toast.LENGTH_SHORT).show();
            EditText locationEditText = (EditText) findViewById(R.id.editTextSearch);
            locationEditText.setText(address.getAddressLine(0)+", "+ address.getLocality() +" - "+address.getAdminArea()+", "+address.getCountryName());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
