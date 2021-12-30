var _M = _M || {};

_M.ws = null;
_M.timeoutTimer = 0;
_M.retryAttempts = 0;
_M.debugMode = true;
_M.retryMode = false;
_M.fatalMode = false;
_M.parser = new DOMParser();
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

_M.eventHandler = function(e) {
    _M.debug(e);

    if(!(Array.isArray(e) && (typeof e[0]['f'] !== "undefined" || typeof e[0]['t'] !== "undefined"))) {
        return;
    }
    
    //e = full event, k = individual event per key, k.f = field, k.v = value or relevant id, k.t = type, k.c = condition
    e.forEach(k => {
        if(typeof k.t === "undefined") { //the default event, a value change, contains the least amount of data, so it has no 'type'
            _M.handleDefaultEvent(k);
        } else if (k.t === 0) { //DOCUMENT TITLE CHANGE
            _M.handleTitleChangeEvent(k);
        } else if (k.t === 1) { //CONDITION CHECK
            _M.handleConditionCheckEvent(k);
        } else if (k.t === 2) { //ITERATION CHECK
            _M.handleIterationCheck(k);
        } else if (k.t === 3) { //CONDITIONAL CLASS CHECK
            _M.handleConditionalClass(k);
        } else if (k.t === 4) { //M ATTR CHECK
            _M.handleMAttributeChange(k);
        } else if (k.t === 5) { //HYDRA MENU
            _M.handleHydraMenuItemChange(k);
        }
    });
}

_M.handleMAttributeChange = function (k) {
    _M.debug(k);
    const expressionEval = _M.evalCondition(_M.injectVariablesIntoExpression(k.c));
    switch(k.f) {
        case "DISABLED":
            _M.handleMAttribute(k.v,(e) => { e.setAttribute("disabled", true); } ,(e) => { e.removeAttribute("disabled"); }, expressionEval);
            break;
        case "HIDE":
            _M.handleVisibilityConditionals(document.getElementsByClassName(k.v), !expressionEval)
            break;
        default:
        // code block
    }
};
_M.handleHydraMenuItemChange = function (k) {
    const menuName = k.f;
    const items = k.v;

    document.querySelectorAll("ul[h-menu="+menuName+"]").forEach(function(e) {
        e.innerHTML = "";
        for(const item of items) {
            let newMenuItem = document.createElement("li");

            let newMenuItemLink = document.createElement("a");
            newMenuItemLink.setAttribute("href", item.endpoint);

            let label = item.label;
            if(label.length === 0) {
                label = item.endpoint;
            }

            const newMenuItemContent = document.createTextNode(label);

            newMenuItemLink.appendChild(newMenuItemContent);
            newMenuItem.appendChild(newMenuItemLink);
            e.appendChild(newMenuItem);
        }
    });
};

_M.injectVariablesIntoExpression = function(expression) {
    for(const key of Object.keys(_M.variables)) {
        if(expression.indexOf(key) !== -1) {
            expression = expression.replaceAll(key, _M.variables[key]);
        }
    }
    return expression;
};

_M.injectVariablesIntoText = function(text) {
    for(const key of Object.keys(_M.variables)) {
        if(text.indexOf(key) !== -1) {
            text = text.replaceAll(key, _M.variables[key]);
        }
    }
    return text;
};

_M.handleMAttribute = function (mId, trueFunc, falseFunc, evalValue) {
    const list = document.getElementsByClassName(mId);
    for (let elem of list) {
        if (evalValue) {
            trueFunc(elem);
        } else {
            falseFunc(elem);
        }
    }
};

_M.handleDefaultEvent = function(k) {
    _M.variables[k.f] = k.v;
    document.querySelectorAll("[from-value="+k.f+"]").forEach(function(e) {
        if(e.hasAttribute("value")) {
            e.setAttribute("value", k.v);
            e.dispatchEvent(new Event('input'));
        } else {
            e.innerText = k.v;
        }
    });

    _M.handleWaitingForEnabled();
};

