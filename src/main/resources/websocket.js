var ws;
var timeoutTimer = 0;

retryConnection();

function retryConnection() {
    setTimeout(function() {
        if(timeoutTimer < 1000) {
            timeoutTimer += 150;
        } else {
            timeoutTimer = 1000;
        }

        try{
            ws = new WebSocket("%WEBSOCKET_URL%");
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
                vR(JSON.parse(message.data));
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

function vR(e) {
    e.forEach(k => {
        if(k.t === undefined) {
            document.querySelectorAll("[from-value="+k.f+"]").forEach(function(e) { e.innerText = k.v; });
        } else if (k.t === 0) {
            document.title = k.v;
        }
    });
}

function sE(e) { 
    ws.send(e); 
}