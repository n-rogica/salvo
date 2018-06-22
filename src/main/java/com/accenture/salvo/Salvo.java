package com.accenture.salvo;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;
    int turn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="GamePlayer_id")
    GamePlayer gamePlayer;

    @ElementCollection
    @Column(name = "salvoLocacions")
    List<String> salvoLocations;

    Salvo() {}

    Salvo(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;


    }
}
