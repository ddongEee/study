package com.study.apps.poc.stock.domain.page38coms.util;

import lombok.AllArgsConstructor;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

@AllArgsConstructor(staticName = "create")
public class ElementTextExtractor {
    private final Element element;

    public String extractClearly(int... childNodes) {
        return extractClearlyWithoutComma(childNodes)
                .replaceAll(",", "");
    }

    public String extractClearlyWithoutComma(int... childNodes) {
        Node tempNode = element.childNode(childNodes[0]);
        boolean first = true;
        for (int childNode : childNodes) {
            if (first) {
                first = false;
                continue;
            }
            tempNode = tempNode.childNode(childNode);
        }
        return tempNode.outerHtml()
                .replaceAll(" ", "")
                .replace("&nbsp;", "");
    }
}
