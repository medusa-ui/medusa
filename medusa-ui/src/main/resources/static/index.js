const { RSocketConnector } = require("rsocket-core");
const { WebsocketClientTransport } = require("rsocket-websocket-client");
const {
    encodeCompositeMetadata,
    encodeRoute,
    WellKnownMimeType,
    encodeSimpleAuthMetadata,
} = require("rsocket-composite-metadata");
const morphdom = require('morphdom');

const MAX_REQUEST_N = 2147483647;

//TODO https://github.com/viglucci/rsocket-chat-demo/blob/main/frontend/src/context/ClientProvider.jsx
//TODO read https://github.com/viglucci/rsocket-chat-demo/blob/main/frontend/src/hooks/useChat.js

async function setupRouter() {
    const map = new Map();
    map.set(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION, encodeSimpleAuthMetadata("user", "pass"));
    const compositeMetaData = encodeCompositeMetadata(map);

    const connector = new RSocketConnector({
        setup: {
            payload: {
                metadata: compositeMetaData
            },
            keepAlive: 60000,
            lifetime: 180000,
            dataMimeType: WellKnownMimeType.APPLICATION_JSON.string,
            metadataMimeType: WellKnownMimeType.MESSAGE_RSOCKET_COMPOSITE_METADATA.string,
        },
        transport: new WebsocketClientTransport({
            url: 'ws://localhost:7000/socket'
        }),
    });
    const rsocket = await connector.connect();
    stream = await buildStream(rsocket);
}

let stream;
async function buildStream(rsocket) {
    const encodedRoute = encodeRoute('event-emitter/' + _M.controller + "/" + _M.sessionId);
    const map = new Map();
    map.set(WellKnownMimeType.MESSAGE_RSOCKET_ROUTING, encodedRoute);
    map.set(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION, encodeSimpleAuthMetadata("user", "pass"));
    const compositeMetaData = encodeCompositeMetadata(map);

    return rsocket.requestChannel({
        metadata: compositeMetaData
    },
    MAX_REQUEST_N,
    false,
    {
        onError(error) {
            console.error(error);
        },
        onComplete() {
            console.error('peer stream complete');
        },
        onNext(payload, isComplete) {
            handleIncomingChange(JSON.parse(payload.data.toString()));
        },
        onExtension(extendedType, content, canBeIgnored) {
        },
        request(requestN) {
            console.log(`peer requested ${requestN}`)
        },
        cancel() {
            console.log(`peer canceled`)
        }
    });
}

function sendMessage(payloadData) {
    stream.onNext({
        data: Buffer.from(JSON.stringify(payloadData))
    });
}

document.addEventListener('DOMContentLoaded', setupRouter);

const _M = new Medusa();
function Medusa() {}
Medusa.prototype.doAction = function(parentFragment, actionToExecute) {
    sendMessage({
        "fragment": null,
        "action": actionToExecute
    });
}

handleIncomingChange = function (obj) {
    const toApply = doLookups(obj);
    applyAllChanges(toApply);
};

doLookups = function (listOfDiffs) {
    for(let diff of listOfDiffs) {
        if(diff.xpath.endsWith("::first")) { //additions - no previous entry, so pick parent pom and mark as first entry
            diff.firstEntry = true;
            diff.xpath = diff.xpath.substring(0, diff.xpath.length - 8); //8 = '/::first'.length
        }
        diff.element = evalXPath(diff.xpath);
    }
    return listOfDiffs;
}

applyAllChanges = function (listOfDiffs) {
    for(let diff of listOfDiffs) {
        if(diff.type === "ADDITION") {
            handleIncomingAddition(diff);
        } else if(diff.type === "EDIT") {
            handleMorph(diff);
        } else if(diff.type === "REMOVAL") {
            handleRemoval(diff);
        }
    }
}

handleIncomingAddition = function (obj) {
    let existingNode = obj.element;
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
}

handleMorph = function (obj) {
    let element = obj.element;
    console.log("handleMorph: obj", obj);
    console.log("handleMorph: element", element);
    console.log("--");
    if(element !== null) {
        morphdom(element, obj.content);
    } else {
        console.error("failed to morphdom", obj.xpath);
    }
}

handleRemoval = function(obj) {
    let element = obj.element;
    if(element !== null) {
        element.remove();
    } else {
        console.error("failed to remove", obj.xpath);
        console.log("handleRemoval: obj", obj);
        console.log("handleRemoval: element", element);
    }
}

evalXPath = function(xpath) {
    return document.evaluate(xpath, document, null, XPathResult.ANY_UNORDERED_NODE_TYPE, null ).singleNodeValue;
}

const tempTemplate = document.createElement('template');
htmlToElement = function (html) {
    html = html.trim();
    tempTemplate.innerHTML = html;
    return tempTemplate.content.firstChild;
};

module.exports = _M;