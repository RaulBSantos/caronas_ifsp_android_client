package projetocaronas.tcc.ifsp.br.projetocarona.entities;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;

import projetocaronas.tcc.ifsp.br.projetocarona.interfaces.UserBuilder;

/**
 * Created by Raul on 24/05/2016.
 */
public class User implements Serializable, UserBuilder {
    private String name;
    private String record;
    private String phone;
    private String email;
    private String firebaseId;
    private boolean canGiveRide;
    private UserLocation location;

    public String getName() {
        return name;
    }

    public String getRecord() {
        return record;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public boolean isCanGiveRide() {
        return canGiveRide;
    }

    public String getFirebaseId() { return firebaseId; }

    public UserLocation getLocation() {
        return location;
    }

    public void setLocation(UserLocation location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return this.record;
    }

    public JSONObject toJSONObject(){
        JSONObject jsonObject = new JSONObject();

        try {
            Class<? extends User> clazz = this.getClass();

            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field: declaredFields) {
                field.setAccessible(true);
                jsonObject.put(field.getName(), field.get(this));
            }

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public User withName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public User withRecord(String record) {
        this.record = record;
        return this;
    }

    @Override
    public User withPhone(String phone) {
        this.phone = phone;
        return this;
    }

    @Override
    public User withEmail(String email) {
        this.email = email;
        return this;
    }
    // Informação específico para Caronas
    public User thatCanGiveRide(boolean canGiveRide){
        this.canGiveRide = canGiveRide;
        return this;
    }
    // Informação necessária caso utilize o Google Firebase para gerenciar
    public User withFireBaseId(String fireBaseId){
        this.firebaseId = fireBaseId;
        return this;
    }
    // Informação específica de localização
    public User withLocation(UserLocation location){
        this.location = location;
        return this;
    }

    // Dada uma estrutura de JSON, cria um novo usuário
    public static User createUserFromJSON(JSONObject jsonUser){
        User user = new User();
        try {
            if (jsonUser.has("name")){
                String name = (String) jsonUser.get("name");
                user.withName(name);
            }

            if (jsonUser.has("record")) {
                String record = (String) jsonUser.get("record");
                user.withRecord(record);
            }

            if (jsonUser.has("canGiveRide")){
                Boolean canGiveRide = (Boolean) jsonUser.get("canGiveRide");
                user.thatCanGiveRide(canGiveRide);
            }

            if (jsonUser.has("phone")) {
                String phone = (String) jsonUser.get("phone");
                user.withPhone(phone);
            }

            if (jsonUser.has("email")) {
                String email = (String) jsonUser.get("email");
                user.withEmail(email);
            }

            if (jsonUser.has("location")){
                JSONObject location = (JSONObject) jsonUser.get("location");

                double latitude = (double) location.get("latitude");
                double longitude = (double) location.get("longitude");

                user.withLocation(new UserLocation(latitude, longitude));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

}
