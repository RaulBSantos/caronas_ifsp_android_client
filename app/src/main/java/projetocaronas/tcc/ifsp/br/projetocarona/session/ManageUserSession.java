package projetocaronas.tcc.ifsp.br.projetocarona.session;

import projetocaronas.tcc.ifsp.br.projetocarona.entities.User;

/**
 * Created by raul on 26/02/17.
 */

public class ManageUserSession {
    /*
        Mantém informações do usu´ario logado
     */
    private static User sessionUser;

    public static void saveSessionUser(User sessionUser) {
        ManageUserSession.sessionUser = sessionUser;
    }

    public static User getSessionUser() {
        return sessionUser;
    }
}
