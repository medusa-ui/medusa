var _M = _M || {};

QUnit.module("_M.isBoolean", function() {
    QUnit.test("\"x\" is not a boolean", function(assert) {
        assert.false(_M.isBoolean("x"));
    });

    QUnit.test("undefined is not a boolean", function(assert) {
        assert.false(_M.isBoolean(undefined));
    });

    QUnit.test("null is not a boolean", function(assert) {
        assert.false(_M.isBoolean(null));
    });

    QUnit.test("\"true\" is a boolean", function(assert) {
        assert.true(_M.isBoolean("true"));
    });

    QUnit.test("\"FALSE\" is a boolean", function(assert) {
        assert.true(_M.isBoolean("FALSE"));
    });
});

QUnit.module("_M.isNumeric", function() {
    QUnit.test("\"x\" is not numeric", function(assert) {
        assert.false(_M.isNumeric("x"));
    });

    QUnit.test("undefined is not a dnumericcimal", function(assert) {
        assert.false(_M.isNumeric(undefined));
    });

    QUnit.test("null is not a numeric", function(assert) {
        assert.false(_M.isNumeric(null));
    });

    QUnit.test("1 is numeric", function(assert) {
        assert.true(_M.isNumeric(1));
    });

    QUnit.test("1345 is numeric", function(assert) {
        assert.true(_M.isNumeric(1345));
    });

    QUnit.test("1.36 is numeric", function(assert) {
        assert.true(_M.isNumeric(1.36));
    });

    QUnit.test("0.5 is numeric", function(assert) {
        assert.true(_M.isNumeric(0.5));
    });

    QUnit.test("'1' is numeric", function(assert) {
        assert.true(_M.isNumeric('1'));
    });

    QUnit.test("'1345' is numeric", function(assert) {
        assert.true(_M.isNumeric('1345'));
    });

    QUnit.test("'1.36' is numeric", function(assert) {
        assert.true(_M.isNumeric('1.36'));
    });

    QUnit.test("'0.5' is numeric", function(assert) {
        assert.true(_M.isNumeric('0.5'));
    });

    QUnit.test("'-0.5' is numeric", function(assert) {
        assert.true(_M.isNumeric('-0.5'));
    });
});

QUnit.module("_M.isDecimal", function() {
    QUnit.test("\"x\" is not a decimal", function(assert) {
        assert.false(_M.isDecimal("x"));
    });

    QUnit.test("undefined is not a decimal", function(assert) {
        assert.false(_M.isDecimal(undefined));
    });

    QUnit.test("null is not a decimal", function(assert) {
        assert.false(_M.isDecimal(null));
    });

    QUnit.test("1 is not a decimal", function(assert) {
        assert.false(_M.isDecimal(1));
    });

    QUnit.test("1.36 is a decimal", function(assert) {
        assert.true(_M.isDecimal(1.36));
    });

    QUnit.test("0.5 is a decimal", function(assert) {
        assert.true(_M.isDecimal(0.5));
    });

    QUnit.test("'1' is not a decimal", function(assert) {
        assert.false(_M.isDecimal('1'));
    });

    QUnit.test("'1.36' is a decimal", function(assert) {
        assert.true(_M.isDecimal('1.36'));
    });

    QUnit.test("'0.5' is a decimal", function(assert) {
        assert.true(_M.isDecimal('0.5'));
    });

    QUnit.test("'-0.5' is a decimal", function(assert) {
        assert.true(_M.isDecimal('-0.5'));
    });
});

QUnit.module("_M.isJavaNumber", function() {
    QUnit.test("\"x\" is not a decimal", function(assert) {
        assert.false(_M.isJavaNumber("x"));
    });
});

