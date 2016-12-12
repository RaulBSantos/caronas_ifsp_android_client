package projetocaronas.tcc.ifsp.br.projetocarona.entities;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.List;

import projetocaronas.tcc.ifsp.br.projetocarona.enums.DIRECTION;

/**
 * Created by Raul on 08/11/2016.
 */

public class Way implements Serializable {
    private DIRECTION direction;
    private String time;
    private int vacancyNumber;

    public DIRECTION getDirection() {
        return direction;
    }

    public void setDirection(DIRECTION direction) {
        this.direction = direction;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getVacancyNumber() {
        return vacancyNumber;
    }

    public void setVacancyNumber(int vacancyNumber) {
        this.vacancyNumber = vacancyNumber;
    }
}
