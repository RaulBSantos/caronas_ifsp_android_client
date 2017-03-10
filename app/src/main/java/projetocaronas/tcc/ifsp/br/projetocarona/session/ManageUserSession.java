package projetocaronas.tcc.ifsp.br.projetocarona.session;

import projetocaronas.tcc.ifsp.br.projetocarona.entities.User;

/**
 * Created by raul on 26/02/17.
 */

public class ManageUserSession {
    /*
        Mantém informações do usu´ario logado
     */
    private static User sessionUser = new User();

    public static void saveSessionUser(User sessionUser) {
        ManageUserSession.sessionUser = sessionUser;
    }
    // Checa se
    public static boolean isThisUserLogged(User user){
        User loggedUser = ManageUserSession.getSessionUser();
        if (loggedUser != null){
            return loggedUser.getRecord().equals(user.getRecord());
        }
        return false;
    }

    public static User getSessionUser() {
        return ManageUserSession.sessionUser;
    }

    public static boolean canCurrentUserGiveRides() {
        User loggedUser = ManageUserSession.getSessionUser();
        return loggedUser.isCanGiveRide();
    }
}
