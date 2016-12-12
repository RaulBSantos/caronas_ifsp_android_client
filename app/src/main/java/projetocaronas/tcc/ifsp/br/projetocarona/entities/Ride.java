package projetocaronas.tcc.ifsp.br.projetocarona.entities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

/**
 * Created by Raul on 05/09/2016.
 */
public class Ride implements Serializable{
    private List<String> daysOfWeek;
    // Mototrista
    private User driver = new User();
    // Sentidos
    List<Way> ways;
    // Rota
    private Route route;

    public List<String> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(List<String> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public List<Way> getWays() {
        return ways;
    }

    public void setWays(List<Way> ways) {
        this.ways = ways;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    // Cria um JSON utilizando relection
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();

        Class<? extends Ride> clazz = this.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field:fields) {
            field.setAccessible(true);
            try {
                jsonObject.put(field.getName(), field.get(this));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }
}
