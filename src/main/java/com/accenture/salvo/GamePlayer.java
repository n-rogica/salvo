package com.accenture.salvo;

import com.accenture.salvo.games.Game;
import com.accenture.salvo.players.Player;
import com.accenture.salvo.salvoes.Salvo;
import com.accenture.salvo.ships.Ship;

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

    public Map<String,Object> getGamePlayerDTO() {
        Map<String,Object>  gamePlayerDTO = new LinkedHashMap<>();
        gamePlayerDTO.put("id", this.id);
        gamePlayerDTO.put("player", this.player.getPlayerDTO());
        gamePlayerDTO.put("ships", this.ships.stream().map(ship -> ship.getShipDTO()).collect(Collectors.toList()));
        return gamePlayerDTO;
    }

    public List<Object> getGamePlayerShipsDTO() {
        return this.ships.stream().map(ship -> ship.getShipDTO()).collect(Collectors.toList());
    }

    public List<Object> getSalvoesDTO() {
        return this.salvoes.stream().map(salvo -> salvo.getSalvoDTO()).collect(Collectors.toList());
    }


}
