Vue.http.options.emulateJSON = true;

var gameData=[];


var app = new Vue({
  el: '#appVue',
  data: {
      lists: [],
      scores:[],
      currentUser:"",
   },

    methods: {
      returnToGame: function(game) {
       var gamePlayer = this.obtenerGamePlayerParaUsuarioActual(game);
        window.location.href = "../web/game.html?gp="+gamePlayer.id;
      },
      obtenerGamePlayerParaUsuarioActual: function(game) {
        return  game.gamePlayers.find(gamePlayer => gamePlayer.player.email === this.currentUser);
      },
      hayLugarEnElJuegoYNoEstaElUsuarioActual: function(game) {
        return game.gamePlayers.length === 1 && !this.obtenerGamePlayerParaUsuarioActual(game);
      },
      unirteAlJuego: function(gameId) {
          this.$http.post('/api/game/'+gameId+'/players')
                  .then(response => {
                       window.location.href = "../web/game.html?gp="+response.body.id;
                  });

        //hacer una llamada ajax con fetch o post para llamar al controller y que unise al juego
        // /api/unirseAlJuego
        // pasarle como parámetro el juego
        // tiene que devolver el gameplayerId
        // hacer la redirección a con algo tipo   window.location.href = "../web/game.html?gp="+gamePlayerId;
      },

      crearJuego: function () {
            this.$http.post('/api/Games')
                          .then(response => {
                              window.location.href = "../web/game.html?gp="+response.body.id;
                            });
        //hacer una llamada ajax con fetch o post para llamar al controller y que crear un juego
        // /api/crearJuego
        // tiene que devolver el gameplayerId
        // hacer la redirección a con algo tipo   window.location.href = "../web/game.html?gp="+gamePlayerId;
      },

   }});


var login = new Vue({
    el: '#login-form',
    data: {
      email: '',
      password: '',
      error: false,
      currentUser:''
    },
    methods: {
      login: function() {
            this.error = false;
              this.$http.post('/api/login', { name: this.email, pwd: this.password })
                    .then(response => {
                        location.reload();
                       }, response => {
                             this.error = true;
                             this.email = '';
                             this.password = '';
                       });
      },
      logout: function() {
                this.$http.post('/api/logout')
                      .then(response => {
                          location.reload();
                         }, response => {
                         });
                }
    }
});


$(function(){

var games = "http://localhost:8080/api/Games";

fetch(games,{
    method: 'GET'
  }).then(function(response){     ///.then(response=>response.json())
        return response.json();
      }).then(function(json){    ///.then(json=>{this.all_data=json})
    app.lists = json.games;
   // app.all_data=json;  /// es lo mismo que hacer all_data=json;?????
    gameData = json.games;
    login.currentUser = json.playerActual.email;
    app.currentUser = json.playerActual.email;
    puntuaciones();

  }).catch(function(error){
    console.log("Fail")
  })
});


function puntuaciones(){
    gameData.forEach(function (game){
        game.gamePlayers.forEach(function(gamePlayer){
            /// guardo en la variable indice el resutado de encontrar en indice del objeto score desaedo por su propíedad.
            //.findIndex me devuelve -1 cuando no se encuentra
            var indice = app.scores.findIndex(score => score.name === gamePlayer.player.email);
            if(indice < 0){
                var  scorePlayer = {
                     name: gamePlayer.player.email,
                     total: 0,
                     victorias: 0,
                     derrotas: 0,
                     empates: 0,

                };

                switch(gamePlayer.score){
                    case 1: scorePlayer.victorias++; break;
                    case 0: scorePlayer.derrotas++; break;
                    case 0.5: scorePlayer.empates++; break;
                }

                scorePlayer.total = scorePlayer.victorias + (scorePlayer.empates * 0.5);

                app.scores.push(scorePlayer);
                //console.log(gamePlayer.score);
            }
            else{
                switch(gamePlayer.score){
                    case 1: app.scores[indice].victorias++; break;
                    case 0: app.scores[indice].derrotas++; break;
                    case 0.5: app.scores[indice].empates++; break;
                }

                app.scores[indice].total = app.scores[indice].victorias + (app.scores[indice].empates * 0.5);
            }
        })
    })
}










 /*$( "<ul/>", {
    "class": "my-new-list",
    html: items.join( "" )
  }).appendTo("body");

////////////////////////////////////////
 var  indice = app.scores.findIndex(score => score.name === gamePlayer.player.email);
 function calculo(gamePlayer,scorePlayer,indice){
     switch(gamePlayer.score){
                         case 1: app.scores[indice].victorias++; break;
                         case 0: app.scores[indice].derrotas++; break;
                         case 0.5: app.scores[indice].empates++; break;
     }
     scorePlayer.total = scorePlayer.victorias + (scorePlayer.empates * 0.5);
     app.scores.push(scorePlayer);
 return  scores
 }

 */




 /*$.getJSON("http://localhost:8080/api/Games", function(data){
   var items = [];
   $.each(data, function(key, val) {
     items.push( "<li id='" + val.id + "'>" + "Game:" + val.Id + " | " + val.Created + " | " + "gamePlayers: " + 'Id: '+ val.GamePlayer[0].Id + " player: "+ val.GamePlayer[0].Player.id + " email: " + val.GamePlayer[0].Player.userName + " , " + "gamePlayers: " + 'Id: ' + val.GamePlayer[1].Id + " player: " + val.GamePlayer[1].Player.id +" email: "+ val.GamePlayer[1].Player.userName + "</li>" );
   });*/

    /*$( "<ul/>", {
       "class": "my-new-list",
       html: items.join( "" )
     }).appendTo("body");
    });*/