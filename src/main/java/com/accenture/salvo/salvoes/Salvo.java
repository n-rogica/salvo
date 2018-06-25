package com.accenture.salvo.salvoes;

import com.accenture.salvo.GamePlayer;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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


    public Map<String,Object> getSalvoDTO() {
        Map<String,Object> salvoDTO = new LinkedHashMap<>();
        salvoDTO.put("turn", this.turn);
        salvoDTO.put("player", this.gamePlayer.getId());
        salvoDTO.put("salvoes", this.salvoLocations);
        return salvoDTO;
    }
}
