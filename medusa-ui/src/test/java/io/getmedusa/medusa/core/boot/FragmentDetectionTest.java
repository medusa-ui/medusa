package io.getmedusa.medusa.core.boot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FragmentDetectionTest {

    private final String htmlWithTags = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="http://www.getmedusa.io/">
            <head>
                <meta charset="UTF-8">
                <title>This page embeds a fragment</title>
            </head>
            <body>
                        
            <h1>Hello, this is a core page</h1>
                        
            <m:fragment service="sample-impl" ref="search-bar">
                <p>Fallback for search-bar</p>
            </m:fragment>
                        
            <p>Mid content</p>
            
            <m:fragment service="sample-2" ref="input-field">
                <p>Fallback for input-field</p>
            </m:fragment>
            
            <p>End of content</p>
                        
            </body>
            </html>
            """;

    private final String htmlWithoutTags = """
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

    @BeforeEach
    void cleanSetup() {
        FragmentDetection.INSTANCE.clear();
    }

    @Test
    void testFindingFragments() {
        String html = FragmentDetection.INSTANCE.prepFile(htmlWithoutTags);
        Assertions.assertEquals(0, FragmentDetection.INSTANCE.getFragmentIds().size());
        Assertions.assertEquals(htmlWithoutTags, html);
        Assertions.assertEquals(0, FragmentDetection.INSTANCE.detectWhichFragmentsArePresent(html, null).size());

        html = FragmentDetection.INSTANCE.prepFile(htmlWithTags);
        Assertions.assertEquals(2, FragmentDetection.INSTANCE.getFragmentIds().size());
        Assertions.assertFalse(html.contains(":fragment"));

        for(String id : FragmentDetection.INSTANCE.getFragmentIds()) {
            Assertions.assertTrue(html.contains(id));
        }

        Assertions.assertEquals(2, FragmentDetection.INSTANCE.detectWhichFragmentsArePresent(html, null).size());
    }
}
