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

        return gamesDTO;
    }

    @RequestMapping("/leaderBoard")
    public List<Object> getLeaderBoard() {
        List<Player> players = playerRepository.findAll();
        List<Object> leaderBoard = players.stream().map(player -> player.getScoreHistoryDTO()).collect(Collectors.toList());
        return leaderBoard;
    }


    @RequestMapping("/game_view/{nn}")
    public Object getGameById(@PathVariable("nn") Long gamePlayerId) {
        /*metodo que devuelve el estado de un juego desde el punto de vista del usuario qeu se pasa por parametro*/
        GamePlayer gamePlayer = gamePlayerRepository.findOne(gamePlayerId);
        return gamePlayer.getGameplayerPovDTO();
    }

    //Metodo para crear un jugador, recibe por parametro el nombre de usuario y el password
    //Si el usuario ya existe, o el nombre esta vacio devuelve un codigo de error
    @RequestMapping(path= "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> createPlayer(@RequestParam("email") String username,
                                               @RequestParam("password") String password) {
        //Nombre de usuario vacio
        if (username.isEmpty() ) {
            return new ResponseEntity<>(this.getResponseMapDTO("Nombre de usuario no ingresado"), HttpStatus.FORBIDDEN);
        }

        Player player = playerRepository.findByUserName(username);
        //Nombre de usuario existente
        if (player != null) {
            return new ResponseEntity<>(this.getResponseMapDTO("Ya existe un usuario con ese nombre"), HttpStatus.CONFLICT);
        }

        playerRepository.save(new Player(username,password));
        return new ResponseEntity<>(this.getResponseMapDTO("Usuario creado"), HttpStatus.CREATED);
    }

    private Map<String,Object> getResponseMapDTO(String mensaje) {
        Map<String,Object> responseMapDTO = new LinkedHashMap<>();
        responseMapDTO.put("error", mensaje);
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
