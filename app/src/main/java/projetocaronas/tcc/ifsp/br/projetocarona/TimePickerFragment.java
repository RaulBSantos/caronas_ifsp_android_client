package projetocaronas.tcc.ifsp.br.projetocarona;

import android.app.Dialog;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import projetocaronas.tcc.ifsp.br.projetocarona.utils.Mask;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    // Usado para registrar o elemento chamou o TimePicker
    private Integer callerViewId = null;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Cria com a hora atual
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Registra qual o elemento chamou o TimePicker
        Bundle arguments = this.getArguments();
        callerViewId = arguments.getInt("viewId");

        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Registra a hora selecionada pelo usuário
        // armazenando no elemento correto
        EditText viewCaller = (EditText) getActivity().findViewById(callerViewId);
        viewCaller.setText(Mask.timeFormatter(view.getCurrentHour(), view.getCurrentMinute()));
    }
}