QUnit.module("_M.isJavaLong", function() {
    QUnit.test("null is not java long", function(assert) {
        assert.false(_M.isJavaLong(null));
    });

    QUnit.test("'bowl' is not java long", function(assert) {
        assert.false(_M.isJavaLong('bowl'));
    });

    QUnit.test("'4435l' is a java long", function(assert) {
        assert.true(_M.isJavaLong('4435l'));
    });

    QUnit.test("'4435L' is a java long", function(assert) {
        assert.true(_M.isJavaLong('4435L'));
    });
});

QUnit.module("_M.isJavaDoubleOrFloat", function() {
    QUnit.test("null is not java float or double", function(assert) {
        assert.false(_M.isJavaDoubleOrFloat(null));
    });

    QUnit.test("'loaf' is not java float or double", function(assert) {
        assert.false(_M.isJavaDoubleOrFloat('loaf'));
    });

    QUnit.test("'road' is not java float", function(assert) {
        assert.false(_M.isJavaDoubleOrFloat('road'));
    });

    QUnit.test("'0.4435f' is a java float", function(assert) {
        assert.true(_M.isJavaDoubleOrFloat('0.4435f'));
    });

    QUnit.test("'0.4435d' is a java double", function(assert) {
        assert.true(_M.isJavaDoubleOrFloat('0.4435d'));
    });

    QUnit.test("'0.4435F' is a java float", function(assert) {
        assert.true(_M.isJavaDoubleOrFloat('0.4435F'));
    });

    QUnit.test("'0.4435D' is a java double", function(assert) {
        assert.true(_M.isJavaDoubleOrFloat('0.4435D'));
    });
});

QUnit.module("_M.javaNumberCompatibility", function() {
    QUnit.test("simple single quoted string", function(assert) {
        assert.equal(_M.javaNumberCompatibility("'hello world'"), "'hello world'");
    });

    QUnit.test("0 < x < 2147483647 should stay as-is", function(assert) {
        assert.equal(_M.javaNumberCompatibility(99999), 99999);
    });

    QUnit.test("decimals should turn to double", function(assert) {
        assert.equal(_M.javaNumberCompatibility(99999.99), "99999.99d");
    });

    QUnit.test("< -2147483647 should become a long", function(assert) {
        assert.equal(_M.javaNumberCompatibility(-9999999999), "-9999999999l");
    });

    QUnit.test("> 2147483647 should become a long", function(assert) {
        assert.equal(_M.javaNumberCompatibility(9999999999), "9999999999l");
    });
});

QUnit.module("_M.isQuoted", function() {
    QUnit.test("simple single quoted string", function(assert) {
        assert.true(_M.isQuoted("'hello world'"));
    });

    QUnit.test("simple double quoted string", function(assert) {
        assert.true(_M.isQuoted("\"hello world\""));
    });

    QUnit.test("non-quoted string", function(assert) {
        assert.false(_M.isQuoted("hello world"));
    });

    QUnit.test("non-quoted string with quote internally used once", function(assert) {
        assert.false(_M.isQuoted("it's a wonderful world"));
    });

    QUnit.test("non-quoted string with quote internally used twice", function(assert) {
        assert.false(_M.isQuoted("the word 'dance' is okay"));
    });

    QUnit.test("deals with undefined", function(assert) {
        assert.false(_M.isQuoted(undefined));
    });

    QUnit.test("deals with null", function(assert) {
        assert.false(_M.isQuoted(null));
    });
});

