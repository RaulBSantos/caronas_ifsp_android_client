package projetocaronas.tcc.ifsp.br.projetocarona.tasks;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import projetocaronas.tcc.ifsp.br.projetocarona.LoginActivity;
import projetocaronas.tcc.ifsp.br.projetocarona.MapsActivity;
import projetocaronas.tcc.ifsp.br.projetocarona.R;
import projetocaronas.tcc.ifsp.br.projetocarona.UserRegisterActivity;
import projetocaronas.tcc.ifsp.br.projetocarona.utils.AndroidUtilsCaronas;

/**
 * Created by Raul on 29/05/2016.
 */
public class ConnectionSendJSONTask extends AsyncTask{
    private Activity context;
    private Activity next;
    private String urlPath;

    /**
     * Construtor para enviar o JSON para o servidor, continuando na mesma activity
     *
     * @param context A activity atual
     * @param urlPath URL relativa de destino
     */
    public ConnectionSendJSONTask(Activity context, String urlPath){
        this.context = context;
        this.urlPath = urlPath;
    }

    /**
     * Construtor para enviar o JSON para o servidor e após o envio chamar outra activity
     * @param context A activity atual
     * @param next A activity que será chamada após o o envio. Exemplo de valor: mew ExemploActivity()
     * @param urlPath URL relativa de destino
     */
    public ConnectionSendJSONTask(Activity context, Activity next, String urlPath){
        this.context = context;
        this.next = next;
        this.urlPath = urlPath;
    }

    @Override
    protected Integer doInBackground(Object[] params) {

        int responseCode = 0;
        JSONObject jsonToSend = (JSONObject) params[0];
        try {

            String response = null;

            URL url = new URL(AndroidUtilsCaronas.SERVER_PREFIX_CONTEXT + this.urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // This set POST as method
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length", "" + jsonToSend.toString().getBytes().length);
            connection.setUseCaches(false);

            connection.setConnectTimeout(10 * 1000);
            connection.setReadTimeout(10 * 1000);

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

            outputStream.writeBytes(jsonToSend.toString());

            responseCode = connection.getResponseCode();

            outputStream.flush();
            outputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return Integer.valueOf(responseCode);
    }

    @Override
    protected void onPostExecute(Object responseCode) {
        Integer respCode = (Integer)responseCode;
        switch (respCode){
            case HttpURLConnection.HTTP_OK :
                if(this.context != null && this.next != null) {
                    callNextActivity();
                }
                break;
            case HttpURLConnection.HTTP_MOVED_TEMP:
                // Caso o usuário deva se cadastrar
                if(this.context != null){
                    this.next = new UserRegisterActivity();
                    callNextActivity();
                }

                break;
            case HttpURLConnection.HTTP_UNAUTHORIZED :
                Toast.makeText(this.context, this.context.getResources().getString(R.string.invalid_record_password) + " \tCódigo de resposta: " + responseCode, Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(this.context, this.context.getResources().getString(R.string.unavailable_server) + " \tCódigo de resposta: " + responseCode, Toast.LENGTH_LONG).show();
        }
    }

    private void callNextActivity(){
        Intent intent = new Intent(this.context, this.next.getClass());
        this.context.startActivity(intent);
    }
}
