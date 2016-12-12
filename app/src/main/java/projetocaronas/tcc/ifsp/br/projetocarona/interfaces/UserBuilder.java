package projetocaronas.tcc.ifsp.br.projetocarona.interfaces;

import projetocaronas.tcc.ifsp.br.projetocarona.entities.User;

/**
 * Métodos obrigam qualquer usuário a ter os parâmetros a seguir(que serão comuns para as aplicações futuras).
 * Parâmetros específicos de carona deverão ser opcionais.
 *
 * Created by Raul on 01/11/2016.
 */

public interface UserBuilder {
    User withName(String name);
    User withRecord(String record);
    User withPhone(String phone);
    User withEmail(String email);
}