QUnit.module("_M.buildMethod", function() {
    QUnit.test("simple method build - no params", function(assert) {
        assert.equal(_M.buildMethod("helloWorld", []), "helloWorld()");
    });

    QUnit.test("simple method build - no params - null", function(assert) {
        assert.equal(_M.buildMethod("helloWorld", null), "helloWorld()");
    });

    QUnit.test("simple method build - no params - undefined", function(assert) {
        assert.equal(_M.buildMethod("helloWorld", undefined), "helloWorld()");
    });

    QUnit.test("simple method build - one param", function(assert) {
        assert.equal(_M.buildMethod("helloWorld", ["'a'"]), "helloWorld('a')");
    });

    QUnit.test("simple method build - two params", function(assert) {
        assert.equal(_M.buildMethod("helloWorld", ["'a'", "'b'"]), "helloWorld('a', 'b')");
    });

    QUnit.test("simple method build - mix", function(assert) {
        assert.equal(_M.buildMethod("helloWorld", [1, "'b'", 2]), "helloWorld(1, 'b', 2)");
    });

    QUnit.test("simple method build - deals with undefined", function(assert) {
        assert.equal(_M.buildMethod("helloWorld", [1, undefined, "'b'"]), "helloWorld(1, null, 'b')");
    });

    QUnit.test("simple method build - deals with null", function(assert) {
        assert.equal(_M.buildMethod("helloWorld", [1, null, "'b'"]), "helloWorld(1, null, 'b')");
    });
});

QUnit.module("_M.findParentWithEachElement", function() {
    const eachName = "xyz";

    QUnit.test("self", function(assert) {
        let parent = document.createElement("div");
        parent.setAttribute("id", "parent-node");
        parent.setAttribute("m-each", eachName);

        let foundVal = _M.findParentWithEachElement(parent, eachName)
        assert.notEqual(foundVal, null);
        assert.equal(foundVal.getAttribute("id"), "parent-node");
    });

    QUnit.test("simple parent", function(assert) {
        let parent = document.createElement("div");
        parent.setAttribute("id", "parent-node");
        parent.setAttribute("m-each", eachName);

        let element = document.createElement("div");
        element.setAttribute("id", "child-node");

        parent.appendChild(element);

        let foundVal = _M.findParentWithEachElement(parent.firstElementChild, eachName)
        assert.notEqual(foundVal, null);
        assert.equal(foundVal.getAttribute("id"), "parent-node");
    });

    QUnit.test("simple parent - 2 deep", function(assert) {
        let parent = document.createElement("div");
        parent.setAttribute("id", "parent-node");
        parent.setAttribute("m-each", eachName);

        let mid = document.createElement("div");
        mid.setAttribute("id", "mid-node");
        mid.setAttribute("m-each", "x");

        let element = document.createElement("div");
        element.setAttribute("id", "child-node");

        mid.appendChild(element);
        parent.appendChild(mid);

        let foundVal = _M.findParentWithEachElement(parent.firstElementChild.firstElementChild, eachName)
        assert.notEqual(foundVal, null);
        assert.equal(foundVal.getAttribute("id"), "parent-node");
    });

    QUnit.test("no match", function(assert) {
        let parent = document.createElement("div");
        parent.setAttribute("id", "parent-node");
        parent.setAttribute("m-each", "0");

        let mid = document.createElement("div");
        mid.setAttribute("id", "mid-node");
        mid.setAttribute("m-each", "0");

        let element = document.createElement("div");
        element.setAttribute("id", "child-node");

        mid.appendChild(element);
        parent.appendChild(mid);

        let foundVal = _M.findParentWithEachElement(parent.firstElementChild.firstElementChild, eachName)
        assert.equal(foundVal, null);
    });

    QUnit.test("no match - deals with null", function(assert) {
        let element = document.createElement("div");
        let foundVal = _M.findParentWithEachElement(element, null)
        assert.equal(foundVal, null);
    });

    QUnit.test("no match - deals with undefined", function(assert) {
        let element = document.createElement("div");
        let foundVal = _M.findParentWithEachElement(element, undefined)
        assert.equal(foundVal, null);
    });
});

