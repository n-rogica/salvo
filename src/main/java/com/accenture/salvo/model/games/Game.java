package com.accenture.salvo.model.games;

import com.accenture.salvo.model.players.Player;
import com.accenture.salvo.model.salvoes.Salvo;
import com.accenture.salvo.model.ships.Ship;
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

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers = new HashSet<>();

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<Score> scores = new HashSet<>();

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
        return this.gamePlayers.stream().map(player -> player.getPlayer()).collect(Collectors.toList());
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
        gameDTO.put("gamePlayers",gamePlayers.stream().map(gp -> gp.getGamePlayerDTO()).collect(Collectors.toList()));
        gameDTO.put("scores", scores.stream().map(Score::getScoreDTO).collect(Collectors.toList()));
        return gameDTO;
     }

    public Object getGameSalvoesDTO() {
      return gamePlayers.stream().flatMap(gp ->
                gp.getSalvoes().stream().map(salvo -> salvo.getSalvoDTO())).collect(Collectors.toList());
    }

    public long getId() {
        return this.id;
    }

    @JsonIgnore
    public Object getGamePlayersDTO() {
        return this.gamePlayers.stream().map(gp -> gp.getGamePlayerDTO()).collect(Collectors.toList());
    }

    public long countGamePlayers() {
        return this.scores.stream().count();
    }

    public Map<String,Object> getHitsDTO(long idOfRequestPlayer) {
        Map<String,Object> hitsDTO = new LinkedHashMap<>();

        if (this.gamePlayers.size()!= 2) {
            hitsDTO.put("self", new ArrayList<>());
            hitsDTO.put("opponent", new ArrayList<>());
            return hitsDTO;
        }
        Iterator<GamePlayer> gpIt = this.gamePlayers.iterator();
        GamePlayer gp1 = gpIt.next();
        GamePlayer gp2 = gpIt.next();

        if (idOfRequestPlayer == gp1.getId() ) {
            hitsDTO.put("self", this.processSalvoes(gp2, gp1));
            hitsDTO.put("opponent", this.processSalvoes(gp1,gp2));
        } else {
            hitsDTO.put("self", this.processSalvoes(gp1, gp2));
            hitsDTO.put("opponent", this.processSalvoes(gp2,gp1));
        }
        return hitsDTO;
    }

    private List<Map<String,Object>> processSalvoes(GamePlayer attacker, GamePlayer receiver ) {
        List<Map<String,Object>> processedSalvoesDTO = new LinkedList<>();
        Map<String,Integer> shipsStatusMap = this.createShipsStatusMap();

        for (Salvo salvo: attacker.getSalvoes()) {
            Map<String, Object> processedTurnDTO = new LinkedHashMap<>();
            processedTurnDTO.put("turn", salvo.getTurn());
            processedTurnDTO.put("hitLocations", salvo.getSalvoLocations());
            process(salvo.getSalvoLocations(), receiver.getShips(), shipsStatusMap);
            processedTurnDTO.put("damages", new LinkedHashMap<>(shipsStatusMap));
            processedTurnDTO.put("missed", countMissedShots(salvo.getSalvoLocations().size(),shipsStatusMap));
            processedSalvoesDTO.add(processedTurnDTO);
            resetShipStatusMap(shipsStatusMap);
        }
        return processedSalvoesDTO;
    }

    private long countMissedShots(int numberOfSalvos, Map<String,Integer> shipsStatusMap) {
        for (String clave: shipsStatusMap.keySet()) {
            if (clave.contains("Hits")) {
                numberOfSalvos = numberOfSalvos - shipsStatusMap.get(clave);
            }
        }
        return numberOfSalvos;
    }

    private void resetShipStatusMap(Map<String,Integer> shipsStatusMap) {
        shipsStatusMap.put("carrierHits",0);
        shipsStatusMap.put("battleshipHits",0);
        shipsStatusMap.put("submarineHits",0);
        shipsStatusMap.put("destroyerHits",0);
        shipsStatusMap.put("patrolboatHits",0);
    }

    private Map<String,Integer> createShipsStatusMap() {
        Map<String,Integer> shipsStatusMap = new LinkedHashMap<>();
        shipsStatusMap.put("carrierHits",0);
        shipsStatusMap.put("battleshipHits",0);
        shipsStatusMap.put("submarineHits",0);
        shipsStatusMap.put("destroyerHits",0);
        shipsStatusMap.put("patrolboatHits",0);
        shipsStatusMap.put("carrier",0);
        shipsStatusMap.put("battleship",0);
        shipsStatusMap.put("submarine",0);
        shipsStatusMap.put("destroyer",0);
        shipsStatusMap.put("patrolboat",0);
        return shipsStatusMap;
    }

    private void process(List<String> salvoLocations, Set<Ship> ships, Map<String,Integer> shipsStatusMap) {
        salvoLocations.stream().forEach(salvoLocation -> checkShipHitted(salvoLocation,ships,shipsStatusMap));
    }

    private void checkShipHitted(String salvoLocation, Set<Ship> ships, Map<String,Integer> shipsStatusMap) {
        ships.stream().forEach(ship -> {
            if (ship.shipPieceHitted(salvoLocation)) {
                shipsStatusMap.merge(ship.getShipTypeAsString() + "Hits", 1, Integer::sum);
                shipsStatusMap.merge(ship.getShipTypeAsString(), 1, Integer::sum);
            }
        });
    }

    public boolean bothPlayersHaveShips() {
        if (this.gamePlayers.size() != 2) {
            return false;
        } else {
            Iterator<GamePlayer> gpIt = this.gamePlayers.iterator();
            GamePlayer gp1 = gpIt.next();
            GamePlayer gp2 = gpIt.next();
            if (gp1.getShips().isEmpty() || gp2.getShips().isEmpty()) {
                return false;
            }
            return true;
        }
    }


    public GameResult getResult(long idOfRequestGP) {
        if (this.gamePlayers.size()!= 2) {
            return GameResult.TBD;
        }
        Iterator<GamePlayer> gpIt = this.gamePlayers.iterator();
        GamePlayer gp1 = gpIt.next();
        GamePlayer gp2 = gpIt.next();
        if (gp1.getId() == idOfRequestGP) {
            return this.getGameResult(gp1,gp2);
        } else {
            return this.getGameResult(gp2,gp1);
        }

    }

    private GameResult getGameResult(GamePlayer gpOfRequest, GamePlayer opponent) {
        if (this.AllShipsSunk(gpOfRequest, opponent)) {
            if (this.AllShipsSunk(opponent, gpOfRequest)) {
                return GameResult.TIE;
            } else {
                return GameResult.WON;
            }
        }

        if (this.AllShipsSunk(opponent,gpOfRequest)) {
            return GameResult.LOST;
        }

        return GameResult.TBD;
    }

    public boolean AllShipsSunk(GamePlayer attacker, GamePlayer receiver) {
        int numberOfPossibleHits = receiver.getShips().stream().mapToInt(ship -> ship.getShipType().getLenght()).sum();
        int numberOfActualHits = 0;
        for (Salvo salvo: attacker.getSalvoes()) {
            for (Ship ship: receiver.getShips()) {
                numberOfActualHits += ship.getShipLocations().stream().
                        filter(location -> salvo.getSalvoLocations().contains(location)).count();
            }
        }
        return numberOfActualHits == numberOfPossibleHits;
    }

    public void setBothPlayersToPlay() {
        Iterator<GamePlayer> gpIt = this.gamePlayers.iterator();
        GamePlayer gp1 = gpIt.next();
        GamePlayer gp2 = gpIt.next();
        gp1.setGameState(GameState.WAIT);
        gp2.setGameState(GameState.WAIT);
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
}
