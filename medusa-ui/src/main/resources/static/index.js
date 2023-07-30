const { RSocketConnector } = require("rsocket-core");
const { WebsocketClientTransport } = require("rsocket-websocket-client");
const escape = require("@braintree/sanitize-url").sanitizeUrl;
const {
    encodeCompositeMetadata,
    encodeRoute,
    WellKnownMimeType,
    encodeSimpleAuthMetadata,
} = require("rsocket-composite-metadata");

function Medusa() {}
const _M = new Medusa();

const debugMode = false;
const XRegExp = require('xregexp');
const {f, cancel} = require("./websocket");

const MAX_REQUEST_N = 2147483647;
let stream;

//based off https://github.com/viglucci/rsocket-chat-demo/blob/main/frontend/src/context/ClientProvider.jsx
//based off https://github.com/viglucci/rsocket-chat-demo/blob/main/frontend/src/hooks/useChat.js

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
    _M.wsP = undefined;
}

function sendMessage(payloadData) {
    stream.onNext({
        data: Buffer.from(JSON.stringify(payloadData))
    });
}

document.addEventListener("DOMContentLoaded", setupRouter);
document.addEventListener('keydown', (event) => {
    let isEscape = false;
    if ("key" in event) {
        isEscape = (event.key === "Escape" || event.key === "Esc");
    }
    if (isEscape) {
        _M.closeModal();
    }
}, false);

Medusa.prototype.uploadFileToMethod = async function (event, fragment, id) {
    if(typeof event !== "undefined") {
        event.preventDefault();
    }
    let files = document.getElementById(id).files;
    if(validateFiles(fragment, files, id) ) {
        startLoading(event);
        for (const file of files) {
            debugLog("upload: " + file.name);
            new Promise(function (resolve, reject) {
                try {
                    resolve(fileToByteArray(fragment, file));
                } catch (e) {
                    reject(e);
                }
            }).catch(e => new Error(e));
        }
    }
    return false;
};

const validateFiles = function (fragment, files, id) {
    if(files.length === 0) {
        return false;
    }

    let message = "Provided file size exceeds max file size";
    let fileSizeOverridden = false;

    for(let possibleFileValidation of _M.validationsPossible) {
        if(possibleFileValidation['validation'] === 'MaxFileSize') {
            MAX_FILE_SIZE = BigInt(possibleFileValidation['value1']);
            message = possibleFileValidation['message'];
            fileSizeOverridden = true;
        }
    }

    if(!fileSizeOverridden) {
        MAX_FILE_SIZE = BigInt(10485760); // reset to default 10MB
    }

    let form = findFormByElementId(id);
    clearAllValidation(form);

    for (const file of files) {
        if(file.size > MAX_FILE_SIZE) {
            markFieldAsFailedValidation(form, id, message);
            sendFileError(fragment, file,message);
            debugLog(message + " for file: " + file.name + " with size: " + file.size);
            cancelAllLoading();
            return false; // fail fast
        }
    }
    return true;
}

function findFormByElementId(elementId) {
    let currentElement = document.getElementById(elementId);
    while (currentElement && currentElement.tagName !== 'FORM') {
        currentElement = currentElement.parentElement;
    }
    return currentElement ? currentElement : document.body;
}

async function fileToByteArray(fragment, file) {
    const expected_amount_of_chunks = Math.ceil(file.size / CHUNK_SIZE);
    const fileID = sendFileStart(fragment, file);
    readFileChunk(fragment, file, fileID, expected_amount_of_chunks, 0, 0);
}

const CHUNK_SIZE = 15000;
let MAX_FILE_SIZE = BigInt(10485760); // default 10MB

function readFileChunk(fragment, file, fileID, expected_amount_of_chunks, index, offset) {
    const reader = new FileReader();
    const blob = file.slice(offset, offset + CHUNK_SIZE);
    reader.readAsArrayBuffer(blob);
    reader.onload = function(e) {

        sendFileChunk(fragment, fileID, [].slice.call(new Uint8Array(e.target.result)), (index/expected_amount_of_chunks)*100);

        offset += CHUNK_SIZE;
        if (offset < file.size) {
            readFileChunk(fragment, file, fileID, expected_amount_of_chunks, ++index, offset);
        } else {
            sendFileCompletion(fragment, fileID);
        }
    };
}

function sendFileStart(fragment, file) {
    const fileId = crypto.randomUUID();
    sendMessage({
        "fragment" : fragment,
        "fileMeta" : {
            "sAct": "upload_start",
            "fileName": file.name,
            "mimeType": file.type,
            "size": file.size,
            "fileId": fileId
        }
    });
    return fileId;
}

