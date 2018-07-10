package com.accenture.salvo.model.salvoes;

import com.accenture.salvo.model.games.GamePlayer;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;
    int turn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="GamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name = "salvoLocacions")
    private List<String> salvoLocations;

    public Salvo() {}

    public Salvo(GamePlayer gamePlayer, int turn, List<String> locations) {
        this.gamePlayer = gamePlayer;
        this.turn = turn;
        this.salvoLocations = locations;
    }

    public int getTurn() {
        return this.turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public void setSalvoLocations(List<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }

    public List<String> getSalvoLocations() {
        return this.salvoLocations;
    }



    public Map<String,Object> getSalvoDTO() {
        Map<String,Object> salvoDTO = new LinkedHashMap<>();
        salvoDTO.put("turn", this.turn);
        salvoDTO.put("player", this.gamePlayer.getPlayer().getId());
        salvoDTO.put("locations", this.salvoLocations);
        return salvoDTO;
    }

    public boolean checkRepeatedLocations(List<String> salvoLocations) {
        for (String location: salvoLocations) {
            if (this.salvoLocations.contains(location)) {
                return true;
            }
        }
        return false;
    }
}
