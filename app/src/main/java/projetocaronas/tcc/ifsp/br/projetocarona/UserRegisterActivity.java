package projetocaronas.tcc.ifsp.br.projetocarona;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import projetocaronas.tcc.ifsp.br.projetocarona.entities.User;
import projetocaronas.tcc.ifsp.br.projetocarona.messaging.MyFirebaseInstanceIdService;
import projetocaronas.tcc.ifsp.br.projetocarona.utils.AndroidUtilsCaronas;
import projetocaronas.tcc.ifsp.br.projetocarona.utils.Mask;

public class UserRegisterActivity extends AppCompatActivity {

    EditText fieldName = null;
    EditText fieldPhone = null;
    EditText fieldEmail = null;
    CheckBox fieldGiveRide = null;

    List<EditText> fieldsList = null;

    Button buttonNext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        //method - pick all values from activity, - validates if password its corretly typed, Create a JSON from User calls another activity that will send it to the server
        fieldName = (EditText) findViewById(R.id.edit_text_user_name);
        fieldPhone = (EditText) findViewById(R.id.edit_text_phone);
        //AddsMask
        fieldPhone.addTextChangedListener(Mask.insert("(##)#####-####", fieldPhone));
        fieldEmail = (EditText) findViewById(R.id.edit_text_email);

        fieldGiveRide = (CheckBox)findViewById(R.id.can_give_ride);


        fieldsList = new ArrayList();
        fieldsList.add(fieldName);
        fieldsList.add(fieldPhone);
        fieldsList.add(fieldEmail);

        // When back to this activity
        Intent intent = getIntent();
        User userToRegister = (User) intent.getSerializableExtra("userToRegister");

        if(userToRegister != null){
            fieldName.setText(userToRegister.getName());
            fieldPhone.setText(userToRegister.getPhone());
            fieldGiveRide.setChecked(userToRegister.isCanGiveRide());
        }
//        buttonNext = (Button) findViewById(R.id.buttonNext);


    }

    public void hideKeyboardUserRegister(View view){
        AndroidUtilsCaronas.hideKeyboard(this);
    }

    public void processUserForm(View view){
        boolean isAllValid = AndroidUtilsCaronas.validateEditTextList(this,fieldsList);
        if(isAllValid){
            User newUser = new User()
                    .withName(fieldName.getText().toString().trim())
                    // Pega o prontuário registrado estaticamente
                    .withRecord(AndroidUtilsCaronas.userRecord)
                    .withPhone( fieldPhone.getText().toString().trim())
                    .withEmail(fieldEmail.getText().toString().trim())
                    .thatCanGiveRide(fieldGiveRide.isChecked());

            // Inclui o id do Firebase para notificações
            newUser.withFireBaseId(obtainFirebaseNewId(this));

            // Envia para outra activity de Maps
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("newUser",newUser);
            startActivity(intent);
        }
    }

    public void backToLoginActivity(View view){
        Intent intent = new Intent(UserRegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     *
     * Obtém o id do Firebase caso exista. Ele será usado para as notificações.
     * @param context Activity atual. É necessária para chamar o método no serviço do FirebaseId.
     * @return String firebaseId, or null if don't exists.
     */
    private String obtainFirebaseNewId(Activity context)  {
        MyFirebaseInstanceIdService firebaseInstanceIdService = new MyFirebaseInstanceIdService();
        return firebaseInstanceIdService.getFirebaseNewId(context);
    }
}
