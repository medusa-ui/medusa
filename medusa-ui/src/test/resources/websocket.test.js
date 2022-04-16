QUnit.module('_M.isBoolean', function() {
    QUnit.test('"x" is not a boolean', function(assert) {
        assert.false(_M.isBoolean("x"));
    });

    QUnit.test('undefined is not a boolean', function(assert) {
        assert.false(_M.isBoolean(undefined));
    });

    QUnit.test('null is not a boolean', function(assert) {
        assert.false(_M.isBoolean(null));
    });

    QUnit.test('"true" is a boolean', function(assert) {
        assert.true(_M.isBoolean("true"));
    });

    QUnit.test('"FALSE" is a boolean', function(assert) {
        assert.true(_M.isBoolean("FALSE"));
    });
});

QUnit.module('_M.isQuoted', function() {
    QUnit.test('simple single quoted string', function(assert) {
        assert.true(_M.isQuoted("'hello world'"));
    });

    QUnit.test('simple double quoted string', function(assert) {
        assert.true(_M.isQuoted("\"hello world\""));
    });

    QUnit.test('non-quoted string', function(assert) {
        assert.false(_M.isQuoted("hello world"));
    });

    QUnit.test('non-quoted string with quote internally used once', function(assert) {
        assert.false(_M.isQuoted("it's a wonderful world"));
    });

    QUnit.test('non-quoted string with quote internally used twice', function(assert) {
        assert.false(_M.isQuoted("the word 'dance' is okay"));
    });

    QUnit.test('deals with undefined', function(assert) {
        assert.false(_M.isQuoted(undefined));
    });

    QUnit.test('deals with null', function(assert) {
        assert.false(_M.isQuoted(null));
    });
});

QUnit.module('_M.buildMethod', function() {
    QUnit.test('simple method build - no params', function(assert) {
        assert.equal(_M.buildMethod("helloWorld", []), "helloWorld()");
    });

    QUnit.test('simple method build - no params - null', function(assert) {
        assert.equal(_M.buildMethod("helloWorld", null), "helloWorld()");
    });

    QUnit.test('simple method build - no params - undefined', function(assert) {
        assert.equal(_M.buildMethod("helloWorld", undefined), "helloWorld()");
    });

    QUnit.test('simple method build - one param', function(assert) {
        assert.equal(_M.buildMethod("helloWorld", ["'a'"]), "helloWorld('a')");
    });

    QUnit.test('simple method build - two params', function(assert) {
        assert.equal(_M.buildMethod("helloWorld", ["'a'", "'b'"]), "helloWorld('a', 'b')");
    });

    QUnit.test('simple method build - mix', function(assert) {
        assert.equal(_M.buildMethod("helloWorld", [1, "'b'", 2]), "helloWorld(1, 'b', 2)");
    });

    QUnit.test('simple method build - deals with undefined', function(assert) {
        assert.equal(_M.buildMethod("helloWorld", [1, undefined, "'b'"]), "helloWorld(1, null, 'b')");
    });

    QUnit.test('simple method build - deals with null', function(assert) {
        assert.equal(_M.buildMethod("helloWorld", [1, null, "'b'"]), "helloWorld(1, null, 'b')");
    });
});

QUnit.module('_M.findParentWithEachElement', function() {
    const eachName = "xyz";

    QUnit.test('self', function(assert) {
        let parent = document.createElement("div");
        parent.setAttribute("id", "parent-node");
        parent.setAttribute("m-each", eachName);

        let foundVal = _M.findParentWithEachElement(parent, eachName)
        assert.notEqual(foundVal, null);
        assert.equal(foundVal.getAttribute("id"), "parent-node");
    });

    QUnit.test('simple parent', function(assert) {
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

    QUnit.test('simple parent - 2 deep', function(assert) {
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

    QUnit.test('no match', function(assert) {
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

    QUnit.test('no match - deals with null', function(assert) {
        let element = document.createElement("div");
        let foundVal = _M.findParentWithEachElement(element, null)
        assert.equal(foundVal, null);
    });

    QUnit.test('no match - deals with undefined', function(assert) {
        let element = document.createElement("div");
        let foundVal = _M.findParentWithEachElement(element, undefined)
        assert.equal(foundVal, null);
    });
});
