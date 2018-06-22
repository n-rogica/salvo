package com.accenture.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO )
    private long id;
    private String userName;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<GamePlayer>  gamePlayers = new HashSet<GamePlayer>();

    public Player(){}

    public Player(String userName) {
        this.userName = userName;
    }

    public String getUserName(){
        return this.userName;
    }

    public void addGamePlayer(GamePlayer gamePlayer){
        this.gamePlayers.add(gamePlayer);
    }


    public List<Game> getGames(){
        return this.gamePlayers.stream().map(game -> game.getGame()).collect(Collectors.toList());
    }


    @Override
    public String toString() {
        return  "username: " + this.userName;
    }

    public Map<String, Object> getPlayerDTO() {
        Map<String,Object>  playerDTO = new LinkedHashMap<String,Object>();
        playerDTO.put("id", this.id);
        playerDTO.put("email", this.userName);
        return playerDTO;
    }
}