QUnit.module("_M.evalCondition", function() {
    QUnit.test("simple value - boolean", function(assert) {
        assert.equal(_M.evalCondition("false"), false);
        assert.equal(_M.evalCondition("true"), true);
    });

    QUnit.test("simple value - integer", function(assert) {
        assert.equal(_M.evalCondition("1"), 1);
        assert.equal(_M.evalCondition("2+3"), 5);
    });

    QUnit.test("simple value - string", function(assert) {
        assert.equal(_M.evalCondition("'x'"), "x");
        assert.equal(_M.evalCondition("'x' + \"y\""), "xy");
    });

    QUnit.test("simple condition", function(assert) {
        assert.equal(_M.evalCondition("5 > 3"), true);
        assert.equal(_M.evalCondition("(5-1) > (1+11)"), false);
    });

    QUnit.test("object condition", function(assert) {
        assert.equal(_M.evalCondition("{'x': 5}['x']"), 5);
        assert.equal(_M.evalCondition("{'x': {y:[10]}}['x']['y'][0] > 2"), true);
    });

    QUnit.test("array condition", function(assert) {
        assert.equal(_M.evalCondition("[{'x': 5}][0]['x']"), 5);
    });

    QUnit.test("broken condition should return false", function(assert) {
        assert.equal(_M.evalCondition("5 > "), false);
    });

    QUnit.test("can deal with undefined", function(assert) {
        assert.equal(_M.evalCondition(undefined), false);
    });

    QUnit.test("can deal with null", function(assert) {
        assert.equal(_M.evalCondition(null), false);
    });

    //Object.values([{"id":"3","name":"PETER"},{"id":"4","name":"JEANETTE"}])[1].id
    //!(4 === Z || !( 4 === Z ) && (4 === 1))
});

QUnit.module("_M.attributeValue", function() {
    let elem = document.createElement("input");
    elem.type = "text";

    QUnit.test("simple existing attribute", function (assert) {
        assert.equal(_M.attributeValue(elem, "type"), "text");
    });

    QUnit.test("deeper existing attribute", function (assert) {
        assert.equal(_M.attributeValue(elem, "type"), "text");
    });

    QUnit.test("can deal with null", function (assert) {
        assert.equal(_M.attributeValue(null, "type"), null);
        assert.equal(_M.attributeValue(elem, null), null);
        assert.equal(_M.attributeValue(undefined, "type"), null);
        assert.equal(_M.attributeValue(elem, undefined), null);
    });
});

QUnit.module("_M.parseSelfReference", function() {
    let originElement = document.createElement("input");
    originElement.value = "Hello world 123";
    originElement.type = "text";
    originElement.name = "my-name";

    QUnit.test("reference without this.", function (assert) {
        let raw = "('x',3,'y')";
        let e = "search('x',3,'y')";
        assert.equal(_M.parseSelfReference(raw, e, originElement), "search('x', 3, 'y')");
    });

    QUnit.test("reference with this. from input", function (assert) {
        let raw = "(this.value,3,this.type,this.name)";
        let e = "search(this.value,3,this.type,this.name)";

        assert.equal(_M.parseSelfReference(raw, e, originElement), "search('Hello%20world%20123', 3, 'text', 'my-name')");
    });

    QUnit.test("reference with this. from input, with undefined property", function (assert) {
        let raw = "(this.value,3,this.type,this.class)";
        let e = "search(this.value,3,this.type,this.class)";

        assert.equal(_M.parseSelfReference(raw, e, originElement), "search('Hello%20world%20123', 3, 'text', null)");
    });

    QUnit.test("input - no origin element", function (assert) {
        let raw = "('x',3,'y')";
        let e = "search('x',3,'y')";
        assert.equal(_M.parseSelfReference(raw, e, null), "search('x', 3, 'y')");
    });

    QUnit.test("can deal with empty", function (assert) {
        assert.equal(_M.parseSelfReference("()", "()", null), "()");
    });
});

