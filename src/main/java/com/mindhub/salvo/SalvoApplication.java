package com.mindhub.salvo;

import com.mindhub.salvo.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class SalvoApplication {

	@Autowired
	PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository , GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, ScoreRepository scoreRepository) {
		return (args) -> {
			// save a couple of customers
			Player player1 = new Player("laura@gmil.com", this.passwordEncoder.encode("24"));
			playerRepository.save(player1);

			Player player2 = new Player("pepito@gmail.com",this.passwordEncoder.encode("42"));
			playerRepository.save(player2);
			Player player3 = new Player("valentina@gmail.com",this.passwordEncoder.encode("kb"));
			playerRepository.save(player3);
			Player player4 = new Player("mauro@gmail.com",this.passwordEncoder.encode("mole"));
			playerRepository.save(player4);
			Player player5 = new Player("antonio@gmail.com",this.passwordEncoder.encode("pepito"));
			playerRepository.save(player5);
			Player player6 = new Player("celeste@gmail.com",this.passwordEncoder.encode("chacha"));
			playerRepository.save(player6);
			Player player7 = new Player("pia@gmail.com",this.passwordEncoder.encode("oso"));
			playerRepository.save(player7);
			Player player8 = new Player("anael@gmail.com",this.passwordEncoder.encode("nina"));
			playerRepository.save(player8);


			Game game1 = new Game(LocalDateTime.of(2019,2,5,17,0));
			gameRepository.save(game1);
			Game game2= new Game(LocalDateTime.of(2019,2,5,17,0));
			gameRepository.save(game2);
			Game game3= new Game(LocalDateTime.of(2019,2,15,17,0));
			gameRepository.save(game3);
			Game game4= new Game(LocalDateTime.of(2019,2,10,17,0));
			gameRepository.save(game4);





			Score score1 = new Score(LocalDateTime.now(),player1,game1,1);
			scoreRepository.save(score1);

			Score score2 = new Score(LocalDateTime.of(2019,2,5,18,5), player2,game1,0);
			scoreRepository.save(score2);

			Score score3 = new Score(LocalDateTime.now(),player1,game2,0.5);
			scoreRepository.save(score3);

			Score score4 = new Score(LocalDateTime.now(),player4,game2,0.5);
			scoreRepository.save(score4);

			Score score5 = new Score(LocalDateTime.now(),player5,game3,0);
			scoreRepository.save(score5);

			Score score6 = new Score(LocalDateTime.now(),player6,game3,1);
			scoreRepository.save(score6);

			Score score7 = new Score(LocalDateTime.now(),player7,game4,0);
			scoreRepository.save(score7);

			Score score8= new Score(LocalDateTime.now(),player8,game4,1);
			scoreRepository.save(score8);



			Ship ship1= new Ship("Destroyer",new ArrayList<>(Arrays.asList("H2", "H3", "H4")));

			Ship ship2= new Ship("Submarine", new ArrayList<>(Arrays.asList("E1", "F1","G1")));

			Ship ship3= new Ship("Patrol Boat",new ArrayList<>(Arrays.asList("B3", "B4")));

			Ship ship4= new Ship("Destroyer",new ArrayList<>(Arrays.asList("B5", "C5", "D5")));

			Ship ship5= new Ship("Patrol Boat",new ArrayList<>(Arrays.asList("F1","F2")));

			Ship ship6= new Ship("Destroyer",new ArrayList<>(Arrays.asList("B5", "C5", "D5")));

			Ship ship7= new Ship("Patrol Boat",new ArrayList<>(Arrays.asList("C6", "C7")));

			Ship ship8= new Ship("Submarine ",new ArrayList<>(Arrays.asList("A2", "A3", "A4")));

			Ship ship9= new Ship("Patrol Boat",new ArrayList<>(Arrays.asList("G6","H6")));

			Ship ship10= new Ship("Destroyer",new ArrayList<>(Arrays.asList("B5", "C5", "D5")));

			Ship ship11= new Ship("Patrol Boat",new ArrayList<>(Arrays.asList("C6", "C7")));

			Ship ship12= new Ship("Submarine ",new ArrayList<>(Arrays.asList("A2", "A3", "A4")));


			Salvo salvo1= new Salvo( new ArrayList<>(Arrays.asList("B5", "C5", "F1")) ,1);

			Salvo salvo2= new Salvo(new ArrayList<>(Arrays.asList("F2", "D5")) ,2);

			Salvo salvo3= new Salvo(new ArrayList<>(Arrays.asList("B4", "B5", "B6")) ,1);

			Salvo salvo4= new Salvo(new ArrayList<>(Arrays.asList("E1","H3", "A2")) ,2);

			Salvo salvo5= new Salvo(new ArrayList<>(Arrays.asList("A2", "A4", "G6")) ,1);

			Salvo salvo6= new Salvo(new ArrayList<>(Arrays.asList("A3", "H6")) ,2);

			Salvo salvo7= new Salvo(new ArrayList<>(Arrays.asList("B5", "D5", "C7")) ,1);

			Salvo salvo8= new Salvo(new ArrayList<>(Arrays.asList("C5", "C6")) ,2);

			Salvo salvo9= new Salvo(new ArrayList<>(Arrays.asList("G6", "H6", "A4")) ,1);

			Salvo salvo10= new Salvo(new ArrayList<>(Arrays.asList("A2", "A3", "D8")) ,2);

			Salvo salvo11= new Salvo(new ArrayList<>(Arrays.asList("H1", "H2", "H3")) ,1);

			Salvo salvo12= new Salvo(new ArrayList<>(Arrays.asList("E1", "F2", "G3")) ,2);



			Set<Ship> shipSet1 = new HashSet<>();
			shipSet1.add(ship1);
			shipSet1.add(ship2);


			Set<Ship> shipSet2 = new HashSet<>();
			shipSet2.add(ship3);
			shipSet2.add(ship4);


			Set<Ship> shipSet3 = new HashSet<>();
			shipSet3.add(ship5);
			shipSet3.add(ship6);

			Set<Ship> shipSet4 = new HashSet<>();
			shipSet4.add(ship7);
			shipSet4.add(ship8);

			Set<Ship> shipSet5 = new HashSet<>();
			shipSet5.add(ship9);
			shipSet5.add(ship10);

			Set<Ship> shipSet6 = new HashSet<>();
			shipSet6.add(ship11);
			shipSet6.add(ship12);


			Set<Salvo> salvoSet1 = new HashSet<>();
			salvoSet1.add(salvo1);
			salvoSet1.add(salvo2);

			Set<Salvo> salvoSet2 = new HashSet<>();
			salvoSet2.add(salvo3);
			salvoSet2.add(salvo4);

			Set<Salvo> salvoSet3 = new HashSet<>();
			salvoSet3.add(salvo5);
			salvoSet3.add(salvo6);

			Set<Salvo> salvoSet4 = new HashSet<>();
			salvoSet4.add(salvo7);
			salvoSet4.add(salvo8);


			Set<Salvo> salvoSet5 = new HashSet<>();
			salvoSet5.add(salvo9);
			salvoSet5.add(salvo10);

			Set<Salvo> salvoSet6 = new HashSet<>();
			salvoSet6.add(salvo11);
			salvoSet6.add(salvo12);







			gamePlayerRepository.save(new GamePlayer(LocalDateTime.now(),player1,game1,shipSet1,salvoSet1));
			gamePlayerRepository.save(new GamePlayer(LocalDateTime.now(),player2,game1,shipSet2,salvoSet2));
			gamePlayerRepository.save(new GamePlayer(LocalDateTime.now(),player1,game2,shipSet3,salvoSet3));
			gamePlayerRepository.save(new GamePlayer(LocalDateTime.now(),player4,game2,shipSet4,salvoSet4));
			gamePlayerRepository.save(new GamePlayer(LocalDateTime.now(),player5,game3,shipSet5,salvoSet5));
			gamePlayerRepository.save(new GamePlayer(LocalDateTime.now(),player6,game3,shipSet6,salvoSet6));
			gamePlayerRepository.save(new GamePlayer(LocalDateTime.now(),player7,game4));
			gamePlayerRepository.save(new GamePlayer(LocalDateTime.now(),player8,game4));


		};
	}

}


@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Autowired
	PlayerRepository playerRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputName-> {
			Player player = playerRepository.findByUserName(inputName);
			if (player != null) {
				return new User(player.getUserName(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unknown user: " + inputName);
			}
		});
	}
}

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/web/game.html").hasAuthority("USER")
				.antMatchers("/api/game_view/**").hasAuthority("USER")
				.antMatchers("/api/**").permitAll()
				.antMatchers("/web/**").permitAll();


		http.formLogin()
				.usernameParameter("name")
				.passwordParameter("pwd")
				.loginPage("/api/login");

		http.logout().logoutUrl("/api/logout");

		// turn off checking for CSRF tokens
		http.csrf().disable();

		http.headers().frameOptions().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}

}

