let ws;
let timeoutTimer = 0;
let debugMode = true;

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
        } else if (k.t === 3) { //CONDITIONAL CLASS CHECK
            handleConditionalClass(k);
        } else if (k.t === 4) { //M ATTR CHECK
            handleMAttributeChange(k);
        }
    });
}

function handleMAttributeChange(k) {
    debug(k);
    const expressionEval = evalCondition(injectVariablesIntoExpression(k.c));
    switch(k.f) {
        case "DISABLED":
            handleMAttribute(k.v,(e) => { e.setAttribute("disabled", true); } ,(e) => { e.removeAttribute("disabled"); }, expressionEval);
            break;
        case "HIDE":
            handleVisibilityConditionals(document.getElementsByClassName(k.v), !expressionEval)
            break;
        default:
        // code block
    }
}

function injectVariablesIntoExpression(expression) {
    const found = expression.match(new RegExp("\\$\\w+(-?\\w*)*"));
    for(const toReplace of found) {
        expression = expression.replaceAll(toReplace, variables[toReplace.substring(1)]);
    }
    return expression;
}

function handleMAttribute(mId, trueFunc, falseFunc, evalValue) {
    const list = document.getElementsByClassName(mId);
    for (let elem of list) {
        if (evalValue) {
            trueFunc(elem);
        } else {
            falseFunc(elem);
        }
    }
}

function handleDefaultEvent(k) {
    variables[k.f] = k.v;
    document.querySelectorAll("[from-value="+k.f+"]").forEach(function(e) {
        if(e.hasAttribute("value")) {
            e.setAttribute("value", k.v);
            e.dispatchEvent(new Event('input'));
        } else {
            e.innerText = k.v;
        }
    });

    handleWaitingForEnabled();
}

function handleWaitingForEnabled() {
    for (let index = 0; index < waitingForEnable.length; index++) {
        const objWaitingForEnable = waitingForEnable[index];
        if(evalCondition(injectVariablesIntoExpression(objWaitingForEnable.expression))) {
            objWaitingForEnable.elem.disabled = false;
            waitingForEnable.splice(index, 1);
        }
    }

    if(waitingForEnable.length === 0) {
        handleVisibilityConditionals([document.getElementById("m-top-load-bar")], false);
        handleVisibilityConditionals([document.getElementById("m-full-loader")], false);
    }
}

function handleTitleChangeEvent(k) {
    document.title = k.v;
}

function handleIterationCheck(k) {
    let template = document.getElementById(k.f);
    let index = 0;

    document.querySelectorAll("[template-id="+k.f+"]").forEach(function(e) { e.remove(); });

    for(const currentEachValue of variables[k.v]) {
        let newDiff = document.createElement("div");
        newDiff.setAttribute("index", index++);
        newDiff.setAttribute("template-id", k.f);

        newDiff.innerHTML = template.innerHTML;
        recursiveObjectUpdate(newDiff, currentEachValue, "$each");

        template.parentNode.insertBefore(newDiff, template);
    }
}

function recursiveObjectUpdate(diff, obj, path) {
    if(typeof obj === 'object' && obj !== null) {
        diff.innerHTML = diff.innerHTML.replaceAll("["+path+"]", JSON.stringify(obj));
        for(const objKey of Object.keys(obj)) {
            recursiveObjectUpdate(diff, obj[objKey], path + "." + objKey);
        }
    } else {
        diff.innerHTML = diff.innerHTML.replaceAll("["+path+"]", obj);
    }
}

function handleConditionCheckEvent(k) {
    let conditionEval = evalCondition(injectVariablesIntoExpression(k.c));
    let elems = document.getElementsByClassName(k.v);
    handleVisibilityConditionals(elems, conditionEval);

    //update templates if needed
    let templates = document.getElementsByTagName("template");
    if(null !== templates && templates.length !== 0) {
        for(const template of templates) {
            const templateElems = template.content.querySelectorAll("." + k.v);
            handleVisibilityConditionals(templateElems, conditionEval);
        }
    }
}

function handleVisibilityConditionals(elems, isVisible) {
    if(null !== elems && elems.length !== 0) {
        if(isVisible) {
            for(let elem of elems) {
                if(elem != null) elem.style.display = null;
            }
        } else {
            for(let elem of elems) {
                if(elem != null) elem.style.display = "none";
            }
        }
    }
}

function handleConditionalClass(k) {
    let condition = k.c;
    const found = condition.match(new RegExp("\\$\\w+(-?\\w*)*"));
    for(const toReplace of found) {
        condition = condition.replaceAll(toReplace, variables[toReplace.substring(1)]);
    }

    const conditionEval = evalCondition(condition);
    const relevantElem = document.querySelector("[data-from='"+k.v+"']");

    let fullClasses = "";
    if(undefined !== relevantElem.dataset.baseClass) {
        fullClasses = relevantElem.dataset.baseClass + " ";
    }
    fullClasses += conditionEval;
    relevantElem.className = fullClasses.trim();
}

function evalCondition(condition){
    return Function('"use strict";return (' + condition + ')')();
}

function debug(textToLog, fullObject) {
    if(debugMode) {
        console.log(textToLog, fullObject);
    }
}
let waitingForEnable = [];
function sendEvent(origin, e) {
    const disableOnClick = origin.attributes["m-disable-on-click-until"];
    if(disableOnClick !== undefined) {
        const loadingStyle = origin.attributes["m-loading-style"];

        waitingForEnable.push({"elem": origin, "expression": disableOnClick.value});
        origin.disabled = true;
        if(loadingStyle === undefined || loadingStyle.value === "top") {
            handleVisibilityConditionals([document.getElementById("m-top-load-bar")], true);
        } else if (loadingStyle.value === "full") {
            handleVisibilityConditionals([document.getElementById("m-full-loader")], true);
        }
    }
    ws.send(e);
}