QUnit.module("_M.parseElementByIdReference", function() {

    let originElement = document.createElement("input");
    originElement.value = "Hello world 123";
    originElement.type = "text";
    originElement.name = "my-name";

    QUnit.test("reference without this.", function (assert) {
        let raw = "('x',3,'y')";
        let e = "search('x',3,'y')";
        assert.equal(_M.parseElementByIdReference(raw, e, originElement), "search('x',3,'y')");
    });

    QUnit.test("reference with #", function (assert) {
        let raw = "(#qunit-filter-input.value,3,#qunit-filter-input.type,#qunit-filter-input.name)";
        let e = "search(#qunit-filter-input.value,3,#qunit-filter-input.type,#qunit-filter-input.name)";

        assert.equal(_M.parseElementByIdReference(raw, e, originElement), "search('',3,'text','filter')");
    });

});

QUnit.module("_M.parseReference", function() {
    let originElement = document.createElement("input");
    originElement.value = "Hello world 123";
    originElement.type = "text";
    originElement.name = "my-name";

    QUnit.test("reference without this.", function (assert) {
        let e = "search('x',3,'y')";
        assert.equal(_M.parseReference(e, originElement), "search('x',3,'y')");
    });

    QUnit.test("reference with this. from input", function (assert) {
        let e = "search(this.value,3,this.type,this.name)";

        assert.equal(_M.parseReference(e, originElement), "search('Hello%20world%20123', 3, 'text', 'my-name')");
    });

    QUnit.test("reference with this. from input, with undefined property", function (assert) {
        let e = "search(this.value,3,this.type,this.class)";
        assert.equal(_M.parseReference(e, originElement), "search('Hello%20world%20123', 3, 'text', null)");
    });

    QUnit.test("input - no origin element", function (assert) {
        let e = "search('x',3,'y')";
        assert.equal(_M.parseReference(e, null), "search('x',3,'y')");
    });

    QUnit.test("can deal with empty", function (assert) {
        assert.equal(_M.parseReference("()", null), "()");
    });

    QUnit.test("reference with #", function (assert) {
        let e = "search(#qunit-filter-input.value,3,#qunit-filter-input.type,#qunit-filter-input.name)";
        assert.equal(_M.parseReference(e, originElement), "search('',3,'text','filter')");
    });
});

QUnit.module("_M.resolveTemplateId", function() {
    QUnit.test("standard lookup", function (assert) {
        let template = document.createElement("template");
        template.setAttribute("m-id", "template-123");

        assert.equal(_M.resolveTemplateId(template), "template-123");
    });

    QUnit.test("can deal with no m-id", function (assert) {
        let template = document.createElement("template");
        assert.equal(_M.resolveTemplateId(template), null);
    });

    QUnit.test("can deal with null", function (assert) {
        assert.equal(_M.resolveTemplateId(null), null);
    });
});

QUnit.module("_M.resolveTemplateCondition", function() {
    QUnit.test("Simple lookup", function (assert) {
        _M.conditionals = { "t-512841462": "items-bought" };
        _M.variables = { "items-bought": ["123"]};

        assert.equal(_M.resolveTemplateCondition("t-512841462"), "123");
    });

    QUnit.test("Lookup not found", function (assert) {
        _M.conditionals = { "t-512841462": "xyz" };
        _M.variables = { "items-bought": ["123"]};

        assert.equal(_M.resolveTemplateCondition("t-512841462"), null);
        assert.equal(_M.resolveTemplateCondition("t-512800xyz"), null);
    });
});

QUnit.module("_M.recursiveObjectUpdate", function() {
    QUnit.test("Simple one-layer update", function (assert) {
        let html = "xyz[john]zyx";
        let obj = "_";
        let path = "john";

        assert.equal(_M.recursiveObjectUpdate(html, obj, path), "xyz_zyx");
    });

    QUnit.test("Simple two-layer update", function (assert) {
        let html = "xyz[x.y]zyx";
        let obj = {"y" : "_" };
        let path = "x";

        assert.equal(_M.recursiveObjectUpdate(html, obj, path), "xyz_zyx");
    });

    //TODO when not found, etc

    QUnit.test("Empty", function (assert) {
        assert.equal(_M.recursiveObjectUpdate("", {}, ""), "");
    });
});

