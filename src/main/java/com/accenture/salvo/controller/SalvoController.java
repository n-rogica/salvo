package com.accenture.salvo.controller;


import com.accenture.salvo.ResponseEntityMsgs;
import com.accenture.salvo.model.games.*;
import com.accenture.salvo.model.salvoes.Salvo;
import com.accenture.salvo.model.ships.Ship;
import com.accenture.salvo.repository.*;
import com.accenture.salvo.model.players.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {


    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private SalvoRepository salvoRepository;


    /*===============================================GET GAME ID======================================================*/

    @RequestMapping(path= "/games", method = RequestMethod.GET)
    /*metodo que devuelve un json con el detalle de todos los juegos*/
    public Object getGameIds() {
        Map<String, Object> gamesDTO = new LinkedHashMap<>();
        List<Game> games = gameRepository.findAll();
        Player player = this.getAuthenticatedPlayer();

        if (player == null) {
            gamesDTO.put("player","Guest");
            gamesDTO.put("games", games.stream().map(Game::getGameDTO).collect(Collectors.toList()));
        } else {
            gamesDTO.put("player",player.getPlayerDTO());
            gamesDTO.put("games", games.stream().map(Game::getGameDTO).collect(Collectors.toList()));
        }

        return gamesDTO;
    }

    /*==========================================CREATE GAME===========================================================*/

    /*metodo que verifica si el usuario esta autenticado y crea un nuevo juego*/
    @RequestMapping(path= "/games", method = RequestMethod.POST)
    public Object createGame() {
        Player authenticatedPlayer = this.getAuthenticatedPlayer();
        if (authenticatedPlayer == null) {
            return this.createResponseEntity(ResponseEntityMsgs.KEY_ERROR, ResponseEntityMsgs.MSG_NO_LOGUEADO,
                    HttpStatus.UNAUTHORIZED);
        } else {
            Game newGame = new Game();
            gameRepository.save(newGame);
            GamePlayer gamePlayer = new GamePlayer(authenticatedPlayer, newGame);
            gamePlayerRepository.save(gamePlayer);
            return this.createResponseEntity(ResponseEntityMsgs.KEY_GPID,
                    gamePlayer.getId(), HttpStatus.CREATED);
        }
    }

    /*==============================================GET SHIPS=========================================================*/

    /*Metodo que devuelve los ships del jugador pasado por parametro en la url*/
    @RequestMapping(path="/games/players/{gamePlayerId}/ships", method = RequestMethod.GET)
    public Object getShips(@PathVariable("gamePlayerId") long gpId) {
        Map<String,Object> playerShips = new LinkedHashMap<>();
        GamePlayer gamePlayer = gamePlayerRepository.findOne(gpId);

        Player player = getAuthenticatedPlayer();

        if (gamePlayer == null) {
            return this.createResponseEntity(ResponseEntityMsgs.KEY_ERROR, ResponseEntityMsgs.MSG_JUGADOR_NO_ENCONTRADO,
                    HttpStatus.NOT_FOUND);
        }

        if (player == null) {
            return this.createResponseEntity(ResponseEntityMsgs.KEY_ERROR,
                    ResponseEntityMsgs.MSG_NO_LOGUEADO, HttpStatus.UNAUTHORIZED);
        }

        if (player.getId() != gamePlayer.getPlayer().getId()) {
            return this.createResponseEntity(ResponseEntityMsgs.KEY_ERROR,
                    ResponseEntityMsgs.MSG_JUGADOR_DISTINTO_AL_LOGUEADO, HttpStatus.UNAUTHORIZED);
        }

        playerShips.put("gpid", gamePlayer.getId());
        playerShips.put("ships", gamePlayer.getGamePlayerShipsDTO());
        gamePlayer.updateGameState();
        return playerShips;
    }

    /*============================================SET SHIPS===========================================================*/

    /*Metodo que recibe una lista de ships en el request y si se cumplen las condiciones los asocia con el gameplayer
    indicado por parametro*/
    @RequestMapping(path="/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public Object setShipsLocations(@PathVariable("gamePlayerId") long gpId, @RequestBody List<Ship> ships) {
    Player authenticatedPlayer = getAuthenticatedPlayer();

    if (authenticatedPlayer == null) {
        return this.createResponseEntity(ResponseEntityMsgs.KEY_ERROR, ResponseEntityMsgs.MSG_NO_LOGUEADO,
                HttpStatus.UNAUTHORIZED);
    }

    GamePlayer gamePlayer = gamePlayerRepository.findOne(gpId);
    if (gamePlayer == null) {
        return this.createResponseEntity(ResponseEntityMsgs.KEY_ERROR, ResponseEntityMsgs.MSG_JUGADOR_NO_ENCONTRADO,
                HttpStatus.NOT_FOUND);
    }

    if (authenticatedPlayer.getId() != gamePlayer.getPlayer().getId()) {
        return this.createResponseEntity(ResponseEntityMsgs.KEY_ERROR,
                ResponseEntityMsgs.MSG_JUGADOR_DISTINTO_AL_LOGUEADO, HttpStatus.UNAUTHORIZED);
    }

    //Verifique que el usuario esta logueado, el gameplayer id existe y es el correspondiente al usuario logueado

    if (gamePlayer.hasNoShips()) {
        gamePlayer.addShips(ships);
        gamePlayer.setGameState(GameState.WAIT);
        gamePlayer.updateGameState();
        gamePlayerRepository.save(gamePlayer);
        return this.createResponseEntity(ResponseEntityMsgs.KEY_SUCCESS, ResponseEntityMsgs.MSG_SHIPS_AGREGADOS,
                HttpStatus.CREATED);
    } else {
        return this.createResponseEntity(ResponseEntityMsgs.KEY_ERROR, ResponseEntityMsgs.MSG_SHIPS_NO_AGREGADOS,
                HttpStatus.FORBIDDEN);
        }
    }


    /*============================================GET SALVOES=========================================================*/

    /*Metodo que devuelve los salvos del jugador pasado por parametro en la url*/
    @RequestMapping(path="/games/players/{gamePlayerId}/salvoes", method = RequestMethod.GET)
    public Object getSalvoes(@PathVariable("gamePlayerId") long gpId) {
        Map<String,Object> playerSalvoes = new LinkedHashMap<>();
        GamePlayer gamePlayer = gamePlayerRepository.findOne(gpId);

        Player authenticatedPlayer = getAuthenticatedPlayer();

        if (authenticatedPlayer == null) {
            return this.createResponseEntity(ResponseEntityMsgs.KEY_ERROR, ResponseEntityMsgs.MSG_NO_LOGUEADO,
                    HttpStatus.FORBIDDEN);
        }

        if (gamePlayer == null) {
            return this.createResponseEntity(ResponseEntityMsgs.KEY_ERROR, ResponseEntityMsgs.MSG_JUGADOR_NO_ENCONTRADO,
                    HttpStatus.UNAUTHORIZED);
        }

        if (authenticatedPlayer.getId() != gamePlayer.getPlayer().getId()) {
            return this.createResponseEntity(ResponseEntityMsgs.KEY_ERROR,
                    ResponseEntityMsgs.MSG_JUGADOR_DISTINTO_AL_LOGUEADO, HttpStatus.FORBIDDEN);
        }

        playerSalvoes.put("gpid", gamePlayer.getId());
        playerSalvoes.put("salvoes", gamePlayer.getSalvoesDTO());
        gamePlayer.updateGameState();
        return playerSalvoes;
    }


    /*==============================================SET SALVOES=======================================================*/
    /* Metodo que recibe por parametro una lista de salvos y si se cumplen las condiciones los asocia
    con el player indicado*/

    @RequestMapping(path="/games/players/{gamePlayerId}/salvoes", method = RequestMethod.POST)
    public Object setSalvoes(@PathVariable("gamePlayerId") long gpId, @RequestBody Salvo salvo) {
        Player authenticatedPlayer = getAuthenticatedPlayer();

        if (authenticatedPlayer == null) {
            return this.createResponseEntity(ResponseEntityMsgs.KEY_ERROR, ResponseEntityMsgs.MSG_NO_LOGUEADO,
                    HttpStatus.UNAUTHORIZED);
        }

        GamePlayer gamePlayer = gamePlayerRepository.findOne(gpId);

        if (gamePlayer == null) {
            return this.createResponseEntity(ResponseEntityMsgs.KEY_ERROR, ResponseEntityMsgs.MSG_JUGADOR_NO_ENCONTRADO,
                    HttpStatus.UNAUTHORIZED);
        }

        if (authenticatedPlayer.getId() != gamePlayer.getPlayer().getId()) {
            return this.createResponseEntity(ResponseEntityMsgs.KEY_ERROR,
                    ResponseEntityMsgs.MSG_JUGADOR_DISTINTO_AL_LOGUEADO, HttpStatus.FORBIDDEN);
        }

        //Verifique que el usuario esta logueado, el gameplayer id existe y es el correspondiente al usuario logueado

        Salvo newSalvo = new Salvo(gamePlayer,gamePlayer.getSalvoes().size()+1, salvo.getSalvoLocations());
        if (this.canPlaceSalvoes(gamePlayer, newSalvo)) {
            gamePlayer.addSalvo(newSalvo);
            gamePlayer.setGameState(GameState.WAIT);
            gamePlayer.updateGameState();
            gamePlayerRepository.save(gamePlayer);
            return this.createResponseEntity(ResponseEntityMsgs.KEY_SUCCESS, ResponseEntityMsgs.MSG_SALVOS_AGREGADOS,
                    HttpStatus.CREATED);
        } else {
            return this.createResponseEntity(ResponseEntityMsgs.KEY_ERROR, ResponseEntityMsgs.MSG_SALVOS_YA_AGREGADOS,
                    HttpStatus.FORBIDDEN);
        }
    }

    /*==================================================JOIN GAME=====================================================*/

    /*Metodo que permite unirse a la partida ingresada por parametro*/
    @RequestMapping(path="/game/{nn}/players", method = RequestMethod.POST)
    public Object joinGame(@PathVariable("nn") Long gameId) {
        Player player = getAuthenticatedPlayer();

        if (player == null) {
            return this.createResponseEntity(ResponseEntityMsgs.KEY_ERROR, ResponseEntityMsgs.MSG_NO_LOGUEADO,
                    HttpStatus.UNAUTHORIZED);
        }

        Game game = gameRepository.findOne(gameId);

        if (game == null) {
            return this.createResponseEntity(ResponseEntityMsgs.KEY_ERROR, ResponseEntityMsgs.MSG_JUEGO_NO_ENCONTRADO,
                    HttpStatus.FORBIDDEN);
        }

        if (game.countGamePlayers() == 2) {
            return this.createResponseEntity(ResponseEntityMsgs.KEY_ERROR, ResponseEntityMsgs.MSG_JUEGO_COMPLETO,
                    HttpStatus.FORBIDDEN);
        }
        GamePlayer gamePlayer = new GamePlayer(player, game);
        gamePlayerRepository.save(gamePlayer);
        return this.createResponseEntity(ResponseEntityMsgs.KEY_GPID, gamePlayer.getId(),HttpStatus.CREATED);
    }

    /*===============================================GET LEADERBOARD==================================================*/

    /*metodo que devuelve el leaderboard con los resultados y el puntaje de cada jugador registrado en la aplicacion*/
    @RequestMapping("/leaderBoard")
    public List<Object> getLeaderBoard() {
        List<Player> players = playerRepository.findAll();
        return players.stream().map(Player::getScoreHistoryDTO).collect(Collectors.toList());
    }

    /*===============================================GET GAME BY ID===================================================*/

    /*metodo que devuelve el estado de un juego desde el punto de vista del usuario qeu se pasa por parametro
     * este metodo requiere que el usuario este autenticado y verifica que el id pasado por parametro corresponda
     * con el usuario que esta autenticado, en caso contrario informa al usuario que no puede acceder a esta
     * informacion*/
    @RequestMapping("/game_view/{nn}")
    public Object getGameById(@PathVariable("nn") Long gamePlayerId) {
        Player authenticatedPlayer = this.getAuthenticatedPlayer();
        if (authenticatedPlayer == null) {
            return this.createResponseEntity(ResponseEntityMsgs.KEY_ERROR, ResponseEntityMsgs.MSG_NO_LOGUEADO,
                    HttpStatus.UNAUTHORIZED);
        }

        long authenticatedPlayerId = authenticatedPlayer.getId();
        GamePlayer gamePlayer = gamePlayerRepository.findOne(gamePlayerId);

        //verifico que sea una partida en la cual se encuentra el usuario autenticado en la aplicacion
        if (gamePlayer.getPlayer().getId() ==  authenticatedPlayerId) {
                gamePlayer.updateGameState();
                //verifico si la partida termino y no se colocaron los scores correspondientes
                if (gamePlayer.gameFinished() && (gamePlayer.getGame().getScores().size() != 2)) {
                    this.updateScores(gamePlayer.getGameState(), gamePlayer);
                }
                gamePlayerRepository.save(gamePlayer);
                return gamePlayer.getGameplayerPovDTO();
        } else {
            return this.createResponseEntity(ResponseEntityMsgs.KEY_ERROR,
                    ResponseEntityMsgs.MSG_JUGADOR_DISTINTO_AL_LOGUEADO, HttpStatus.UNAUTHORIZED);
        }
    }
    /*=================================== CREATE PLAYER ==============================================================*/

    //Metodo para crear un jugador, recibe por parametro el nombre de usuario y el password
    //Si el usuario ya existe, o el nombre esta vacio devuelve un codigo de error
    @RequestMapping(path= "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> createPlayer(@RequestParam("email") String username,
                                               @RequestParam("password") String password) {
        //Nombre de usuario vacio
        if (username.isEmpty() ) {
            return this.createResponseEntity(ResponseEntityMsgs.KEY_ERROR,
                    ResponseEntityMsgs.MSG_NOMBRE_DE_USUARIO_INEXISTENTE, HttpStatus.FORBIDDEN);
        }

        Player player = playerRepository.findByUserName(username);
        //Verifico que no exista un usuario con ese nombre
        if (player != null) {
            return this.createResponseEntity(ResponseEntityMsgs.KEY_ERROR,
                    ResponseEntityMsgs.MSG_NOMBRE_DE_USUARIO_REPETIDO, HttpStatus.CONFLICT);
        }

        playerRepository.save(new Player(username,password));
        return this.createResponseEntity(ResponseEntityMsgs.KEY_SUCCESS, ResponseEntityMsgs.MSG_USUARIO_CREADO,
                HttpStatus.CREATED);
    }

    /*============================================METODOS PRIVADOS====================================================*/
    private Player getAuthenticatedPlayer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        } else {
            return playerRepository.findByUserName(authentication.getName());
        }
    }

    private void updateScores(GameState gameState, GamePlayer gamePlayerOfRequest) {
        switch (gameState) {
            case WON:
                scoreRepository.save(new Score(1.0, gamePlayerOfRequest.getGame(), gamePlayerOfRequest.getPlayer()));
                scoreRepository.save(new Score(0.0, gamePlayerOfRequest.getGame(), gamePlayerOfRequest.getOpponent()));
                break;
            case LOST:
                scoreRepository.save(new Score(0.0, gamePlayerOfRequest.getGame(), gamePlayerOfRequest.getPlayer()));
                scoreRepository.save(new Score(1.0, gamePlayerOfRequest.getGame(), gamePlayerOfRequest.getOpponent()));
                break;
            case TIE:
                scoreRepository.save(new Score(0.5, gamePlayerOfRequest.getGame(), gamePlayerOfRequest.getPlayer()));
                scoreRepository.save(new Score(0.5, gamePlayerOfRequest.getGame(), gamePlayerOfRequest.getOpponent()));
                break;
            default:
                throw new IllegalStateException("Error al computar el score");
        }
    }

    private boolean canPlaceSalvoes(GamePlayer gamePlayer, Salvo salvo) {
        if (gamePlayer.getSalvoes().isEmpty()) {
            return true;
        }
        return (salvo.getTurn() == gamePlayer.getSalvoes().size() + 1) &&
                (!gamePlayer.repeatedSalvo(salvo.getSalvoLocations()));
    }

    private ResponseEntity<Object> createResponseEntity(String tipoDeRespuesta, Object valor, HttpStatus httpStatus ) {
        Map<String,Object> responseMap = new LinkedHashMap<>();
        responseMap.put(tipoDeRespuesta, valor);
        return new ResponseEntity<>(responseMap, httpStatus);
    }
}
