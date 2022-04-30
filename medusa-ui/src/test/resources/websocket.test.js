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
        let foundVal = _M.findParentWithEachElement(element, null);
        assert.equal(foundVal, null);
        assert.equal(_M.findParentWithEachElement(null, null), null);
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

    QUnit.test("operation", function (assert) {
        let button = document.createElement("button");
        button.id = "btn_mul";
        button.setAttribute("operation", "multiply");

        assert.equal(_M.attributeValue(button, "operation"), "multiply");
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

    QUnit.test("operation", function (assert) {
        let button = document.createElement("button");
        button.id = "btn_mul";
        button.operation = "multiply";

        let raw = "('x',3,this.operation)";
        let e = "calc('x',3,this.operation)";
        assert.equal(_M.parseSelfReference(raw, e, button), "calc('x', 3, 'multiply')");
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

    QUnit.test("operation", function (assert) {
        let button = document.createElement("button");
        button.id = "btn_mul";
        button.operation = "multiply";

        let e = "calc('x',3,this.operation)";
        assert.equal(_M.parseReference(e, button), "calc('x', 3, 'multiply')");
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

QUnit.module("_M.findPotentialEachValue", function() {
    _M.variables = {};

    QUnit.test("Simple find", function (assert) {
        _M.variables = { "letters" : ['A', 'B', 'C'] };
        _M.conditionals = { "t-123" : "letters" };
        let parent = document.createElement("div");
        parent.setAttribute("template-id", "t-123");
        parent.setAttribute("m-each", "xyz");
        parent.setAttribute("index", "0");

        parent.appendChild(document.createElement("div"));
        let element = parent.childNodes.item(0);

        assert.equal(_M.findPotentialEachValue(element, "xyz"), "A");
    });

    QUnit.test("Find with # - Nested loop", function (assert) {
        _M.variables = { "letters" : ['A', 'B', 'C'] };
        _M.conditionals = { "t-123" : "letters" };
        let parent = document.createElement("div");
        parent.setAttribute("template-id", "t-543#t-123");
        parent.setAttribute("m-each", "xyz");
        parent.setAttribute("index", "1");

        parent.appendChild(document.createElement("div"));
        let element = parent.childNodes.item(0);

        assert.equal(_M.findPotentialEachValue(element, "xyz"), "B");
    });

    QUnit.test("Find with no index in parent", function (assert) {
        _M.variables = {};
        _M.conditionals = {};
        let parent = document.createElement("div");
        parent.setAttribute("template-id", "t-123");
        parent.setAttribute("m-each", "xyz");

        parent.appendChild(document.createElement("div"));
        let element = parent.childNodes.item(0);

        assert.equal(_M.findPotentialEachValue(element, "xyz"), "xyz");
    });

    QUnit.test("Find with no template-id in parent", function (assert) {
        _M.variables = {};
        _M.conditionals = {};
        let parent = document.createElement("div");
        parent.setAttribute("index", "0");
        parent.setAttribute("m-each", "xyz");

        parent.appendChild(document.createElement("div"));
        let element = parent.childNodes.item(0);

        assert.equal(_M.findPotentialEachValue(element, "xyz"), "xyz");
    });

    QUnit.test("No variables to find", function (assert) {
        _M.variables = {};
        _M.conditionals = { "t-123" : "letters" };
        let parent = document.createElement("div");
        parent.setAttribute("template-id", "t-123");
        parent.setAttribute("m-each", "xyz");
        parent.setAttribute("index", "0");

        parent.appendChild(document.createElement("div"));
        let element = parent.childNodes.item(0);

        assert.equal(_M.findPotentialEachValue(element, "xyz"), "xyz");
    });

    QUnit.test("No conditionals to find", function (assert) {
        _M.variables = { "letters" : ['A', 'B', 'C'] };
        _M.conditionals = {};
        let parent = document.createElement("div");
        parent.setAttribute("template-id", "t-123");
        parent.setAttribute("m-each", "xyz");
        parent.setAttribute("index", "0");

        parent.appendChild(document.createElement("div"));
        let element = parent.childNodes.item(0);

        assert.equal(_M.findPotentialEachValue(element, "xyz"), "xyz");
    });

    QUnit.test("No find - default to eachName", function (assert) {
        assert.equal(_M.findPotentialEachValue(document.createElement("p"), "x"), "x");
    });

    QUnit.test("Can handle null", function (assert) {
        assert.equal(_M.findPotentialEachValue( document.createElement("p"), null), null);
        assert.equal(_M.findPotentialEachValue( null, null), null);
        assert.equal(_M.findPotentialEachValue( null, "x"), "x");
    });
});

QUnit.module("_M.findThroughObjectPath", function() {

    QUnit.test("Simple find", function (assert) {
        assert.equal(_M.findThroughObjectPath({"x":123}, null, "x", null, null), 123);
    });

    QUnit.test("Simple find - array", function (assert) {
        assert.equal(_M.findThroughObjectPath([541, 123, 678], 1, "x", null, null), 123);
    });

    QUnit.test("Simple find - object", function (assert) {
        assert.equal(_M.findThroughObjectPath({"x":541}, "x", null, null, null), 541);
    });

    QUnit.test("Path find", function (assert) {
        assert.equal(_M.findThroughObjectPath({"x":{"y":678}}, null, ["x", "y"], null, null), 678);
    });

    QUnit.test("Path find - no path", function (assert) {
        assert.deepEqual(_M.findThroughObjectPath({"y":678}, null, [], null, null), {"y": 678});
    });

    QUnit.test("Map path find - key, no index", function (assert) {
        assert.equal(_M.findThroughObjectPath({"x":{"y":678}}, null, ["x", "key"], null, null), "y");
    });

    QUnit.test("Path find w/ index", function (assert) {
        assert.equal(_M.findThroughObjectPath({"x":678}, 0, ["x"], null, null), 678);
    });

    QUnit.test("Map path find - key, w/ index", function (assert) {
        assert.equal(_M.findThroughObjectPath({"x":{"y":678}}, 0, ["x", "key"], null, null), "x");
    });

    QUnit.test("Map path find - value, no index", function (assert) {
        assert.equal(_M.findThroughObjectPath({"x":{"y":678}}, null, ["x", "value"], null, null), 678);
    });

    QUnit.test("Map path find - value, w/ index", function (assert) {
        assert.equal(_M.findThroughObjectPath({"x":{"y":678}}, 0, ["x", "value"], null, null), 678);
    });

    QUnit.test("Direct object path find - key, no index", function (assert) {
      assert.equal(_M.findThroughObjectPath({"x":2}, null, ["x", "key"], null, null), 0);
    });

    QUnit.test("Direct object map path find - value, no index", function (assert) {
      assert.equal(_M.findThroughObjectPath({"x":"2"}, null, ["x", "value"], null, null), "2");
    });

    QUnit.test("Direct object path find - null, no index", function (assert) {
        assert.equal(_M.findThroughObjectPath({"x": null}, null, ["x", "y"], null, null), null);
    });

    QUnit.test("Deep direct object path find - null, no index", function (assert) {
        assert.equal(_M.findThroughObjectPath({"x":{"y": null}}, null, ["x", "y"], null, null), null);
    });

    QUnit.test("Each object", function (assert) {
        assert.equal(_M.findThroughObjectPath({}, 1, ["eachLookup"], [123, 567], "eachLookup"), 567);
    });

    QUnit.test("Each object - without index", function (assert) {
        assert.equal(_M.findThroughObjectPath({}, null, ["eachLookup"], 123, "eachLookup"), 123);
    });

    QUnit.test("Can deal with null", function (assert) {
        assert.equal(_M.findThroughObjectPath(null, null, [], null, null), null);
    });

});

QUnit.module("_M.buildTemplateMap ", function() {
    QUnit.test("no template-id", function(assert) {
        assert.deepEqual(_M.buildTemplateMap (document.createElement("p")), []);
    });

    QUnit.test("no meach", function(assert) {
        let element = document.createElement("div");
        element.setAttribute("template-id", "t-123");

        assert.deepEqual(_M.buildTemplateMap (element), []);
    });

    QUnit.test("simple build", function(assert) {
        let element = document.createElement("div");
        element.setAttribute("template-id", "t-123");
        element.setAttribute("m-each", "person");
        element.setAttribute("index", "0");

        assert.deepEqual(_M.buildTemplateMap (element), [{"eachName": "person", "index": "0", "templateId": "t-123"}]);
    });

    QUnit.test("nested build", function(assert) {
        let element = document.createElement("div");
        element.setAttribute("template-id", "t-534#t-123");
        element.setAttribute("m-each", "person");
        element.setAttribute("index", "5");

        assert.deepEqual(_M.buildTemplateMap (element), [{"eachName": "person", "index": "5", "templateId": "t-123"}]);
    });

    QUnit.test("no index", function(assert) {
        let element = document.createElement("div");
        element.setAttribute("template-id", "t-123");
        element.setAttribute("m-each", "person");

        assert.deepEqual(_M.buildTemplateMap (element), [{"eachName": "person", "index": null, "templateId": "t-123"}]);
    });
});

QUnit.module("_M.determineDeeperObjectPath", function() {
    QUnit.test("no path to parse", function(assert) {
        assert.deepEqual(_M.determineDeeperObjectPath(""), []);
    });

    QUnit.test("simple path, disregards first value so empty", function(assert) {
        assert.deepEqual(_M.determineDeeperObjectPath("x"), []);
    });

    QUnit.test("simple chain", function(assert) {
        assert.deepEqual(_M.determineDeeperObjectPath("x.y.z"), ["y", "z"]);
    });

    QUnit.test("complex array chain", function(assert) {
        assert.deepEqual(_M.determineDeeperObjectPath("x.y[a].z"), ["y", "a", "z"]);
    });

    QUnit.test("complex array chain with inner object depth", function(assert) {
        assert.deepEqual(_M.determineDeeperObjectPath("x.y[a.b].z"), ["y", "a", "b", "z"]);
    });

    QUnit.test("complex array chain - quotes", function(assert) {
        assert.deepEqual(_M.determineDeeperObjectPath("x.y['a'].z"), ["y", "a", "z"]);
    });
});

QUnit.module("_M.resolveVariableLookup", function() {
    QUnit.test("no path, no lookup", function(assert) {
        assert.equal(_M.resolveVariableLookup("x", null), "x");
    });

    QUnit.test("simple lookup", function(assert) {
        _M.variables = { "a" : 123 };
        assert.equal(_M.resolveVariableLookup(null, "a"), 123);
    });

    QUnit.test("chained path lookup as object notation", function(assert) {
        _M.variables = { "a" : { "b" : 567 } };
        assert.equal(_M.resolveVariableLookup(null, "a.b"), 567);
    });

    QUnit.test("chained path lookup as array notation", function(assert) {
        _M.variables = { "a" : { "b" : 7890 } };
        assert.equal(_M.resolveVariableLookup(null, "a[b]"), 7890);
    });

    QUnit.test("chained path lookup as array notation with quoted object", function(assert) {
        _M.variables = { "b" : { "a" : { "test" : 4239 } } };
        assert.equal(_M.resolveVariableLookup(null, "b.a['test']"), 4239);
    });
});

QUnit.module("_M.lookupVariable", function() {

    QUnit.test("can deal with no element", function(assert) {
        assert.equal(_M.lookupVariable("x", null), "'x'");
    });

    QUnit.test("simple lookup", function(assert) {
        _M.variables = { "letters" : ['A', 'B', 'C'] };
        _M.conditionals = { "t-123" : "letters" };
        let parent = document.createElement("div");
        parent.setAttribute("template-id", "t-123");
        parent.setAttribute("m-each", "x");
        parent.setAttribute("index", "1");

        parent.appendChild(document.createElement("div"));
        let element = parent.childNodes.item(0);

        assert.equal(_M.lookupVariable("x", element), "'B'");
    });

    QUnit.test("complex lookup", function(assert) {
        _M.variables = { "letters" : ['A', { "y" : 123 }, 'C'] };
        _M.conditionals = { "t-123" : "letters" };
        let parent = document.createElement("div");
        parent.setAttribute("template-id", "t-123");
        parent.setAttribute("m-each", "x");
        parent.setAttribute("index", "1");

        parent.appendChild(document.createElement("div"));
        let element = parent.childNodes.item(0);

        assert.equal(_M.lookupVariable("x.y", element), "123");
    });

    QUnit.test("complex lookup as array", function(assert) {
        _M.variables = { "letters" : ['A', { "y" : 5345 }, 'C'] };
        _M.conditionals = { "t-123" : "letters" };
        let parent = document.createElement("div");
        parent.setAttribute("template-id", "t-123");
        parent.setAttribute("m-each", "x");
        parent.setAttribute("index", "1");

        parent.appendChild(document.createElement("div"));
        let element = parent.childNodes.item(0);

        assert.equal(_M.lookupVariable("x[y]", element), "5345");
    });

});

QUnit.module("_M.considerVariableWrap", function() {
    QUnit.test("string should wrap", function(assert) {
        assert.equal(_M.considerVariableWrap ("hello world"), "'hello world'");
    });

    QUnit.test("number should not wrap", function(assert) {
        assert.equal(_M.considerVariableWrap (-1234), "-1234");
    });

    QUnit.test("already wrapped should not wrap", function(assert) {
        assert.equal(_M.considerVariableWrap ("'something'"), "'something'");
    });

    QUnit.test("simple wrap - boolean", function(assert) {
        assert.equal(_M.considerVariableWrap(true), true);
    });

    QUnit.test("simple wrap - integer", function(assert) {
        assert.equal(_M.considerVariableWrap(2), 2);
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

QUnit.module("_M.injectVariablesIntoConditionalExpression", function() {
    QUnit.test("simple boolean does not need injection", function(assert) {
        assert.equal(_M.injectVariablesIntoConditionalExpression(true, null), true);
    });

    QUnit.test("empty does not need injection", function(assert) {
        assert.equal(_M.injectVariablesIntoConditionalExpression("", null), "");
    });

    QUnit.test("simple integer does not need injection", function(assert) {
        assert.equal(_M.injectVariablesIntoConditionalExpression("234", null), 234);
    });

    QUnit.test("no elem injection - number", function(assert) {
        _M.variables = { "items-bought" : 2 };
        assert.equal(_M.injectVariablesIntoConditionalExpression("!( items-bought === 0 ) && (items-bought > 3)", null), "!( 2 === 0 ) && (2 > 3)");
    });

    QUnit.test("no elem injection - string", function(assert) {
        _M.variables = { "items-bought" : "n" };
        assert.equal(_M.injectVariablesIntoConditionalExpression("( items-bought === 'y' ) || (items-bought === 'yes')", null), "( 'n' === 'y' ) || ('n' === 'yes')");
    });

    QUnit.test("no elem injection - object", function(assert) {
        _M.variables = { "items-bought" : {"x":"y"} };
        assert.equal(_M.injectVariablesIntoConditionalExpression("items-bought === {}", null), '{"x":"y"} === {}');
    });

    QUnit.test("complex map injection", function(assert) {
        _M.variables = { "letters" : ['A', 'name', 'C'] };
        _M.conditionals = { "t-123" : "letters" };
        let parent = document.createElement("div");
        parent.setAttribute("template-id", "t-123");
        parent.setAttribute("m-each", "letters");
        parent.setAttribute("index", "1");

        parent.appendChild(document.createElement("div"));
        let element = parent.childNodes.item(0);

        //this condition means: as part of a map,
        // we have an each object of {"id":"3","name":"PETER"}
        // we have an index (based on parent, in this case 1)
        // we have an eachName of 'letters';

        //so we look up the eachName in viarables, which is { "letters" : ['A', 'name', 'C'] }
        //we apply the index, which gives us 'name'

        //this is then applied to the eachObject, which results in 'PETER'

        assert.equal(_M.injectVariablesIntoConditionalExpression("{\"id\":\"3\",\"name\":\"PETER\"}[$index#letters].value === \"PETER\"", element), "\"PETER\" === \"PETER\"");
    });

    QUnit.test("contrived example to test [object] parsing", function(assert) {
        _M.variables = { "letters" : ['A', 'B', 'C'] };
        _M.conditionals = { "t-123" : "letters" };
        let parent = document.createElement("div");
        parent.setAttribute("template-id", "t-123");
        parent.setAttribute("m-each", "letters");
        parent.setAttribute("index", "1");

        parent.appendChild(document.createElement("div"));
        let element = parent.childNodes.item(0);

        //TODO because this is an array of objects, the result is still an object from the eval, thus causes a [object] to string

        assert.notEqual(_M.injectVariablesIntoConditionalExpression("[{\"id\":\"3\",\"name\":\"PETER\"},{\"id\":\"4\",\"name\":\"JEANETTE\"}][$index#letters].value === true", element), '[object Object] === true');
    });
});

QUnit.module("_M.parseObjectFromConditionalExpression", function() {
    QUnit.test("simple parse", function(assert) {
        assert.deepEqual(_M.parseObjectFromConditionalExpression("[\"C\",\"D\"][$index#string] === \\'Z\\'"), '["C","D"]');
    });

    QUnit.test("complexer parse", function(assert) {
        assert.deepEqual(_M.parseObjectFromConditionalExpression(
            "[{\"id\":\"3\",\"name\":\"PETER\"},{\"id\":\"4\",\"name\":\"JEANETTE\"}][$index#person].id === \\'Z\\'"),
            '[{"id":"3","name":"PETER"},{"id":"4","name":"JEANETTE"}]');
    });

    QUnit.test("has nothing to parse", function(assert) {
        assert.deepEqual(_M.parseObjectFromConditionalExpression("0123"), "");
    });

    QUnit.test("can deal with undefined", function(assert) {
        assert.deepEqual(_M.parseObjectFromConditionalExpression(undefined), "{}");
    });

    QUnit.test("can deal with null", function(assert) {
        assert.deepEqual(_M.parseObjectFromConditionalExpression(null), "{}");
    });
});

QUnit.module("_M.parseArrayFromConditionalExpression", function() {
    QUnit.test("simple parse", function(assert) {
        assert.deepEqual(_M.parseArrayFromConditionalExpression("[0,1,2][$index#string] === Z"), "[0,1,2]");
        assert.deepEqual(_M.parseArrayFromConditionalExpression("[{[0,1,2]}][$index#string] === Z"), "[{[0,1,2]}]");
    });

    QUnit.test("has nothing to parse", function(assert) {
        assert.deepEqual(_M.parseArrayFromConditionalExpression("0123"), "");
    });

    QUnit.test("can deal with undefined", function(assert) {
        assert.deepEqual(_M.parseArrayFromConditionalExpression(undefined), "[]");
    });

    QUnit.test("can deal with null", function(assert) {
        assert.deepEqual(_M.parseArrayFromConditionalExpression(null), "[]");
    });
});