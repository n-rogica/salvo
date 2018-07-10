package com.accenture.salvo.controller;


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
    GameRepository gameRepository;

    @Autowired
    ScoreRepository scoreRepository;

    @Autowired
    GamePlayerRepository gamePlayerRepository;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    ShipRepository shipRepository;

    @Autowired
    SalvoRepository salvoRepository;


    /*=========================GET GAME ID=========================*/

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

    /*=========================CREATE GAME===================================*/

    /*metodo que verifica si el usuario esta autenticado y crea un nuevo juego*/
    @RequestMapping(path= "/games", method = RequestMethod.POST)
    public Object createGame() {
        Player authenticatedPlayer = this.getAuthenticatedPlayer();
        if (authenticatedPlayer == null) {
            return new ResponseEntity<>(this.getResponseMapDTO("error", "No estas logueado capo"), HttpStatus.UNAUTHORIZED);
        } else {
            GamePlayer gamePlayer = new GamePlayer(authenticatedPlayer, gameRepository.save(new Game()));
            gamePlayerRepository.save(gamePlayer);
            return this.createResponseEntity("gpid", gamePlayer.getId(), HttpStatus.CREATED);
        }
    }

    /*=========================GET SHIPS=========================*/

    /*Metodo que devuelve los ships del jugador pasado por parametro en la url*/
    @RequestMapping(path="/games/players/{gamePlayerId}/ships", method = RequestMethod.GET)
    public Object getShips(@PathVariable("gamePlayerId") long gpId) {
        Map<String,Object> playerShips = new LinkedHashMap<>();
        GamePlayer gamePlayer = gamePlayerRepository.findOne(gpId);

        Player player = getAuthenticatedPlayer();

        if (gamePlayer == null) {
            return this.createResponseEntity("error", "no se pudo obtener la informacion", HttpStatus.NOT_FOUND);
        }

        if (player == null || (player.getId() != gamePlayer.getPlayer().getId())) {
            return this.createResponseEntity("error", "no quieras hacer trampa capo", HttpStatus.UNAUTHORIZED);
        }

        playerShips.put("gpid", gamePlayer.getId());
        playerShips.put("ships", gamePlayer.getGamePlayerShipsDTO());
        gamePlayer.updateGameState();
        return playerShips;
    }

    /*========================================SET SHIPS===============================================================*/

    /*Metodo que recibe una lista de ships en el request y si se cumplen las condiciones los asocia con el gameplayer
    indicado por parametro
     */
    @RequestMapping(path="/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public Object setShipsLocations(@PathVariable("gamePlayerId") long gpId, @RequestBody List<Ship> ships) {
    Player authenticatedPlayer = getAuthenticatedPlayer();

    if (authenticatedPlayer == null) {
        return this.createResponseEntity("error", "no estas logueado capo", HttpStatus.UNAUTHORIZED);
    }

    GamePlayer gamePlayer = gamePlayerRepository.findOne(gpId);
    if (gamePlayer == null) {
        return this.createResponseEntity("error", "no se pudo acceder al juego", HttpStatus.NOT_FOUND);
    }

    if (authenticatedPlayer.getId() != gamePlayer.getPlayer().getId()) {
        return this.createResponseEntity("error", "no se pueden agregar barcos", HttpStatus.UNAUTHORIZED);
    }

    //Verifique que el usuario esta logueado, el gameplayer id existe y es el correspondiente al usuario logueado

    if (gamePlayer.hasNoShips()) {
        ships.stream().forEach(ship -> {
            Ship newShip = new Ship(ship.getShipType(), gamePlayer, ship.getShipLocations());
            shipRepository.save(newShip);
            gamePlayer.addShip(newShip);
        });
        gamePlayer.setGameState(GameState.WAIT);
        gamePlayer.updateGameState();
        gamePlayerRepository.save(gamePlayer);
        return this.createResponseEntity("mensaje", "Barcos agregados", HttpStatus.CREATED);
    } else {
        return this.createResponseEntity("error", "Ya se colocaron los barcos", HttpStatus.FORBIDDEN);
        }
    }


    /*==============================GET SALVOES=======================*/

    /*Metodo que devuelve los salvos del jugador pasado por parametro en la url*/
    @RequestMapping(path="/games/players/{gamePlayerId}/salvoes", method = RequestMethod.GET)
    public Object getSalvoes(@PathVariable("gamePlayerId") long gpId) {
        Map<String,Object> playerSalvoes = new LinkedHashMap<>();
        GamePlayer gamePlayer = gamePlayerRepository.findOne(gpId);

        Player player = getAuthenticatedPlayer();

        if (player == null || (player.getId() != gamePlayer.getPlayer().getId())) {
            return this.createResponseEntity("error", "no quieras hacer trampa capo", HttpStatus.FORBIDDEN);
        }

        if (gamePlayer == null) {
            return this.createResponseEntity("error", "Id de gameplayer incorrecto", HttpStatus.UNAUTHORIZED);
        }

        playerSalvoes.put("gpid", gamePlayer.getId());
        playerSalvoes.put("salvoes", gamePlayer.getSalvoesDTO());
        gamePlayer.updateGameState();
        return playerSalvoes;
    }


    /*=================================SET SALVOES==================================================*/
    /* Metodo que recibe por parametro una lista de salvos y si se cumplen las condiciones los asocia
    con el player indicado
     */
    @RequestMapping(path="/games/players/{gamePlayerId}/salvoes", method = RequestMethod.POST)
    public Object setSalvoes(@PathVariable("gamePlayerId") long gpId, @RequestBody Salvo salvo) {
        Player authenticatedPlayer = getAuthenticatedPlayer();

        if (authenticatedPlayer == null) {
            return this.createResponseEntity("error", "No esta autenticado", HttpStatus.UNAUTHORIZED);
        }

        GamePlayer gamePlayer = gamePlayerRepository.findOne(gpId);

        if (gamePlayer == null) {
            return this.createResponseEntity("error", "id de gameplayer incorrecto", HttpStatus.UNAUTHORIZED);
        }

        if (authenticatedPlayer.getId() != gamePlayer.getPlayer().getId()) {
            return this.createResponseEntity("error", "id de jugador no coincide con el juego", HttpStatus.FORBIDDEN);
        }

        //Verifique que el usuario esta logueado, el gameplayer id existe y es el correspondiente al usuario logueado


        //Verifico que el turno que le toca al usuario se corresponda con el turno del salvo
        Salvo newSalvo = new Salvo(gamePlayer,gamePlayer.getSalvoes().size()+1, salvo.getSalvoLocations());
        if (this.canPlaceSalvoes(gamePlayer, newSalvo)) {
            salvoRepository.save(newSalvo);
            gamePlayer.addSalvo(newSalvo);
            gamePlayer.setGameState(GameState.PLAY);
            gamePlayer.updateGameState();
            gamePlayerRepository.save(gamePlayer);

            return this.createResponseEntity("Mensaje", "Salvos agregados", HttpStatus.CREATED);
        } else {
            return this.createResponseEntity("error", "Ya se ingresaron los salvos del turno correspondiente", HttpStatus.FORBIDDEN);
        }


    }

    private boolean canPlaceSalvoes(GamePlayer gamePlayer, Salvo salvo) {
        if (gamePlayer.getSalvoes().isEmpty()) {
            return true;
        }
        if ((salvo.getTurn() == gamePlayer.getSalvoes().size() +1) && (!gamePlayer.repeatedSalvo(salvo.getSalvoLocations()))) {
            return true;
        }
        return false;
    }


    /*=====================================JOIN GAME===========================================*/

    /*Metodo que permite unirse a la partida ingresada por parametro*/
    @RequestMapping(path="/game/{nn}/players", method = RequestMethod.POST)
    public Object joinGame(@PathVariable("nn") Long gameId) {
        Player player = getAuthenticatedPlayer();

        if (player == null) {
            return new ResponseEntity<>(this.getResponseMapDTO("error", "No esta autenticado"), HttpStatus.UNAUTHORIZED);
        }

        Game game = gameRepository.findOne(gameId);

        if (game == null) {
            return new ResponseEntity<>(this.getResponseMapDTO("error", "Id de juego invalido"), HttpStatus.FORBIDDEN);
        }

        if (game.countGamePlayers() == 2) {
            return new ResponseEntity<>(this.getResponseMapDTO("error", "Juego completo"), HttpStatus.FORBIDDEN);
        }

        GamePlayer gamePlayer = new GamePlayer(player,game);
        gamePlayerRepository.save(gamePlayer);
        return new ResponseEntity<>(this.getResponseMapDTO("gpid", gamePlayer.getId()), HttpStatus.CREATED);
    }

    /*===============================================GET LEADERBOARD==================================================*/

    @RequestMapping("/leaderBoard")
    public List<Object> getLeaderBoard() {
        List<Player> players = playerRepository.findAll();
        List<Object> leaderBoard = players.stream().map(player -> player.getScoreHistoryDTO()).collect(Collectors.toList());
        return leaderBoard;
    }

    /*===============================================GET GAME BY ID===================================================*/

    /*metodo que devuelve el estado de un juego desde el punto de vista del usuario qeu se pasa por parametro
     * este metodo requiere que el usuario este autenticado y verifica que el id pasado por parametro corresponda
     * con el usuario que esta autenticado, en caso contrario informa al usuario que no puede acceder a esta
     * informacion*/
    @RequestMapping("/game_view/{nn}")
    public Object getGameById(@PathVariable("nn") Long gamePlayerId) {
        long authenticatedPlayerId = this.getAuthenticatedPlayer().getId();
        GamePlayer gamePlayer = gamePlayerRepository.findOne(gamePlayerId);

        //verifico que sea una partida en la cual se encuentra el usuario autenticado en la aplicacion
        if (gamePlayer.getPlayer().getId() ==  authenticatedPlayerId) {
                gamePlayer.updateGameState();
                if (gamePlayer.gameFinished() && (gamePlayer.getGame().getScores().size() != 2)) {
                    this.updateScores(gamePlayer.getGameState(), gamePlayer);
                }
                gamePlayerRepository.save(gamePlayer);
                return gamePlayer.getGameplayerPovDTO();
        } else {
            return new ResponseEntity<>(this.getResponseMapDTO("error", "no autorizado"), HttpStatus.UNAUTHORIZED);
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
            return new ResponseEntity<>(this.getResponseMapDTO("error","Nombre de usuario no ingresado"), HttpStatus.FORBIDDEN);
        }

        Player player = playerRepository.findByUserName(username);
        //Nombre de usuario existente
        if (player != null) {
            return new ResponseEntity<>(this.getResponseMapDTO("error","Ya existe un usuario con ese nombre"), HttpStatus.CONFLICT);
        }

        playerRepository.save(new Player(username,password));
        return new ResponseEntity<>(this.getResponseMapDTO("resultado: ","Usuario creado"), HttpStatus.CREATED);
    }


    /*==========================================================METODO PRIVADO========================================S*/

    private Map<String,Object> getResponseMapDTO(String clave, Object valor) {
        Map<String,Object> responseMapDTO = new LinkedHashMap<>();
        responseMapDTO.put(clave, valor);
        return responseMapDTO;
    }

    private Player getAuthenticatedPlayer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        } else {
            return playerRepository.findByUserName(authentication.getName());
        }
    }

    private ResponseEntity<Object> createResponseEntity(String tipoDeRespuesta, Object valor, HttpStatus httpStatus ) {
        Map<String,Object> responseMap = new LinkedHashMap<>();
        responseMap.put(tipoDeRespuesta, valor);
        return new ResponseEntity<>(responseMap, httpStatus);
    }

}
