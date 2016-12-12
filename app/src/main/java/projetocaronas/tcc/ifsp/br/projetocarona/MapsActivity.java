package projetocaronas.tcc.ifsp.br.projetocarona;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import projetocaronas.tcc.ifsp.br.projetocarona.entities.User;
import projetocaronas.tcc.ifsp.br.projetocarona.tasks.ConnectionReceiveJSONTask;
import projetocaronas.tcc.ifsp.br.projetocarona.tasks.ConnectionSendJSONTask;
import projetocaronas.tcc.ifsp.br.projetocarona.utils.AndroidUtilsCaronas;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ConnectionReceiveJSONTask.OnJsonTransmitionCompleted {
    private LatLng latLng = null;
    private GoogleMap mMap;
    private User userToRegister = null;
    private JSONArray usersData = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtains user from UserRegisterActivity
        Intent intent = getIntent();
        userToRegister = (User) intent.getSerializableExtra("newUser");

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

                        // Fill markers
                        populateMapWithUsers(usersData);
                    }
                });

                return false;
            }
        });
        // Fill map with data of users
        new ConnectionReceiveJSONTask(this,MapsActivity.this, "/getAllUsersAndPools").execute();

        // Event of click on Info Window each marker (User or Ride)
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(MapsActivity.this, "User selected " + marker.getTitle().toString(), Toast.LENGTH_SHORT).show();//TODO  Call activity Confirma carona
            }
        });

    }

    public void onSearch(View view){
        hideKeyboardFromUtils(view);

        EditText locationEditText = (EditText) findViewById(R.id.editTextSearch);
        String location = locationEditText.getText().toString();

        mMap.clear(); // Clear All Markers and other stuff

        if(location != null && !location.isEmpty()){

            Geocoder geocoder = new Geocoder(this);
            try {
                List<Address> addressList = geocoder.getFromLocationName(location, 1);
                if(addressList.size() > 0) {
                    Address address = addressList.get(0);

                    latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Seu local"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }else{
                    Toast.makeText(MapsActivity.this, "O endereço informado não foi encontrado", Toast.LENGTH_SHORT).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void hideKeyboardFromUtils(View view){
        AndroidUtilsCaronas.hideKeyboard(this);
    }

    public void onSaveLocation(View view){
        if (latLng != null){

            JSONObject postParameters = this.userToRegister.toJSONObject();

            try {
                postParameters.put("latitude",latLng.latitude);
                postParameters.put("longitude",latLng.longitude);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Toast.makeText(this, "Cadastro realizado, localização : Latitude: " + latLng.latitude + " Longitude: " + latLng.longitude, Toast.LENGTH_LONG).show();
            // Envia os dados, chamando a prócima activity de cadastro de caronas
            new ConnectionSendJSONTask(MapsActivity.this, new RegisterRidesActivity(), "/register_user_and_coordinates").execute(postParameters);
        }else{
            Toast.makeText(this, "Cannot read location!", Toast.LENGTH_LONG).show();
        }
    }

    public void onBackButton(View view){
        Intent intent = new Intent(this, UserRegisterActivity.class);
        intent.putExtra("userToRegister", userToRegister);
        startActivity(intent);
    }

    @Override
    public void onTrasmitionCompleted(JSONArray jsonArray) {
        // Prevenir null pointer exception
        if(jsonArray == null) jsonArray = new JSONArray();
        // Fill with location markers
        populateMapWithUsers(jsonArray);
        Toast.makeText(this, "Encontrados: " + jsonArray.length() + " usuários", Toast.LENGTH_LONG).show();
        usersData = jsonArray;
    }

    public void populateMapWithUsers(JSONArray usersData){
        boolean canUserGiveRide = checkUserCanGiveRide();
        for (int i = 0 ; i < usersData.length() ; i++){
            try {
                JSONObject user = (JSONObject) usersData.get(i);

                boolean giveRide = (Boolean) user.get("canGiveRide");
//                int vacancy = (int) user.get("vacancy");
                int vacancy = 0; //FIXME Só para teste, pegar da carona do usuário (ou listar as caronas separado, tirando o campo vagas?)



                LatLng userLatLng = new LatLng((Double)user.getJSONObject("location").get("latitude"), (Double)user.getJSONObject("location").get("longitude"));
                if(giveRide){
                    if(vacancy > 0) {
                        mMap.addMarker(new MarkerOptions().position(userLatLng).title(user.get("name").toString()).snippet("Vagas : "+vacancy +" - Pedir carona").icon(BitmapDescriptorFactory.fromResource(R.drawable.car_vacancy_marker)));
                    }else{
                        mMap.addMarker(new MarkerOptions().position(userLatLng).title(user.get("name").toString()).snippet("Lotado").icon(BitmapDescriptorFactory.fromResource(R.drawable.car_marker)));
                    }
                }else{
                    mMap.addMarker(new MarkerOptions().position(userLatLng).title(user.get("name").toString()).snippet(canUserGiveRide ? "Oferecer carona" : "Ver perfil").icon(BitmapDescriptorFactory.fromResource(R.drawable.man_marker)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private boolean checkUserCanGiveRide() {
        if(this.userToRegister != null){
            return this.userToRegister.isCanGiveRide();
        }
        return false;
    }
}
