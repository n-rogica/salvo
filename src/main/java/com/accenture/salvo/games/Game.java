package com.accenture.salvo.games;

import com.accenture.salvo.GamePlayer;
import com.accenture.salvo.Score;
import com.accenture.salvo.players.Player;
import com.accenture.salvo.salvoes.Salvo;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private final Date creationDate;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers = new HashSet<>();

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<Score> score = new HashSet<>();



    public Game(){
        this.creationDate = new Date();
    }

    public Game(Date date){
        this.creationDate = date;
    }


    public Date getCreationDate(){
        return this.creationDate;
    }

    @JsonIgnore
    public List<Player> getPlayers(){
        return this.gamePlayers.stream().map(player -> player.getPlayer()).collect(Collectors.toList());
    }

    public void addGamePlayer(GamePlayer gamePlayer){
        this.gamePlayers.add(gamePlayer);
    }


     public Map<String,Object> getGameDTO() {
        Map<String,Object>  gameDTO = new LinkedHashMap<>();
        gameDTO.put("id", this.id);
        gameDTO.put("created", this.creationDate);
        gameDTO.put("gamePlayers",gamePlayers.stream().map(gp -> gp.getGamePlayerDTO()).collect(Collectors.toList()));
        return gameDTO;
     }

    public Map<String,Object> getGamePovDTO(List<Object> ships) {
        //DTO que representa el estado de la partida desde el punto de vista de un jugador (el owner de los ships)
        Map<String,Object>  gameDTO = new LinkedHashMap<>();
        gameDTO.put("id", this.id);
        gameDTO.put("created", this.creationDate);
        gameDTO.put("gamePlayers",gamePlayers.stream().map(gp -> gp.getGamePlayerDTO()).collect(Collectors.toList()));
        gameDTO.put("ships", ships);

        //para cada gameplayer su set de salvos y para cada salvo su dto
        gameDTO.put("salvoes", gamePlayers.stream().flatMap(gp ->
                gp.getSalvoes().stream().map(salvo -> salvo.getSalvoDTO())).collect(Collectors.toList()));

        return gameDTO;
    }





}
