package com.accenture.salvo.model.games;

import com.accenture.salvo.model.ships.ShipType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class HitsTaken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    private int turn;
    
    private Map<String,Integer> hitsOnMyFleet = new LinkedHashMap<>();
    
    public HitsTaken() {}
    
    public HitsTaken(int turn) {
        this.turn = turn;
        for (ShipType shipType: ShipType.values()) {
            hitsOnMyFleet.put(shipType.toString(),0);
        }
    }

    public void updateHitsOnMyFleet(String shipType) {
        this.hitsOnMyFleet.merge(shipType, 1, Integer::sum);
    }

    public int getHitsTaken(String shipType) {
        return this.hitsOnMyFleet.get(shipType);
    }

    public int numberOfSunkShips() {
        int acum = 0;
        for (ShipType shipType: ShipType.values()) {
            if (shipType.getLenght() == this.hitsOnMyFleet.get(shipType.toString())) {
                acum++;
            }
        }
        return acum;
    }
}