_M.handleWaitingForEnabled = function() {
    for (let index = 0; index < _M.waitingForEnable.length; index++) {
        const objWaitingForEnable = _M.waitingForEnable[index];
        if(_M.evalCondition(_M.injectVariablesIntoExpression(objWaitingForEnable.expression))) {
            objWaitingForEnable.elem.disabled = false;
            _M.waitingForEnable.splice(index, 1);
        }
    }

    if(_M.waitingForEnable.length === 0) {
        _M.handleVisibilityConditionals([document.getElementById("m-top-load-bar")], false);
        _M.handleVisibilityConditionals([document.getElementById("m-full-loader")], false);
    }
};

_M.handleTitleChangeEvent = function(k) {
    document.title = _M.injectVariablesIntoText(k.v);
};

_M.handleIterationCheckEach = function(templateId, template, currentEachValue, parent) {
    let newDiff = document.createElement("div");
    newDiff.innerHTML = _M.resolveInnerTemplate(template.innerHTML, [currentEachValue]);
    while (newDiff.firstChild) parent.appendChild(newDiff.firstChild);
}

_M.handleIterationCheck = function (k) {
    // clear old values
    document.querySelectorAll("[template-id="+k.f+"]").forEach(function(e) { e.remove(); });

    // set new values
    document.querySelectorAll("[m-id="+k.f+"]").forEach(function(template) {
        const templateId = _M.resolveTemplateId(template);
        const currentEachValues = _M.resolveTemplateCondition(templateId);
        const divTemplate = template.content.children[0];

        let index = 0;
        if(Array.isArray(currentEachValues)) {
            for(const currentEachValue of currentEachValues) {
                const block = _M.buildIterationBlock(currentEachValue, divTemplate, index++);
                if(typeof _M.preRender !== "undefined") { _M.preRender(k, block); }
                template.parentNode.insertBefore(block, template);
            }
        } else {
            for (let i = 0; i < currentEachValues; i++) {
                const block = _M.buildIterationBlock(i, divTemplate, index++);
                if(typeof _M.preRender !== "undefined") { _M.preRender(k, block); }
                template.parentNode.insertBefore(block, template);
            }
        }
    });
};
_M.buildIterationBlock = function (rootEachValue, templateDiv, index) {
    const node = templateDiv.cloneNode(true);
    node.querySelectorAll("template").forEach(function (templateNode) { templateNode.remove(); });
    node.setAttribute("index", index.toString());

    _M.buildIterationBlockMEachHandling(node);
    node.querySelectorAll("[m-each]").forEach(function (divWithMEach) {
        _M.buildIterationBlockMEachHandling(divWithMEach);
    });

    return node;
}

_M.buildIterationBlockMEachHandling = function (divWithMEach) {
    const templateMap = _M.buildTemplateMap(divWithMEach);

    for(const mapEntry of templateMap) {
        const eachName = mapEntry['eachName'];
        divWithMEach.querySelectorAll("[from-value='"+eachName+"']").forEach(function (specificSpan) {
            const index = parseInt(mapEntry['index']);
            const conditional = _M.conditionals[mapEntry['templateId']];
            specificSpan.textContent = _M.variables[conditional][index];
        });
    }
}

_M.resolveTemplateEachValues = function (wrapper, rootIndex) {
    wrapper.querySelectorAll("[m-each]").forEach(function (divWithMEach) {
        const templateMap = _M.buildTemplateMap(divWithMEach, rootIndex);

        for(const mapEntry of templateMap) {
            divWithMEach.querySelectorAll("[from-value='"+mapEntry['eachName']+"']").forEach(function (specificSpan) {
                const index = parseInt(mapEntry['index']);
                const conditional = _M.conditionals[mapEntry['templateId']];
                specificSpan.textContent = _M.variables[conditional][index];
            });
        }
    });
    return wrapper;
}

