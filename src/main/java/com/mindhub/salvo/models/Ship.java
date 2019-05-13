package com.mindhub.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

//llamar a la clase siempre con minuscula.
@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String type;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;


    //Como tenemos una relación de uno a varios entre una entidad y un dato simple (lista de cadenas de cells)
    // Agregamos
    //@ElementCollection: crea listas de  objetos con dato destinado a usarse solo en el objeto que lo contiene.
   //                      Todos los tipos de datos incorporados, como  Integer y  String , son integrables.
    //@Column(name="email")
    //private List<String> emails = new ArrayList<>();
    @ElementCollection()
    private List<String> cells; //pq no instancio el ArrayList????



    public Map<String,Object> shipDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("type" ,type);
        dto.put("cells", cells);
        return dto;
    }




    public Ship(String type, List<String> cells) {

        this.type = type;
        this.cells = cells;
    }



    public Ship(){}


    public Long getId() {
        return id;
    }


    public String getTipo() {
        return type;
    }

    public void setTipo(String type) {
        this.type = type;
    }
    //// Agregue estos seters y geters
    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public List<String> getCells() {
        return cells;
    }
}
