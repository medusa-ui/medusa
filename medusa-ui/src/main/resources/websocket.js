var _M = _M || {};

_M.ws = null;
_M.timeoutTimer = 0;
_M.retryAttempts = 0;
_M.testMode = _M.testMode || false;
_M.debugMode = _M.debugMode || true;
_M.retryMode = false;
_M.fatalMode = false;
_M.parser = new DOMParser();
_M.isLocal = window.location.host.indexOf("localhost") !== -1;

_M.retryConnection = function () {
    if(_M.testMode) { return; }
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

    if(!(Array.isArray(e) && e.length > 0 && (typeof e[0]['f'] !== "undefined" || typeof e[0]['t'] !== "undefined"))) {
        return;
    }
    
    //e = full event, k = individual event per key, k.f = field, k.v = value or relevant id, k.t = type, k.c = condition
    e.forEach(k => {
        if(typeof k.t === "undefined" || k.t === null) { //the default event, a value change, contains the least amount of data, so it has no 'type'
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
    const expressionEval = _M.evalCondition(_M.injectVariablesIntoConditionalExpression(k.c));
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

_M.injectVariablesIntoConditionalExpression = function(expression, elem) {
    if(typeof expression === "boolean") { return expression; }

    let found = expression.match(new RegExp("[\\w-]+","g"));
    if(found) {
        found = [...new Set(found)];
        for(const toReplace of found) {
            const varValue = _M.variables[toReplace];
            if(typeof varValue === "undefined") {
                //no action
            } else if(typeof varValue === "object") {
                expression = expression.replaceAll(toReplace, JSON.stringify(varValue));
            } else if(typeof varValue === "string") {
                expression = expression.replaceAll( toReplace , "'" + varValue + "'");
            } else {
                expression = expression.replaceAll(toReplace, varValue);
            }
        }
    }

    if(elem !== null && typeof elem !== "undefined") {
        const eachNameForIndex = _M.parseEachNameFromConditionalExpression(expression);
        const parentElement = _M.findParentWithEachElement(elem, eachNameForIndex);

        if(null !== parentElement && typeof parentElement !== "undefined") {
            // expression: escape all, ignore \' (already escaped)
            expression = expression.replace(/([^\\])'/g,"$1\\'");

            const index = parentElement.getAttribute("index");
            const indexRef = "[$index#" + eachNameForIndex + "]";

            const isArray = expression.indexOf("([{") !== -1;
            const isMap = expression.indexOf("[$index#") !== -1;

            //handle object array
            if (isArray) {
                const objectToWrap = _M.parseArrayFromConditionalExpression(expression);
                let property = expression.substring(expression.indexOf(indexRef) + indexRef.length);
                property = property.substring(0, property.indexOf(" "));
                const valueReplacement = objectToWrap + indexRef ;
                expression = expression.replaceAll(valueReplacement, _M.evalCondition("Object.values(" + objectToWrap + ")[" + index + "]" + property ));
                expression = expression.replaceAll( property, "");
            }

            //handle maps
            //replace {}[$index#eachName].value with Object.values() w/ index of eachName
            else if (isMap) {
                const objectToWrap = _M.parseObjectFromConditionalExpression(expression);
                const valueReplacement = objectToWrap + indexRef + ".value";
                const keyReplacement = objectToWrap + indexRef + ".key";
                expression = expression.replaceAll(valueReplacement, _M.evalCondition("Object.values(" + objectToWrap + ")[" + index + "]"));
                expression = expression.replaceAll(keyReplacement, _M.evalCondition("Object.keys(" + objectToWrap + ")[" + index + "]"));
            }
        }
    }

    return expression;
}
_M.parseArrayFromConditionalExpression = function(expression) {
    const endIndex = expression.indexOf("[$index#");
    const subExpression = expression.substring(0, endIndex);
    const beginIndex = subExpression.indexOf("[{");
    return subExpression.substring(beginIndex, endIndex);
}

_M.parseObjectFromConditionalExpression = function(expression) {
    const endIndex = expression.indexOf("[$index#");
    const subExpression = expression.substring(0, endIndex);
    const beginIndex = subExpression.indexOf("{");
    return subExpression.substring(beginIndex, endIndex);
}

_M.parseEachNameFromConditionalExpression = function(expression) {
    const startIndex = expression.indexOf("[$index#") + 8;
    const subExpression = expression.substring(startIndex);
    const endIndex = subExpression.indexOf("]");
    return subExpression.substring(0, endIndex);
}

_M.injectVariablesIntoMethodExpression = function(expression, element) {
    if(typeof expression === "boolean") { return expression; }

    const startIndexOfParameters = expression.indexOf("(");

    //determine if this is a method call or a conditional
    if(startIndexOfParameters === -1) {
        return _M.injectVariablesIntoConditionalExpression(expression);
    } else {
        //interpret this as an actual expression, so we can differentiate between literals, numbers, booleans and to replace variables
        const methodName = expression.substring(0, startIndexOfParameters);
        const parametersAsOneString = expression.substring(startIndexOfParameters + 1, expression.lastIndexOf(")"));

        let roughParameters = parametersAsOneString.split("(?<=')\\s{0,1},\\s{0,1}(?=')");

        let splitParameters = [];
        for(let parameter of roughParameters) {
            let roughParameter = parameter.trim();
            if (roughParameter.indexOf(",") === -1) {
                splitParameters.push(roughParameter);
            } else {
                let deeperSplit = roughParameter.split(",");
                for (let j = 0; j < deeperSplit.length; j++) {
                    const currentSplitValue = deeperSplit[j];
                    const nextSplitValue = deeperSplit[j + 1];
                    if(typeof nextSplitValue === "undefined") {
                        splitParameters.push(currentSplitValue.trim());
                    } else {
                        if (currentSplitValue.trim().startsWith("'") && !currentSplitValue.trim().endsWith("'") &&
                            !nextSplitValue.trim().startsWith("'") && nextSplitValue.trim().endsWith("'")) {
                            let combinedValue = currentSplitValue + "," + nextSplitValue;
                            splitParameters.push(combinedValue.trim());
                            j++;
                        } else {
                            splitParameters.push(currentSplitValue.trim());
                        }
                    }
                }
            }
        }

        let parameters = [];
        for(const roughParameter of splitParameters) {
            let parameter = roughParameter.trim();
            if(parameter.length === 0) {
                continue;
            }
            parameter = _M.javaNumberCompatibility(parameter);
            if(!(_M.isJavaNumber(parameter) ||_M.isQuoted(parameter) || _M.isNumeric(parameter) || _M.isBoolean(parameter) )) {
                parameter = _M.lookupVariable(parameter, element);
                if(typeof parameter === "undefined") {
                    parameter = roughParameter;
                }
            }

            parameters.push(parameter);
        }

        return _M.buildMethod(methodName, parameters);
    }
};

_M.javaNumberCompatibility = function(parameter) {
    if(_M.isDecimal(parameter)) {
        return parameter + "d";
    } else if(_M.isNumeric(parameter)) {
        if(parameter > 2147483647 || parameter < -2147483648) {
            return parameter + "L";
        }
    }
    return parameter;
}

_M.lookupVariable = function(parameter, element) {
    let baseParameter = parameter;
    if(parameter.indexOf(".") !== -1) {
        baseParameter = parameter.substring(0, parameter.indexOf("."));
    }

    if (-1 !== baseParameter.indexOf("[")) {
        baseParameter = baseParameter.split(new RegExp("[.[]"))[0];
    }

    const deeperObjectPath = _M.determineDeeperObjectPath(parameter);
    const paramValue = _M.findPotentialEachValue(element, baseParameter);
    return _M.considerVariableWrap(_M.findThroughObjectPath(paramValue, null, deeperObjectPath, null, null));
};

_M.considerVariableWrap = function (value) {
    if (typeof value === "string") {
        return "'" + value + "'";
        if (value.length === 0 || (value.startsWith("'") && value.endsWith("'"))) {
            return value;
        } else {
            return "'" + value + "'";
        }
    } else {
        return value;
    }
};

_M.isNull = function (value) {
    return value === null || typeof value === "undefined";
};

_M.findPotentialEachValue = function (element, eachName) {
    let paramValue = _M.variables[eachName];
    if(null !== element && typeof element !== "undefined") {
        const parentElement = _M.findParentWithEachElement(element, eachName);
        if(null !== parentElement && typeof parentElement !== "undefined") {
            const index = parentElement.getAttribute("index");
            let relevantTemplateId = parentElement.getAttribute("template-id");
            relevantTemplateId = relevantTemplateId.substring(relevantTemplateId.lastIndexOf("#") + 1);
            const relevantVariableName = _M.conditionals[relevantTemplateId];
            if(null !== relevantVariableName && typeof relevantVariableName !== "undefined") {
                paramValue = _M.variables[relevantVariableName][index];
            }
        }
    }
    if(typeof paramValue === "undefined") {
        return eachName;
    }
    return paramValue;
}

_M.findParentWithEachElement = function (element, eachName) {
    if(eachName === null || typeof eachName === "undefined") { return null; }
    let currentElement = element;
    while(currentElement !== null && typeof currentElement !== "undefined") {
        if(eachName === currentElement.getAttribute("m-each")) {
            return currentElement;
        }
        currentElement = currentElement.parentElement;
    }
    return null;
};

_M.buildMethod = function(methodName, parameters) {
    let builder = methodName + "(";
    let comma = "";
    if(!(parameters === null || typeof parameters === "undefined")) {
        for(const parameter of parameters) {
            builder += comma;
            if(typeof parameter === "undefined") {
                builder += "null";
            } else {
                builder += parameter;
            }
            comma = ", ";
        }
    }
    builder += ")";
    return builder;
};

_M.isQuoted = function(itemToEval) {
    if(itemToEval === null || typeof itemToEval === "undefined") { return false; }
    return (itemToEval.startsWith("'") && itemToEval.endsWith("'")) || (itemToEval.startsWith("\"") && itemToEval.endsWith("\""));
};

_M.isBoolean = function(itemToEval) {
    if(itemToEval === null || typeof itemToEval === "undefined") { return false; }
    return "true" === itemToEval.toLowerCase() || "false" === itemToEval.toLowerCase();
};

_M.isDecimal = function(x) {
    if(_M.isNumeric(x)) {
        return x % 1 !== 0;
    } else {
        return false;
    }
}

_M.isNumeric = function(str) {
    if (typeof str != "string") { return false; }
    return !isNaN( parseFloat( str ) ) && isFinite( str );
}

_M.isJavaNumber = function(str) {
   return _M.isJavaLong(str) || _M.isJavaDoubleOrFloat(str);
}

_M.isJavaLong = function(str) {
    return str.match(/^[+-]?\d+[l]$/i);
}

_M.isJavaDoubleOrFloat = function(str) {
    return str.match(/^[+-]?(\d+\.?\d*|\.\d+)[df]$/ig);
}

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
    if(typeof k.v === "undefined" || k.v === null) {
        k.v = "";
    }
    _M.variables[k.f] = k.v;
    document.querySelectorAll("[from-value^="+k.f+"]").forEach(function(e) {
        let valueToSet = k.v;
        const deeperObjectPath = _M.determineDeeperObjectPath(e.getAttribute("from-value"));
        valueToSet = _M.findThroughObjectPath(valueToSet, null, deeperObjectPath, null, null);

        if(e.hasAttribute("value")) {
            e.setAttribute("value", valueToSet);
            e.dispatchEvent(new Event('input'));
        } else {
            e.innerText = valueToSet;
        }
    });

    _M.handleWaitingForEnabled();
};

_M.handleWaitingForEnabled = function() {
    for (let index = 0; index < _M.waitingForEnable.length; index++) {
        const objWaitingForEnable = _M.waitingForEnable[index];
        if(_M.evalCondition(_M.injectVariablesIntoConditionalExpression(objWaitingForEnable.expression))) {
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

_M.handleIterationCheck = function (k) {
    // clear old values
    document.querySelectorAll("[template-id="+k.f+"]").forEach(function(e) { e.remove(); });

    // set new values
    document.querySelectorAll("[m-id="+k.f+"]").forEach(function(template) {
        const templateId = _M.resolveTemplateId(template);
        const eachObject = _M.resolveTemplateCondition(templateId);
        const currentEachValues = Object.keys(eachObject);
        const divTemplate = template.content.children[0];

        let index = 0;
        if(Array.isArray(currentEachValues)) {
            for(const currentEachValue of currentEachValues) {
                const block = _M.buildIterationBlock(divTemplate, index++, eachObject);
                if(typeof _M.preRender !== "undefined") { _M.preRender(k, block); }
                template.parentNode.insertBefore(block, template);
            }
        } else {
            for (let i = 0; i < currentEachValues; i++) {
                const block = _M.buildIterationBlock(divTemplate, index++, eachObject);
                if(typeof _M.preRender !== "undefined") { _M.preRender(k, block); }
                template.parentNode.insertBefore(block, template);
            }
        }
    });
};
_M.buildIterationBlock = function (templateDiv, index, eachObject) {
    const node = templateDiv.cloneNode(true);
    node.querySelectorAll("template").forEach(function (templateNode) { templateNode.remove(); });
    node.setAttribute("index", index.toString());

    _M.buildIterationBlockMEachHandling(node, eachObject);
    node.querySelectorAll("[m-each]").forEach(function (divWithMEach) {
        _M.buildIterationBlockMEachHandling(divWithMEach, eachObject);
    });

    return node;
}

_M.buildIterationBlockMEachHandling = function (divWithMEach, eachObject) {
    const templateMap = _M.buildTemplateMap(divWithMEach);

    for(const mapEntry of templateMap) {
        const eachName = mapEntry["eachName"];
        const keyRefWhichIsValueReferential = eachName + "[" + eachName + ".key]";
        const valueReferential = eachName + ".value";
        divWithMEach.querySelectorAll("[from-value^='"+eachName+"']").forEach(function (specificSpan) {
            let path = specificSpan.getAttribute("from-value");
            if(keyRefWhichIsValueReferential === path) {
                path = valueReferential;
            }
            const deeperObjectPath = _M.determineDeeperObjectPath(path);
            const index = parseInt(mapEntry["index"], 10);
            const conditional = _M.conditionals[mapEntry["templateId"]];
            let foundObject = _M.findThroughObjectPath(_M.variables[conditional], index, deeperObjectPath, eachObject, eachName);
            const via = specificSpan.getAttribute("via");
            if(via === null) {
                specificSpan.textContent = foundObject;
            } else {
                specificSpan.setAttribute(via, foundObject);
            }
        });

        //if the 'each' is part of an expression, it's more difficult and we need to parse the each side first and then re-run the default event
        //we don't support very complex expressions, but one layer deep is acceptable
        divWithMEach.querySelectorAll("[from-value*='"+eachName+"']").forEach(function (specificSpan) {
            let path = specificSpan.getAttribute("from-value");
            if(path.startsWith(eachName)) { return; }

            const deeperObjectPath = _M.determineDeeperObjectPath(path);
            const index = parseInt(mapEntry["index"], 10);
            const conditional = _M.conditionals[mapEntry["templateId"]];
            let foundObject = _M.findThroughObjectPath(_M.variables[conditional], index, deeperObjectPath, eachObject, eachName);
            if(foundObject !== null && typeof foundObject !== "undefined") {
                const via = specificSpan.getAttribute("via");

                let mergedPath = _M.mergeIntoOnePath(deeperObjectPath);
                let finalValue = foundObject;

                if(mergedPath.indexOf(eachName) !== -1) {
                    if(typeof foundObject === "string") {
                        foundObject = "'" + foundObject + "'";
                    }
                    let finalPath = path.replaceAll(mergedPath, foundObject);
                    finalValue = _M.resolveVariableLookup(_M.variables, finalPath);
                }

                if(via === null) {
                    specificSpan.textContent = finalValue;
                } else {
                    specificSpan.setAttribute(via, finalValue);
                }
            }
        });
    }
}

_M.mergeIntoOnePath = function (path) {
    let final = "";
    let concat = "";
    for(let pathVal of path) {
        final += concat;
        final += pathVal;
        concat = ".";
    }
    return final;
}

_M.resolveVariableLookup = function (variable, path) {
    if(typeof path === "undefined") { return variable; }
    if(!(path.indexOf(".") !== -1 || (path.indexOf("[") !== -1 && path.indexOf("]") !== -1))) {
        return _M.variables[path];
    } else {
        let paths = path.split(new RegExp("[.[]"));
        let index = 1;
        let max = paths.length;
        let object = _M.variables[paths[0]];

        while (index !== max) {
            const p = paths[index++];
            let unBracketedPath = p.substring(0, p.length - 1);
            if(_M.isQuoted(unBracketedPath)) {
                unBracketedPath = unBracketedPath.substring(1, unBracketedPath.length - 1);
            }
            object = object[unBracketedPath];
        }
        return object;
    }
}

_M.findThroughObjectPath = function (variable, index, path, eachObject, eachName) {
    let object;
    if(index == null) {
        object = variable;
    } else {
        object = variable[index];
    }

    if(typeof object === "string" || typeof object === "number") {
        return object;
    } else {
        const possibleKeys = Object.keys(variable);
        const possibleKey = possibleKeys[index];

        if(path.length === 0) return object;

        while (path.length > 0) {
            const currentPath = path[0];
            if("key" === currentPath && _M.currentPathUnreachable(object, currentPath)) {
                return possibleKey;
            } else if ("value" === currentPath && _M.currentPathUnreachable(object, currentPath)) {
                return variable[possibleKey];
            } else {
                if((eachObject !== null && typeof object === "undefined") || (currentPath === eachName && index === null)) {
                    object = eachObject;
                } else if (currentPath === eachName && index !== null) {
                    object = eachObject[index];
                } else {
                    if(typeof object === "undefined") { return null; }
                    object = object[currentPath];
                    if(typeof object === "undefined") { return null; }
                }
                path = path.slice(1);
            }
        }

        return object;
    }
}

_M.currentPathUnreachable = function (object, currentPath) {
    return typeof object === "undefined" || null === object[currentPath];
}

_M.determineDeeperObjectPath = function (path) {
    if(!(path.indexOf(".") !== -1 || (path.indexOf("[") !== -1 && path.indexOf("]") !== -1))) {
        return [];
    } else {
        let paths = path.split(new RegExp("[.[]")).slice(1);
        let index = 0;
        for(const p of paths) {
            if(p.endsWith("]")) {
                let unBracketedPath = p.substring(0, p.length - 1);
                if(_M.isQuoted(unBracketedPath)) {
                    unBracketedPath = unBracketedPath.substring(1, unBracketedPath.length - 1);
                }
                paths[index] = unBracketedPath;
            }
            index++;
        }
        return paths;
    }
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
    let elems = _M.findElementByMIF(k.v);
    _M.handleVisibilityConditionals(elems, k.c);

    //update templates if needed
    let templates = document.getElementsByTagName("template");
    if(null !== templates && templates.length !== 0) {
        for(const template of templates) {
            const templateElems = template.content.querySelectorAll("." + k.v);
            _M.handleVisibilityConditionals(templateElems, k.c);
        }
    }
};

_M.handleVisibilityConditionals = function (elemsToIterateOver, expression) {
    if(null !== elemsToIterateOver && elemsToIterateOver.length !== 0) {
        for(let elem of elemsToIterateOver) {
            if(elem != null) {
                _M.setVisibilityOnElement(elem, expression);
            }
        }
    }
}

_M.setVisibilityOnElement = function (elem, expression) {
    const parsedCondition = _M.injectVariablesIntoConditionalExpression(expression, elem);
    let isVisible = _M.evalCondition(parsedCondition);

    if(isVisible) {
        elem.style.display = null;
    } else {
        elem.style.display = "none";
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
    if(_M.isNull(condition)) { return false; }
    _M.debug("Evaluating condition: " , condition);
    try{
        return Function('"use strict";return (' + condition + ')')();
    } catch (e) {
        _M.debug("Failure to evaluate condition due to: " , e);
        return false;
    }
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
    if(_M.isNull(element) || _M.isNull(attribute)) {
        return null;
    }
    if ( attribute in element ) {
        return element[attribute];
    }
    if(_M.isNull(element[attribute])) {
        return null;
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
            if(_M.isNull(resolvedParam)) {
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
    _M.debug("sendEvent: " + e );
    const disableOnClick = originElem.attributes["m:disable-on-click-until"];
    if(typeof disableOnClick !== "undefined") {
        const loadingStyle = originElem.attributes["m:loading-style"];

        _M.waitingForEnable.push({"elem": originElem, "expression": disableOnClick.value});
        originElem.disabled = true;
        if(typeof loadingStyle === "undefined" || loadingStyle.value === "top") {
            _M.handleVisibilityConditionals([document.getElementById("m-top-load-bar")], true);
        } else if (loadingStyle.value === "full") {
            _M.handleVisibilityConditionals([document.getElementById("m-full-loader")], true);
        }
    }

    e = _M.parseReference(e, originElem);
    _M.ws.send(_M.injectVariablesIntoMethodExpression(e, originElem));
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
