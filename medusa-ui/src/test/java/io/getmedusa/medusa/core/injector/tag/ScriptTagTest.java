package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.cache.HTMLCache;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ScriptTagTest {

    String SCRIPT_TAG =
            """
            <script>
                // redirect script once loaded - should eventually be a Medusa feature
                var _M = _M || {};
                _M.postRender = function (incomingMessage) {
                    if(incomingMessage !== null && typeof incomingMessage !== "undefined" && incomingMessage.length === 1) {
                        if("redirect" === incomingMessage[0]['f']) {
                            window.location.href = incomingMessage[0]['v'];
                        }
                    }
                };
            </script>
            """;

    String MULTI_SCRIPT_TAGS =
            """
            <script>
                // redirect script once loaded - should eventually be a Medusa feature
                var _M = _M || {};
                _M.postRender = function (incomingMessage) {
                    if(incomingMessage !== null && typeof incomingMessage !== "undefined" && incomingMessage.length === 1) {
                        if("redirect" === incomingMessage[0]['f']) {
                            window.location.href = incomingMessage[0]['v'];
                        }
                    }
                };
            </script>
              <h3>Hello World</h3>
              <script>
                // redirect script once loaded - should eventually be a Medusa feature
                var _M = _M || {};
                _M.postRender = function (incomingMessage) {
                    if(incomingMessage !== null && typeof incomingMessage !== "undefined" && incomingMessage.length === 1) {
                        if("redirect" === incomingMessage[0]['f']) {
                            window.location.href = incomingMessage[0]['v'];
                        }
                    }
                };
               </script>
            """;

    @Test
    void singleScriptTagEncodingTest() {
        String expect =
                """
                <script>/* <!--CDATA[[ */
                    // redirect script once loaded - should eventually be a Medusa feature
                    var _M = _M || {};
                    _M.postRender = function (incomingMessage) {
                        if(incomingMessage !== null && typeof incomingMessage !== "undefined" && incomingMessage.length === 1) {
                            if("redirect" === incomingMessage[0]['f']) {
                                window.location.href = incomingMessage[0]['v'];
                            }
                        }
                    };
                /* ]]--> */</script>
                """;

        Document document = HTMLCache.getInstance().getHTMLOrAdd("singleScriptTagEncodingTest", SCRIPT_TAG);
        Assertions.assertEquals(expect, document.toString());
    }

    @Test
    void multipleScriptTagEncodingTest() {
        String expect =
                """
                <script>/* <!--CDATA[[ */
                    // redirect script once loaded - should eventually be a Medusa feature
                    var _M = _M || {};
                    _M.postRender = function (incomingMessage) {
                        if(incomingMessage !== null && typeof incomingMessage !== "undefined" && incomingMessage.length === 1) {
                            if("redirect" === incomingMessage[0]['f']) {
                                window.location.href = incomingMessage[0]['v'];
                            }
                        }
                    };
                /* ]]--> */</script>
                  <h3>Hello World</h3>
                  <script>/* <!--CDATA[[ */
                    // redirect script once loaded - should eventually be a Medusa feature
                    var _M = _M || {};
                    _M.postRender = function (incomingMessage) {
                        if(incomingMessage !== null && typeof incomingMessage !== "undefined" && incomingMessage.length === 1) {
                            if("redirect" === incomingMessage[0]['f']) {
                                window.location.href = incomingMessage[0]['v'];
                            }
                        }
                    };
                /* ]]--> */   </script>
                """;

        Document document = HTMLCache.getInstance().getHTMLOrAdd("multipleScriptTagEncodingTest", MULTI_SCRIPT_TAGS);
        Assertions.assertEquals(expect, document.toString());
    }

}
