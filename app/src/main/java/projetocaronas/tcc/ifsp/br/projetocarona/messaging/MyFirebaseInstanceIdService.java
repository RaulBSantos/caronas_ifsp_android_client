package projetocaronas.tcc.ifsp.br.projetocarona.messaging;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by Raul on 29/10/2016.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    public static final String firebaseFileName = "firebase.txt";
    public static final String tokenName = "refreshedToken";

    public void onTokenRefresh(){
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        saveTokenId(refreshedToken);
    }

    /**
     * Armazena o novo token gerado para as notificações. Ele será enviado assim que possível para ser atualizado no servidor
     * @param refreshedToken
     */
    private void saveTokenId(String refreshedToken){
        File file = new File(getFilesDir(), firebaseFileName);
        FileOutputStream outputStream;
        try {
        if(! file.exists())
            file.createNewFile();

         outputStream = openFileOutput(firebaseFileName, Context.MODE_PRIVATE);
         outputStream.write(("TokenId="+refreshedToken).getBytes());
         outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Caso exista um token
     * @return String tokenId
     */
    public String getFirebaseNewId(Activity context) {
        String idString = "";
        try {
            File file = new File(context.getFilesDir(), firebaseFileName);
            FileInputStream inputStream = new FileInputStream(file);
            int c;

            while( (c = inputStream.read()) != -1 ){
                idString = idString + Character.toString((char) c);
            }

            if (idString != null && idString.contains("TokenId=")){
                idString = idString.substring(idString.indexOf("=") + 1);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return idString;
    }
}
