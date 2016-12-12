package projetocaronas.tcc.ifsp.br.projetocarona.messaging;

import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Raul on 30/10/2016.
 */
public class FirebaseController {
    public String firebasePreferences = "firebase";
    public String tokenName = "refreshedToken";
    private SharedPreferences preferencesFirebase;

    public FirebaseController() {
    }

    public void saveTokenId(String tokenId){

    }

}
