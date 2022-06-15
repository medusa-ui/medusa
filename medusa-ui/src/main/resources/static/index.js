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
    if(obj.type === "ADDITION") {
        handleIncomingAddition(obj);
    } else if(obj.type === "EDIT") {
        handleMorph(obj);
    } else if(obj.type === "REMOVAL") {
        handleMorph(obj);
    }
};

handleIncomingAddition = function (obj) {
    let element = evalXPath(obj.xpath);
    let nodeToAdd = htmlToElement(obj.content);

    element.parentNode.insertBefore(nodeToAdd, element);
}

handleMorph = function (obj) {
    let element = evalXPath(obj.xpath);
    morphdom(element, obj.content);
}

handleRemoval = function(obj) {
    let element = evalXPath(obj.xpath);
    element.delete();
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