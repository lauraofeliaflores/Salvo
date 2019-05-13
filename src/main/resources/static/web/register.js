Vue.http.options.emulateJSON = true; // send as
  var gameData=[];


   var login = new Vue({
        el: '#register-form',
        data: {
          email: '',
          password: '',
          error: false
        },
        methods: {
          register: function() {
                this.error = false;
                  this.$http.post('/api/players', { userName: this.email, password: this.password })
                        .then(response => {
                            window.location.href = "../web/games.html";
                           }, response => {
                                 this.error = true;
                           });
          }
         }
   });