function sendFileError(fragment, file, message) {
    const fileId = crypto.randomUUID();
    sendMessage({
        "fragment" : fragment,
        "fileMeta" : {
            "sAct": "upload_error",
            "fileName": file.name,
            "mimeType": file.type,
            "size": file.size,
            "message": message, //TODO maybe not needed
            "fileId": fileId
        }
    });
    return fileId;
}

function sendFileChunk(fragment, fileId, chunk, percentage) {
    sendMessage({
        "fragment" : fragment,
        "fileMeta" : {
            "sAct": "upload_chunk",
            "fileId": fileId,
            "chunk": chunk,
            "percentage": percentage
        }
    });
}

function sendFileCompletion(fragment, fileId) {
    if(typeof fileId !== "undefined") {
        console.log("File upload completed from a local perspective:" + fileId);
        sendMessage({
            "fragment" : fragment,
            "fileMeta" : {
                "sAct": "upload_complete",
                "fileId": fileId
            }
        });
        offset = 0;
        return fileId;
    }
}

Medusa.prototype.doFormAction = function(event, parentFragment, actionToExecute) {
    if(typeof event !== "undefined") {
        event.preventDefault();
    }

    const multiElems = [];
    const form = event.target;

    clearAllValidation(form);

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

    let validationPass = true;

    for(let validationDef of _M.validationsPossible) {
        //validationDef.formContext = doNotEmptyForm
        //onsubmit="_M.doFormAction(event, '__FRAGMENT__', `doNotEmptyForm(:{form})`)"
        //so this part makes it so that you only apply the right frontend validations to the right form
        if(form.getAttribute("onsubmit").indexOf(validationDef.formContext) === -1) {
            continue;
        }
        const field = validationDef.field;
        let valueToCheck = formProps[field];
        let fieldElement = form.querySelector("[name='"+field+"']");
        if(null === fieldElement) {
            //not in this form
            continue;
        }
        const fieldType = fieldElement.getAttribute("type");
        if(fieldElement.getAttribute("value") === null && fieldType === "checkbox") {
            valueToCheck = !!fieldElement.checked;
        } else if(fieldType === "date" || fieldType === "datetime-local") {
            valueToCheck = new Date(fieldElement.value).getTime() + "";
        }
        if(valueToCheck === undefined) {
            valueToCheck = null;
        }
        formProps[field] = valueToCheck;
        let validationResult = validate(validationDef.validation, valueToCheck, validationDef.value1, validationDef.value2);
        debugLog("Local validation of field '"+field+"': " + validationResult);
        if(!validationResult) {
            validationPass = markFieldAsFailedValidation(form, field, validationDef.message);
        }
    }

    if(!validationPass) {
        debugLog("Local validation did not pass, so no backend call is made");
        return;
    } else {
        debugLog("Local validation passed, so proceeding to backend");
    }

    _M.doAction(event, parentFragment, actionToExecute.replace(":{form}", JSON.stringify(formProps) ));
};

//a true value passes the validation, a false value fails it
//so if I enter a negative number for Positive, it will return false
validate = function(type, value, arg1, arg2) {
    if(type === "NotBlank") {
        return notBlank(value);
    } else if(type === "NotEmpty") {
        return notEmpty(value);
    } else if(type === "Pattern") {
        return pattern(value, arg1);
    } else if(type === "AssertFalse") {
        return assert(value, false);
    } else if(type === "AssertTrue") {
        return assert(value, true);
    } else if(type === "DecimalMax" || type === "Max") {
        return decimalMax(value, arg1);
    } else if(type === "DecimalMin" || type === "Min") {
        return decimalMin(value, arg1);
    } else if(type === "Email") {
        return email(value);
    } else if(type === "Digits") {
        return digits(value, arg1, arg2);
    } else if(type === "Positive") {
        return positive(value);
    } else if(type === "Negative") {
        return negative(value);
    } else if(type === "PositiveOrZero") {
        return positiveOrZero(value);
    } else if(type === "NegativeOrZero") {
        return negativeOrZero(value);
    } else if(type === "Future") {
        return future(value, false);
    } else if(type === "FutureOrPresent") {
        return future(value, true);
    } else if(type === "Past") {
        return past(value, false);
    } else if(type === "PastOrPresent") {
        return past(value, true);
    } else if(type === "Null") {
        return value === null || value === undefined;
    } else if(type === "NotNull") {
        return value !== null && value !== undefined;
    } else if(type === "Size") {
        return size(value, arg1, arg2);
    }

    return true;
};

