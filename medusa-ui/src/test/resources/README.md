#Unit test how to
websocket.js gets unit tested via Qunit, a minimal (somewhat old) in-browser tool that allows for easy unit tests

To run these tests, either run:
- io.getmedusa.medusa.core.websocket.WebsocketJSTest (which is also what the CI runs, but is a little slower since it needs to boot the headless browser)
- do a mvn clean install -DskipTests, then open the test.html in \target\test-classes

#Code coverage how to
To run code coverage, we are unfortunately more tied to node. I've tried blanketjs, but that's too deprecated at this point and has issue with CORS.
We use 'Karma' for this. With adequate setup, you can simply runcd 

./node_modules/.bin/karma start
from /test/resources

And you will generate a code coverage report in a /coverage/ folder

I will also commit this report upon each major run

#Code coverage initial setup
Go to /test/resources
npm init
npm i karma karma-qunit karma-chrome-launcher qunit -D
./node_modules/.bin/karma init karma.conf.js 