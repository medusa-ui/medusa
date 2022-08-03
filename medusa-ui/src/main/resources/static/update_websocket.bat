call npm install
call npm audit fix --force
call browserify --s _M -g uglifyify ./index.js > websocket.js