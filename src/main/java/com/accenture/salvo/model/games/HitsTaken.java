package com.accenture.salvo.model.games;

import com.accenture.salvo.model.ships.ShipType;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class HitsTaken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    private int turn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection(fetch = FetchType.LAZY)
    private Map<String,Integer> hitsOnMyFleet = new LinkedHashMap<>();
    
    public HitsTaken() {}
    
    public HitsTaken(GamePlayer gamePlayer, int turn) {
        this.gamePlayer = gamePlayer;
        this.turn = turn;
        if (hitsOnMyFleet.isEmpty()) {
            for (ShipType shipType : ShipType.values()) {
                hitsOnMyFleet.put(shipType.toString().toLowerCase(), 0);
            }
        }
    }

    public HitsTaken(HitsTaken previousHitsTaken) {
        this.turn = previousHitsTaken.turn +1;
        this.gamePlayer = previousHitsTaken.gamePlayer;
        this.hitsOnMyFleet = new LinkedHashMap<>(previousHitsTaken.hitsOnMyFleet);
    }

    public Map<String,Integer> getHitsOnMyFleet() {
        return this.hitsOnMyFleet;
    }

    public void updateHitsOnMyFleet(String shipType) {
        this.hitsOnMyFleet.merge(shipType, 1, Integer::sum);
    }

    public int getTurn() {
        return this.turn;
    }

    public int getHitsTaken(String shipType) {
        return this.hitsOnMyFleet.get(shipType);
    }

    public int numberOfSunkShips() {
        int acum = 0;
        for (ShipType shipType: ShipType.values()) {
            if (shipType.getLenght() == this.hitsOnMyFleet.get(shipType.toString().toLowerCase())) {
                acum++;
            }
        }
        return acum;
    }
}
