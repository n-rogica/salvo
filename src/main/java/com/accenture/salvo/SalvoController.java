package com.accenture.salvo;


import com.accenture.salvo.games.Game;
import com.accenture.salvo.games.GamePlayer;
import com.accenture.salvo.games.GamePlayerRepository;
import com.accenture.salvo.games.GameRepository;
import com.accenture.salvo.players.Player;
import com.accenture.salvo.players.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping("/games")
    /*metodo que devuelve un json con el detalle de todos los juegos*/
    public Object getGameIds() {
        List<Game> games = gameRepository.findAll();
        return games.stream().map(Game::getGameDTO).collect(Collectors.toList());
    }

    @RequestMapping("/leaderBoard")
    public List<Object> getLeaderBoard() {
        List<Player> players = playerRepository.findAll();
        List<Object> leaderBoard = players.stream().map(player -> player.getScoreHistoryDTO()).collect(Collectors.toList());
        return leaderBoard;
    }


    @RequestMapping("game_view/{nn}")
    public Object getGameById(@PathVariable("nn") Long gamePlayerId) {
        /*metodo que devuelve el estado de un juego desde el punto de vista del usuario qeu se pasa por parametro*/
        GamePlayer gamePlayer = gamePlayerRepository.findOne(gamePlayerId);
        return gamePlayer.getGameplayerPovDTO();
    }
}
