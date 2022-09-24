const { RSocketConnector } = require("rsocket-core");
const { WebsocketClientTransport } = require("rsocket-websocket-client");
const escape = require("@braintree/sanitize-url").sanitizeUrl;
const {
    encodeCompositeMetadata,
    encodeRoute,
    WellKnownMimeType,
    encodeSimpleAuthMetadata,
} = require("rsocket-composite-metadata");
const morphdom = require("morphdom");

function Medusa() {}
const _M = new Medusa();

const debugMode = true;

const MAX_REQUEST_N = 2147483647;
let stream;

//TODO https://github.com/viglucci/rsocket-chat-demo/blob/main/frontend/src/context/ClientProvider.jsx
//TODO read https://github.com/viglucci/rsocket-chat-demo/blob/main/frontend/src/hooks/useChat.js

async function setupRouter() {
    const map = new Map();
    map.set(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION, encodeSimpleAuthMetadata(_M.sessionId, _M.wsP));
    const compositeMetaData = encodeCompositeMetadata(map);

    //determine socket url
    const socketArray = window.location.href.split("://");
    const socketProtocol = socketArray[0].replace("http", "ws");
    const socketRootUrl = socketArray[1].split("/")[0];
    const socketUrl = socketProtocol + "://" + socketRootUrl + _M.wsURL;

    const connector = new RSocketConnector({
        setup: {
            payload: {
                metadata: compositeMetaData
            },
            keepAlive: 10000, //determines how long between health checks - heroku kills connections at 30/55s, so should be earlier
            lifetime: 11000, //determines how long we can 'miss' a health check before we determine connection is dead
            dataMimeType: WellKnownMimeType.APPLICATION_JSON.string,
            metadataMimeType: WellKnownMimeType.MESSAGE_RSOCKET_COMPOSITE_METADATA.string,
        },
        transport: new WebsocketClientTransport({
            url: socketUrl
        }),
    });
    const rsocket = await connector.connect();
    stream = await buildStream(rsocket);
}


function sendMessage(payloadData) {
    stream.onNext({
        data: Buffer.from(JSON.stringify(payloadData))
    });
}

document.addEventListener("DOMContentLoaded", setupRouter);

Medusa.prototype.doFormAction = function(event, parentFragment, actionToExecute) {
    const multiElems = [];
    for(const multiElem of event.target.querySelectorAll("[multiple]")) {
        multiElems.push(multiElem.name);
    }

    const formData = new FormData(event.target);
    const formProps = {};
    for (const [key, value] of formData) {
        let v = value;
        if(typeof formProps[key] !== "undefined") { //existing value
            if(!Array.isArray(formProps[key])) {
                formProps[key] = [formProps[key]];
            }
            formProps[key].push(v);
        } else {
            if(multiElems.includes(key) && !Array.isArray(value)) {
                v = [value];
            }
            formProps[key] = v;
        }
    }
    _M.doAction(event, parentFragment, actionToExecute.replace(":{form}", JSON.stringify(formProps) ));
}

Medusa.prototype.doAction = function(event, parentFragment, actionToExecute) {
    if(typeof event !== "undefined") {
        event.preventDefault();
    }
    if(typeof event.target.attributes['m:loading-until'] !== 'undefined') {
        const waitFor = getTargetAttributeIfExists(event,'m:loading-until');
        const loadingStyle = getTargetAttributeIfExists(event,'m:loading-style');

        let loader;
        if(loadingStyle === 'top') {
            loader = document.getElementById("m-top-load-bar");
        } else if(loadingStyle === 'button') {
            loader = document.getElementById("m-full-loader");
        } else {
            loader = document.getElementById("m-full-loader");
        }

        if(typeof loader !== "undefined") {
            loader.setAttribute("waiting-for", waitFor);
            loader.removeAttribute("style");
        }
    }

    sendMessage({
        "fragment": parentFragment,
        "action": actionToExecute
    });
    return false;
};

getTargetAttributeIfExists = function(event, attribute) {
    const a = event.target.attributes[attribute];
    if(typeof a !== 'undefined') {
        return a.value;
    } else {
        return null;
    }
};

Medusa.prototype.doActionOnKeyUp = function(key, event, parentFragment, actionToExecute) {
    if(event.key === key || event.keyCode === key) {
        _M.doAction(event, parentFragment, actionToExecute);
    }
};

evalXPath = function(xpath) {
    return document.evaluate(xpath, document, null, XPathResult.ANY_UNORDERED_NODE_TYPE, null ).singleNodeValue;
};

doLookups = function (listOfDiffs) {
    for(let diff of listOfDiffs) {
        if (diff.xpath !== null) {
            if (diff.xpath.endsWith("::first")) { //additions - no previous entry, so pick parent pom and mark as first entry
                diff.firstEntry = true;
                diff.xpath = diff.xpath.substring(0, diff.xpath.length - 8); //8 = '/::first'.length
            }
            diff.element = evalXPath(diff.xpath);
        }
    }
    return listOfDiffs;
};

