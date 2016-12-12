package projetocaronas.tcc.ifsp.br.projetocarona.tasks;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import projetocaronas.tcc.ifsp.br.projetocarona.R;
import projetocaronas.tcc.ifsp.br.projetocarona.utils.AndroidUtilsCaronas;

/**
 * Created by Raul on 17/06/2016.
 */
public class ConnectionReceiveJSONTask extends AsyncTask{
        private Activity context;
        private Activity next;
        private String urlPath;
        private OnJsonTransmitionCompleted mCallback;


        public ConnectionReceiveJSONTask(OnJsonTransmitionCompleted mCallback, Activity context, Activity next, String urlPath){
            this.mCallback = mCallback;
            this.context = context;
            this.next = next;
            this.urlPath = urlPath;
        }

        public ConnectionReceiveJSONTask(OnJsonTransmitionCompleted mCallback,Activity context, String urlPath){
            this.mCallback = mCallback;
            this.context = context;
            this.urlPath = urlPath;
        }

        @Override
        protected JSONArray doInBackground(Object[] params) {
            return getJSONFromUrl(this.urlPath);
        }

        @Override
        protected void onPostExecute(Object jsonArray) {
            this.mCallback.onTrasmitionCompleted((JSONArray) jsonArray);
        }

        public JSONArray getJSONFromUrl(String urlPath){
            BufferedReader bufferedReader = null;
            StringBuilder stringBuilder = null;
            HttpURLConnection connection = null;
            String jsonString = null;
            JSONArray jsonArray = null;

            try {
                URL url = new URL(AndroidUtilsCaronas.SERVER_PREFIX_CONTEXT + urlPath);
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                stringBuilder = new StringBuilder();

                String line; //Avoid null pointer here
                while (( line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line + "\n");
                }

                jsonString = stringBuilder.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                connection.disconnect();
            }

            try {
                jsonArray = new JSONArray(jsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return jsonArray;
        }

        private void callNextActivity(){
            Intent intent = new Intent(this.context, this.next.getClass());
            this.context.startActivity(intent);
        }

        // Interface to manage OnComplete Json Transmition
        public interface OnJsonTransmitionCompleted{
            void onTrasmitionCompleted(JSONArray jsonArray);
        }
    }
