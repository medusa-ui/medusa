let ws;
let timeoutTimer = 0;
let debugMode = true;

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
                debug("ws.onopen", ws);
                debug("ws.readyState", "wsstatus");
            }
            ws.onclose = function(error) {
                debug("ws.onclose", ws, error);
                retryConnection();
            }
            ws.onerror = function(error) {
                debug("ws.onerror", ws, error);
                log("An error occured");
            }
            ws.onmessage = function(message) {
                debug("ws.onmessage", ws, message);
                eventHandler(JSON.parse(message.data));
            }
        } catch (e) {
            debug(e);
            retryConnection();
        }
    }, timeoutTimer);
}

function log(responseEvent) {
    debug(responseEvent);
}

function eventHandler(e) {
    debug(e);
    //e = full event, k = individual event per key, k.f = field, k.v = value or relevant id, k.t = type, k.c = condition
    e.forEach(k => {
        if(k.t === undefined) { //the default event, a value change, contains the least amount of data, so it has no 'type'
            handleDefaultEvent(k);
        } else if (k.t === 0) { //DOCUMENT TITLE CHANGE
            handleTitleChangeEvent(k);
        } else if (k.t === 1) { //CONDITION CHECK
            handleConditionCheckEvent(k);
        } else if (k.t === 2) { //ITERATION CHECK
            handleIterationCheck(k);
        }
    });
}

function handleDefaultEvent(k) {
    variables[k.f] = k.v;
    document.querySelectorAll("[from-value="+k.f+"]").forEach(function(e) { e.innerText = k.v; });
}

function handleTitleChangeEvent(k) {
    document.title = k.v;
}

function handleIterationCheck(k) {
    let template = document.getElementById(k.f);
    let index = 0;

    document.querySelectorAll("[template-id="+k.f+"]").forEach(function(e) { e.remove(); });

    for(v of variables[k.v]) {
        let newDiff = document.createElement("div");
        let currentEachValue = variables[k.v][index];
        newDiff.setAttribute("index", index++);
        newDiff.setAttribute("template-id", k.f);
        newDiff.innerHTML = template.innerHTML.replaceAll("[$each]", currentEachValue)

        template.parentNode.insertBefore(newDiff, template);
    }
}

function handleConditionCheckEvent(k) {
    let condition = k.c;
    const found = condition.match(new RegExp("\\$\\w+-?\\w*"));
    for(const toReplace of found) {
        condition = condition.replaceAll(toReplace, variables[toReplace.substring(1)]);
    }

    if(evalCondition(condition)) {
        debug("#" + k.v + " // visible || " + condition);
        document.getElementById(k.v).style.display = null;
    } else {
        debug("#" + k.v + " // hidden || " + condition);
        document.getElementById(k.v).style.display = "none";
    }
}

function evalCondition(condition){
    return Function('"use strict";return (' + condition + ')')();
}

function debug(textToLog, fullObject) {
    if(debugMode) {
        console.log(textToLog, fullObject);
    }
}

function sendEvent(e) {
    ws.send(e);
}

function changePage(e) {
    ws.send("changePage(\""+e+"\")");
}