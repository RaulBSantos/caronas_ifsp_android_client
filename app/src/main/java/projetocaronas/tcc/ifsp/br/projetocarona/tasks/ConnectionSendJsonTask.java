package projetocaronas.tcc.ifsp.br.projetocarona.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import projetocaronas.tcc.ifsp.br.projetocarona.R;
import projetocaronas.tcc.ifsp.br.projetocarona.UserRegisterActivity;
import projetocaronas.tcc.ifsp.br.projetocarona.utils.AndroidUtilsCaronas;

/**
 * Created by raul on 18/04/17.
 */

public class ConnectionSendJsonTask extends AsyncTask{

    private Activity context;
    private String urlPath;
    private String successToastText;
    private String errorToastText;

    public ConnectionSendJsonTask(Activity context, String urlPath){
        this.context = context;
        this.urlPath = urlPath;
    }

    public void withOnSuccessToast(String message){
        this.successToastText = message;
    }

    public void withOnErrorToast(String message){
        this.errorToastText = message;
    }

    @Override
    protected Object doInBackground(Object[] params) {
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
        Integer respCode = (Integer) responseCode;
        if (respCode == HttpURLConnection.HTTP_OK) {
            if (this.successToastText != null) {
                Toast.makeText(this.context, this.successToastText, Toast.LENGTH_SHORT).show();
            }
        }else {
            if (this.errorToastText != null) {
                Toast.makeText(this.context, this.errorToastText, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
