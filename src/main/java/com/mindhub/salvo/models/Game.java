package com.mindhub.salvo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private LocalDateTime fechaActual;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    private Set<GamePlayer> gamePlayers = new HashSet<>();

    //////////////////
    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    private Set<Score> scores = new HashSet<>();

    public Game(){}

    public Game(LocalDateTime fechaActualJuego){
        fechaActual = fechaActualJuego;
    }

    public Map<String, Object> toDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.id);
        dto.put("created", this.fechaActual);
        dto.put("gamePlayers",this.gamePlayers.stream().map(GamePlayer::toDTO).collect(toList()));
        ///dudas nose como agregar score y finishDate!!!!!
        //dto.put("score",this.scores.stream().map(Score::toDTO).collect(toList()));
       // dto.put("finishDate",this.getGamePlayers().stream().map(Score::toDTO).collect(toList()));

        return dto;
    }

    @JsonIgnore
    public List<Player> getPlayers() {
            return gamePlayers.stream().map(sub -> sub.getPlayer()).collect(toList());
    }

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

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

}
