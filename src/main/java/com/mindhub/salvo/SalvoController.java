package com.mindhub.salvo;

import com.mindhub.salvo.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import javax.xml.stream.Location;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;



    @GetMapping("/Games")
    public Map<String, Object> getAll(Authentication authentication) {

        Map<String, Object> dto = new LinkedHashMap<String, Object>();

        if (isGuest(authentication)) {
            dto.put("playerActual", "Invitado");
            dto.put("games", gameRepository.findAll().stream().map(Game::toDTO).collect(toList()));
        } else {
            Player player = obtenerJugador(authentication);
            dto.put("playerActual", player.toDTO());
            dto.put("games", gameRepository.findAll().stream().map(Game::toDTO).collect(toList()));
        }

        return dto;
    }

    @PostMapping("/Games")
    public ResponseEntity<Map<String, Object>> addGame(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();

        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "nombre de usuario ya existe"), HttpStatus.CONFLICT);
        } else {
            Player player = obtenerJugador(authentication);
            Game game = gameRepository.save(new Game((LocalDateTime.now())));
            GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(LocalDateTime.now(), player, game));
            return new ResponseEntity<>(makeMap("id", gamePlayer.getId()), HttpStatus.CREATED);
        }
    }
    //

    @PostMapping("/game/{gameId}/players")
    public ResponseEntity<Map<String, Object>> addPlayerToGame(@PathVariable("gameId") long gameId, Authentication authentication) {
        Player player = obtenerJugador(authentication);
        Optional<Game> gameOpt = gameRepository.findById(gameId);

        if(!gameOpt.isPresent()) {
            return new ResponseEntity<>(makeMap("error", "No hay tal juego"), HttpStatus.FORBIDDEN);
        }

        Game game = gameOpt.get();

        if( estaLlenoElJuego(game)){
            return new ResponseEntity<>(makeMap("error", "El juego está lleno"), HttpStatus.CONFLICT);
        }

        if (elJugadorYaSeEncuentraEnElJuego(game, player)){
            return new ResponseEntity<>(makeMap("error", "El juegador ya se encuentra en el juego"), HttpStatus.CONFLICT);
        }

        GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(LocalDateTime.now(), player, game));
        return new ResponseEntity<>(makeMap("id", gamePlayer.getId()), HttpStatus.CREATED);
    }



    @GetMapping(value = "/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> getGameView(@PathVariable long gamePlayerId) {
        Optional<GamePlayer> gp = gamePlayerRepository.findById(gamePlayerId);
        if(gp.isPresent())
            return new ResponseEntity<>(gp.get().gameViewDTO(), HttpStatus.OK);
        else
            return new ResponseEntity<>(makeMap("error", "No existe el gp"), HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value="/players")
    public ResponseEntity<Map<String, Object>> addPlayer(@RequestParam String userName, @RequestParam String password){
        Player playerFound = playerRepository.findByUserName(userName);

        if (playerFound != null)
            return new ResponseEntity<>(makeMap("error", "Username already exists"), HttpStatus.CONFLICT);

        Player newPlayer = playerRepository.save(new Player(userName,this.passwordEncoder.encode(password)));

        return new ResponseEntity<>(makeMap("id", newPlayer.getId()), HttpStatus.CREATED);
    }


    @PostMapping(value ="/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> addListShips(@PathVariable long gamePlayerId, @RequestBody Set<Ship> ships, Authentication authentication) {
        //Se debe enviar una respuesta Prohibida si el usuario ya tiene envíos.
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "No esta logueado"), HttpStatus.CONFLICT);
        }

        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);
        if(!gamePlayer.isPresent()) {
            return new ResponseEntity<>(makeMap("error", "No existe el recurso con el id"), HttpStatus.CONFLICT);
        }

        Player player = obtenerJugador(authentication);
        if(player.getUserName() != gamePlayer.get().getPlayer().getUserName()){
            return new ResponseEntity<>(makeMap("error", "El usuario no esta conectado"), HttpStatus.FORBIDDEN);
        }

        if(gamePlayer.get().getShips().size() > 0) {
            return new ResponseEntity<>(makeMap("error", "El usuario ya tiene envíos"), HttpStatus.FORBIDDEN);
        }

        gamePlayer.get().addShips(ships);
        gamePlayerRepository.save(gamePlayer.get());
        return new ResponseEntity<>(makeMap("ships", "agregadas"), HttpStatus.CREATED);
    }

    @PostMapping(value ="/games/players/{gamePlayerId}/salvos")
    public ResponseEntity<Map<String, Object>> addListSalvos(@PathVariable long gamePlayerId, @RequestBody Salvo salvo, Authentication authentication) {
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "No esta logueado"), HttpStatus.CONFLICT);
        }

        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);
        if(!gamePlayer.isPresent()) {
            return new ResponseEntity<>(makeMap("error", "No existe el recurso con el id"), HttpStatus.CONFLICT);
        }

        Player player = obtenerJugador(authentication);
        if(player.getUserName() != gamePlayer.get().getPlayer().getUserName()){
            return new ResponseEntity<>(makeMap("error", "o se puede modificar un game player ajeno"), HttpStatus.FORBIDDEN);
        }


      /*  if(gamePlayer.get().getShips().size() == 0){
            return new ResponseEntity<>(makeMap("error", "debe ingresar ships"), HttpStatus.FORBIDDEN);
        }

        */
        ///
       String estadoActual = gamePlayer.get().getEstadoGame();
       if(estadoActual == "GANADO" || estadoActual == "EMPATE" || estadoActual == "PERDIDO"){
            return new ResponseEntity<>(makeMap("error", "el juego termino"), HttpStatus.FORBIDDEN);
        }


        if (salvo.getLocations().size() > 5){
            return new ResponseEntity<>(makeMap("error", "cantidad de disparos no permitido"), HttpStatus.FORBIDDEN);
        }

        if(gamePlayer.get().getOpponent() == null){
            return new ResponseEntity<>(makeMap("error", "no puedes disparar porque no hay enemigo"), HttpStatus.FORBIDDEN);
        }

       Salvo salvoTurno = gamePlayer.get().getOpponent().getSalvoes().stream().sorted(Comparator.comparing(Salvo::getTurn).reversed()).findFirst().orElse(null);
       salvo.setTurn(gamePlayer.get().getSalvoes().size()+ 1);

       /////////////
      /*  if (estadoActual == "ESPERAR_TURNO") {
            return new ResponseEntity<>(makeMap("error", "cantidad de disparos no permitido,falta disparo del enemigo"),HttpStatus.FORBIDDEN);
        }*/

        /////////////

       int num = salvoTurno != null ? salvoTurno.getTurn() : 0;
       int num2 = salvo != null ? salvo.getTurn() : 0;

        if (num - num2 > 1 || num - num2 < -1){
           return new ResponseEntity<>(makeMap("error", "cantidad de disparos no permitido,falta disparo del enemigo"), HttpStatus.FORBIDDEN);
       }

        gamePlayer.get().addSalvo(salvo);
        gamePlayerRepository.save(gamePlayer.get());
        return new ResponseEntity<>(makeMap("salvos", "agregadas"), HttpStatus.CREATED);


    }




    private Map<String, Object> makeMap(String key, Object value)
    {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    private boolean isGuest(Authentication authentication)
    {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    private Player obtenerJugador (Authentication authentication)
    {
        return playerRepository.findByUserName(authentication.getName());
    }

    private boolean estaLlenoElJuego (Game game){
        return game.getGamePlayers().size() == 2;
    }

    private boolean elJugadorYaSeEncuentraEnElJuego (Game game, Player player) {
        return (game.getGamePlayers().stream().anyMatch(gamePlayer -> gamePlayer.getPlayer().getId() == player.getId()));
    }

}

/*  return gameRepository.findAll().stream().map(Game::toDTO).collect(toList()); ("player añadido", HttpStatus.CREATED) */

   /* private List<Map<String, Object>> makeAaDTO(int num) {
        List<Map<String, Object>> lista = new ArrayList<>();
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("num", num);
        lista.add(dto);
        Map<String, Object> dto2 = new LinkedHashMap<String, Object>();
        dto2.put("num", num+1);
        lista.add(dto2);
        return lista;
    }*/