handleIncomingAddition = function (obj) {
    let existingNode = obj.element;

    if(existingNode === null) { //if node added by previous event, it would only exist now, so do last chance lookup
        existingNode = evalXPath(obj.xpath);
    }

    let nodeToAdd = htmlToElement(obj.content);

    if(existingNode !== null && nodeToAdd !== null) {
        if(obj.firstEntry) {
            //existingNode is parentNode, so add as child
            existingNode.appendChild(nodeToAdd);
        } else {
            //existing node is previous node, so do an 'add after' (= addBefore of nextSibling)
            existingNode.parentNode.insertBefore(nodeToAdd, existingNode.nextSibling);
        }
    } else {
        console.error("failed to add", obj.xpath);
        console.log("handleIncomingAddition: obj", obj);
        console.log("handleIncomingAddition: element", existingNode);
        console.log("handleIncomingAddition: nodeToAdd", nodeToAdd);
    }
};

handleAttrChange = function (obj) {
    let element = obj.element;

    if(element !== null) {
        element.setAttribute(obj.attribute, obj.content);
    } else {
        console.error("failed to attr value change", obj.xpath);
        console.log("handleAttrChange: obj", obj);
        console.log("handleAttrChange: element", element);
        console.log("--");
    }
};

handleMorph = function (obj) {
    let element = obj.element;

    if(element !== null) {
        morphdom(element, obj.content);
    } else {
        console.error("failed to morphdom", obj.xpath);
        console.log("handleMorph: obj", obj);
        console.log("handleMorph: element", element);
        console.log("--");
    }
};

handleRemoval = function(obj) {
    let element = obj.element;
    if(element !== null) {
        element.remove();
    } else {
        console.error("failed to remove", obj.xpath);
        console.log("handleRemoval: obj", obj);
        console.log("handleRemoval: element", element);
    }
};

applyAllChanges = function (listOfDiffs) {
    for(let diff of listOfDiffs) {
        if(diff.type === "ADDITION") {
            handleIncomingAddition(diff);
        } else if(diff.type === "EDIT") {
            handleMorph(diff);
        } else if(diff.type === "REMOVAL") {
            handleRemoval(diff);
        } else if(diff.type === "ATTR_CHANGE") {
            handleAttrChange(diff);
        } else if(diff.type === "REDIRECT") {
            window.location.href = escape(diff.content);
        } else if(diff.type === "JS_FUNCTION") {
            runFunction(escape(diff.content), []);
        } else if(diff.type === "LOADING") {
            applyLoadingUpdate(escape(diff.content));
        }
    }
};

applyLoadingUpdate = function(loadingName) {
    for(const elem of document.querySelectorAll("[waiting-for='"+loadingName+"']")) {
        if(elem.id === "m-top-load-bar" || elem.id === "m-full-loader") {
            elem.setAttribute("style", "display: none;");
        } else {
            elem.removeAttribute("disabled");
        }
    }
}

runFunction = function(name, arguments) {
    const fn = window[name];
    if(typeof fn !== 'function')
        return;

    fn.apply(window, arguments);
};

handleIncomingChange = function (obj) {
    debugLog(obj);
    const toApply = doLookups(obj);
    applyAllChanges(toApply);
};

debugLog = function (objToLog) {
    if(debugMode) {
        console.log(objToLog);
    }
};

async function buildStream(rsocket) {
    const encodedRoute = encodeRoute("event-emitter/" + _M.controller + "/" + _M.sessionId);
    const map = new Map();
    map.set(WellKnownMimeType.MESSAGE_RSOCKET_ROUTING, encodedRoute);
    map.set(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION, encodeSimpleAuthMetadata(_M.sessionId, _M.wsP));
    const compositeMetaData = encodeCompositeMetadata(map);

    return rsocket.requestChannel({
            metadata: compositeMetaData
        },
        MAX_REQUEST_N,
        false,
        {
            onError(error) {
                if(!debugMode) {
                    location.reload();
                } else {
                    console.error(error);
                }
            },
            onComplete() {
                if(!debugMode) {
                    location.reload();
                }
            },
            onNext(payload, isComplete) {
                handleIncomingChange(JSON.parse(payload.data.toString()));
            },
            onExtension(extendedType, content, canBeIgnored) {
            },
            request(requestN) { },
            cancel() {
                if(!debugMode) {
                    location.reload();
                }
            }
        });
}

const tempTemplate = document.createElement("template");
htmlToElement = function (html) {
    html = html.trim();
    tempTemplate.innerHTML = html;
    return tempTemplate.content.firstChild;
};

module.exports = _M;
