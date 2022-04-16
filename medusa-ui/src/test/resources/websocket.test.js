QUnit.module('_M.isBoolean', function() {
    QUnit.test('"x" is not a boolean', function(assert) {
        assert.equal(_M.isBoolean("x"), false);
    });

    QUnit.test('"true" is a boolean', function(assert) {
        assert.equal(_M.isBoolean("true"), true);
    });

    QUnit.test('"FALSE" is a boolean', function(assert) {
        assert.equal(_M.isBoolean("FALSE"), true);
    });
});