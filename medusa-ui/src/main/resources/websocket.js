var _M = _M || {};

_M.ws = null;
_M.timeoutTimer = 0;
_M.debugMode = true;

_M.retryConnection = function () {
    setTimeout(function() {
        if(_M.timeoutTimer < 1000) {
            _M.timeoutTimer += 150;
        } else {
            _M.timeoutTimer = 1000;
        }

        try{
            _M.ws = new WebSocket("ws://" + window.location.host + "%WEBSOCKET_URL%");
            if(_M.ws.readyState === _M.ws.CLOSED || _M.ws.readyState === _M.ws.CLOSING) {
                _M.retryConnection();
            }
            _M.ws.onopen = function() {
                _M.debug("ws.onopen", _M.ws);
                _M.debug("ws.readyState", "wsstatus");
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
                _M.debug("ws.onmessage", _M.ws, message);
                _M.eventHandler(JSON.parse(message.data));
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

    //e = full event, k = individual event per key, k.f = field, k.v = value or relevant id, k.t = type, k.c = condition
    e.forEach(k => {
        if(k.t === undefined) { //the default event, a value change, contains the least amount of data, so it has no 'type'
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

_M.injectVariablesIntoExpression = function(expression) {
    const found = expression.match(new RegExp("\\$[\\w-]+","g"));
    if(found) {
        for(const toReplace of found) {
            expression = expression.replaceAll(toReplace, _M.variables[toReplace.substring(1)]);
        }
    }
    return expression;
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
    document.title = k.v;
};

_M.handleIterationCheck = function (k) {
    // clear old values
    document.querySelectorAll("[template-id="+k.f+"]").forEach(function(e) { e.remove(); });
    // set new values
    document.querySelectorAll("[m-id="+k.f+"]").forEach(
        function(template) {

            let index = 0;

            for(const currentEachValue of _M.variables[k.v]) {
                let newDiff = document.createElement("div");
                newDiff.setAttribute("index", (index++).toString());
                newDiff.setAttribute("template-id", k.f);

                newDiff.innerHTML = template.innerHTML;
                _M.recursiveObjectUpdate(newDiff, currentEachValue, "$each");

                template.parentNode.insertBefore(newDiff, template);
            }
        });
};

_M.recursiveObjectUpdate = function(diff, obj, path) {
    if(typeof obj === 'object' && obj !== null) {
        diff.innerHTML = diff.innerHTML.replaceAll("["+path+"]", JSON.stringify(obj));
        for(const objKey of Object.keys(obj)) {
            _M.recursiveObjectUpdate(diff, obj[objKey], path + "." + objKey);
        }
    } else {
        diff.innerHTML = diff.innerHTML.replaceAll("["+path+"]", obj);
    }
};

_M.handleConditionCheckEvent = function(k) {
    let conditionEval = _M.evalCondition(_M.injectVariablesIntoExpression(k.c));
    let elems = document.getElementsByClassName(k.v);
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
    if(undefined !== relevantElem.dataset.baseClass) {
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

_M.parseReference = function(e, originElem) {
    e = _M.parseSelfReference(e, originElem);
    e = _M.parseElementByIdReference(e, originElem);
    return e;
};

_M.parseElementByIdReference = function(e, originElem) {
    const raw = e.match(/\(.*\)/)[0];
    let resolved = raw;
    if(null !== raw && raw.indexOf("#") !== -1) {
        const partToEval = raw.substring(1, raw.length-1);
        const part = partToEval.split(",");
        for(const attrToEval of part) {
            let part = attrToEval.trim();
            if(part.indexOf("#") === 0) {
                const exp = part.split(".");
                const target = document.querySelector(exp[0]);
                const attrName = exp[1];
                const value = target[attrName] ? target[attrName] : target.attributes[attrName].value;
                resolved = resolved.replace(part, "'" +  value + "'");
            }
        }
        const result = e.replace(raw, resolved);
        return result;
    }
    return e;
};

_M.parseSelfReference = function(e, originElem) {
    const parametersUnparsed = e.match(/\(.*\)/)[0];
    if(null !== parametersUnparsed && parametersUnparsed.indexOf("this.") !== -1) {
        const partToEval = parametersUnparsed.substring(1, parametersUnparsed.length-1);
        const parametersToEval = partToEval.split(",");
        let parameters = "(";
        let appender = "";
        for(const paramToEval of parametersToEval) {
            parameters += appender;
            let param = paramToEval.trim();
            if(param.indexOf("this.") === 0) {
                const attrName = param.replace("this.", "");
                const resolvedParam = originElem[attrName] ? originElem[attrName] : originElem.attributes[attrName].value;
                if(resolvedParam === undefined) {
                    param = null;
                } else {
                    param = "'" + _M.elementEscape(resolvedParam) + "'";
                }
            }
            parameters += param;
            appender = ", ";
        }
        parameters += ")";
        return e.replace(parametersUnparsed, parameters);
    }
    return e;
};

_M.parseSelfReference = function(e, originElem) {
    const parametersUnparsed = e.match(/\(.*\)/)[0];
    if(null !== parametersUnparsed && parametersUnparsed.indexOf("this.") !== -1) {
        const partToEval = parametersUnparsed.substring(1, parametersUnparsed.length-1);
        const parametersToEval = partToEval.split(",");
        let parameters = "(";
        let appender = "";
        for(const paramToEval of parametersToEval) {
            parameters += appender;
            let param = paramToEval.trim();
            let index = param.indexOf("this.");
            if(index !== -1) {
                const attrName = param.substring(index, param.length-index).replace("this.", "");
                let resolvedParam = originElem[param.replace("this.", "")];
                if(resolvedParam === undefined) {
                     resolvedParam = originElem.attributes[attrName].value;
                }
                const result = param.replace("this."+attrName, resolvedParam);
                if(result === undefined) {
                    param = null;
                } else {
                    param = "'" + _M.elementEscape(resolvedParam) + "'";
                }
            }
            parameters += param;
            appender = ", ";
        }
        parameters += ")";
        return e.replace(parametersUnparsed, parameters);
    }
    return e;
};

_M.waitingForEnable = [];
_M.sendEvent = function(originElem, e) {
    const disableOnClick = originElem.attributes["m-disable-on-click-until"];
    if(disableOnClick !== undefined) {
        const loadingStyle = originElem.attributes["m-loading-style"];

        _M.waitingForEnable.push({"elem": originElem, "expression": disableOnClick.value});
        originElem.disabled = true;
        if(loadingStyle === undefined || loadingStyle.value === "top") {
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