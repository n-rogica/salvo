package com.accenture.salvo;


import com.accenture.salvo.games.Game;
import com.accenture.salvo.games.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    GameRepository gameRepository;

    @Autowired
    GamePlayerRepository gamePlayerRepository;

    @RequestMapping("/games")
    /*metodo que devuelve un json con el detalle de todos los juegos*/
    public List<Object> getGameIds() {
        List<Game> games = gameRepository.findAll();
        return games.stream().map(Game::getGameDTO).collect(Collectors.toList());
    }


    @RequestMapping("game_view/{nn}")
    public Object getGameById(@PathVariable("nn") String gamePlayerId) {
        /*metodo que devuelve el estado de un juego desde el punto de vista del usuario qeu se pasa por parametro*/
        GamePlayer gamePlayer = gamePlayerRepository.findById(Long.parseLong(gamePlayerId));
        Game game = gamePlayer.getGame();
        List<Object> ships = gamePlayer.getGamePlayerShipsDTO();
        return game.getGamePovDTO(ships);
    }
}
