Vue.http.options.emulateJSON = true; // send as

var gameData=[];

var app = new Vue({
  el: '#app',
  data: {
    player: "",
    opponent: "",
    oponentSalvoes: [],
    gamePlayerId: 0,
    current:'true',
    salvosCount: 0,
    tabla:[],
    interval : null,
    estadoActual : ""
  },
  methods: {
    agregarShips: function(gamePlayerId) {
        var ships = [];

        grid.grid.nodes.forEach(function(ship){
            ships.push({tipo: ship.el[0].id, cells: getCells(ship)});
        });

        this.$http.post("/api/games/players/"+gamePlayerId+"/ships",JSON.stringify(ships))
            .then(response => {
                 window.location.href = "../web/game.html?gp="+gamePlayerId;

            });

    },
    agregarSalvos: function(gamePlayerId) {
         var salvo = {turn: 0, locations: []};

         gridSalvo.grid.nodes
         .filter(function(node){
            return node.el[0].firstChild.classList.contains('newShoot');
         })
         .forEach(function(salvoItem){    // preguntar grid.gris
               salvo.locations.push(getSalvoCell(salvoItem));
          });
          console.log(salvo);

         this.$http.post("/api/games/players/"+gamePlayerId+"/salvos",JSON.stringify(salvo))
            .then(response => {
                window.location.href = "../web/game.html?gp="+gamePlayerId;});
    }

  }
});

var login = new Vue({
       el: '#login-form',
       data: {
         email: '',
         password: '',
         error: false,
         currentUser:'',
         lists: [],

    },

    methods: {
        logout: function logout() {
                       this.$http.post('/api/logout')
                             .then(response => {
                                  window.location.href='/web/games.html';
                                }, response => {
                                });
                       }
    }
});

(function logout(){

    var games = "http://localhost:8080/api/Games";

    fetch(games,{
        method: 'GET'
      }).then(function(response){     ///.then(response=>response.json())
            return response.json();
          }).then(function(json){    ///.then(json=>{this.all_data=json})
        app.lists = json.games;
       // app.all_data=json;  /// es lo mismo que hacer all_data=json;?????
        login.currentUser = json.playerActual.email;

      }).catch(function(error){
             console.log("Fail")
      })

  });

$(cargarPagina);

function cargarPagina(){
    cargarDatos();
    $("#gridSalvoes").click(function(e){
            if(gameData.estadoGame == "JUGAR_TURNO" ){
               var parentOffset = $(this).offset();
               //or $(this).offset(); if you really just want the current element's offset
               var relX = e.pageX - parentOffset.left;
               var relY = e.pageY - parentOffset.top;
               addSalvo(gridSalvo,grid.getCellFromPixel({top:relY , left:relX}));
            }
        });
}

function cargarDatos(){

//setInterval(function() { window.location.reload() }, 5000);


//propiedad que le indicará la ubicación actual de URL del navegador. Cambiar el valor de la propiedad redireccionará la página.
app.gamePlayerId = paramObj(window.location.href).gp




// Ajax a / api / game_view / nn
var games = "/api/game_view/"+app.gamePlayerId;


fetch(games,{
    method: 'GET'
  }).then(function(response){
        return response.json();
      }).then(function(json){

      gameData = json;
      app.estadoActual = gameData.estadoGame;
      if(app.inteval == null){
          if(gameData.estadoGame != "COLOCAR_FICHAS" && gameData.estadoGame != "JUGAR_TURNO"){
            app.inteval = setInterval(cargarDatos,5000);
          }
      }else{
        if(gameData.estadoGame == "COLOCAR_FICHAS" || gameData.estadoGame == "JUGAR_TURNO"){
            clearInterval(app.inteval);
            app.inteval = null;
         }
         else{
            if(app.inteval == null)
                app.inteval = setInterval(cargarDatos,5000);
         }
      }

      information(app.gamePlayerId);
      createGrid();
      shipsState();

  }).catch(function(error){
    console.log(error)
  })
}

function shipsState(){
    app.tabla = [];
    gameData.salvoes.forEach(function(salvo){
        var actual = app.tabla.findIndex(elem => elem.turn === salvo.turn);

        if (actual>=0){
            if (salvo.player.email == app.player.email) {
                app.tabla[actual].hitsP = salvo.hits.length;
                app.tabla[actual].sinkP = salvo.sink.length;
            } else {
                app.tabla[actual].hitsE = salvo.hits.length;
                app.tabla[actual].sinkE = salvo.sink.length;
            }
        } else {
            var fila = {};
            fila.turn = salvo.turn;
            if (salvo.player.email == app.player.email) {
                fila.hitsP = salvo.hits.length;
                fila.sinkP = salvo.sink.length;
                fila.hitsE = null;
                fila.sinkE = null;
            } else {
                fila.hitsE = salvo.hits.length;
                fila.sinkE = salvo.sink.length;
                fila.hitsP = null;
                fila.sinkP = null;
            }
            app.tabla.push(fila);
        }
    });

    console.log(app.tabla);

}




