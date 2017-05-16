package projetocaronas.tcc.ifsp.br.projetocarona;

import android.nfc.Tag;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.geometry.Bounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import projetocaronas.tcc.ifsp.br.projetocarona.controllers.NotificationController;
import projetocaronas.tcc.ifsp.br.projetocarona.entities.Ride;
import projetocaronas.tcc.ifsp.br.projetocarona.entities.User;
import projetocaronas.tcc.ifsp.br.projetocarona.enums.RIDE_ACTION;
import projetocaronas.tcc.ifsp.br.projetocarona.tasks.ConnectionSendAndReceiveJSONTask;
import projetocaronas.tcc.ifsp.br.projetocarona.tasks.ConnectionSendJsonTask;

public class                                                                                                                                                                                                                                                                                                    RideDetailMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private User userSender = null; // Remetente da mensagem
    private User userRecipient = null; // Destinatário da mensagem (usuário atual)
    private RIDE_ACTION rideAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_detail_maps);

        // Obtém dados passados via notificaçãoo Firebase
        StringBuilder stringBuilder = new StringBuilder();

        if(getIntent().getExtras() != null){
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);

                if("ride".equals(key) && value != null){
                    try {
                        JSONArray rideContent = new JSONArray((String) getIntent().getExtras().get(key));
                        JSONObject userSenderJson = (JSONObject) rideContent.get(0);
                        if (userSenderJson != null){
                            this.userSender = User.createUserFromJSON((JSONObject) userSenderJson.get("user_sender"));
                        }
                        JSONObject userRecipientJson = (JSONObject) rideContent.get(1);
                        if (userRecipientJson != null){
                            this.userRecipient = User.createUserFromJSON((JSONObject) userRecipientJson.get("user_recipient"));
                        }
                        JSONObject rideActionJson = (JSONObject) rideContent.get(2);
                        if(rideActionJson != null){
                            String rideAction = (String) rideActionJson.get("ride_action");
                            this.rideAction = RIDE_ACTION.valueOf(rideAction);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // Prepara texto da carona
        TextView titleTextView = (TextView) findViewById(R.id.ride_detail_title_text);
        titleTextView.setText(this.userSender.getName() + " está te " + (this.rideAction == RIDE_ACTION.OFFER ? "oferecendo" : "pedindo") + " uma carona. Deseja confirmar?");

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

        // Calback necessária para mover a câmera somente quando o mapa tiver sido carregado
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                // Adiciona um marcador na localização do usuário que pediu a carona
                LatLng userSenderLocation = new LatLng(userSender.getLocation().getLatitude(), userSender.getLocation().getLongitude());
                mMap.addMarker(new MarkerOptions().position(userSenderLocation).title(userSender.getName()));

                // Adiciona um marcador na localização do usuário atual do aplicativo
                LatLng userCurrentLocation = new LatLng(userRecipient.getLocation().getLatitude(), userRecipient.getLocation().getLongitude());
                mMap.addMarker(new MarkerOptions().position(userCurrentLocation).title(userRecipient.getName()));

                // Cria o limite
                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                boundsBuilder.include(userSenderLocation).include(userCurrentLocation);

                // Reposiciona
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100));

                // Calcula distância
                double distanceBetween = SphericalUtil.computeDistanceBetween(userSenderLocation, userCurrentLocation);
                Toast.makeText(RideDetailMapsActivity.this, "Distância de " + distanceBetween + "m", Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * Envia para o servidor a confirmação da carona entre os usuários
     * @param view
     */
    public void onConfirmRide(View view){
        NotificationController notificationController = new NotificationController(RideDetailMapsActivity.this);
        // Resposta a notificação anterior. Invertem-se os paéis de usuários
        notificationController.sendRideConfirm(this.userRecipient, this.userSender);
        this.finish();
    }

    /**
     * Envia para o servidor que o pedido/oferta de carona foi rejeitado pelo usuário
     * @param view
     */
    public void onRejectRide(View view){
        NotificationController notificationController = new NotificationController(RideDetailMapsActivity.this);
        // Resposta a notificação anterior. Invertem-se os paéis de usuários
        notificationController.sendRideReject(this.userRecipient, this.userSender);
        this.finish();
    }
}