function size(value, min, max) {
    if (value === null || value === undefined) {
        return false;
    }
    if(min === null || min === undefined) {
        min = "0";
    }
    let length = value.length;
    if (typeof value === "object") {
        length = Object.keys(value).length;
    }
    return !(length < Number(min) || (max !== undefined && length > Number(max)));
}

function past(value, allowPresent) {
    const valAsNum = Number(value.replace("L", ""));
    if(allowPresent) {
        return new Date(valAsNum) <= new Date();
    }
    return new Date(valAsNum) < new Date();
}

function future(value, allowPresent) {
    const valAsNum = Number(value.replace("L", ""));
    if(allowPresent) {
        return new Date(valAsNum) >= new Date();
    }
    return new Date(valAsNum) > new Date();
}

function negativeOrZero(value) {
    return value !== undefined && value.trim().length !== 0 && Number(value) <= 0;
}

function negative(value) {
    return value !== undefined && value.trim().length !== 0 && Number(value) < 0;
}

function positiveOrZero(value) {
    return value !== undefined && value.trim().length !== 0 && Number(value) >= 0;
}

function positive(value) {
    return value !== undefined && value.trim().length !== 0 && Number(value) > 0;
}

function digits(value, integer, fractions) {
    let regex = new RegExp(`^[0-9]{1,${integer}}$`);
    if(Number(fractions) !== 0) {
        regex = new RegExp(`^[0-9]{1,${integer}}([,.][0-9]{1,${fractions}})?$`);
    }
    return regex.test(value);
}

function decimalMin(value, minAsString) {
    return value !== undefined && value.trim().length !== 0 && Number(value) >= Number(minAsString);
}

function decimalMax(value, maxAsString) {
    return value !== undefined && value.trim().length !== 0 && Number(value) <= Number(maxAsString);
}

function notBlank(value) {
    return value !== undefined && value.trim().length !== 0;
}

function notEmpty(value) {
    return value !== undefined && value.length !== 0;
}

function pattern(value, pattern) {
    return value !== undefined && XRegExp(pattern).test(value);
}

function assert(value, assertValue) {
    return value !== undefined && /^true$/i.test(value) === assertValue;
}

function email(value) {
    const emailPattern ="^[\\p{L}+\\p{M}0-9.!#$%&’'\\\"*+\\/=?^_ `{|}~-]+(\\.{1}[\\p{L}+\\p{M}0-9.!#$%&’'\\\"*+\\/=?^_ `{|}~-]+)*[0-9.!#$%&’'\\\"*+\\/=?^_ `{|}~-]*@[\\p{L}+\\p{M}0-9.!#$%&’'\\\"*+\\/=?^_ `{|}~-]+[0-9.!#$%&’'\\\"*+\\/=?^_ `{|}~-]*(\\.{1}[\\p{L}+\\p{M}0-9!#$%&’'\\\"*+\\/=?^_ `{|}~-]+)+$";
    return pattern(value, emailPattern);
}

markFieldAsFailedValidation = function(form, name, message) {
    let field = form.querySelector("[validation='"+name+"']");
    if(null !== field) {
        //- make visible, add error class, add message
        field.innerText = message;
        field.classList.remove("hidden");
    }

    let validationGlobal = form.querySelector("ul[validation='form-global']");
    if(null !== validationGlobal) {
        //- make visible, added li with message
        const li = document.createElement('li');
        li.innerText = message;
        validationGlobal.appendChild(li);
    }

    return false;
};

clearAllValidation = function (form) {
    let allLi = form.querySelectorAll("ul[validation='form-global'] li");
    for(let li of allLi) {
        li.remove();
    }
    for (const validationElement of form.querySelectorAll("[validation]:not([validation='form-global'])")) {
        clearValidationErrorForField(form, validationElement.getAttribute("validation"));
    }
}

clearValidationErrorForField = function (form, name) {
    let field = form.querySelector("[validation='"+name+"']");
    if(null !== field) {
        field.innerText = "";
        field.classList.add("hidden");

        let validationGlobal = form.querySelector("ul[validation='form-global']");
        if(null !== validationGlobal) {
            let relatedValidationError = validationGlobal.querySelector("[validation-error='" + name + "']");
            if(null !== relatedValidationError) {
                relatedValidationError.remove();
            }
        }
    }
}

const buttonLoader = document.getElementById("m-template-button-load").content.firstElementChild.outerHTML;