_M.buildTemplateMap = function (divWithMEach) {
    const map = [];
    let evalNode = divWithMEach;

    while(evalNode !== null) {
        if(evalNode.getAttribute("template-id") !== null) {
            const currentEachName = evalNode.getAttribute("m-each");
            if(null !== currentEachName) {
                const currentTemplateId = evalNode.getAttribute("template-id");
                let currentIndex = evalNode.getAttribute("index");
                map.push({
                    "templateId": currentTemplateId.substring(currentTemplateId.lastIndexOf("#") + 1),
                    "index": currentIndex,
                    "eachName": currentEachName
                });
            }
        }
        evalNode = evalNode.parentNode;
    }
    return map;
}

_M.resolveTemplateId = function (template) {
    return template.attributes["m-id"].nodeValue;
}

_M.resolveTemplateCondition = function (templateId) {
    const condition = _M.conditionals[templateId];
    return _M.variables[condition];
}

_M.resolveInnerTemplateEach = function (index, parents, currentEachValue, templateId, template, replacement) {
    index = index++;
    let localParent = [...parents]; //clone
    localParent.unshift(currentEachValue);
    let replacementInner = "<div index='" + index++ + "' template-id='" + templateId + "'>" + _M.resolveInnerTemplate(template.innerHTML, localParent) + "</div>";
    replacement += replacementInner;
    return replacement;
}

_M.resolveInnerTemplate = function (innerContent, parents) {
    innerContent = innerContent.toString();

    //if this contains template, then run the resolver one layer deeper;
    if(innerContent.includes("<template m-id=")) {
         let doc = _M.parser.parseFromString(innerContent, "text/html");
         doc.querySelectorAll("template").forEach(function (template) {
             const templateId = _M.resolveTemplateId(template);
             const eachValues = _M.resolveTemplateCondition(templateId);
             let index = 0;
             let replacement = "";
             if(Array.isArray(eachValues)) {
                 for (const currentEachValue of eachValues) {
                     replacement = _M.resolveInnerTemplateEach(index, parents, currentEachValue, templateId, template, replacement);
                 }
             } else {
                 for (let i = 0; i < eachValues; i++) {
                     replacement = _M.resolveInnerTemplateEach(index, parents, i, templateId, template, replacement);
                 }
             }

             innerContent = innerContent.replaceAll("<template m-id=\""+ templateId +"\">" + template.innerHTML + "</template>", replacement);
         });
    }

    return innerContent;
};

_M.unwrap = function (node){
    return node.replaceWith(...node.childNodes);
}

_M.recursiveObjectUpdate = function(html, obj, path) {
    if(typeof obj === 'object' && obj !== null) {
        html = html.replaceAll("["+path+"]", JSON.stringify(obj));
        for(const objKey of Object.keys(obj)) {
            html = _M.recursiveObjectUpdate(html, obj[objKey], path + "." + objKey);
        }
    } else {
        html = html.replaceAll("["+path+"]", obj);
    }
    return html;
};

_M.findElementByMIF = function(key) {
    return document.querySelectorAll("[m-if='" + key + "']");
}

_M.handleConditionCheckEvent = function(k) {
    let conditionEval = _M.evalCondition(_M.injectVariablesIntoExpression(k.c));
    let elems = _M.findElementByMIF(k.v);
    _M.handleVisibilityConditionals(elems, conditionEval);

    //update templates if needed
    let templates = document.getElementsByTagName("template");
    if(null !== templates && templates.length !== 0) {
        for(const template of templates) {
            const templateElems = template.content.querySelectorAll("." + k.v);
            _M.handleVisibilityConditionals(templateElems, conditionEval);
        }
    }
};

_M.handleVisibilityConditionals = function (elems, isVisible) {
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
};

_M.handleConditionalClass = function(k) {
    let condition = k.c;
    const found = condition.match(new RegExp("\\$\\w+(-?\\w*)*"));
    for(const toReplace of found) {
        condition = condition.replaceAll(toReplace, _M.variables[toReplace.substring(1)]);
    }

    const conditionEval = _M.evalCondition(condition);
    const relevantElem = document.querySelector("[data-from='"+k.v+"']");

    let fullClasses = "";
    if(typeof relevantElem.dataset.baseClass !== "undefined") {
        fullClasses = relevantElem.dataset.baseClass + " ";
    }
    fullClasses += conditionEval;
    relevantElem.className = fullClasses.trim();
};

