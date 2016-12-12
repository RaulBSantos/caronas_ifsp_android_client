package projetocaronas.tcc.ifsp.br.projetocarona.entities;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import projetocaronas.tcc.ifsp.br.projetocarona.enums.DIRECTION;

/**
 * Created by Raul on 11/10/2016.
 */

/*
 * Dados da API Google Maps.
 * Ex. de url: http://maps.googleapis.com/maps/api/directions/json?origin=<cidade, nome-de-rua, número>&destination=<cidade, nome-de-rua, número>&sensor=false
 */
public class Route implements Serializable {
    private double distance;
    private double duration;
    private String startAddress;
    private String endAddress;
    private List<LatLng> path = new ArrayList<>();
    private DIRECTION direction;


}
