package projetocaronas.tcc.ifsp.br.projetocarona.tasks;

import android.app.Activity;
import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import projetocaronas.tcc.ifsp.br.projetocarona.LoginActivity;
import projetocaronas.tcc.ifsp.br.projetocarona.MapsActivity;
import projetocaronas.tcc.ifsp.br.projetocarona.R;
import projetocaronas.tcc.ifsp.br.projetocarona.UserRegisterActivity;
import projetocaronas.tcc.ifsp.br.projetocarona.utils.AndroidUtilsCaronas;

/**
 * Created by raul on 25/02/17.
 */

public class ConnectionSendAndReceiveJSONTask extends AsyncTask{

    //FIXME Criar o construtor com Context, NextActivity, Url e a Callback
    private Activity context;
    private Activity next;
    private String urlPath;
    private OnJsonTransmitionDone mCallback;

    public ConnectionSendAndReceiveJSONTask(OnJsonTransmitionDone mCallback, Activity context, Activity next, String urlPath) {
        this.mCallback = mCallback;
        this.context = context;
        this.next = next;
        this.urlPath = urlPath;
    }



    @Override
    protected JSONObject doInBackground(Object[] params) {
        JSONObject jsonToSend = (JSONObject) params[0];
        return postJSONAndReceiveFromURL(jsonToSend);
    }

    @Override
    protected void onPostExecute(Object jsonObject) {
        JSONObject receivedJson = (JSONObject) jsonObject;
        // Get response code
        try {
            int respCode = (int) receivedJson.get("status_code");

            switch (respCode){
                case HttpURLConnection.HTTP_OK :
                    if(this.context != null && this.next != null) {
                        this.mCallback.onJsonReceived(receivedJson);
                        callNextActivity();
                    }
                    break;
                case HttpURLConnection.HTTP_MOVED_TEMP:
                    // Caso o usuário deva se cadastrar
                    if(this.context != null){
                        this.next = new UserRegisterActivity();
                        this.mCallback.onJsonReceived(receivedJson);
                        callNextActivity();
                    }

                    break;
                case HttpURLConnection.HTTP_UNAUTHORIZED :
                    Toast.makeText(this.context, this.context.getResources().getString(R.string.invalid_record_password) + " \tCódigo de resposta: " + respCode, Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(this.context, this.context.getResources().getString(R.string.unavailable_server) + " \tCódigo de resposta: " + respCode, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject postJSONAndReceiveFromURL(JSONObject jsonToSend){
        JSONObject jsonResponse = null;
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

            //Receber o JSON
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();

            String line;
            while (( line = bufferedReader.readLine()) != null){
                stringBuilder.append(line + "\n");
            }

            String jsonString = stringBuilder.toString();
            // Montar o JSON de resposta
            jsonResponse = new JSONObject(jsonString);


            outputStream.flush();
            outputStream.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  jsonResponse;
    }

    private void callNextActivity(){
        Intent intent = new Intent(this.context, this.next.getClass());
        this.context.startActivity(intent);
    }

    // Interface to manage OnComplete Json Transmition
    public interface OnJsonTransmitionDone{
        void onJsonReceived(JSONObject jsonResponse);
    }
}
