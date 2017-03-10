package projetocaronas.tcc.ifsp.br.projetocarona.controllers;

import android.content.res.Resources;
import android.provider.Settings;

import org.json.JSONObject;

import projetocaronas.tcc.ifsp.br.projetocarona.LoginActivity;
import projetocaronas.tcc.ifsp.br.projetocarona.MapsActivity;
import projetocaronas.tcc.ifsp.br.projetocarona.R;
import projetocaronas.tcc.ifsp.br.projetocarona.entities.RideNotification;
import projetocaronas.tcc.ifsp.br.projetocarona.entities.User;
import projetocaronas.tcc.ifsp.br.projetocarona.enums.RIDE_ACTION;
import projetocaronas.tcc.ifsp.br.projetocarona.tasks.ConnectionSendAndReceiveJSONTask;
import projetocaronas.tcc.ifsp.br.projetocarona.utils.AndroidUtilsCaronas;
import projetocaronas.tcc.ifsp.br.projetocarona.utils.JsonUtils;

/**
 * Created by raul on 09/03/17.
 */

public class NotificationController {
    private final String NOTIFICATION_FULL_URL = AndroidUtilsCaronas.SERVER_PREFIX_CONTEXT + "/notification";

    public void sendRideRequest(User origin, User destination){
        RideNotification notification = new RideNotification(origin, RIDE_ACTION.REQUEST, destination);
        String message = Resources.getSystem().getString(R.string.request_ride);
        notification.setMessage(message);
        JSONObject json = JsonUtils.convertToJson(notification);
        //FIXME Envia a notificação para a URL de PEDIDOS de carona

    }

    public void sendRideOffer(User origin, User destination){
        RideNotification notification = new RideNotification(origin, RIDE_ACTION.REQUEST, destination);
        String message = Resources.getSystem().getString(R.string.offer_ride);
        notification.setMessage(message);
        JSONObject json = JsonUtils.convertToJson(notification);
        //FIXME Envia a notificação para a URL de OFERTA de carona
        new ConnectionSendAndReceiveJSONTask(LoginActivity.this, LoginActivity.this, new MapsActivity(), "/login").execute(postParameters);

    }

}
