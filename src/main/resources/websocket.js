let ws;
let timeoutTimer = 0;

retryConnection();

//this makes sure that when a back button is used, it not only changes the url but then also reloads the page
window.addEventListener( "popstate", function ( event ) {
    let perfEntries = performance.getEntriesByType("navigation");
    if (perfEntries[0].type === "reload" || perfEntries[0].type === "back_forward") {
        window.location.reload();
    }
});

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
                condition = condition.replaceAll(toReplace, variables[toReplace.substring(1)]);
            }

            if(evalCondition(condition)) {
                console.log("#" + k.v + " // visible || " + condition);
                document.getElementById(k.v).style.display = null;
            } else {
                console.log("#" + k.v + " // hidden || " + condition);
                document.getElementById(k.v).style.display = "none";
            }
        } else if (k.t === 2) { //PAGE CHANGE
            let parser = new DOMParser();
            let doc = parser.parseFromString(k.v, 'text/html');
            document.querySelector('body').innerHTML = doc.body.innerHTML;
            //k.c = url => not the best choice here, just temp
            window.history.pushState(null, k.f, k.c);
            document.title = k.f;
            //with a full replace we get a white flash when styles change, so we should be able to deal with that
            //maybe we could change the body first (which we do now) and only the details of the head if any changes occur (and what about scripts?)
        }
    });
}

function evalCondition(condition){
    return Function('"use strict";return (' + condition + ')')();
}

function sendEvent(e) {
    ws.send(e);
}

function changePage(e) {
    ws.send("changePage(\""+e+"\")");
}