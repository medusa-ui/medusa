package io.getmedusa.medusa.core.boot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FragmentDetectionTest {

    private final String html_with_tags = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="http://www.getmedusa.io/">
            <head>
                <meta charset="UTF-8">
                <title>This page embeds a fragment</title>
            </head>
            <body>
                        
            <h1>Hello, this is a core page</h1>
                        
            <th:block m:fragment="sample-impl#search-bar">
                <p>Fallback for search-bar</p>
            </th:block>
                        
            <p>Mid content</p>
            
            <th:block m:fragment="sample-2#input-field">
                <p>Fallback for input-field</p>
            </th:block>
            
            <p>End of content</p>
                        
            </body>
            </html>
            """;

    private final String html_without_tags = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="http://www.getmedusa.io/">
            <head>
                <meta charset="UTF-8" />
                <title>Hello world</title>
            </head>
            <body>
                <h1>Hello world</h1>
                        
                <div th:text="${counterValue}"></div>
                <button onclick="_M.doAction(null, 'increaseCounter()')">Increase counter</button>
                        
                <table>
                    <thead>
                        <tr>
                            <td>Random text</td>
                            <td>Conditional button</td>
                        </tr>
                    </thead>
                    <tbody>
                        <tr th:each="person: ${people}">
                            <td th:text="${person.name}"></td>
                            <td>
                                <th:block th:if="${person.number > 5}">
                                    <button onclick="_M.doAction(null, 'randomNewTable()')">Regenerate table</button>
                                </th:block>
                                <th:block th:if="${person.number <= 5}">
                                    <button disabled="disabled">Cannot remove</button>
                                </th:block>
                            </td>
                        </tr>
                    </tbody>
                </table>
                <div>total = <span th:text="${expectedTableCount}"></span></div>
                        
                <button onclick="_M.doAction(null, 'addPerson()')">Add person</button>
                <button onclick="_M.doAction(null, 'removePerson()')">Remove person</button>
                        
                <h2>The end</h2>
            </body>
            </html>
            """;

    @Test
    void testFindingFragments() {
        String html = FragmentDetection.INSTANCE.prepFile(html_without_tags);
        Assertions.assertEquals(0, FragmentDetection.INSTANCE.getFragmentIds().size());
        Assertions.assertEquals(html_without_tags, html);
        Assertions.assertEquals(0, FragmentDetection.INSTANCE.detectWhichFragmentsArePresent(html).size());

        html = FragmentDetection.INSTANCE.prepFile(html_with_tags);
        Assertions.assertEquals(2, FragmentDetection.INSTANCE.getFragmentIds().size());
        Assertions.assertFalse(html.contains(":fragment"));

        for(String id : FragmentDetection.INSTANCE.getFragmentIds()) {
            Assertions.assertTrue(html.contains(id));
        }

        Assertions.assertEquals(2, FragmentDetection.INSTANCE.detectWhichFragmentsArePresent(html).size());
    }
}
