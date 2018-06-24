package com.accenture.salvo;

import com.accenture.salvo.games.Game;
import com.accenture.salvo.games.GameRepository;
import com.accenture.salvo.players.Player;
import com.accenture.salvo.players.PlayerRepository;
import com.accenture.salvo.ships.Ship;
import com.accenture.salvo.ships.ShipRepository;
import com.accenture.salvo.ships.ShipType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.*;

@SpringBootApplication
public class SalvoApplication {



	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository,
									  GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository){

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());

		/* =================== PLAYERS =================== */
		Player jbauer = new Player("j.bauer@ctu.gov");
		Player cobrian = new Player("c.obrian@ctu.gov");
		Player talmeida = new Player("t.almeida@cut.gov");
		Player dpalmer = new Player("d.palmer@whitehouse.gov");


		/* =================== GAMES =================== */
		Game gameBauerObrian = new Game();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		Game gameBauerObrian2 = new Game(cal.getTime());
		cal.add(Calendar.HOUR_OF_DAY, 1);
		Game gameObrianAlmeida = new Game(cal.getTime());
		cal.add(Calendar.HOUR_OF_DAY, 1);
		Game gameBauerObrian3 = new Game(cal.getTime());
		cal.add(Calendar.HOUR_OF_DAY, 1);
		Game gameAlmeidaBauer = new Game(cal.getTime());
		cal.add(Calendar.HOUR_OF_DAY, 1);
		Game gamePalmer = new Game(cal.getTime());

		/* =================== GAME PLAYERS =================== */
		GamePlayer gp1BauerObrian = new GamePlayer(jbauer,gameBauerObrian);
		GamePlayer gp2BauerObrian = new GamePlayer(cobrian,gameBauerObrian);

		GamePlayer gp1BauerObrian2 = new GamePlayer(jbauer,gameBauerObrian2);
		GamePlayer gp2BauerObrian2 = new GamePlayer(cobrian,gameBauerObrian2);


		/* =================== SHIPS =================== */
		String[] locations = new String[]{"H2","H3","H4"};
		List<String> arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship1 = new Ship(ShipType.DESTROYER, gp1BauerObrian, arrayLocations);

		locations = new String[]{"E1", "F1", "G1"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship2 = new Ship(ShipType.SUBMARINE, gp1BauerObrian, arrayLocations);

		locations = new String[]{"B4", "B5"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship3 = new Ship(ShipType.PATROL_BOAT, gp1BauerObrian, arrayLocations);

		locations = new String[]{"B5", "C5", "D5"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship4 = new Ship(ShipType.DESTROYER, gp2BauerObrian, arrayLocations);

		locations = new String[]{"F1", "F2"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship5 = new Ship(ShipType.PATROL_BOAT, gp2BauerObrian, arrayLocations);

		locations = new String[]{"B5", "C5", "D5"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship6 = new Ship(ShipType.DESTROYER, gp1BauerObrian2, arrayLocations);

		locations = new String[]{"C6","C7"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship7 = new Ship(ShipType.PATROL_BOAT, gp1BauerObrian2, arrayLocations);

		locations = new String[]{"A2", "A3", "A4"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship8 = new Ship(ShipType.SUBMARINE, gp2BauerObrian2, arrayLocations);

		locations = new String[]{"G6","H7"};
		arrayLocations = new ArrayList<>(Arrays.asList(locations));
		Ship ship9 = new Ship(ShipType.PATROL_BOAT, gp2BauerObrian2, arrayLocations);



		return (args) -> {
			playerRepository.save(jbauer);
			playerRepository.save(cobrian);
			playerRepository.save(talmeida);
			playerRepository.save(dpalmer);
			gameRepository.save(gameBauerObrian);
			gameRepository.save(gameBauerObrian2);
			gameRepository.save(gameObrianAlmeida);
			gameRepository.save(gameBauerObrian3);
			gameRepository.save(gameAlmeidaBauer);
			gameRepository.save(gamePalmer);

			gamePlayerRepository.save(gp1BauerObrian);
            gamePlayerRepository.save(gp2BauerObrian);

            gamePlayerRepository.save(gp1BauerObrian2);
			gamePlayerRepository.save(gp2BauerObrian2);

			gamePlayerRepository.save(new GamePlayer(cobrian,gameObrianAlmeida));
			gamePlayerRepository.save(new GamePlayer(talmeida,gameObrianAlmeida));

			gamePlayerRepository.save(new GamePlayer(jbauer,gameBauerObrian3));
			gamePlayerRepository.save(new GamePlayer(cobrian,gameBauerObrian3));

			gamePlayerRepository.save(new GamePlayer(talmeida,gameAlmeidaBauer));
			gamePlayerRepository.save(new GamePlayer(jbauer,gameAlmeidaBauer));

			gamePlayerRepository.save(new GamePlayer(dpalmer,gamePalmer));


			shipRepository.save(ship1);
			shipRepository.save(ship2);
			shipRepository.save(ship3);
			shipRepository.save(ship4);
			shipRepository.save(ship5);
			shipRepository.save(ship6);
			shipRepository.save(ship7);
			shipRepository.save(ship8);
			shipRepository.save(ship9);




		};
	}
}


