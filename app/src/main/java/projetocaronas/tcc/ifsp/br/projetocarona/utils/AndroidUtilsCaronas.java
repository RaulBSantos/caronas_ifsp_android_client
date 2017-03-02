package projetocaronas.tcc.ifsp.br.projetocarona.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * Created by Raul on 26/05/2016.
 */
public class AndroidUtilsCaronas {
    public final static String SERVER_PREFIX_CONTEXT = "https://caronas-ifsp.tk:8080/caronas";

    /**
     * Métodos: validarEditText, DesaparecerTeclado
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean validateEditTextList(Activity context, List<EditText> fieldsList){
        // Hard to implement same way :(
        String passwordToValidate = null;
        boolean isAllValid = true;
        for (EditText field : fieldsList) {
            // To avoid null pointer exception
            if(field == null) {
                Toast.makeText(context, "Erro ao validar os dados. O campo está nulo.", Toast.LENGTH_LONG).show();
                return false;
            }

            // Especific rule for vacant EditText
            if(field.getHint().toString().endsWith("Vagas")){
                if(field.getText().toString().trim().isEmpty()){
                    field.setText("0");
                }
            }
            // Is there blank field?
            if(field.getText().toString().trim().isEmpty()){// validates if null without null pointer???
                isAllValid = false;
                //show message field

                Toast.makeText(context, "Preencha o campo " + field.getHint() + " corretamente", Toast.LENGTH_LONG).show();

                break;
            }
            // Are passwords equals?
            if(field.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD + InputType.TYPE_CLASS_TEXT ){ //Uses sum between 128 of PASSWORD and 1 of CLASS_TEXT
                if(passwordToValidate != null){
                    if(!passwordToValidate.equals(field.getText().toString())){
                        isAllValid = false;
                        Toast.makeText(context,"As senhas informadas não conferem", Toast.LENGTH_LONG).show();
                        break;
                    }
                }else{
                    passwordToValidate = field.getText().toString();
                }
            }
        }
        return isAllValid;
    }

    public static void hideKeyboard(Activity context){
        IBinder windowToken = context.getCurrentFocus() != null ? context.getCurrentFocus().getWindowToken() : null;
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static boolean checkHasInternetConnection(Activity context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
