package com.accenture.salvo;

import org.hibernate.annotations.JoinColumnOrFormula;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    private Set<Ship> ships = new HashSet<>();


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
        Map<String,Object>  gamePlayerDTO = new LinkedHashMap<String,Object>();
        gamePlayerDTO.put("id", this.id);
        gamePlayerDTO.put("player", this.player.getPlayerDTO());
        gamePlayerDTO.put("ships", this.ships.stream().map(ship -> ship.getShipDTO()).collect(Collectors.toList()));
        return gamePlayerDTO;
    }

    public List<Object> getGamePlayerShipsDTO() {
        return this.ships.stream().map(ship -> ship.getShipDTO()).collect(Collectors.toList());
    }


}
