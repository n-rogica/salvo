package com.accenture.salvo.model.games;

import com.accenture.salvo.model.players.Player;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private Double score;
    private Date finishDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    public Score(){}

    public Score(Double score, Game game, Player player) {
        this.score = score;
        if (this.score == null) {
            this.finishDate = game.getCreationDate();
            this.finishDate.toInstant().plusSeconds(1800);
        } else {
            this.finishDate = null;
        }
        this.game = game;
        this.player = player;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public boolean finishedGame() {
        return this.finishDate != null;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setFinishDate(Date date) {
        this.finishDate = date;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public double getScore() {
        return this.score;
    }

    public Game getGame() {
        return this.game;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Date getFinishDate() {
        return this.finishDate;
    }

    public Object getScoreDTO() {
        Map<String,Object> scoreDTO = new LinkedHashMap<>();
        scoreDTO.put("playerID", this.player.getId());
        scoreDTO.put("score", this.score);
        scoreDTO.put("finish date", this.finishDate);
        return scoreDTO;
    }
}