_M.evalCondition = function(condition){
    return Function('"use strict";return (' + condition + ')')();
};

_M.debug = function(textToLog, fullObject) {
    if(_M.debugMode) {
        console.log(textToLog, fullObject);
    }
};

_M.elementEscape = function(valueToEscape) {
    return encodeURIComponent(valueToEscape).replace(/[!'()*]/g, function(c) {
        return '%' + c.charCodeAt(0).toString(16);
    });
};

_M.attributeValue = function (element, attribute) {
    if ( attribute in element ) {
        return element[attribute];
    }
    return element.attributes[attribute].value;
};

_M.parseReference = function(e, originElem) {
    let raw = e.match(/\(.*\)/)[0];
    if (null !== raw && raw.indexOf("this.") !== -1) {
      e = _M.parseSelfReference(raw, e, originElem);
    }
    raw = e.match(/\(.*\)/)[0];
    if(null !== raw && raw.indexOf("#") !== -1) {
      e = _M.parseElementByIdReference(raw, e, originElem);
    }
    return e;
};

_M.parseElementByIdReference = function(raw, e, originElem) {
    let resolved = raw;
    const partToEval = raw.substring(1, raw.length-1);
    const part = partToEval.split(",");
    for(const attrToEval of part) {
        let part = attrToEval.trim();
        if(part.indexOf("#") === 0) {
            const elmAttr = part.split(".");
            const value = _M.attributeValue(document.querySelector(elmAttr[0]) , elmAttr[1]);
            resolved = resolved.replace(part, "'" +  value + "'");
        }
    }
    return e.replace(raw, resolved);
};

_M.parseSelfReference = function(raw, e, originElem) {
    const partToEval = raw.substring(1, raw.length-1);
    const parametersToEval = partToEval.split(",");
    let parameters = "(";
    let appender = "";
    for(const paramToEval of parametersToEval) {
        parameters += appender;
        let param = paramToEval.trim();
        if(param.indexOf("this.") === 0) {
            const attrName = param.replace("this.", "");
            const resolvedParam = _M.attributeValue(originElem,attrName);
            if(typeof resolvedParam === "undefined") {
                param = null;
            } else {
                param = "'" + _M.elementEscape(resolvedParam) + "'";
            }
        }
        parameters += param;
        appender = ", ";
    }
    parameters += ")";
    return e.replace(raw, parameters);
};

_M.waitingForEnable = [];

_M.sendEvent = function(originElem, e) {
    const disableOnClick = originElem.attributes["m-disable-on-click-until"];
    if(typeof disableOnClick !== "undefined") {
        const loadingStyle = originElem.attributes["m-loading-style"];

        _M.waitingForEnable.push({"elem": originElem, "expression": disableOnClick.value});
        originElem.disabled = true;
        if(typeof loadingStyle === "undefined" || loadingStyle.value === "top") {
            _M.handleVisibilityConditionals([document.getElementById("m-top-load-bar")], true);
        } else if (loadingStyle.value === "full") {
            _M.handleVisibilityConditionals([document.getElementById("m-full-loader")], true);
        }
    }

    e = _M.parseReference(e, originElem);
    _M.ws.send(_M.injectVariablesIntoExpression(e));
};

_M.onEnter = function(originElem, action, event) {
    if (event.key === "Enter") {
        event.preventDefault();
        _M.sendEvent(originElem, action);
        return false;
    }
};

_M.preventDefault = function(event) {
    if (event.key === "Enter") {
        event.preventDefault();
        return false;
    }
};

_M.retryConnection();

_M.connectionDropRetry = "<div id='m-top-error-retry' style=\"position: absolute; top: 0; left: 0; background:orange; color: black; width:100%; text-align:center;\">Your connection with the server dropped. Retrying to connect.</div>";
_M.connectionDropFatal = "<div id='m-top-error-fatal' style=\"position: absolute; top: 0; left: 0; background:red; color: black; width:100%; text-align:center;\">Your connection with the server dropped and reconnecting is not possible. Please reload.</div>";