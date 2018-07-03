package com.accenture.salvo.model.games;

import com.accenture.salvo.model.players.Player;
import com.accenture.salvo.model.salvoes.Salvo;
import com.accenture.salvo.model.ships.Ship;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    private Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    private Set<Salvo> salvoes = new HashSet<>();


    private Date joinDate;

    public GamePlayer(){}

    public GamePlayer(Player player, Game game){
        this.player = player;
        this.game = game;
        this.joinDate = new Date();
    }

    public Player getPlayer(){
        return this.player;
    }

    public Game getGame(){
        return this.game;
    }

    public Long getId() {
        return this.id;
    }

    public Date getJoinDate(){
        return this.joinDate;
    }

    public Set<Ship> getShips() {
        return this.ships;
    }

    public void addShip(Ship ship) {
        this.ships.add(ship);
    }

    public Set<Salvo> getSalvoes() {
        return this.salvoes;
    }

    @JsonIgnore
    public Map<String,Object> getGamePlayerDTO() {
        Map<String,Object>  gamePlayerDTO = new LinkedHashMap<>();
        gamePlayerDTO.put("id", this.id);
        gamePlayerDTO.put("player", this.player.getPlayerWithMailDTO());
        gamePlayerDTO.put("joinDate", this.joinDate);
        return gamePlayerDTO;
    }

    public Map<String,Object> getGameplayerPovDTO() {
        Map<String,Object>  gamePlayerDTO = new LinkedHashMap<>();
        gamePlayerDTO.put("id", this.game.getId());
        gamePlayerDTO.put("created", this.game.getCreationDate());
        gamePlayerDTO.put("gamePlayers", this.game.getGamePlayersDTO());
        gamePlayerDTO.put("ships", this.getGamePlayerShipsDTO());
        gamePlayerDTO.put("salvoes", this.game.getGameSalvoesDTO());
        return gamePlayerDTO;

    }

    public List<Object> getGamePlayerShipsDTO() {
        return this.ships.stream().map(ship -> ship.getShipDTO()).collect(Collectors.toList());
    }

    public Object getSalvoesDTO() {
        return this.salvoes.stream().map(salvo -> salvo.getSalvoDTO()).collect(Collectors.toList());
    }
}
