package projetocaronas.tcc.ifsp.br.projetocarona.entities;

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


}