QUnit.module("_M.injectVariablesIntoText", function() {
    QUnit.test("Simple injection", function (assert) {
        let text = "ABC y";
        _M.variables = {"y" : "123"};
        assert.equal(_M.injectVariablesIntoText(text), "ABC 123");
    });

    QUnit.test("No injection", function (assert) {
        let text = "ABC y";
        _M.variables = {};
        assert.equal(_M.injectVariablesIntoText(text), "ABC y");
    });

    QUnit.test("Can deal with null", function (assert) {
        _M.variables = {};
        assert.equal(_M.injectVariablesIntoText(null), null);
    });
});

QUnit.module("_M.currentPathUnreachable", function() {
    QUnit.test("Simple reachable path", function (assert) {
        assert.false(_M.currentPathUnreachable({"x":1}, "x"));
    });

    QUnit.test("Deeper path unreachable", function (assert) {
        assert.true(_M.currentPathUnreachable({"x": {"y": {"z": {"_" : 1}}}}, "y"));
    });

    QUnit.test("Can deal with null", function (assert) {
        assert.true(_M.currentPathUnreachable(null, "x"));
    });
});

QUnit.module("_M.mergeIntoOnePath", function() {
    QUnit.test("Simple merge", function (assert) {
        assert.equal(_M.mergeIntoOnePath(["x", "y", "z"]), "x.y.z");
    });

    QUnit.test("Can deal with empty", function (assert) {
        assert.equal(_M.mergeIntoOnePath([]), "");
    });

    QUnit.test("Can deal with null", function (assert) {
        assert.equal(_M.mergeIntoOnePath(null), null);
    });
});

/*
QUnit.module("_M.parseEachNameFromConditionalExpression", function() {
    QUnit.test("simple parse", function(assert) {
        assert.equal(_M.parseEachNameFromConditionalExpression("0123-[$index#hello-world]-0123"), "hello-world");
        assert.equal(_M.parseEachNameFromConditionalExpression("[\"C\",\"D\"][$index#string] === Z"), "string");
    });

    QUnit.test("has nothing to parse", function(assert) {
        assert.equal(_M.parseEachNameFromConditionalExpression("0123"), null);
    });

    QUnit.test("can deal with undefined", function(assert) {
        assert.equal(_M.parseEachNameFromConditionalExpression(undefined), null);
    });

    QUnit.test("can deal with null", function(assert) {
        assert.equal(_M.parseEachNameFromConditionalExpression(null), null);
    });
});

QUnit.module("_M.parseArrayFromConditionalExpression", function() {
    QUnit.test("simple parse", function(assert) {
        //TODO something weird here, find proper examples
        assert.equal(_M.parseArrayFromConditionalExpression("[0,1,2][$index#string] === Z"), "[0,1,2]");
        assert.equal(_M.parseArrayFromConditionalExpression("[{[0,1,2]}][$index#string] === Z"), "[{[0,1,2]}]");
    });

    QUnit.test("has nothing to parse", function(assert) {
        assert.equal(_M.parseArrayFromConditionalExpression("0123"), null);
    });

    QUnit.test("can deal with undefined", function(assert) {
        assert.equal(_M.parseArrayFromConditionalExpression(undefined), null);
    });

    QUnit.test("can deal with null", function(assert) {
        assert.equal(_M.parseArrayFromConditionalExpression(null), null);
    });
});

QUnit.module("_M.parseObjectFromConditionalExpression", function() {
    QUnit.test("simple parse", function(assert) {
        //TODO something weird here, find proper examples
        assert.equal(_M.parseObjectFromConditionalExpression("{'x':5}[$index#eachName].value"), "{'x':5}");
        assert.equal(_M.parseObjectFromConditionalExpression("[\"C\",\"D\"][$index#string] === Z"), "[\"C\",\"D\"]");
    });

    QUnit.test("has nothing to parse", function(assert) {
        assert.equal(_M.parseObjectFromConditionalExpression("0123"), null);
    });

    QUnit.test("can deal with undefined", function(assert) {
        assert.equal(_M.parseObjectFromConditionalExpression(undefined), null);
    });

    QUnit.test("can deal with null", function(assert) {
        assert.equal(_M.parseObjectFromConditionalExpression(null), null);
    });
});

//_M.considerVariableWrap = function (value)
QUnit.module("_M.considerVariableWrap", function() {
    QUnit.test("simple wrap - string", function(assert) {
        assert.equal(_M.considerVariableWrap("hello"), "'hello'");
    });

    QUnit.test("simple wrap - boolean", function(assert) {
        assert.equal(_M.considerVariableWrap(true), true);
    });

    QUnit.test("simple wrap - integer", function(assert) {
        assert.equal(_M.considerVariableWrap(2), 2);
    });

    QUnit.test("already wrapped", function(assert) {
        assert.equal(_M.considerVariableWrap("'hello'"), "'hello'");
    });

    QUnit.test("has nothing to wrap", function(assert) {
        assert.equal(_M.considerVariableWrap(""), "");
    });

    QUnit.test("can deal with undefined", function(assert) {
        assert.equal(_M.considerVariableWrap(undefined), null);
    });

    QUnit.test("can deal with null", function(assert) {
        assert.equal(_M.considerVariableWrap(null), null);
    });
});
*/

