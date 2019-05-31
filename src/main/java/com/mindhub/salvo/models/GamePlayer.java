package com.mindhub.salvo.models;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.Authentication;

import javax.persistence.*;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;
import java.util.*;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private LocalDateTime fechaActual;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;


    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Ship> ships = new HashSet<>();
     ///// Agregue el get????????


    @OneToMany(mappedBy="gamePlayer",fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Salvo> salvoes = new HashSet<>();



    public GamePlayer() { }

    public GamePlayer(LocalDateTime fechaActual, Player player, Game game, Set<Ship> ships, Set<Salvo> salvoes) {
        this.fechaActual = fechaActual;
        this.player = player;
        this.game= game;
        ships.forEach(this::addShip);
        salvoes.forEach(this::addSalvo);
    }

    public GamePlayer(LocalDateTime fechaActual, Player player, Game game, Set<Ship> ships) {
        this.fechaActual = fechaActual;
        this.player = player;
        this.game= game;
        ships.forEach(this::addShip);

    }

    public GamePlayer(LocalDateTime fechaActual, Player player, Game game) {
        this.fechaActual = fechaActual;
        this.player = player;
        this.game= game;
    }

    public GamePlayer( Player player, Game game) {
        this.player = player;
        this.game= game;
    }

    public Map<String, Object> toDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.id);
        dto.put("player", this.player.toDTO());
        Score score = this.getScore();
        if(score != null)
            dto.put("score",score.getScore());
        else
            dto.put("score",null);

        return dto;
    }

    public Map<String, Object> gameViewDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.game.getId());
        dto.put("created", this.game.getFechaActual());
        dto.put("gamePlayers", this.game.getGamePlayers().stream().map(GamePlayer::toDTO));
        dto.put("Ships", this.getShips().stream().map(Ship::shipDTO));
        dto.put("salvoes", this.game.getGamePlayers().stream().flatMap(gamePlayer -> gamePlayer.getSalvoes().stream().sorted(Comparator.comparing(Salvo::getTurn)).map(Salvo::salvoDTO)));
        dto.put("estadoGame",this.getEstadoGame());
        return dto;
    }




    public Score getScore(){
        return this.player.getScore(this.game);
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public void addShip(Ship ship) {
        ship.setGamePlayer(this);   /// DUDASS setGame Player????
        ships.add(ship);
    }

    public Set<Salvo> getSalvoes() {
        return salvoes;
    }

    public void addSalvo(Salvo salvo) {
        salvo.setGamePlayer(this);   /// DUDASS setGame Player????
        salvoes.add(salvo);
    }

    public GamePlayer getOpponent(){
        return this.getGame().getGamePlayers().stream().filter(gamePlayer -> gamePlayer.getId() != this.id).findFirst().orElse(null);
    }



    public String getEstadoGame() {
      String  estado ="JUGAR_TURNO";

        //Ya que al crear el juego lo primero que debe hacer el usario es posicionar los barcos valido eso primero
        //esto me va a permitir que en el mmapping de salvo no permita hacer nada si no se han posicionado las piezas
        //tambien que en el método de posicionar piezas si el estado es distinto a "COLOCAR_FICHAS" no deje guardar
        if(this.getShips().size() != 0){
            //retornando si no hay enemigo, en el mapping de salvo si queremos podemos validar que si el estado
            //es que no hay enemigo entonces que no dispare fichas, si fuese esa una regla de negocio.. o sería util para
            //indicarlo al usuario en el front end que el juego no tiene enemigo...
            if(this.getOpponent() != null){
                Optional<Salvo> ultimoSalvo = this.getSalvoes().stream().sorted(Comparator.comparing(Salvo::getTurn).reversed()).findFirst();
                Optional<Salvo> ultimoSalvoOponente = Optional.ofNullable(null);
                if (this.getOpponent() != null) {
                    ultimoSalvoOponente = this.getOpponent().getSalvoes().stream().sorted(Comparator.comparing(Salvo::getTurn).reversed()).findFirst();
                }
                if (ultimoSalvo.isPresent() && ultimoSalvoOponente.isPresent()) {
                    //si estan en el mismo turno verificar si alguien ganó
                    if (ultimoSalvo.get().getTurn() == ultimoSalvoOponente.get().getTurn()) {
                        if ((ultimoSalvo.get().getSink().size() == this.getOpponent().getShips().size()) && (ultimoSalvoOponente.get().getSink().size() < this.getShips().size())) {
                            estado = "GANADO";

                        }
                        if ((ultimoSalvo.get().getSink().size() < this.getOpponent().getShips().size()) && (ultimoSalvoOponente.get().getSink().size() == this.getShips().size())) {
                            estado = "PERDIDO";
                        }
                        if ((ultimoSalvo.get().getSink().size() == this.getOpponent().getShips().size()) && (ultimoSalvoOponente.get().getSink().size() == this.getShips().size())) {
                            estado = "EMPATE";
                        }
                    }
                    // no tengo que validar jugar ya que es el estado por defecto de la varibale estado
                    //valido el sino está en el mismo turno y a este jugador le toca esperar, además se coloca
                    //sino para que no se ejecuten los dos if siempre
                    else if(ultimoSalvo.get().getTurn() > ultimoSalvoOponente.get().getTurn()){
                        estado = "ESPERAR_TURNO";
                    }
                    //pasa lo mismo acá no tengo que validar si toca jugar, tengo que validar si toca esperar
                    //si no entra en ninguno de todos estos if, la funcion retornara jugar.
                    //Si colocamos un estado por defcto en la variable que vamos a retornar
                    //debemos crear la función pensando en ése estado.
                } else if((ultimoSalvo.isPresent()) && !(ultimoSalvoOponente.isPresent())) {
                    estado = "ESPERAR_TURNO";
                }
            }else{
                //este esperar oponente se refiere a que entre un oponente al juego
                estado = "ESPERAR_OPONENTE";
            }
        }else{
            estado = "COLOCAR_FICHAS";
        }
        return estado;
    }

  /*  public String getEstadoGame() {
        estado ="disparar//////";

        if(this.getShips().size() != 0){
            Optional<Salvo> ultimoSalvo = this.getSalvoes().stream().sorted(Comparator.comparing(Salvo::getTurn).reversed()).findFirst();
            Optional<Salvo> ultimoSalvoOponente = Optional.ofNullable(null);
            if (this.getOpponent() != null) {
                ultimoSalvoOponente = this.getOpponent().getSalvoes().stream().sorted(Comparator.comparing(Salvo::getTurn).reversed()).findFirst();
            }

            if (ultimoSalvo.isPresent() && ultimoSalvoOponente.isPresent()) {

                if (ultimoSalvo.get().getTurn() == ultimoSalvoOponente.get().getTurn()) {
                    if ((ultimoSalvo.get().getSink().size() == this.getShips().size()) && (ultimoSalvoOponente.get().getSink().size() < this.getShips().size())) {
                        return estado = "ganado";

                    }
                    if ((ultimoSalvo.get().getSink().size() < this.getShips().size()) && (ultimoSalvoOponente.get().getSink().size() == this.getShips().size())) {
                        return estado = "perdido";
                    }
                    if ((ultimoSalvo.get().getSink().size() == this.getShips().size()) && (ultimoSalvoOponente.get().getSink().size() == this.getShips().size())) {
                        return estado = "empate";
                    }
                }
                if(ultimoSalvo.get().getTurn() < ultimoSalvoOponente.get().getTurn()){
                        return estado = "disparar------";
                }
                if(ultimoSalvo.get().getTurn() > ultimoSalvoOponente.get().getTurn()){
                            return estado = "esperarAlOponente!!!!!!";
                }


            } else {
                if(ultimoSalvo.isPresent() == false ){
                    return  estado = "disparar";
                }

                if (ultimoSalvo.isPresent() == false) {
                    return estado = "disparar=====";
                }

                if (ultimoSalvoOponente.isPresent() == false) {
                    return estado = "esperarAlOponente";
                }
            }
        }
        else {
            if(getOpponent() != null){
                return estado = "enterShip";

            }else{
                return  estado = "esperandoOponente====";
            }

        }

        return estado;
    }

    */

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getFechaActual() {
        return fechaActual;
    }

    public void setFechaActual(LocalDateTime fechaActual) {
        this.fechaActual = fechaActual;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void addShips(Set<Ship> ships) {
        ships.forEach(this::addShip);
    }
}
