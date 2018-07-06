package com.accenture.salvo.model.ships;

import com.accenture.salvo.model.games.GamePlayer;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private ShipType shipType;

    @ElementCollection
    @Column(name="shipLocations")
    private List<String> shipLocations;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GamePlayer_id")
    private GamePlayer gamePlayer;

    public Ship() {}

    public Ship(ShipType shipType, GamePlayer gamePlayer, List<String> shipLocations) {
        this.shipType = shipType;
        this.gamePlayer = gamePlayer;
        this.shipLocations = shipLocations;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public void setShipType(String shipType) {
        this.shipType = ShipType.valueOf(shipType.toUpperCase());
    }

    public void setShipLocations(List<String> shipLocations) {
        this.shipLocations = shipLocations;
    }

    public GamePlayer getGamePlayer() {
        return this.gamePlayer;
    }


    public Map<String, Object> getShipDTO() {
        Map<String,Object>  shipDTO = new LinkedHashMap<>();
        shipDTO.put("type", this.shipType);
        shipDTO.put("locations", this.shipLocations);
        return shipDTO;
    }

    public ShipType getShipType() {
        return this.shipType;
    }

    public List<String> getShipLocations() {
        return this.shipLocations;
    }

    public boolean shipPieceHitted(String location) {
        return this.shipLocations.stream().anyMatch(shipLocation -> shipLocation == location);
    }

    public String getShipTypeAsString() {
        return this.shipType.name().toLowerCase();
    }
}
