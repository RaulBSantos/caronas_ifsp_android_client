package projetocaronas.tcc.ifsp.br.projetocarona.tasksbackground;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import projetocaronas.tcc.ifsp.br.projetocarona.utils.AndroidUtilsCaronas;

/**
 * Created by raul on 10/03/17.
 */

public class ConnectionBGSendJSONTask extends AsyncTask {

    private String urlPath;

    public ConnectionBGSendJSONTask(String urlPath) {
        this.urlPath = urlPath;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        int responseCode = 0;
        JSONObject jsonToSend = (JSONObject) objects[0];

        try {

            URL url = new URL(this.urlPath);
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
    protected void onPostExecute(Object o) {
        //FIXME Implementar
    }
}
