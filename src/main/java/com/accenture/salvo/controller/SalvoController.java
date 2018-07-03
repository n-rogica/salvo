package com.accenture.salvo.controller;


import com.accenture.salvo.model.games.Game;
import com.accenture.salvo.model.games.GamePlayer;
import com.accenture.salvo.repository.GamePlayerRepository;
import com.accenture.salvo.repository.GameRepository;
import com.accenture.salvo.model.players.Player;
import com.accenture.salvo.repository.PlayerRepository;
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
    GamePlayerRepository gamePlayerRepository;

    @Autowired
    PlayerRepository playerRepository;

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

        return gamesDTO;    }

    /*metodo que verifica si el usuario esta autenticado y crea un nuevo juego*/
    @RequestMapping(path= "/games", method = RequestMethod.POST)
    public Object createGame() {
        Player authenticatedPlayer = this.getAuthenticatedPlayer();
        if (authenticatedPlayer == null) {
            return new ResponseEntity<>(this.getResponseMapDTO("error", "No esta autenticado"), HttpStatus.UNAUTHORIZED);
        } else {
            Game game = new Game();
            GamePlayer gamePlayer = new GamePlayer(authenticatedPlayer,game);
            gameRepository.save(game);
            gamePlayerRepository.save(gamePlayer);
            return new ResponseEntity<>(this.getResponseMapDTO("gpid", gamePlayer.getId()), HttpStatus.CREATED);
        }
    }

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

    @RequestMapping("/leaderBoard")
    public List<Object> getLeaderBoard() {
        List<Player> players = playerRepository.findAll();
        List<Object> leaderBoard = players.stream().map(player -> player.getScoreHistoryDTO()).collect(Collectors.toList());
        return leaderBoard;
    }

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
            return gamePlayer.getGameplayerPovDTO();
        } else {
            return new ResponseEntity<>(this.getResponseMapDTO("error", "no autorizado"), HttpStatus.UNAUTHORIZED);
        }
    }

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
}
