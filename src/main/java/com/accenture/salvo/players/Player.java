package com.accenture.salvo.players;

import com.accenture.salvo.games.Score;
import com.accenture.salvo.games.Game;
import com.accenture.salvo.games.GamePlayer;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO )
    private long id;
    private String userName;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<GamePlayer>  gamePlayers = new HashSet<>();

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<Score>  scores = new HashSet<>();

    public Player(){}

    public Player(String userName) {
        this.userName = userName;
    }

    public String getUserName(){
        return this.userName;
    }

    public void addGamePlayer(GamePlayer gamePlayer){
        this.gamePlayers.add(gamePlayer);
    }


    public List<Game> getGames(){
        return this.gamePlayers.stream().map(game -> game.getGame()).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return  "username: " + this.userName;
    }

    public Map<String, Object> getPlayerDTO() {
        Map<String,Object>  playerDTO = new LinkedHashMap<>();
        playerDTO.put("id", this.id);
        playerDTO.put("email", this.userName);
        return playerDTO;
    }

    public Object getScoreHistoryDTO() {
        Map<String,Object> scoreHistoryDTO = new LinkedHashMap<>();

        scoreHistoryDTO.put("name", this.userName);
        scoreHistoryDTO.put("score", this.getScoreResumeDTO());
        return scoreHistoryDTO;
    }

    private Object getScoreResumeDTO() {
        Map<String,Object> scoreResume = new LinkedHashMap<>();
        long acumWon = this.getWonGames();
        double acumTie = this.getTiedGames();
        double acumLost = this.getLostGames();
        double acumScore = 1 * acumWon + 0.5 * acumTie;

        scoreResume.put("total", acumScore);
        scoreResume.put("won", acumWon);
        scoreResume.put("lost", acumLost);
        scoreResume.put("tied", acumTie);
        return scoreResume;
    }

    private long getWonGames() {
        long acum = scores.stream().filter(score -> score.getScore() == 1).count();
        return acum;
    }

    private long getTiedGames() {
        long acum = scores.stream().filter(score -> score.getScore() == 0.5).count();
        return acum;
    }

    private double getLostGames() {
        long acum = scores.stream().filter(score -> score.getScore() == 0).count();
        return acum;
    }

    public long getId() {
        return this.id;
    }
}
