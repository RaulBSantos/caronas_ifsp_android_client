package projetocaronas.tcc.ifsp.br.projetocarona.controllers;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import projetocaronas.tcc.ifsp.br.projetocarona.MapsActivity;
import projetocaronas.tcc.ifsp.br.projetocarona.tasks.ConnectionSendJSONTask;

/**
 * Created by Raul on 11/10/2016.
 */
public class RouteController {
    // Número mínimo de coordenadas aceito
    private static int MIN_COORDIMATES = 2;
    // Número máximo de coordenadas aceito
    private static int MAX_COORDIMATES = 3;

    /*
     * Busca a rota através da API do GoogleMaps
     *
     * @param coordinates
     * @return
     */
    public void getRouteJSON(Activity parent, List<LatLng> coordinates){
        JSONObject jsonCoordinateParam = prepareAsJsonParameters(coordinates);
        new ConnectionSendJSONTask(parent, "/sendToDirectionsApi").execute(jsonCoordinateParam);
    }

    /**
     * Prepara os parâmetros para serem enviados para a APIGoogleMaps.
     * Regras do array "coordinates"
     * @param coordinates tamanho mínimo 2 e máximo 3, sendo o 1º valor o local de partida, o segundo o local de
     *                    destino e o possível 3º, o local intermediário
     * @return
     */
    private JSONObject prepareAsJsonParameters(List<LatLng> coordinates)  {
        JSONObject jsonParameter = new JSONObject();
        if(coordinates != null && coordinates.size() >= MIN_COORDIMATES){
            try {
                jsonParameter.put("origin", coordinates.get(0));
                jsonParameter.put("destination", coordinates.get(1));
                jsonParameter.put("waypoints", (coordinates.size() == MAX_COORDIMATES ? coordinates.get(2) : null));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return jsonParameter;

    }

    /**
     * Obtém as coordenadas a partir dos endereços passados na Activity
     *
     * @param activity
     * @param textLocations
     * @return lista de coordenadass LatLng
     */
    public List<LatLng> parseToCoordinates(Activity activity, List<String> textLocations) {
        List<LatLng> listCoordinates = new ArrayList<>();
        // A lista precisa ter pelo menos o local de partida e o local de
        // destino preenchidos para executar o méotodo
        if(textLocations != null && textLocations.size() >= 2){

            Geocoder geocoder = new Geocoder(activity);
            try {
                for(String textLocation : textLocations ){
                    // Devolve uma lista com apenas um Address
                    List<Address> addressList = geocoder.getFromLocationName(textLocation, 1);
                    if(addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        // Cria um novo LatLng passando as coordenadas do address
                        listCoordinates.add(new LatLng(address.getLatitude(), address.getLongitude()));
                    }else{
                        Toast.makeText(activity, "Nenhuma localização encontrada para '"+textLocation+"", Toast.LENGTH_SHORT).show();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return listCoordinates;
    }

}


