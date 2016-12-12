// blocker.js
/*
   This code is a bad example! It will
   block the server. It is for demonstration
   only.
*/

var restify = require('restify');
var fs = require('fs');


var server = restify.createServer();

server.use(restify.acceptParser(server.acceptable));
server.use(restify.jsonp());
server.use(restify.bodyParser({mapParams : true}))

// method Receives JSON login
server.post('/caronas/login',function(req, res) {
   var record_value = req.params.record;
   var pass_value = req.params.password;

   var response_code;

   if(record_value === "1234" && pass_value === "1111"){
     response_code = 200;
   }else{
     response_code = 401;
   }
   console.log(response_code);
   res.send(response_code);
});

// method Receives JSON user and coordinates
server.post('/caronas/register_user_and_coordinates',function(req, res) {
   var latitude_value = req.params.latitude;
   var longitude_value = req.params.longitude;

   var name_value = req.params.name;
   var record_value = req.params.record;
   var password_value = req.params.password;

   console.log('User: '+name_value+' Record:  '+record_value+' Passeord: '+password_value);
   console.log('\nlat: '+latitude_value+' lon: '+longitude_value);

   res.send(200);
});



// Home page:
server.get('/',function(req, res) {
   var bodyHtml = '<!DOCTYPE html><html><head><title>'
                + 'Teste Node.js - O Bom Programador</title></head>'
                + '<body>'
		+ '<br/>Ok, agora, <a href="/blocker">o blocker</a>';


   bodyHtml += '</code></pre></body></html>';
   res.writeHead(200, {
     'Content-Length': Buffer.byteLength(bodyHtml),
     'Content-Type': 'text/html'
   });
   res.write(bodyHtml);
   res.end();
});

// Blocker page:
server.get('/blocker',function(req, res) {
   var inicio = new Date();
   var bodyHtml = '<!DOCTYPE html><html><head><title>'
                + 'Teste Node.js - O Bom Programador</title></head>'
                + '<body>';

   var resultado = fibonacci(45);
   var fim = new Date();
   bodyHtml += '<br/>' + resultado;
   bodyHtml += '<br/>inicio: ' + inicio + ' fim: ' + fim;
   bodyHtml += '</code></pre></body></html>';
   res.writeHead(200, {
     'Content-Length': Buffer.byteLength(bodyHtml),
     'Content-Type': 'text/html'
   });
   res.write(bodyHtml);
   res.end();
});



// Start server
server.listen(8080, function() {
  console.log('Online: 8080');
});
