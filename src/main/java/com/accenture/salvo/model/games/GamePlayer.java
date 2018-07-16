package com.accenture.salvo.model.games;

import com.accenture.salvo.model.players.Player;
import com.accenture.salvo.model.salvoes.Salvo;
import com.accenture.salvo.model.ships.Ship;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Salvo> salvoes = new LinkedHashSet<>();

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<HitsTaken> hitsTaken = new HashSet<>();
    private Date joinDate;
    private GameState gameState;

    public GamePlayer(){}

    public GamePlayer(Player player, Game game){
        this.player = player;
        this.game = game;
        this.gameState = GameState.PLACESHIPS;
        this.joinDate = new Date();
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer(){
        return this.player;
    }

    public Game getGame(){
        return this.game;
    }

    public Long getId() {
        return this.id;
    }

    public Date getJoinDate(){
        return this.joinDate;
    }

    public Set<Ship> getShips() {
        return this.ships;
    }

    public void addShip(Ship ship) {
        this.ships.add(ship);
    }

    public Set<Salvo> getSalvoes() {
        return this.salvoes;
    }

    public boolean hasNoShips() {
        return this.ships.isEmpty();
    }

    public HitsTaken getHitsTakenForTurn(int turn) {
        if (this.hitsTaken == null) {
            return null;
        } else {
            return this.hitsTaken.stream().filter(ht -> ht.getTurn() == turn).findAny().orElse(null);
        }
    }

    public void addShips(List<Ship> ships) {
        ships.forEach(ship -> this.ships.add(new Ship(ship.getShipType(), this,
                    ship.getShipLocations())));
    }

    public void updateHitsTaken(Salvo attackerSalvo) {
        HitsTaken newHitsTaken;
        if (this.hitsTaken.isEmpty()) {
            newHitsTaken = new HitsTaken(this, attackerSalvo.getTurn());
        } else {
            HitsTaken previousHitsTaken = this.hitsTaken.stream().
                    filter(ht -> ht.getTurn() == attackerSalvo.getTurn()-1).findAny().orElse(null);
            newHitsTaken = new HitsTaken(previousHitsTaken);
        }

        attackerSalvo.getSalvoLocations().forEach(location -> {
            for (Ship ship: this.getShips()) {
                if (ship.getShipLocations().contains(location)) {
                    newHitsTaken.updateHitsOnMyFleet(ship.getShipTypeAsString());
                }
            }
        });
        this.hitsTaken.add(newHitsTaken);
    }

    @JsonIgnore
    public Map<String,Object> getGamePlayerDTO() {
        Map<String,Object>  gamePlayerDTO = new LinkedHashMap<>();
        gamePlayerDTO.put("id", this.id);
        gamePlayerDTO.put("player", this.player.getPlayerWithMailDTO());
        gamePlayerDTO.put("joinDate", this.joinDate);
        return gamePlayerDTO;
    }

    public boolean repeatedSalvo(List<String> salvoLocations) {
        for (Salvo salvo: this.salvoes) {
            if (salvo.checkRepeatedLocations(salvoLocations)) {
                return true;
            }
        }
        return false;
    }

    public List<Object> getGamePlayerShipsDTO() {
        return this.ships.stream().map(Ship::getShipDTO).collect(Collectors.toList());
    }

    public Object getSalvoesDTO() {
        return this.salvoes.stream().map(Salvo::getSalvoDTO).collect(Collectors.toList());
    }

    public void addSalvo(Salvo salvo) {
        this.salvoes.add(salvo);
        this.getGame().updateHitsTakenForSalvo(this.id,salvo);
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void updateGameState() {

        if (this.gameState == GameState.PLACESHIPS && !this.ships.isEmpty()) {
            this.gameState = GameState.WAIT;
        }

        if ((this.gameState == GameState.WAIT) && (this.game.bothPlayersHaveShips())
                && (this.game.salvosTurnMatch())) {
            //los dos jugadores tiraron salvos y estan esperando el resultado
            //verifico si el jugador gano/perdio/empato
            GameResult gameResult = this.game.getResult(this.id);
            if (gameResult != GameResult.TBD) {
                //termino la partida
                this.gameState = GameState.valueOf(gameResult.toString());
                return;
            }

            //la partida no termino, ambos dispararon para el turno correspondiente y colocaron sus barcos
            if (this.gameState != GameState.PLACESHIPS) {
                this.gameState = GameState.PLAY;
            }
        }
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public boolean gameFinished() {
        switch (gameState) {
            case WON:
                return true;
            case TIE:
                return true;
            case LOST:
                return true;
            default:
                return false;
        }
    }

    public Player getOpponent() {
        return this.game.getOpponent(this.id).getPlayer();
    }

    public GamePlayer getGpOpponent() {
        return this.game.getOpponent(this.id);
    }

    public void updateHitsTakenIfNeeded() {

        if ((this.hitsTaken.isEmpty()) && (!this.game.getOpponent(this.id).getSalvoes().isEmpty())) {
            GamePlayer opponent = this.game.getOpponent(this.id);
            opponent.getSalvoes().stream().sorted(Comparator.comparingInt(Salvo::getTurn)).forEach(this::updateHitsTaken);
        }

    }

    public boolean areAllShipsSunk() {
        if (this.hitsTaken.isEmpty()) {
            return false;
        }
        int numberOfLastTurn = this.getSalvoes().size();
        HitsTaken hitsTakenForTurn = this.getHitsTakenForTurn(numberOfLastTurn);
        return (hitsTakenForTurn.numberOfSunkShips() != 0) &&
                (hitsTakenForTurn.numberOfSunkShips() == this.getShips().size());
    }
}
