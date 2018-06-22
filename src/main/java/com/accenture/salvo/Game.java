package com.accenture.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.tomcat.jni.Local;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private Date creationDate;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers = new HashSet<GamePlayer>();

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
        Map<String,Object>  gameDTO = new LinkedHashMap<String,Object>();
        gameDTO.put("id", this.id);
        gameDTO.put("created", this.creationDate);
        gameDTO.put("gamePlayers",gamePlayers.stream().map(gp -> gp.getGamePlayerDTO()).collect(Collectors.toList()));
        return gameDTO;
     }

    public Map<String,Object> getGamePovDTO(List<Object> ships) {
        //DTO que representa el estado de la partida desde el punto de vista de un jugador (el owner de los ships)
        Map<String,Object>  gameDTO = new LinkedHashMap<String,Object>();
        gameDTO.put("id", this.id);
        gameDTO.put("created", this.creationDate);
        gameDTO.put("gamePlayers",gamePlayers.stream().map(gp -> gp.getGamePlayerDTO()).collect(Collectors.toList()));
        gameDTO.put("ships", ships);
        return gameDTO;
    }





}