//devuelve un objeto JavaScript. Si lo usa en la cadena de consulta "? Ab = 12 & cd = 34" , obtendrá un objeto de la forma {ab: "12", cd: "34"}

function paramObj(search) {
  var obj = {};
  var reg = /(?:[?&]([^?&#=]+)(?:=([^&#]*))?)(?:#.*)?/g;

  search.replace(reg, function(match, param, val) {
    obj[decodeURIComponent(param)] = val === undefined ? "" : decodeURIComponent(val);
  });

  return obj;
}

function getShipsLocations(){
    var ships = gameData.Ships;

    if (ships.length > 0){
        //en ships tengo el array de ships y lo recorro con un foreach de dos
        ships.forEach(function (ship){
          //obtengo del array de cells el valor del primer elemento y solo tomo el número con slice y lo convierto en un entero
          var x = +(ship.cells[0].slice(1))- 1;
          //obtengo del array de cells el valor del primer elemento y solo tomo la letra con slice (o,1) y al resultado(string), lo transformo en int con la function toInt()
          var y = toIntGridStack((ship.cells[0].slice(0,1)));

//          var w;
//          var h;
//          //comparo si las letras de las cadenas son iguales, si son iguales estan en posicion horizontal sino vertical
//          if(ship.cells[0].slice(0,1) == ship.cells[1].slice(0,1)){
//            w = ship.cells.length;
//            h = 1;
//          }else {
//            w = 1;
//            h = ship.cells.length;
//          }
//
//          grid.addWidget($('<div id="'+ship.type+'"><div class="grid-stack-item-content shoot" id="ship_'+x + '_' + y +'"></div><div/>'),
//                          x, y, w, h);

             //comparo si las letras de las cadenas son iguales, si son iguales estan en posicion horizontal sino vertical
             var orientacion = "";
             if(ship.cells[0].slice(0,1) == ship.cells[1].slice(0,1)){
               orientacion =  "Horizontal";
             }else {
               orientacion = "Vertical";
             }

            addShip(grid, ship.type, x, y, ship.cells.length, orientacion, false);
          })
    } else {
        addShip(grid, "portaaviones", 0, 0, 5, "Horizontal",true);
        addShip(grid, "acorazado", 0, 1, 4, "Horizontal",true);
        addShip(grid, "submarino", 0, 2, 3, "Horizontal",true);
        addShip(grid, "destructor", 0, 3, 2, "Horizontal",true);
        addShip(grid, "botePatrulla", 0, 4, 2, "Horizontal",true);
    }
}

function getSalvoLocations(){
    var salvos = app.playerSalvoes;   //salvos =gameData.salvoes?????

    salvos.forEach(function (salvo){
      salvo.locations.forEach(function (location){
        var x =  +(location.slice(1))-1;
        var y = toIntGridStack((location.slice(0,1)));

        if(salvo.hits.indexOf(location) != -1)
            gridSalvo.addWidget($('<div id="'+salvo.turn+'"><div class="grid-stack-item-content shootFire">'+salvo.turn+'</div><div/>'),
                              x, y, 1, 1);
        else
            gridSalvo.addWidget($('<div id="'+salvo.turn+'"><div class="grid-stack-item-content shoot">'+salvo.turn+'</div><div/>'),
                      x, y, 1, 1);

      })
    })
}

function getOpponentTurn() {

    var opponentSalvoes = (app.opponentSalvoes || []);
    var ships = gameData.Ships;

    $(".enemyShoot").remove();

    opponentSalvoes.forEach(function (salvo){
      salvo.locations.forEach(function (location){
        var xSalvo =  +(location.slice(1)) - 1;
        var ySalvo = Math.abs( toInt((location.slice(0,1))));
        ships.forEach(function (ship){
              ship.cells.forEach(function (cell){
                var xBarco = +(cell.slice(1))- 1;
                var yBarco = Math.abs(toInt((cell.slice(0,1))));                                                    //         ' +salvo.turn+ '
                if(xSalvo == xBarco && ySalvo == yBarco){
                   $('#grid-board').append('<div class="enemyShoot" style="top:'+(ySalvo)*41+'px; left:'+(xSalvo+1)*41+'px;"></div>');
                }
              });
        });
      });
    });
}

function toInt(letra){
  switch(letra){
  case "A":
  return 1;
  case "B":
  return 2;
  case "C":
  return 3;
  case "D":
  return 4;
  case "E":
  return 5;
  case "F":
  return 6;
  case "G":
  return 7;
  case "H":
  return 8;
  case "I":
  return 9;
  case "J":
  return 10;
  }
}

function toIntGridStack(letra){
  switch(letra){
  case "A":
  return 0;
  case "B":
  return 1;
  case "C":
  return 2;
  case "D":
  return 3;
  case "E":
  return 4;
  case "F":
  return 5;
  case "G":
  return 6;
  case "H":
  return 7;
  case "I":
  return 8;
  case "J":
  return 9;
  }
}

function createGrid () {
    var options = {
        //grilla de 10 x 10
        width: 10,
        height: 10,
        //separacion entre elementos (les llaman widgets)
        verticalMargin: 0,
        //altura de las celdas
        cellHeight: 41,
        //desabilitando el resize de los widgets
        disableResize: true,
        //widgets flotantes
		float: true,
        //removeTimeout: 100,
        //permite que el widget ocupe mas de una columna
        disableOneColumnMode: true,
        //false permite mover, true impide
        staticGrid: false,
        //activa animaciones (cuando se suelta el elemento se ve más suave la caida)
        animate: true
    }

    var ships = gameData.Ships;

    if(ships.length > 0)
       options.staticGrid = true;

    //se inicializa el grid con las opciones
    $('#grid').gridstack(options);
    grid = $('#grid').data('gridstack');
    grid.removeAll();

    $('#gridSalvo').gridstack(options);
    gridSalvo = $('#gridSalvo').data('gridstack');
    gridSalvo.removeAll();

    getShipsLocations();
    getSalvoLocations();
    getOpponentTurn();

}



function information(gamePlayerId){
    gameData.gamePlayers.forEach(function(gamePlayer){
        if(gamePlayer.id == gamePlayerId){
           app.player = gamePlayer.player;
           app.playerSalvoes = gameData.salvoes.filter(function(salvo){
               return salvo.player.id == app.player.id;
            });
        }
        else{
          app.opponent = gamePlayer.player;
          app.opponentSalvoes = gameData.salvoes.filter(function(salvo){
            return salvo.player.id == app.opponent.id;
          });
          console.log(app.opponentSalvoes);
        }
    })

}

function addShip(grid, id, x, y, length, orientacion, addEvent, current) {

  if (orientacion == "Horizontal"){
    grid.addWidget($('<div id="' + id + '"><div class="grid-stack-item-content ' + id + 'Horizontal"></div><div/>'), x, y, length, 1);
  } else {
    grid.addWidget($('<div id="' + id + '"><div class="grid-stack-item-content ' + id + 'Vertical"></div><div/>'), x, y, 1, length);
  }


   if(addEvent){
      $("#" + id).click(function(){
         if($(this).children().hasClass(id + "Horizontal")){
            var positionX = parseInt($(this)[0].dataset.gsX);
            var positionY = parseInt($(this)[0].dataset.gsY);
            var maxPositionY =  10 - length;
            var x = parseInt($(this)[0].dataset.gsX);
            var y = parseInt($(this)[0].dataset.gsY);

            if(grid.isAreaEmpty(x,y + 1 , 1,length-1)){
                if (positionY > maxPositionY){
                    grid.move($(this),positionX,maxPositionY);
                }
                grid.resize($(this),1, length);
                $(this).children().removeClass(id + "Horizontal");
                $(this).children().addClass(id + "Vertical");
            }

         }else{
            var positionX = parseInt($(this)[0].dataset.gsX);
            var positionY = parseInt($(this)[0].dataset.gsY);
            var maxPositionX =  10 - length;
            var x = parseInt($(this)[0].dataset.gsX);
            var y = parseInt($(this)[0].dataset.gsY);

            if(grid.isAreaEmpty(x +1, y, 1,length-1)){
                if (positionX > maxPositionX){
                    grid.move($(this),maxPositionX,positionY);
                }

                grid.resize($(this), length, 1);
                $(this).children().removeClass(id + "Vertical");
                $(this).children().addClass(id + "Horizontal");
             }


       }});
   }else{ app.current =false;}

}

function addSalvo(gridSalvo,position) {

     if(gridSalvo.isAreaEmpty(position.x, position.y, 1, 1)){
        if(app.salvosCount < 5 && position.y != -1 &&  position.x != -1) {
          gridSalvo.addWidget($('<div><div class="grid-stack-item-content newShoot"></div><div/>'),position.x, position.y, 1, 1,false);
          app.salvosCount++;
         }
      }
      else{
        gridSalvo.grid.nodes
         .filter(function(node){
            return node.el[0].firstChild.classList.contains('newShoot') && node.x == position.x && node.y == position.y;
         })
         .forEach(function(salvoItem){    // preguntar grid.gris
               gridSalvo.removeWidget(salvoItem.el[0])
               app.salvosCount--;
          });
      }
}

function getCells(ship){
    var cells = [];
    var x = ship.x;
    var y = ship.y;
    var x_final = x + ship.width;
    var y_final = y + ship.height;

    cells.push(toCoordenada(x,y));

    //HORIZONTAL
    if (ship.width > 1){
        x++; // x = x + 1
        while (x < x_final) {
            cells.push(toCoordenada(x,y));
            x++;
        }
    }

    //VERTICAL
    if (ship.height > 1){
        y++;
        while (y < y_final) {
            cells.push(toCoordenada(x,y));
            y++;
        }
    }

    return cells;
}

function toCoordenada(x, y) {
    var letra =String.fromCharCode(y + 65);
    var numero = (x + 1).toString();
    return letra + numero;
}


function getSalvoCell(salvo){
    var x = salvo.x; /// salvo.xBarco?????
    var y = salvo.y;
   return toCoordenada(x,y);
}

//var json = {"turns": 1 , "Hits on you":[{"tipos":{"tipe":portaaviones, "hits":3, "active": true},{"tipe":acorazado, "hits":1, "active": true},{"tipe":submarino  "hits":4, "active": true},{"tipe":destructor ,"hits":2, "active": true},{"tipe":botePatrulla ,"hits":0 "active": true}}],"Hits on opponent":[{"tipos":{"tipe":portaaviones, "hits":2, "active": true},{"tipe":acorazado, "hits":2, "active": true},{"tipe":submarino , "hits":1, "active": true},{"tipe":destructor ,"hits":2, "active": true},{"tipe":botePatrulla ,"hits":1 ,"active": true}}]};

//var jsonA = {"tabla":[{"turn":[1..15],"Hits on you":[{"tipe":"patroal boat","golpes": 3."long":3, "sunk":"sunk!!"}],{"Hits on opponent":[{"tipe":"patroal boat","golpes": 3."long":3, "sunk":"sunk!!"}]};
//para cambiar posicion!!!

  /*  $("#patroal,#patroal2").click(function(){
        if($(this).children().hasClass("patroalHorizontal")){
            grid.resize($(this),1,2);
            $(this).children().removeClass("patroalHorizontal");
            $(this).children().addClass("patroalHorizontalRed");
        }else{
            grid.resize($(this),2,1);
            $(this).children().addClass("patroalHorizontal");
            $(this).children().removeClass("patroalHorizontalRed");
        }
    });*/


        //agregando un elmento(widget) desde el javascript
        /*grid.addWidget($('<div id="carrier2"><div class="grid-stack-item-content carrierHorizontal"></div><div/>'),
            1, 5, 3, 1);

        grid.addWidget($('<div id="patroal2"><div class="grid-stack-item-content patroalHorizontal"></div><div/>'),
            1, 8, 2, 1);
    */

    //todas las funciones se encuentran en la documentación
        //t


        //    grid.addWidget($('<div id="acorazado2"><div class="grid-stack-item-content acorazadoHorizontal ship"></div><div/>'), 1, 2, 4, 1);
        //    grid.addWidget($('<div id="submarino"><div class="grid-stack-item-content submarinoHorizontal ship"></div><div/>'), 1, 4, 3, 1);
        //    grid.addWidget($('<div id="destructor"><div class="grid-stack-item-content destructorHorizontal ship"></div><div/>'), 1, 6, 3, 1);
        //    grid.addWidget($('<div id="bote-patrulla"><div class="grid-stack-item-content bote-patrullaHorizontal ship"></div><div/>'), 1, 8, 2, 1);

            //verificando si un area se encuentra libre
            //no está libre, false
            //console.log(grid.isAreaEmpty(1, 8, 3, 1));
            //está libre, true
            //console.log(grid.isAreaEmpty(1, 7, 3, 1));



            //////////////////////////
            /*function shipsState(){
            var salvoesOrdenado = gameData.salvoes;

            salvoesOrdenado.sort(function (a, b) {
              if (a.turn> b.turn) {
                    return 1;
                  }
              if (a.turn < b.turn) {
                    return -1;
                  }
                  return 0;
                });

            */