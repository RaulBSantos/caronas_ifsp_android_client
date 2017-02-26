package projetocaronas.tcc.ifsp.br.projetocarona.tasks;

import android.app.Activity;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.view.View;

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
        this.mCallback.onJsonReceived( (JSONObject) jsonObject);
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

            int statusCode = (int) jsonResponse.get("status_code");
            // Verifica o código de resposta
            if (statusCode != 200){
                return null;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  jsonResponse;
    }

    // Interface to manage OnComplete Json Transmition
    public interface OnJsonTransmitionDone{
        void onJsonReceived(JSONObject jsonResponse);
    }
}
