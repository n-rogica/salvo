package com.accenture.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
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
    @Column(name="locations")
    private List<String> locations;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GamePlayer_id")
    GamePlayer gamePlayer;


    public Ship() {}

    public Ship(ShipType shipType, GamePlayer gamePlayer, List<String> locations) {
        this.shipType = shipType;
        this.gamePlayer = gamePlayer;
        this.locations = locations;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public void setShipType(ShipType shipType) {
        this.shipType = shipType;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public GamePlayer getGamePlayer() {
        return this.gamePlayer;
    }


    public Map<String, Object> getShipDTO() {
        Map<String,Object>  shipDTO = new LinkedHashMap<String,Object>();
        shipDTO.put("type", this.shipType);
        shipDTO.put("locations", this.locations);
        return shipDTO;
    }




    public ShipType getShipType() {
        return this.shipType;
    }
}
