package com.accenture.salvo;

public final class ResponseEntityMsgs {
    //keys
    public static final String KEY_ERROR = "Error";
    public static final String KEY_SUCCESS = "Exito";
    public static final String KEY_GPID = "gpid";



    //mensajes
    public static final String MSG_NO_LOGUEADO = "No estas logueado capo";
    public static final String MSG_JUEGO_CREADO = "Juego creado";
    public static final String MSG_JUGADOR_NO_ENCONTRADO = "No se encontro al jugador";
    public static final String MSG_JUGADOR_DISTINTO_AL_LOGUEADO = "No quieras hacer trampa capo";
    public static final String MSG_JUEGO_NO_ENCONTRADO = "Juego no encontrado";
    public static final String MSG_SHIPS_AGREGADOS = "Ships agregados";
    public static final String MSG_SHIPS_NO_AGREGADOS = "Ships ya agregados";
    public static final String MSG_SALVOS_AGREGADOS = "Salvos agregados";
    public static final String MSG_SALVOS_YA_AGREGADOS = "Salvos ya agregados para el turno actual";
    public static final String MSG_JUEGO_COMPLETO = "El juego ya esta completo";
    public static final String MSG_UNIENDOSE_A_LA_PARTIDA = "Uniendose a la partida";
    public static final String MSG_NOMBRE_DE_USUARIO_INEXISTENTE = "No existe usuario con ese nombre";
    public static final String MSG_NOMBRE_DE_USUARIO_REPETIDO = "Ya existe un usuario con ese nombre";
    public static final String MSG_USUARIO_CREADO = "Usuario creado";

    private ResponseEntityMsgs() {
        throw new IllegalStateException("Utility Class");
    }



}
