package projetocaronas.tcc.ifsp.br.projetocarona.controllers;

import android.app.Activity;
import android.os.AsyncTask;

import org.json.JSONObject;

import projetocaronas.tcc.ifsp.br.projetocarona.R;
import projetocaronas.tcc.ifsp.br.projetocarona.entities.RideNotification;
import projetocaronas.tcc.ifsp.br.projetocarona.entities.User;
import projetocaronas.tcc.ifsp.br.projetocarona.enums.RIDE_ACTION;
import projetocaronas.tcc.ifsp.br.projetocarona.tasks.ConnectionSendJsonTask;
import projetocaronas.tcc.ifsp.br.projetocarona.tasks.ConnectionSendLoginJSONTask;
import projetocaronas.tcc.ifsp.br.projetocarona.utils.JsonUtils;

/**
 * Created by raul on 09/03/17.
 */

public class NotificationController {
    private final String NOTIFICATION_URL =  "/notification";

    private Activity context;

    public NotificationController(Activity activity) {
        this.context = activity;
    }

    public void sendRideRequest(User origin, User destination){
        RideNotification notification = new RideNotification(origin, RIDE_ACTION.REQUEST, destination);
        String message = this.context.getString(R.string.request_ride); //FIXME Desnecessário
        notification.setMessage(message); //FIXME Desnecessário
        JSONObject notificationJson = JsonUtils.convertToJson(notification);
        //FIXME Envia a notificação para a URL de PEDIDOS de carona
        new ConnectionSendLoginJSONTask(this.context, this.NOTIFICATION_URL).execute(notificationJson);
    }

    public void sendRideOffer(User origin, User destination){
        RideNotification notification = new RideNotification(origin, RIDE_ACTION.OFFER, destination);
        String message = this.context.getString(R.string.offer_ride); //FIXME Desnecessário
        notification.setMessage(message); //FIXME Desnecessário
        JSONObject notificationJson = JsonUtils.convertToJson(notification);
        //FIXME Envia a notificação para a URL de OFERTA de carona
        new ConnectionSendLoginJSONTask(this.context, this.NOTIFICATION_URL).execute(notificationJson);
    }

    public void sendRideConfirm(User origin, User destination){
        RideNotification notification = new RideNotification(origin, RIDE_ACTION.CONFIRM, destination);
        JSONObject notificationJson = JsonUtils.convertToJson(notification);
        ConnectionSendJsonTask sendJsonTask = new ConnectionSendJsonTask(this.context, this.NOTIFICATION_URL + "/confirm-ride");
        sendJsonTask.withOnSuccessToast("A carona foi aceita. O usuário será notificado.");
        sendJsonTask.withOnErrorToast("Erro ao exceutar transação. Tente novamente mais tarde.");

        sendJsonTask.execute(notificationJson);
    }

    public void sendRideReject(User origin, User destination) {
        RideNotification notification = new RideNotification(origin, RIDE_ACTION.REJECT, destination);
        JSONObject notificationJson = JsonUtils.convertToJson(notification);
        ConnectionSendJsonTask sendJsonTask = new ConnectionSendJsonTask(this.context, this.NOTIFICATION_URL + "/reject-ride");
        sendJsonTask.withOnSuccessToast("A carona não foi aceita. O usuário será notificado");
        sendJsonTask.withOnErrorToast("Erro ao exceutar transação. Tente novamente mais tarde.");

        sendJsonTask.execute(notificationJson);
    }
}
