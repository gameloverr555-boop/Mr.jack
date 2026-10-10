package com.example.prank

object WebEditorScript {

    fun getEnableEditingScript(): String {
        return """
            (function() {
                document.designMode = 'on';
                document.body.contentEditable = 'true';
                
                var style = document.getElementById('ghostshield-editor-style');
                if (!style) {
                    style = document.createElement('style');
                    style.id = 'ghostshield-editor-style';
                    style.textContent = `
                        *:hover {
                            outline: 2px dashed #00E5FF !important;
                            cursor: text !important;
                        }
                        *:focus {
                            outline: 3px solid #9D4EDD !important;
                        }
                    `;
                    document.head.appendChild(style);
                }
            })();
        """.trimIndent()
    }

    fun getDisableEditingScript(): String {
        return """
            (function() {
                document.designMode = 'off';
                document.body.contentEditable = 'false';
                var style = document.getElementById('ghostshield-editor-style');
                if (style) {
                    style.remove();
                }
            })();
        """.trimIndent()
    }

    fun getBillionairePrankScript(): String {
        return """
            (function() {
                function walkText(node) {
                    if (node.nodeType === 3) {
                        var text = node.nodeValue;
                        // Replace small amounts or balances with huge numbers
                        var modified = text.replace(/(\$|€|£|¥)?\s?\b\d+(\.\d{1,2})?\b/g, function(match) {
                            if (match.startsWith('$')) return '$1,489,250,000';
                            if (match.startsWith('€')) return '€1,240,500,000';
                            if (match.startsWith('£')) return '£980,400,000';
                            return '8,950,000';
                        });
                        if (modified !== text) {
                            node.nodeValue = modified;
                        }
                    } else if (node.nodeType === 1 && node.nodeName !== 'SCRIPT' && node.nodeName !== 'STYLE') {
                        for (var i = 0; i < node.childNodes.length; i++) {
                            walkText(node.childNodes[i]);
                        }
                    }
                }
                walkText(document.body);
            })();
        """.trimIndent()
    }

    fun getMatrixThemePrankScript(): String {
        return """
            (function() {
                var matrixStyle = document.getElementById('ghostshield-matrix-style');
                if (matrixStyle) {
                    matrixStyle.remove();
                    return;
                }
                matrixStyle = document.createElement('style');
                matrixStyle.id = 'ghostshield-matrix-style';
                matrixStyle.textContent = `
                    html, body, div, p, span, h1, h2, h3, h4, h5, h6, a, table, tr, td {
                        background-color: #050a05 !important;
                        color: #00FF66 !important;
                        font-family: monospace, 'Courier New', Courier !important;
                        text-shadow: 0 0 5px #00FF66 !important;
                        border-color: #00FF66 !important;
                    }
                    img, video {
                        filter: invert(100%) hue-rotate(90deg) contrast(150%) !important;
                    }
                `;
                document.head.appendChild(matrixStyle);
            })();
        """.trimIndent()
    }

    fun getStampPrankScript(stampText: String = "TOP SECRET"): String {
        val safeText = stampText.replace("'", "\\'")
        return """
            (function() {
                var existing = document.getElementById('ghostshield-prank-stamp');
                if (existing) existing.remove();
                
                var stamp = document.createElement('div');
                stamp.id = 'ghostshield-prank-stamp';
                stamp.textContent = '$safeText';
                stamp.style.cssText = `
                    position: fixed;
                    top: 25%;
                    left: 10%;
                    transform: rotate(-18deg);
                    font-size: 42px;
                    font-weight: 900;
                    color: rgba(255, 23, 68, 0.85);
                    border: 8px solid rgba(255, 23, 68, 0.85);
                    border-radius: 12px;
                    padding: 10px 28px;
                    text-transform: uppercase;
                    letter-spacing: 6px;
                    font-family: sans-serif;
                    z-index: 2147483647;
                    pointer-events: none;
                    text-shadow: 2px 2px 0 rgba(0,0,0,0.4);
                    box-shadow: 0 0 20px rgba(255, 23, 68, 0.5);
                `;
                document.body.appendChild(stamp);
            })();
        """.trimIndent()
    }

    fun getReplaceHeadlineScript(customHeadline: String): String {
        val safeHeadline = customHeadline.replace("'", "\\'")
        return """
            (function() {
                var h1 = document.querySelector('h1') || document.querySelector('h2');
                if (h1) {
                    h1.textContent = '$safeHeadline';
                    h1.style.color = '#00E5FF';
                }
            })();
        """.trimIndent()
    }
}
