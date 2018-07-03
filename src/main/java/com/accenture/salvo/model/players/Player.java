package com.accenture.salvo.model.players;

import com.accenture.salvo.model.games.GamePlayer;
import com.accenture.salvo.model.games.Score;
import com.accenture.salvo.model.games.Game;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO )
    private long id;
    private String userName;
    private String password;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<GamePlayer>  gamePlayers = new HashSet<>();

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<Score>  scores = new HashSet<>();

    public Player(){}

    public Player(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName(){
        return this.userName;
    }

    public void addGamePlayer(GamePlayer gamePlayer){
        this.gamePlayers.add(gamePlayer);
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
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
        playerDTO.put("name", this.userName);
        return playerDTO;
    }

    public Map<String, Object> getPlayerWithMailDTO() {
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
        double acumScore = scores.stream().filter(score -> score.getScore() != -1).mapToDouble(score -> score.getScore()).sum();

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
