package com.mindhub.salvo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.xml.stream.Location;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
@Entity
public class Salvo {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private int turn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    @JsonIgnore
    private GamePlayer gamePlayer;


    //Como tenemos una relación de uno a varios entre una entidad y un dato simple (lista de cadenas de cells)
    // Agregamos
    //@ElementCollection: crea listas de  objetos con dato destinado a usarse solo en el objeto que lo contiene.
    //                      Todos los tipos de datos incorporados, como  Integer y  String , son integrables.
    //@Column(name="email")
    //private List<String> emails = new ArrayList<>();
    @ElementCollection()
    private List<String> locations; //pq no instancio el ArrayList????

    public Salvo() {
    }

    public Salvo(List<String> locations, int number) {
        this.locations = locations;
        this.turn = number;
    }


    public Map<String, Object> salvoDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("player", this.gamePlayer.getPlayer().toDTO());
        dto.put("turn", turn);
        dto.put("locations", locations);
        dto.put("hits", getLocationsHits());
        dto.put("sink", getSink());
        return dto;
    }


    private List<String> getLocationsHits() {
        List<String> result = new ArrayList<>();
        this.locations.stream().forEach(location ->
                this.gamePlayer.getOpponent().getShips().stream().forEach(ship -> {
                            if (ship.getCells().contains(location)) {
                                result.add(location);
                            }
                        }
                ));

        return result;
    }

    public List<String> getSink() {
        List<String> resultSink = new ArrayList<>();
        List<String> resultTotalHits = new ArrayList<>();
        this.gamePlayer.getSalvoes().stream().forEach(salvo -> {
            if(salvo.getTurn() <= this.turn) {
                resultTotalHits.addAll(salvo.getLocationsHits());
            }
        });

        this.gamePlayer.getOpponent().getShips().stream().forEach(ship -> {
            if(resultTotalHits.containsAll(ship.getCells())){
                resultSink.add(ship.getTipo());

            }
        });


        return resultSink;
    }






    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }
}
