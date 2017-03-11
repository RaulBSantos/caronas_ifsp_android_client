package projetocaronas.tcc.ifsp.br.projetocarona.entities;

import java.io.Serializable;

import projetocaronas.tcc.ifsp.br.projetocarona.enums.RIDE_ACTION;

/**
 * Created by raul on 09/03/17.
 */
public class RideNotification implements Serializable {
    private User origin;
    private User destination;
    private String message;
    private RIDE_ACTION action;

    public RideNotification(User origin, RIDE_ACTION action, User destination) {
        this.origin = origin;
        this.action = action;
        this.destination = destination;
    }

    public User getOrigin() {
        return origin;
    }

    public User getDestination() {
        return destination;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RIDE_ACTION getAction() {
        return action;
    }

}
