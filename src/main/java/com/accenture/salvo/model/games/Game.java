package com.accenture.salvo.model.games;

import com.accenture.salvo.model.players.Player;
import com.accenture.salvo.model.salvoes.Salvo;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private final Date creationDate;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<GamePlayer> gamePlayers = new LinkedHashSet<>();

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<Score> scores = new LinkedHashSet<>();

    public Game(){
        this.creationDate = new Date();
    }

    public Game(Date date){
        this.creationDate = date;
    }

    public Date getCreationDate(){
        return this.creationDate;
    }

    @JsonIgnore
    public List<Player> getPlayers(){
        return this.gamePlayers.stream().map(GamePlayer::getPlayer).collect(Collectors.toList());
    }

    public Set<Score> getScores() {
        return this.scores;
    }

    public void addGamePlayer(GamePlayer gamePlayer){
        this.gamePlayers.add(gamePlayer);
    }


     public Map<String,Object> getGameDTO() {
        Map<String,Object>  gameDTO = new LinkedHashMap<>();
        gameDTO.put("id", this.id);
        gameDTO.put("created", this.creationDate);
        gameDTO.put("gamePlayers",gamePlayers.stream().map(GamePlayer::getGamePlayerDTO).collect(Collectors.toList()));
        gameDTO.put("scores", scores.stream().map(Score::getScoreDTO).collect(Collectors.toList()));
        return gameDTO;
     }

    public Object getGameSalvoesDTO() {
      return gamePlayers.stream().flatMap(gp ->
                gp.getSalvoes().stream().map(Salvo::getSalvoDTO)).collect(Collectors.toList());
    }

    public long getId() {
        return this.id;
    }

    @JsonIgnore
    public Object getGamePlayersDTO() {
        return this.gamePlayers.stream().map(GamePlayer::getGamePlayerDTO).collect(Collectors.toList());
    }

    public long countGamePlayers() {
        return this.gamePlayers.size();
    }

    public boolean bothPlayersHaveShips() {
        if (this.gamePlayers.size() != 2) {
            return false;
        } else {
            Iterator<GamePlayer> gpIt = this.gamePlayers.iterator();
            GamePlayer gp1 = gpIt.next();
            GamePlayer gp2 = gpIt.next();
            return !gp1.getShips().isEmpty() && !gp2.getShips().isEmpty();
        }
    }


    public GameResult getResult(long idOfRequestGP) {
        if (this.gamePlayers.size()!= 2) {
            return GameResult.TBD;
        }

        GamePlayer gamePlayerOfRequest = this.gamePlayers.stream().filter(gp -> gp.getId() == idOfRequestGP).findAny().orElse(null);

        GamePlayer opponent = this.getOpponent(idOfRequestGP);

        if (gamePlayerOfRequest == null || opponent == null) {
            return GameResult.TBD;
        }

        gamePlayerOfRequest.updateHitsTakenIfNeeded();
        opponent.updateHitsTakenIfNeeded();
        return this.getGameResult(gamePlayerOfRequest,opponent);
    }

    private GameResult getGameResult(GamePlayer gpOfRequest, GamePlayer opponent) {
        if (opponent.areAllShipsSunk()) {
            if (gpOfRequest.areAllShipsSunk()) {
                return GameResult.TIE;
            } else {
                return GameResult.WON;
            }
        }

        if (gpOfRequest.areAllShipsSunk()) {
            return GameResult.LOST;
        }
        return GameResult.TBD;
    }

    public GamePlayer getOpponent(long id) {
        Iterator<GamePlayer> gpIt = this.gamePlayers.iterator();
        GamePlayer gp1 = gpIt.next();
        GamePlayer gp2 = gpIt.next();
        if (gp1.getId() == id) {
            return gp2;
        } else {
            return gp1;
        }
    }

    public boolean salvosTurnMatch() {
        if (this.gamePlayers.size() != 2) {
            return false;

        }
        Iterator<GamePlayer> gpIt = this.gamePlayers.iterator();
        GamePlayer gp1 = gpIt.next();
        GamePlayer gp2 = gpIt.next();
        return gp1.getSalvoes().size() == gp2.getSalvoes().size();
    }



    public void updateHitsTakenForSalvo(Long idOfAttacker, Salvo newSalvo) {
        GamePlayer receiver = this.getOpponent(idOfAttacker);
        receiver.updateHitsTaken(newSalvo);
    }
}
