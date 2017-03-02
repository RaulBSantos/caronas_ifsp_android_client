package projetocaronas.tcc.ifsp.br.projetocarona;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import projetocaronas.tcc.ifsp.br.projetocarona.entities.User;
import projetocaronas.tcc.ifsp.br.projetocarona.session.ManageUserSession;
import projetocaronas.tcc.ifsp.br.projetocarona.tasks.ConnectionSendAndReceiveJSONTask;
import projetocaronas.tcc.ifsp.br.projetocarona.utils.AndroidUtilsCaronas;
import projetocaronas.tcc.ifsp.br.projetocarona.utils.Mask;

public class LoginActivity extends AppCompatActivity implements ConnectionSendAndReceiveJSONTask.OnJsonTransmitionDone {



    EditText recordToLogin = null;
    EditText passwordToLogin = null;

    Button buttonQuit = null;
    Button buttonLogin = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //FIXME Teste do Firebase
//        MyFirebaseMessagingService teste = new MyFirebaseMessagingService();


        buttonQuit = (Button) findViewById(R.id.buttonQuit);
        buttonLogin =  (Button) findViewById(R.id.buttonLogin);

        recordToLogin = (EditText) findViewById(R.id.record_login_edit_text);
        recordToLogin.addTextChangedListener(Mask.insert("######-#", recordToLogin));

        passwordToLogin = (EditText) findViewById(R.id.password_login_edit_text);

        buttonQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkHasInternetConnectionFromUtils(LoginActivity.this)) {
                    JSONObject postParameters = new JSONObject();

                    try {
                        postParameters.put("record", recordToLogin.getText().toString());
                        postParameters.put("password", passwordToLogin.getText().toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new ConnectionSendAndReceiveJSONTask(LoginActivity.this, LoginActivity.this, new MapsActivity(), "/login").execute(postParameters);
                }else{
                    Toast.makeText(LoginActivity.this,getResources().getString(R.string.check_connection), Toast.LENGTH_LONG).show();
                }


            }
        });
        // Advice use Moodle data
        Toast.makeText(LoginActivity.this,getResources().getString(R.string.use_moodle_data), Toast.LENGTH_LONG).show();
    }


    public void hideKeyboardLogin(View view){
        AndroidUtilsCaronas.hideKeyboard(LoginActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!checkHasInternetConnectionFromUtils(this)){
            Toast.makeText(this,"Verifique sua conexão", Toast.LENGTH_LONG).show();
        }

    }

    private boolean checkHasInternetConnectionFromUtils(Activity context) {
        //Check connection
        return AndroidUtilsCaronas.checkHasInternetConnection(context);
    }


    @Override
    public void onJsonReceived(JSONObject jsonResponse) {
        ManageUserSession userSession = new ManageUserSession();
        JSONObject jsonUser = null;
        try {
            // Extrai o objeto Usuário do JSON
            jsonUser = (JSONObject) jsonResponse.get("user");
            User sessionUser =  User.createUserFromJSON(jsonUser);
            // Salva dados do usuário logado
            userSession.saveSessionUser(sessionUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
