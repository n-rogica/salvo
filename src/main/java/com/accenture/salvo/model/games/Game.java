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

    private static final String SELF = "self";
    private static final String OPPONENT = "opponent";

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

    private List<Map<String,Object>> processSalvoes(GamePlayer attacker, GamePlayer receiver) {
        List<Map<String,Object>> processedSalvoesDTO = new LinkedList<>();
        Map<String,Integer> shipsStatusMap = this.createShipsStatusMap();
        final boolean hideLastSalvo;

        if (attacker.getSalvoes().size() > receiver.getSalvoes().size()) {
            //si un jugador disparo y el otro no, no tengo que revelar el resultado
            //de ese salvo hasta que el otro haya disparado
            hideLastSalvo = true;
        } else {
            hideLastSalvo = false;
        }

        attacker.getSalvoes().stream().sorted(Comparator.comparingInt(Salvo::getTurn)).forEach(salvo -> {
            Map<String, Object> processedTurnDTO = new LinkedHashMap<>();
            processedTurnDTO.put("turn", salvo.getTurn());
            if (hideLastSalvo && (salvo.getTurn() == attacker.getSalvoes().size())) {
                processedTurnDTO.put("hitLocations", new ArrayList<>());
                processedTurnDTO.put("damages", new LinkedHashMap<>());
                processedTurnDTO.put("missed", -1);
                processedSalvoesDTO.add(processedTurnDTO);
            } else {
                processedTurnDTO.put("hitLocations", salvo.getSalvoLocations());
                process(salvo.getSalvoLocations(), receiver.getShips(), shipsStatusMap);
                processedTurnDTO.put("damages", new LinkedHashMap<>(shipsStatusMap));
                processedTurnDTO.put("missed", countMissedShots(salvo.getSalvoLocations().size(), shipsStatusMap));
                processedSalvoesDTO.add(processedTurnDTO);
                resetShipStatusMap(shipsStatusMap);
            }
        });
        return processedSalvoesDTO;
    }

    private long countMissedShots(int numberOfSalvos, Map<String,Integer> shipsStatusMap) {
        for (Map.Entry<String, Integer> shipStatus: shipsStatusMap.entrySet()) {
            if (shipStatus.getKey().contains("Hits")) {
                numberOfSalvos = numberOfSalvos - shipStatus.getValue();
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

    private void process(List<String> salvoLocations, Set<Ship> ships, Map<String, Integer> shipsStatusMap) {
        salvoLocations.forEach(salvoLocation -> checkShipHitted(salvoLocation,ships,shipsStatusMap));
    }

    private void checkShipHitted(String salvoLocation, Set<Ship> ships, Map<String,Integer> shipsStatusMap) {
        ships.forEach(ship -> {
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
            return !gp1.getShips().isEmpty() && !gp2.getShips().isEmpty();
        }
    }


    public GameResult getResult(long idOfRequestGP) {
        if (this.gamePlayers.size()!= 2) {
            return GameResult.TBD;
        }

        GamePlayer gamePlayerOfRequest = this.gamePlayers.stream().
                filter(gp -> gp.getId() == idOfRequestGP).findFirst().get();

        GamePlayer opponent = this.getOpponent(idOfRequestGP);
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
        /*
        if (this.allShipsSunk(gpOfRequest, opponent)) {
            if (this.allShipsSunk(opponent, gpOfRequest)) {
                return GameResult.TIE;
            } else {
                return GameResult.WON;
            }
        }

        if (this.allShipsSunk(opponent,gpOfRequest)) {
            return GameResult.LOST;
        }

        return GameResult.TBD;*/
    }

    private boolean allShipsSunk(GamePlayer attacker, GamePlayer receiver) {
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

    private Map<String,Object> getPlaceHolderHitsDTO() {
        Map<String,Object> hitsDTO = new LinkedHashMap<>();
        hitsDTO.put("self", new ArrayList<>());
        hitsDTO.put("opponent", new ArrayList<>());
        return  hitsDTO;
    }

    public void updateHitsTakenForSalvo(Long idOfAttacker, Salvo newSalvo) {
        GamePlayer receiver = this.getOpponent(idOfAttacker);
        receiver.updateHitsTaken(newSalvo);
    }
}
