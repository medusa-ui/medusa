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
    const form = event.target;

    for(const multiElem of form.querySelectorAll("[multiple]")) {
        multiElems.push(multiElem.name);
    }

    // output multiple checkboxes with same name should be an array
    const names = [];
    for(const named of form.querySelectorAll("input[type='checkbox'][name]")) {
        let name = named.name;
        if(names.includes(name)) { // same name more the once
            multiElems.push(name);
        } else {
            names.push(name);
        }
    }

    const formData = new FormData(form);
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

const buttonLoader = document.getElementById("m-template-button-load").content.firstElementChild.outerHTML;

Medusa.prototype.doAction = function(event, parentFragment, actionToExecute) {
    if(typeof event !== "undefined") {
        event.preventDefault();
    }
    const target = event.target;
    if(typeof target.attributes['data-loading-until'] !== 'undefined') {
        const waitFor = getTargetAttributeIfExists(event,'data-loading-until');
        const loadingStyle = getTargetAttributeIfExists(event,'data-loading-style');

        let loader;
        if(loadingStyle === 'top') {
            loader = document.getElementById("m-top-load-bar");
        } else if(loadingStyle === 'button') {
            loader = null;
        } else {
            loader = document.getElementById("m-full-loader");
        }

        if(typeof loader !== "undefined" && null != loader) {
            loader.setAttribute("waiting-for", waitFor);
            loader.removeAttribute("style");
        }

        target.setAttribute("waiting-for", waitFor);
        target.setAttribute("disabled", true);
        target.innerHTML = buttonLoader + target.innerHTML;
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
    return document.evaluate("/html[1]" + xpath, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null ).singleNodeValue;
};

handleSequenceChange = function (obj) {
    let xpathToAddBefore = obj.content;

    if("::LAST" === xpathToAddBefore) {
        obj.element.parentNode.appendChild(obj.element);
    } else {
        if(obj.contentElement === null && obj.content !== null) {
            obj.contentElement = evalXPath(obj.content); //why?
        }
        obj.element.parentNode.insertBefore(obj.element, obj.contentElement);
    }
};

handleIncomingAddition = function (obj) {
    let nodeToAdd = htmlToElement(obj.content);

    if(obj.before !== undefined) {
        let matchingBeforeElement = evalXPath(obj['before']);
        addBefore(matchingBeforeElement, nodeToAdd);
    } else if(obj.after !== undefined) {
        let matchingAfterElement = evalXPath(obj['after']);
        addAfter(matchingAfterElement, nodeToAdd);
    } else if(obj.in !== undefined) {
        let matchingParentElement = evalXPath(obj['in']);
        matchingParentElement.appendChild(nodeToAdd);
    }
};

addBefore = function (reference, elementToAdd) {
    reference.parentNode.insertBefore(elementToAdd, reference);
};

addAfter = function (reference, elementToAdd) {
    reference.parentNode.insertBefore(elementToAdd, reference.nextSibling);
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
    let element = evalXPath(obj.xpath);

    if(element !== null) {
        //morphdom(element, obj.content);
        element.textContent = obj.content;
    } else {
        console.error("failed to morphdom", obj.xpath);
        console.log("handleMorph: obj", obj);
        console.log("handleMorph: element", element);
        console.log("--");
    }
};

handleRemoval = function(obj) {
    let element = evalXPath(obj.xpath);
    if(element !== null) {
        element.remove();
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
        } else if(diff.type === "SEQUENCE_CHANGE") {
            handleSequenceChange(diff);
        }
    }
};

applyLoadingUpdate = function(loadingName) {
    for(const elem of document.querySelectorAll("[waiting-for='"+loadingName+"']")) {
        if(elem.id === "m-top-load-bar" || elem.id === "m-full-loader") {
            elem.setAttribute("style", "display: none;");
        } else {
            elem.removeAttribute("disabled");
            let loadingSpan = elem.querySelector("span[class='m-button-loader']");
            if(null != loadingSpan) {
                loadingSpan.remove();
            }
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
    applyAllChanges(obj);
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