function startLoading(event) {
    const target = event.target;
    if (typeof target.attributes['data-loading-until'] !== 'undefined') {
        const waitFor = getTargetAttributeIfExists(event, 'data-loading-until');
        const loadingStyle = getTargetAttributeIfExists(event, 'data-loading-style');

        let loader;
        if (loadingStyle === 'top') {
            loader = document.getElementById("m-top-load-bar");
        } else if (loadingStyle === 'button') {
            loader = null;
        } else {
            loader = document.getElementById("m-full-loader");
        }

        if (typeof loader !== "undefined" && null != loader) {
            loader.setAttribute("waiting-for", waitFor);
            loader.removeAttribute("style");
        }

        target.setAttribute("waiting-for", waitFor);
        target.setAttribute("disabled", true);
        target.innerHTML = buttonLoader + target.innerHTML;
    }
}

Medusa.prototype.doAction = function(event, parentFragment, actionToExecute) {
    if(typeof event !== "undefined") {
        event.preventDefault();
    }
    startLoading(event);

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

Medusa.prototype.findValueById = function (id, attribute) {
    let foundItem = document.querySelector(id);
    if(foundItem === null) {
        return null;
    }
    return foundItem[attribute] || foundItem.value || foundItem.textContent;
};

Medusa.prototype.doActionOnKeyUp = function(key, event, parentFragment, actionToExecute) {
    if(event.key === key || event.keyCode === key) {
        _M.doAction(event, parentFragment, actionToExecute);
    }
};

Medusa.prototype.openModal = function(id) {
    document.getElementById(id).classList.add("is-open");
};

Medusa.prototype.closeModal = function() {
    for (const openElem of document.getElementsByClassName("is-open")) {
        if(openElem.classList.contains("modal-background")) {
            openElem.classList.remove("is-open");
        }
    }
};

evalXPath = function(xpath) {
    if(-1 !== xpath.indexOf("/text(")) {
        let xpathSplit = xpath.split("/text()");
        let element = evalXPath(xpathSplit[0]);
        let expectedIndex = parseInt(xpathSplit[1].substring(1, xpathSplit[1].length -1), 10);
        let currentIndex = 0;
        for (const child of element.childNodes) {
            if(child.nodeName === "#text" && currentIndex++ === expectedIndex) {
                return child;
            }
        }
    }

    return document.evaluate("/html[1]" + xpath, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null ).singleNodeValue;
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
    let element = evalXPath(obj.xpath);

    if(obj.attributeValue === null) {
        element.removeAttr(obj.attributeKey);
    } else {
        element.setAttribute(obj.attributeKey, obj.attributeValue);
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

function findFormBasedOnContext(formContext) {
    return document.querySelector("form[onsubmit*='{}']".replace("{}", formContext));
}

applyAllChanges = function (listOfDiffs) {
    const extend = (typeof _M_extend !== 'undefined') ? _M_extend : {};
    if(typeof extend.preRender === 'function') { extend.preRender(listOfDiffs); }
    for(let diff of listOfDiffs) {
        if(typeof extend.preEvent === 'function') { extend.preEvent(diff); }
        //main
        if(diff.type === "ADDITION") {
            handleIncomingAddition(diff);
        } else if(diff.type === "REMOVAL") {
            handleRemoval(diff);
        } else if(diff.type === "TEXT_EDIT") {
            handleMorph(diff);
        } else if(diff.type === "ATTR_CHANGE") {
            handleAttrChange(diff);
        //extra
        } else if(diff.type === "REDIRECT") {
            window.location.href = escape(diff.content);
        } else if(diff.type === "JS_FUNCTION") {
            runFunction(escape(diff.content), []);
        } else if(diff.type === "LOADING") {
            applyLoadingUpdate(escape(diff.content));
        } else if(diff.type === "VALIDATION") {
            const formAttributeSplit = diff.attributeKey.split("#");
            markFieldAsFailedValidation(findFormBasedOnContext(formAttributeSplit[0]), formAttributeSplit[1], diff.attributeValue);
        }
        if(typeof extend.postEvent === 'function') { extend.postEvent(diff); }
    }
    if(typeof extend.postRender === 'function') { extend.postRender(listOfDiffs); }
};

cancelAllLoading = function() {
    for(const elem of document.querySelectorAll("[waiting-for]")) {
        applyLoadingUpdate(elem.getAttribute("waiting-for"));
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
};

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
    const locale = (navigator.language || navigator.userLanguage);
    const encodedRoute = encodeRoute("event-emitter/" + _M.controller + "/" + _M.sessionId + "/" + locale);
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
