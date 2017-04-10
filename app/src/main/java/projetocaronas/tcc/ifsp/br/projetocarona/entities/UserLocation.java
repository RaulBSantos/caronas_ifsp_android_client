package projetocaronas.tcc.ifsp.br.projetocarona.entities;

/**
 * Created by raul on 10/04/17.
 */

public class UserLocation {

    public UserLocation(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private double latitude;
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
