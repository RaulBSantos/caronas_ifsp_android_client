package projetocaronas.tcc.ifsp.br.projetocarona.utils;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.List;

import projetocaronas.tcc.ifsp.br.projetocarona.entities.Route;

/**
 * Created by Raul on 11/10/2016.
 */
public class MapsAPIUtils {
    private LatLng startPoint;
    private LatLng middlePoint;
    private LatLng endPoint;



    public static Route generateRouteFromJson(JSONObject routeJson){
        return new Route();
    }


}
