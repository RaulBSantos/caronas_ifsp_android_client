package projetocaronas.tcc.ifsp.br.projetocarona.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;

import projetocaronas.tcc.ifsp.br.projetocarona.entities.User;

/**
 * Created by raul on 10/03/17.
 */

public class JsonUtils {

    public static JSONObject convertToJson(Serializable objectToParse){
        JSONObject jsonObject = new JSONObject();

        try {
            Class clazz = objectToParse.getClass();

            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field: declaredFields) {
                field.setAccessible(true);
                jsonObject.put(field.getName(), field.get(objectToParse));
            }

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
