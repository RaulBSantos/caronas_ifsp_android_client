package projetocaronas.tcc.ifsp.br.projetocarona.controllers;

import android.app.Activity;
import android.content.res.Resources;

import org.json.JSONObject;

import projetocaronas.tcc.ifsp.br.projetocarona.R;
import projetocaronas.tcc.ifsp.br.projetocarona.entities.RideNotification;
import projetocaronas.tcc.ifsp.br.projetocarona.entities.User;
import projetocaronas.tcc.ifsp.br.projetocarona.enums.RIDE_ACTION;
import projetocaronas.tcc.ifsp.br.projetocarona.tasks.ConnectionSendJSONTask;
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
        String message = this.context.getString(R.string.request_ride);
        notification.setMessage(message);
        JSONObject notificationJson = JsonUtils.convertToJson(notification);
        //FIXME Envia a notificação para a URL de PEDIDOS de carona
        new ConnectionSendJSONTask(this.context, this.NOTIFICATION_URL).execute(notificationJson);
    }

    public void sendRideOffer(User origin, User destination){
        RideNotification notification = new RideNotification(origin, RIDE_ACTION.OFFER, destination);
        String message = this.context.getString(R.string.offer_ride);
        notification.setMessage(message);
        JSONObject notificationJson = JsonUtils.convertToJson(notification);
        //FIXME Envia a notificação para a URL de OFERTA de carona
        new ConnectionSendJSONTask(this.context, this.NOTIFICATION_URL).execute(notificationJson);

    }

}
