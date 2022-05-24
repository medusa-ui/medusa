var _M = _M || {};
var module = module || {};

_M.ws = null;
_M.timeoutTimer = 0;
_M.retryAttempts = 0;
_M.retryMode = false;
_M.fatalMode = false;
_M.isLocal = window.location.host.indexOf("localhost") !== -1;

_M.retryConnection = function () {
    _M.retryAttempts++;

    if(!_M.retryMode && _M.retryAttempts > 2) {
        document.body.innerHTML += _M.connectionDropRetry;
        _M.retryMode = true;
    }

    if(!_M.fatalMode && _M.retryAttempts > 5) {
        document.body.innerHTML += _M.connectionDropFatal;
        _M.fatalMode = true;
    }

    setTimeout(function() {
        if(_M.timeoutTimer < 1000) {
            _M.timeoutTimer += 150;
        } else {
            _M.timeoutTimer = 1000;
        }

        try{
            _M.ws = new WebSocket(((_M.isLocal) ? "ws://" : "wss://") + window.location.host + "%WEBSOCKET_URL%");
            if(_M.ws.readyState === _M.ws.CLOSED || _M.ws.readyState === _M.ws.CLOSING) {
                _M.retryConnection();
            }
            _M.ws.onopen = function() {
                _M.debug("ws.onopen", _M.ws);
                _M.debug("ws.readyState", "wsstatus");
                _M.retryAttempts = 0;
                if(_M.fatalMode) document.getElementById('m-top-error-fatal').remove();
                if(_M.retryMode) document.getElementById('m-top-error-retry').remove();
                _M.fatalMode = false;
                _M.retryMode = false;
                _M.ws.send("unq//%SECURITY_CONTEXT_ID%");
            }
            _M.ws.onclose = function(error) {
                _M.debug("ws.onclose", _M.ws, error);
                _M.retryConnection();
            }
            _M.ws.onerror = function(error) {
                _M.debug("ws.onerror", _M.ws, error);
                _M.log("An error occurred");
            }
            _M.ws.onmessage = function(message) {
                if(message.data.indexOf("unq//") === -1) {
                    _M.debug("ws.onmessage", _M.ws, message);
                    let data = JSON.parse(message.data);
                    if(typeof _M.preEventHandler !== "undefined") { _M.preEventHandler(data); }
                    _M.eventHandler(data);
                    if(typeof _M.postRender !== "undefined") { _M.postRender(data); }
                } else {
                    _M.debug("ws.onmessage - unq confirm", _M.ws, message);
                }
            }
        } catch (e) {
            _M.debug(e);
            _M.retryConnection();
        }
    }, _M.timeoutTimer);
};

_M.log = function(responseEvent) {
    _M.debug(responseEvent);
};

_M.debug = function(textToLog, fullObject) {
    if(_M.debugMode) {
        console.log(textToLog, fullObject);
    }
};

_M.sendEvent = function(originElem, e) {
    _M.debug("sendEvent: " + e );
    const disableOnClick = originElem.attributes["m:disable-on-click-until"];
    if(typeof disableOnClick !== "undefined") {
        const loadingStyle = originElem.attributes["m:loading-style"];

        //_M.waitingForEnable.push({"elem": originElem, "expression": disableOnClick.value});
        originElem.disabled = true;
        if(typeof loadingStyle === "undefined" || loadingStyle.value === "top") {
            //TODO _M.handleVisibilityConditionals([document.getElementById("m-top-load-bar")], true);
        } else if (loadingStyle.value === "full") {
            //TODO _M.handleVisibilityConditionals([document.getElementById("m-full-loader")], true);
        }
    }

    //TODO e = _M.parseReference(e, originElem);
    _M.ws.send(e);
};

_M.eventHandler = function(e) {
    _M.debug(e);
    e.forEach(obj => { _M.handleIncomingChange(obj); });
};

_M.handleIncomingChange = function (obj) {
    if(obj.type === "ADDITION") {
        _M.handleIncomingAddition(obj);
    } else if(obj.type === "EDIT") {
        _M.handleMorph(obj);
    } else if(obj.type === "REMOVAL") {
        _M.handleMorph(obj);
    }
};

_M.handleIncomingAddition = function (obj) {
    let element = _M.evalXPath(obj.xpath);
    let nodeToAdd = _M.htmlToElement(obj.content);

    element.parentNode.insertBefore(nodeToAdd, element);
}

_M.handleMorph = function (obj) {
    let element = _M.evalXPath(obj.xpath);
    morphdom(element, obj.content);
}

_M.handleRemoval = function(obj) {
    let element = _M.evalXPath(obj.xpath);
    element.delete();
}

_M.evalXPath = function(xpath) {
    return document.evaluate(xpath, document, null, XPathResult.ANY_UNORDERED_NODE_TYPE, null ).singleNodeValue;
}

_M.tempTemplate = document.createElement('template');
_M.htmlToElement = function (html) {
    html = html.trim();
    _M.tempTemplate.innerHTML = html;
    return _M.tempTemplate.content.firstChild;
};

_M.retryConnection();

_M.connectionDropRetry = "<div id='m-top-error-retry' style=\"position: absolute; top: 0; left: 0; background:orange; color: black; width:100%; text-align:center;\">Your connection with the server dropped. Retrying to connect.</div>";
_M.connectionDropFatal = "<div id='m-top-error-fatal' style=\"position: absolute; top: 0; left: 0; background:red; color: black; width:100%; text-align:center;\">Your connection with the server dropped and reconnecting is not possible. Please reload.</div>";
