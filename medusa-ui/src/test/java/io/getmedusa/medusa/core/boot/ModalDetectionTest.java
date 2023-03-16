package io.getmedusa.medusa.core.boot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ModalDetectionTest {

    private final String htmlWithTags = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="http://www.getmedusa.io/">
            <head>
                <meta charset="UTF-8">
                <title>This page embeds a fragment</title>
            </head>
            <body>
                        
            <h1>Hello, this is a core page</h1>
                        
            <m:modal id="modal-abc" title="xyz">
                <p>Model A content</p>
            </m:modal>
                        
            <p>Mid content</p>
            
            <m:modal id="modal-b">
                <p>Model B content</p>
            </m:modal>
            
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

    @Test
    void testHandleModals() {
        String html = ModalDetection.INSTANCE.prepFile(htmlWithoutTags);
        Assertions.assertEquals(htmlWithoutTags, html);

        html = ModalDetection.INSTANCE.prepFile(htmlWithTags);
        System.out.println(html);
        Assertions.assertTrue(htmlWithTags.contains(":modal"));
        Assertions.assertFalse(html.contains(":modal"), "Modal was not replaced");
        Assertions.assertTrue(html.contains("id=\"modal-abc\""), "Id was not applied");
        Assertions.assertTrue(html.contains(">xyz<"), "Title was not present");
        Assertions.assertTrue(html.contains("<p>Model A content</p>"), "Content of modal A was not present");
        Assertions.assertTrue(html.contains("<p>Model B content</p>"), "Content of modal B was not present");

    }
}