//TODO injectVariablesIntoConditionalExpression <-- replaces === 'a' with === \'a\' which crashes eval

//TODO
//_M.isDecimal = function(x)
//_M.isNumeric = function(str)
//_M.isJavaNumber = function(str)
//_M.isJavaLong = function(str)
//_M.isJavaDoubleOrFloat = function(str)
//_M.injectVariablesIntoConditionalExpression = function(expression, elem) <--- has some problems in it
//_M.injectVariablesIntoMethodExpression = function(expression, element)
//_M.javaNumberCompatibility = function(parameter)
//_M.lookupVariable = function(parameter, element)

//_M.findPotentialEachValue = function (element, eachName)
//_M.injectVariablesIntoText = function(text)
//_M.buildIterationBlock = function (templateDiv, index, eachObject)
//_M.buildIterationBlockMEachHandling = function (divWithMEach, eachObject)
//_M.mergeIntoOnePath = function (path)
//_M.resolveVariableLookup = function (variable, path)
//_M.findThroughObjectPath = function (variable, index, path, eachObject, eachName)
//_M.currentPathUnreachable = function (object, currentPath)
//_M.determineDeeperObjectPath = function (path)
//_M.buildTemplateMap = function (divWithMEach)
//_M.resolveTemplateId = function (template)
//_M.resolveTemplateCondition = function (templateId)
//_M.recursiveObjectUpdate = function(html, obj, path)
//_M.findElementByMIF = function(key)
//_M.setVisibilityOnElement = function (elem, expression)
//_M.elementEscape = function(valueToEscape)
//_M.attributeValue = function (element, attribute)
//_M.parseReference = function(e, originElem)
//_M.parseElementByIdReference = function(raw, e, originElem)
//_M.parseSelfReference = function(raw, e, originElem)
//_M.sendEvent = function(originElem, e)
//_M.onEnter = function(originElem, action, event)
//_M.preventDefault = function(event)
//_M.eventHandler = function(e)
//_M.handleMAttributeChange = function (k)
//_M.handleHydraMenuItemChange = function (k)
//_M.handleConditionCheckEvent = function(k)
//_M.handleVisibilityConditionals
//_M.handleConditionalClass = function(k)
//_M.handleMAttribute = function (mId, trueFunc, falseFunc, evalValue)
//_M.handleDefaultEvent = function(k)
//_M.handleWaitingForEnabled = function()
//_M.handleTitleChangeEvent = function(k)
//_M.handleIterationCheck = function (k)