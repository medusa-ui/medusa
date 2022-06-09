const {RSocketConnector} = require("rsocket-core");
const {WebsocketClientTransport} = require("rsocket-websocket-client");
const {
    encodeCompositeMetadata,
    encodeRoute,
    WellKnownMimeType,
    encodeSimpleAuthMetadata,
} = require("rsocket-composite-metadata");

let client = undefined;
let rsocket = undefined;

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

async function main() {
    if (rsocket !== undefined) {
        //rsocket.close();
        document.getElementById("messages").innerHTML = "";
    }

    // Create an instance of a client
    client = new RSocketConnector({
        setup: {
            // ms btw sending keepalive to server
            keepAlive: 60000,
            // ms timeout if no keepalive response
            lifetime: 180000,
            // format of `data`
            dataMimeType: WellKnownMimeType.APPLICATION_JSON.string,
            // format of `metadata`
            metadataMimeType: WellKnownMimeType.MESSAGE_RSOCKET_COMPOSITE_METADATA.string,
        },
        transport: new WebsocketClientTransport({
            url: 'ws://localhost:7000/tweetsocket'
        }),
    });

    rsocket = await client.connect();
    await new Promise((resolve, reject) => {
        const encodedRoute = encodeRoute('tweets.by.author');

        const map = new Map();
        map.set(WellKnownMimeType.MESSAGE_RSOCKET_ROUTING, encodedRoute);
        map.set(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION, encodeSimpleAuthMetadata("user", "pass"));
        const compositeMetaData = encodeCompositeMetadata(map);

        let payloadData = {author: document.getElementById("author-filter").value};

        const requester = rsocket.requestChannel(
            {
                data: Buffer.from(JSON.stringify(payloadData)),
                metadata: compositeMetaData,
            },
            1,
            false,
            {
                onError: error => {
                    console.error(error);
                    addErrorMessage("Connection has been closed due to ", error);
                },
                onNext: payload => {
                    console.log("onNext", payload.data.toString());
                    addMessage(JSON.parse(payload.data.toString()));
                },
                onSubscribe: subscription => {
                    console.log("onSubscribe", subscription);
                },
                cancel: () => { },
                request: () => { },
                onComplete: () => console.log('Request-channel completed')
            }
        );
    });
}

document.addEventListener('DOMContentLoaded', main);
document.getElementById('author-filter').addEventListener('change', main);