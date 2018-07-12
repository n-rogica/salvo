package com.accenture.salvo;

import com.accenture.salvo.model.games.Game;
import com.accenture.salvo.model.games.GamePlayer;
import com.accenture.salvo.model.games.Score;
import com.accenture.salvo.model.players.Player;
import com.accenture.salvo.repository.PlayerRepository;
import com.accenture.salvo.repository.GamePlayerRepository;
import com.accenture.salvo.repository.GameRepository;
import com.accenture.salvo.repository.ScoreRepository;
import com.accenture.salvo.model.salvoes.Salvo;
import com.accenture.salvo.repository.SalvoRepository;
import com.accenture.salvo.model.ships.Ship;
import com.accenture.salvo.repository.ShipRepository;
import com.accenture.salvo.model.ships.ShipType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;


import java.util.*;

@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {



	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository,
									  GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository,
									  SalvoRepository salvoRepository, ScoreRepository scoreRepository){

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());

		/* =================== PLAYERS =================== */
		Player jbauer = new Player("j.bauer@ctu.gov", "24");
		Player cobrian = new Player("c.obrian@ctu.gov", "42");
		Player kbauer = new Player("kim_bauer@gmail.com", "kb");
		Player talmeida = new Player("t.almeida@cut.gov", "mole");



		/* =================== GAMES ===================== */
		Game gameBauerObrian = new Game();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		Game gameBauerObrian2 = new Game(cal.getTime());
		cal.add(Calendar.HOUR_OF_DAY, 1);
		Game gameObrianAlmeida = new Game(cal.getTime());
		cal.add(Calendar.HOUR_OF_DAY, 1);
		Game gameObrianBauer = new Game(cal.getTime());
		cal.add(Calendar.HOUR_OF_DAY, 1);
		Game gameAlmeidaBauer = new Game(cal.getTime());
		cal.add(Calendar.HOUR_OF_DAY, 1);
		Game gameKBauer = new Game(cal.getTime());
		cal.add(Calendar.HOUR_OF_DAY, 1);
		Game gameAlmeida = new Game(cal.getTime());
		cal.add(Calendar.HOUR_OF_DAY, 1);
		Game gameKbaueAlmeida = new Game(cal.getTime());

		/* =================== GAME PLAYERS =================== */
		GamePlayer g1pBauergp1BauerObrian = new GamePlayer(jbauer,gameBauerObrian);
		GamePlayer g1pObriangp2BauerObrian = new GamePlayer(cobrian,gameBauerObrian);

		GamePlayer g2pBauergp1BauerObrian2 = new GamePlayer(jbauer,gameBauerObrian2);
		GamePlayer g2pObraingp2BauerObrian2 = new GamePlayer(cobrian,gameBauerObrian2);

		GamePlayer g3pObriangp1ObrianAlmeida = new GamePlayer(cobrian, gameObrianAlmeida);
		GamePlayer g3pAlmeidagp2ObrianAlmeida = new GamePlayer(talmeida, gameObrianAlmeida);

		GamePlayer g4pObriangp1ObrianBauer = new GamePlayer(cobrian, gameObrianBauer);
		GamePlayer g4pBauergp2ObrianBauer = new GamePlayer(jbauer, gameObrianBauer);

		GamePlayer g5pAlmeidagp1AlmeidaBauer = new GamePlayer(talmeida, gameAlmeidaBauer);
		GamePlayer g5pBauergp2AlmeidaBauer = new GamePlayer(jbauer, gameAlmeidaBauer);

		GamePlayer g6pKbauergp1Kbauer = new GamePlayer(kbauer, gameKBauer);

		GamePlayer g7pAlmeidagp1Almeida = new GamePlayer(talmeida, gameAlmeida);

		GamePlayer g8pKbauergp1KbauerAlmeida = new GamePlayer(kbauer, gameKbaueAlmeida);
		GamePlayer g8pAlmeidagp2KbauerAlmeida = new GamePlayer(talmeida, gameKbaueAlmeida);


		/* =================== SHIPS =================== */
		String[] locations = new String[]{"H2","H3","H4"};
		List<String> arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship1 = new Ship(ShipType.DESTROYER, g1pBauergp1BauerObrian, arrayLocations);

		locations = new String[]{"E1", "F1", "G1"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship2 = new Ship(ShipType.SUBMARINE, g1pBauergp1BauerObrian, arrayLocations);

		locations = new String[]{"B4", "B5"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship3 = new Ship(ShipType.PATROLBOAT, g1pBauergp1BauerObrian, arrayLocations);

		locations = new String[]{"B5", "C5", "D5"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship4 = new Ship(ShipType.DESTROYER, g1pObriangp2BauerObrian, arrayLocations);

		locations = new String[]{"F1", "F2"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship5 = new Ship(ShipType.PATROLBOAT, g1pObriangp2BauerObrian, arrayLocations);

		locations = new String[]{"B5", "C5", "D5"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship6 = new Ship(ShipType.DESTROYER, g2pBauergp1BauerObrian2, arrayLocations);

		locations = new String[]{"C6","C7"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship7 = new Ship(ShipType.PATROLBOAT, g2pBauergp1BauerObrian2, arrayLocations);

		locations = new String[]{"A2", "A3", "A4"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship8 = new Ship(ShipType.SUBMARINE, g2pObraingp2BauerObrian2, arrayLocations);

		locations = new String[]{"G6","H6"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship9 = new Ship(ShipType.PATROLBOAT, g2pObraingp2BauerObrian2, arrayLocations);

		locations = new String[]{"B5","C5","D5"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship10 = new Ship(ShipType.DESTROYER, g3pObriangp1ObrianAlmeida, arrayLocations);

		locations = new String[]{"C6", "C7"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship11 = new Ship(ShipType.PATROLBOAT, g3pObriangp1ObrianAlmeida, arrayLocations);

		locations = new String[]{"A2","A3","A4"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship12 = new Ship(ShipType.SUBMARINE, g3pAlmeidagp2ObrianAlmeida, arrayLocations);

		locations = new String[]{"G6","H6"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship13 = new Ship(ShipType.PATROLBOAT, g3pAlmeidagp2ObrianAlmeida, arrayLocations);

		locations = new String[]{"B5","C5","D5"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship14 = new Ship(ShipType.DESTROYER, g4pObriangp1ObrianBauer, arrayLocations);

		locations = new String[]{"C6","C7"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship15 = new Ship(ShipType.PATROLBOAT, g4pObriangp1ObrianBauer, arrayLocations);

		locations = new String[]{"A2","A3","A4"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship16 = new Ship(ShipType.SUBMARINE, g4pBauergp2ObrianBauer, arrayLocations);

		locations = new String[]{"G6","H6"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship17 = new Ship(ShipType.PATROLBOAT, g4pBauergp2ObrianBauer, arrayLocations);

		locations = new String[]{"B5","C5","D5"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship18 = new Ship(ShipType.DESTROYER, g5pAlmeidagp1AlmeidaBauer, arrayLocations);

		locations = new String[]{"C6","C7"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship19 = new Ship(ShipType.PATROLBOAT, g5pAlmeidagp1AlmeidaBauer, arrayLocations);

		locations = new String[]{"A2","A3","A4"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship20 = new Ship(ShipType.SUBMARINE, g5pBauergp2AlmeidaBauer, arrayLocations);

		locations = new String[]{"G6","H6"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship21 = new Ship(ShipType.PATROLBOAT, g5pBauergp2AlmeidaBauer, arrayLocations);

		locations = new String[]{"B5","C5","D5"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship22 = new Ship(ShipType.DESTROYER, g6pKbauergp1Kbauer, arrayLocations);

		locations = new String[]{"C6","D7"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship23 = new Ship(ShipType.PATROLBOAT, g6pKbauergp1Kbauer, arrayLocations);

		locations = new String[]{"B5","C5","D5"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship24 = new Ship(ShipType.DESTROYER, g8pKbauergp1KbauerAlmeida, arrayLocations);

		locations = new String[]{"C6","C7"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship25 = new Ship(ShipType.PATROLBOAT, g8pKbauergp1KbauerAlmeida, arrayLocations);

		locations = new String[]{"A2","A3","A4"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship26 = new Ship(ShipType.SUBMARINE, g8pAlmeidagp2KbauerAlmeida, arrayLocations);

		locations = new String[]{"G6","H6"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship27 = new Ship(ShipType.PATROLBOAT, g8pAlmeidagp2KbauerAlmeida, arrayLocations);




        /* =================== SALVOES =================== */
        locations = new String[]{"B5","C5","F1"};
        arrayLocations = new ArrayList<>(Arrays.asList(locations));
        Salvo salvoG1T1P1 = new Salvo(g1pBauergp1BauerObrian, 1, arrayLocations);

        locations = new String[]{"B4","B5","B6"};
        arrayLocations = new ArrayList<>(Arrays.asList(locations));
        Salvo salvoG1T1P2 = new Salvo(g1pObriangp2BauerObrian, 1, arrayLocations);

		locations = new String[]{"F2","D5"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Salvo salvoG1T2P1 = new Salvo(g1pBauergp1BauerObrian, 2, arrayLocations);

		locations = new String[]{"E1","H3","A2"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Salvo salvoG1T2P2 = new Salvo(g1pObriangp2BauerObrian, 2, arrayLocations);

		locations = new String[]{"A2","A4","A6"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Salvo salvoG2T1P1 = new Salvo(g2pBauergp1BauerObrian2, 1, arrayLocations);

		locations = new String[]{"B5","D5","C7"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Salvo salvoG2T1P2 = new Salvo(g2pObraingp2BauerObrian2, 1, arrayLocations);

		locations = new String[]{"A3","H6"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Salvo salvoG2T2P1 = new Salvo(g2pBauergp1BauerObrian2, 2, arrayLocations);

		locations = new String[]{"C5","C6"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Salvo salvoG2T2P2 = new Salvo(g2pObraingp2BauerObrian2, 2, arrayLocations);

		locations = new String[]{"G6","H6","A4"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Salvo salvoG3T1P1 = new Salvo(g3pObriangp1ObrianAlmeida, 1, arrayLocations);

		locations = new String[]{"H1","H2","H3"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Salvo salvoG3T1P2 = new Salvo(g3pAlmeidagp2ObrianAlmeida, 1, arrayLocations);

		locations = new String[]{"A2","A3","D8"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Salvo salvoG3T2P1 = new Salvo(g3pObriangp1ObrianAlmeida, 2, arrayLocations);

		locations = new String[]{"E1","F2","G3"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Salvo salvoG3T2P2 = new Salvo(g3pAlmeidagp2ObrianAlmeida, 2, arrayLocations);

		locations = new String[]{"A3","A4","F7"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Salvo salvoG4T1P1 = new Salvo(g4pObriangp1ObrianBauer, 1, arrayLocations);

		locations = new String[]{"B5","C6","H1"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Salvo salvoG4T1P2 = new Salvo(g4pBauergp2ObrianBauer, 1, arrayLocations);

		locations = new String[]{"A2","G6","H6"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Salvo salvoG4T2P1 = new Salvo(g4pObriangp1ObrianBauer, 2, arrayLocations);

		locations = new String[]{"C5","C7","D5"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Salvo salvoG4T2P2 = new Salvo(g4pBauergp2ObrianBauer, 2, arrayLocations);

		locations = new String[]{"A1","A2","A3"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Salvo salvoG5T1P1 = new Salvo(g5pAlmeidagp1AlmeidaBauer, 1, arrayLocations);

		locations = new String[]{"B5","B6","C7"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Salvo salvoG5T1P2 = new Salvo(g5pBauergp2AlmeidaBauer, 1, arrayLocations);

		locations = new String[]{"G6","G7","G8"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Salvo salvoG5T2P1 = new Salvo(g5pAlmeidagp1AlmeidaBauer, 2, arrayLocations);

		locations = new String[]{"C6","D6","E6"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Salvo salvoG5T2P2 = new Salvo(g5pBauergp2AlmeidaBauer, 2, arrayLocations);

		locations = new String[]{"H1","H8"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Salvo salvoG5T3P2 = new Salvo(g5pBauergp2AlmeidaBauer, 3, arrayLocations);



		/* =================== GAMES =================== */

		Score scoreGame1Bauer = new Score(1.0,gameBauerObrian, jbauer);
		Score scoreGame1Obrian = new Score(0.0,gameBauerObrian, cobrian);

		Score scoreGame2Bauer = new Score(0.5,gameBauerObrian2, jbauer);
		Score scoreGame2Obrian = new Score(0.5,gameBauerObrian2, cobrian);

		Score scoreGame3Obrian = new Score(1.0,gameObrianAlmeida,cobrian);
		Score scoreGame3Almeida = new Score(0.0,gameObrianAlmeida, talmeida);

		Score scoreGame4Obrian = new Score(0.5,gameObrianBauer,cobrian);
		Score scoreGame4Bauer = new Score(0.5,gameObrianBauer, jbauer);

		Score scoreGame5Almeida = new Score(null,gameAlmeidaBauer,talmeida);
		Score scoreGame5Bauer = new Score(null,gameAlmeidaBauer, jbauer);

		Score scoreGame6Kbauer = new Score(null, gameKBauer, kbauer);

		Score scoreGame7Almeida = new Score(null, gameAlmeida, talmeida);

		Score scoreGame8Kbauer = new Score(null, gameKbaueAlmeida, kbauer);
		Score scoreGame8Almeida = new Score(null, gameKbaueAlmeida, talmeida);



		return args -> {
			playerRepository.save(jbauer);
			playerRepository.save(cobrian);
			playerRepository.save(talmeida);
			playerRepository.save(kbauer);
			gameRepository.save(gameBauerObrian);
			gameRepository.save(gameBauerObrian2);
			gameRepository.save(gameObrianAlmeida);
			gameRepository.save(gameObrianBauer);
			gameRepository.save(gameAlmeidaBauer);
			gameRepository.save(gameKBauer);
			gameRepository.save(gameAlmeida);
			gameRepository.save(gameKbaueAlmeida);

			gamePlayerRepository.save(g1pBauergp1BauerObrian);
            gamePlayerRepository.save(g1pObriangp2BauerObrian);

            gamePlayerRepository.save(g2pBauergp1BauerObrian2);
			gamePlayerRepository.save(g2pObraingp2BauerObrian2);

			gamePlayerRepository.save(g3pObriangp1ObrianAlmeida);
			gamePlayerRepository.save(g3pAlmeidagp2ObrianAlmeida);

			gamePlayerRepository.save(g4pObriangp1ObrianBauer);
			gamePlayerRepository.save(g4pBauergp2ObrianBauer);

			gamePlayerRepository.save(g5pAlmeidagp1AlmeidaBauer);
			gamePlayerRepository.save(g5pBauergp2AlmeidaBauer);

			gamePlayerRepository.save(g6pKbauergp1Kbauer);

			gamePlayerRepository.save(g7pAlmeidagp1Almeida);

			gamePlayerRepository.save(g8pKbauergp1KbauerAlmeida);
			gamePlayerRepository.save(g8pAlmeidagp2KbauerAlmeida);


			shipRepository.save(ship1);
			shipRepository.save(ship2);
			shipRepository.save(ship3);
			shipRepository.save(ship4);
			shipRepository.save(ship5);
			shipRepository.save(ship6);
			shipRepository.save(ship7);
			shipRepository.save(ship8);
			shipRepository.save(ship9);
			shipRepository.save(ship10);
			shipRepository.save(ship11);
			shipRepository.save(ship12);
			shipRepository.save(ship13);
			shipRepository.save(ship14);
			shipRepository.save(ship15);
			shipRepository.save(ship16);
			shipRepository.save(ship17);
			shipRepository.save(ship18);
			shipRepository.save(ship19);
			shipRepository.save(ship20);
			shipRepository.save(ship21);
			shipRepository.save(ship22);
			shipRepository.save(ship23);
			shipRepository.save(ship24);
			shipRepository.save(ship25);
			shipRepository.save(ship26);
			shipRepository.save(ship27);



            salvoRepository.save(salvoG1T1P1);
            salvoRepository.save(salvoG1T1P2);
			salvoRepository.save(salvoG1T2P1);
			salvoRepository.save(salvoG1T2P2);
			salvoRepository.save(salvoG2T1P1);
			salvoRepository.save(salvoG2T1P2);
			salvoRepository.save(salvoG2T2P1);
			salvoRepository.save(salvoG2T2P2);
			salvoRepository.save(salvoG3T1P1);
			salvoRepository.save(salvoG3T1P2);
			salvoRepository.save(salvoG3T2P1);
			salvoRepository.save(salvoG3T2P2);
			salvoRepository.save(salvoG4T1P1);
			salvoRepository.save(salvoG4T1P2);
			salvoRepository.save(salvoG4T2P1);
			salvoRepository.save(salvoG4T2P2);
			salvoRepository.save(salvoG5T1P1);
			salvoRepository.save(salvoG5T1P2);
			salvoRepository.save(salvoG5T2P1);
			salvoRepository.save(salvoG5T2P2);
			salvoRepository.save(salvoG5T3P2);


			scoreRepository.save(scoreGame1Bauer);
			scoreRepository.save(scoreGame1Obrian);
			scoreRepository.save(scoreGame2Bauer);
			scoreRepository.save(scoreGame2Obrian);
			scoreRepository.save(scoreGame3Obrian);
			scoreRepository.save(scoreGame3Almeida);
			scoreRepository.save(scoreGame4Obrian);
			scoreRepository.save(scoreGame4Bauer);
			scoreRepository.save(scoreGame5Almeida);
			scoreRepository.save(scoreGame5Bauer);
			scoreRepository.save(scoreGame6Kbauer);
			scoreRepository.save(scoreGame7Almeida);
			scoreRepository.save(scoreGame8Kbauer);
			scoreRepository.save(scoreGame8Almeida);
		};
	}
}

