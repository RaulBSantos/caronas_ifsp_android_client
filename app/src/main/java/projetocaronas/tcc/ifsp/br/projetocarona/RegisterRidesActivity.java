package projetocaronas.tcc.ifsp.br.projetocarona;


import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import projetocaronas.tcc.ifsp.br.projetocarona.entities.Ride;
import projetocaronas.tcc.ifsp.br.projetocarona.tasks.ConnectionSendLoginJSONTask;
import projetocaronas.tcc.ifsp.br.projetocarona.utils.AndroidUtilsCaronas;
import projetocaronas.tcc.ifsp.br.projetocarona.utils.Mask;

public class RegisterRidesActivity extends AppCompatActivity {
    public TextView labelGoTime = null;
    public EditText editTextGoTime = null;
    public TextView labelBackTime = null;
    public EditText editTextBackTime = null;
    public RadioGroup rgWay = null;
    public RadioButton rbCheckedWay = null;

    public TextView labelBackVacancy = null;
    public EditText editTextBackVacancy = null;
    public TextView labelGoVacancy = null;
    public EditText editTextGoVacancy = null;

    // Elemento conteiner dos checkboxes de dias de semana
    public CheckBox dayMon = null;
    public CheckBox dayTue = null;
    public CheckBox dayWed = null;
    public CheckBox dayThu = null;
    public CheckBox dayFri = null;
    public CheckBox daySat = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_rides);

        // CheckBoxes dias da semana
        dayMon = (CheckBox) findViewById(R.id.check_mon);
        dayTue = (CheckBox) findViewById(R.id.check_tue);
        dayWed = (CheckBox) findViewById(R.id.check_wed);
        dayThu = (CheckBox) findViewById(R.id.check_thu);
        dayFri = (CheckBox) findViewById(R.id.check_fri);
        daySat = (CheckBox) findViewById(R.id.check_sat);

        // Visualização para preencher as horas de ida e volta

        labelBackTime = (TextView) findViewById(R.id.label_back_time);
        editTextBackTime = (EditText) findViewById(R.id.edit_text_back_time);

        labelGoTime = (TextView) findViewById(R.id.label_go_time);
        editTextGoTime = (EditText) findViewById(R.id.edit_text_go_time);

        labelBackVacancy = (TextView) findViewById(R.id.label_back_vacancy);
        editTextBackVacancy = (EditText) findViewById(R.id.edit_text_back_vacancy);
        labelGoVacancy = (TextView) findViewById(R.id.label_go_vacancy);
        editTextGoVacancy = (EditText) findViewById(R.id.edit_text_go_vacancy);

        // Adiciona máscara
        editTextBackVacancy.addTextChangedListener(Mask.insert("##", editTextBackVacancy));
        editTextGoVacancy.addTextChangedListener(Mask.insert("##", editTextGoVacancy));

        rgWay = (RadioGroup) findViewById(R.id.radio_group_way);
        rbCheckedWay = (RadioButton) rgWay.findViewById(rgWay.getCheckedRadioButtonId());

        // Listener do RadioGroup
        rgWay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                String teste = "";
                //FIXME Refactor para Switch case
                switch (checkedId){
                    case R.id.radio_way_go_and_back:
                        labelBackTime.setVisibility(View.VISIBLE);
                        editTextBackTime.setVisibility(View.VISIBLE);
                        labelGoTime.setVisibility(View.VISIBLE);
                        editTextGoTime.setVisibility(View.VISIBLE);

                        labelBackVacancy.setVisibility(View.VISIBLE);
                        editTextBackVacancy.setVisibility(View.VISIBLE);
                        labelGoVacancy.setVisibility(View.VISIBLE);
                        editTextGoVacancy.setVisibility(View.VISIBLE);
                        break;
                    case R.id.radio_way_go:
                        // Show just Go
                        labelBackTime.setVisibility(View.GONE);
                        editTextBackTime.setVisibility(View.GONE);
                        labelGoTime.setVisibility(View.VISIBLE);
                        editTextGoTime.setVisibility(View.VISIBLE);

                        labelBackVacancy.setVisibility(View.GONE);
                        editTextBackVacancy.setVisibility(View.GONE);
                        labelGoVacancy.setVisibility(View.VISIBLE);
                        editTextGoVacancy.setVisibility(View.VISIBLE);
                        break;
                    case R.id.radio_way_back:
                        // Show just Back
                        labelGoTime.setVisibility(View.GONE);
                        editTextGoTime.setVisibility(View.GONE);
                        labelBackTime.setVisibility(View.VISIBLE);
                        editTextBackTime.setVisibility(View.VISIBLE);

                        labelGoVacancy.setVisibility(View.GONE);
                        editTextGoVacancy.setVisibility(View.GONE);
                        labelBackVacancy.setVisibility(View.VISIBLE);
                        editTextBackVacancy.setVisibility(View.VISIBLE);
                        break;
                }
                // Adiciona reseta o radioButton checado
                rbCheckedWay = checkedRadioButton;

            }
        } );

        editTextBackTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showTimePickerDialog(v);
            }
        });

        editTextGoTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }
        });


//        backTime = (EditText) findViewById(R.id.back_time);
//        #backTimePicker = (TimePicker) findViewById(R.id.back_time_picker);
//
//        backTime.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                #backTimePicker.setVisibility(View.VISIBLE);
//                return false;
//            }
//        });

    }

    public void showTimePickerDialog(View v) {
        DialogFragment timePickerFragment = new TimePickerFragment();
        // Coloca o id do elemento que chamou
        Bundle bundle = new Bundle();
        bundle.putInt("viewId",v.getId());
        timePickerFragment.setArguments(bundle);

        timePickerFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void editRoute(View view){
        Ride ride = new Ride();
        Intent intent = new Intent(this, RegisterRoutesActivity.class);
        intent.putExtra("newRide",ride);
        startActivity(intent);
    }

    public void hideKeyboardRegisterRides(View view){
        AndroidUtilsCaronas.hideKeyboard(this);
    }

    /**
     * Executa ao salvar uma carona
     * @param view
     */
    public void onSaveRide(View view){
        // monta o Json
        String stringfiedJson = new Gson().toJson(this);


        // Envia para o controller
        new ConnectionSendLoginJSONTask(RegisterRidesActivity.this, new MapsActivity(), "/register_ride").execute(stringfiedJson);

        /*
        *
        *   TextView labelGoTime = null;
            EditText editTextGoTime = null;
            TextView labelBackTime = null;
            EditText editTextBackTime = null;
            RadioGroup rgWay = null;
            RadioButton rbCheckedWay = null;

            TextView labelBackVacancy = null;
            EditText editTextBackVacancy = null;
            TextView labelGoVacancy = null;
            EditText editTextGoVacancy = null;
        *
        *
        *
        *
        * */


        // Envia para o Server
    }

}
