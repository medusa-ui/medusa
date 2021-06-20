let ws;
let timeoutTimer = 0;

retryConnection();

function retryConnection() {
    setTimeout(function() {
        if(timeoutTimer < 1000) {
            timeoutTimer += 150;
        } else {
            timeoutTimer = 1000;
        }

        try{
            ws = new WebSocket("ws://" + window.location.host + "%WEBSOCKET_URL%");
            if(ws.readyState === ws.CLOSED || ws.readyState === ws.CLOSING) {
                retryConnection();
            }
            ws.onopen = function() {
                console.log("ws.onopen", ws);
                console.log("ws.readyState", "wsstatus");
            }
            ws.onclose = function(error) {
                console.log("ws.onclose", ws, error);
                retryConnection();
            }
            ws.onerror = function(error) {
                console.log("ws.onerror", ws, error);
                log("An error occured");
            }
            ws.onmessage = function(message) {
                console.log("ws.onmessage", ws, message);
                eventHandler(JSON.parse(message.data));
            }
        } catch (e) {
            console.log(e);
            retryConnection();
        }
    }, timeoutTimer);
}

function log(responseEvent) {
    console.log(responseEvent);
}

function eventHandler(e) {
    e.forEach(k => {
        if(k.t === undefined) {
            variables[k.f] = k.v;
            document.querySelectorAll("[from-value="+k.f+"]").forEach(function(e) { e.innerText = k.v; });
        } else if (k.t === 0) { //DOCUMENT TITLE CHANGE
            document.title = k.v;
        } else if (k.t === 1) { //CONDITION CHECK
            let condition = k.c;
            const found = condition.match(new RegExp("\\$\\w+-?\\w*"));
            for(const toReplace of found) {
                condition = condition.replace(toReplace, variables[toReplace.substring(1)]);
            }

            if(evalCondition(condition)) {
                document.getElementById(k.v).style.display = null;
            } else {
                document.getElementById(k.v).style.display = "none";
            }
        }
    });
}

function evalCondition(condition){
    return Function('"use strict";return (' + condition + ')')();
}

function sendEvent(e) {
    ws.send(e); 
}