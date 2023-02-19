package io.getmedusa.medusa.core.boot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public enum ModalDetection {

    INSTANCE;

    private static final String MODAL_CSS = """
            <style>
                  .modal-background {
                    display: none;
                    align-items: center;
                    background: rgba(0,0,0,.6);
                    bottom: 0;
                    justify-content: center;
                    left: 0;
                    position: fixed;
                    right: 0;
                    top: 0;
                  }
                        
                  .modal-container {
                    display: none;
                        
                    background-color: #fff;
                    border-radius: 4px;
                    box-sizing: border-box;
                    max-height: 100vh;
                    max-width: 500px;
                    width: 500px;
                    overflow-y: auto;
                    padding: 1.5em 2em;
                  }
                        
                  .is-open {
                    display: flex;
                  }
                        
                  .is-open .modal-container {
                    display: block;
                  }
                        
                  .modal-close {
                    display: inline-block;
                    cursor: pointer;
                    background: transparent;
                    border: 0;
                    font-size: 16px;
                    float: right;
                  }
                  .modal-header {
                    clear: both;
                    margin-bottom: 1em;
                  }
                  .modal-title {
                    margin: 0;
                  }
                </style>
            """;

    public String prepFile(String html) {
        if(html.contains(":modal ")) {
            final Document document = Jsoup.parse(html);
            document.outputSettings().indentAmount(0).prettyPrint(false);
            final String prefix = FragmentDetection.findPrefix(document); //reuse, maybe move to a better location?
            if (prefix == null) {
                return html;
            }

            final String modalAttribute = prefix + ":modal";
            final Elements modalElements = document.getElementsByTag(modalAttribute);
            if(modalElements.isEmpty()) {
                return html;
            }

            for(Element modalElement : modalElements) {
                final String id = modalElement.attributes().get("id");
                String title = modalElement.attributes().get("title");
                modalElement.replaceWith(modalBuild(id, title, modalElement.html()));
            }

            document.head().append(MODAL_CSS);
            return document.outerHtml();
        }

        return html;
    }

    private Node modalBuild(String id, String title, String content) {
        String html = """
                    <div class="modal-background" id="__my-modal-id">
                        <div class="modal-container">
                            <header class="modal-header">
                                <button class="modal-close" aria-label="Close modal" onclick="_M.closeModal()">&#10005;</button>
                                <h3 class="modal-title">__my-modal-title</h3>
                            </header>
                            <div class="modal-content">__my-modal-content</div>
                        </div>
                    </div>
                    """;
        return Jsoup.parse(html
                .replace("__my-modal-id", id.trim())
                .replace("__my-modal-title", title.trim())
                .replace("__my-modal-content", content.trim())
        ).select("div.modal-background").first();
    }
}
