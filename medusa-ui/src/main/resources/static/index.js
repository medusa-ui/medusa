const { RSocketConnector } = require("rsocket-core");
const { WebsocketClientTransport } = require("rsocket-websocket-client");
const {
    encodeCompositeMetadata,
    encodeRoute,
    WellKnownMimeType,
    encodeSimpleAuthMetadata,
} = require("rsocket-composite-metadata");

const MAX_REQUEST_N = 2147483647;

function addErrorMessage(prefix, error) {
    var ul = document.getElementById("messages");
    var li = document.createElement("li");
    li.appendChild(document.createTextNode(prefix + error));
    ul.appendChild(li);
}

function addMessage(message) {
    var ul = document.getElementById("messages");

    var li = document.createElement("li");
    li.appendChild(document.createTextNode(JSON.stringify(message)));
    ul.appendChild(li);
}

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
            url: 'ws://localhost:7000/tweetsocket'
        }),
    });
    const rsocket = await connector.connect();
    stream = await buildStream(rsocket);
}

let stream;
async function buildStream(rsocket) {
    const encodedRoute = encodeRoute('tweets.by.author');
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
            console.log(
                `payload[data: ${payload.data}; metadata: ${payload.metadata}]|${isComplete}`
            );
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

function sendNextMessage() {
    let payloadData = { author: document.getElementById("author-filter").value };
    sendMessage(payloadData);
}

function sendMessage(payloadData) {
    stream.onNext({
        data: Buffer.from(JSON.stringify(payloadData))
    });
}

document.addEventListener('DOMContentLoaded', setupRouter);
document.getElementById('author-filter').addEventListener('change', sendNextMessage);