package io.getmedusa.medusa.core.util;

import io.getmedusa.medusa.core.boot.StaticResourcesDetection;

import java.util.Objects;

public final class LoaderStatics {

    private LoaderStatics() {}

    public static String getTopLoader() {
        return TOP_LOADER;
    }

    public static String getFullLoader() {
        return Objects.requireNonNullElse(StaticResourcesDetection.LOADER_GLOBAL, DEFAULT_FULL_LOADER);
    }

    public static String getPerButtonLoader() {
        return Objects.requireNonNullElse(StaticResourcesDetection.LOADER_BUTTON, DEFAULT_BUTTON_LOADER);
    }

    private static final String TOP_LOADER = """
            <div id="m-top-load-bar" class="progress-line" style="display:none;"></div>
            <style>
            div#m-top-load-bar {
                position: fixed;
                top: 0;
                left: 0;
                right: 0;
                width: 100%;
            }
            .progress-line, .progress-line:before {
                height: 3px;
                width: 100%;
                margin: 0;
            }
            .progress-line {
                background-color: #7a00ff;
                display: -webkit-flex;
                display: flex;
            }
            .progress-line:before {
                background-color: #f4abba;
                content: '';
                -webkit-animation: running-progress 2s cubic-bezier(0.4, 0, 0.2, 1) infinite;
                animation: running-progress 2s cubic-bezier(0.4, 0, 0.2, 1) infinite;
            }
            @-webkit-keyframes running-progress {
                0% { margin-left: 0px; margin-right: 100%; }
                50% { margin-left: 25%; margin-right: 0%; }
                100% { margin-left: 100%; margin-right: 0; }
            }
            @keyframes running-progress {
                0% { margin-left: 0px; margin-right: 100%; }
                50% { margin-left: 25%; margin-right: 0%; }
                100% { margin-left: 100%; margin-right: 0; }
            }
            </style>
            """;

    private static final String DEFAULT_FULL_LOADER = """
            <div id="m-full-loader" style="display:none;">Loading ...</div>
            <style>
            div#m-full-loader {
                background: #0000006e;
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                text-align: center;
                padding-top: 15%;
            }
            </style>
            """;

    private static final String DEFAULT_BUTTON_LOADER = """
            <template id="m-template-button-load">
                <span class="m-button-loader"><span></span><span></span><span></span><span></span></span>
            </template>
            <style>
                    .m-button-loader {
                        display: inline-block;
                        position: relative;
                        width: 1em;
                        height: 1em;
                        -webkit-font-smoothing: antialiased;
                        -moz-osx-font-smoothing: grayscale;
                        opacity: 0.6;
                        margin-right: 0.25em;
                    }
                    .m-button-loader span {
                        box-sizing: border-box;
                        display: block;
                        position: absolute;
                        width: 1em;
                        height: 1em;
                        border: 0.15em solid;
                        border-radius: 50%;
                        animation: loading-spin 1.2s cubic-bezier(0.5, 0, 0.5, 1) infinite;
                        border-color: gray transparent transparent transparent;
                    }
                    .m-button-loader span:nth-child(1) {
                        animation-delay: -0.45s;
                    }
                    .m-button-loader span:nth-child(2) {
                        animation-delay: -0.3s;
                    }
                    .m-button-loader span:nth-child(3) {
                        animation-delay: -0.15s;
                    }
                    @keyframes loading-spin {
                        0% {
                            transform: rotate(0deg);
                        }
                        100% {
                            transform: rotate(360deg);
                        }
                    }
                </style>
            """;

}
