package com.bakholdin.stock_management.service.yahoo;

import com.bakholdin.stock_management.model.PerformanceOutlook;
import com.bakholdin.stock_management.model.StockManagementRowId;
import com.bakholdin.stock_management.model.YahooRow;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.xerces.dom.ElementImpl;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.w3c.dom.Document;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Component
public class YahooHtmlParser {
    private static final String FAIR_VALUE_XPATH = "//div[@id='quote-summary']//div[1]//div[2]//div[2]/text()";
    private static final String ESTIMATED_RETURN_XPATH = "//div[@id='quote-summary']//div[1]//div[3]//div[1]/text()";
    private static final String PERFORMANCE_OUTLOOK_XPATH_FORMAT = "//div[@id='chrt-evts-mod']//div[3]//ul//li[%d]//a//div[1]//div[2]//svg";

    @SneakyThrows
    public YahooRow parseHtml(String symbol, String htmlStr) {
        TagNode tagNode = new HtmlCleaner().clean(htmlStr);
        Document doc = new DomSerializer(new CleanerProperties()).createDOM(tagNode);
        XPath xpath = XPathFactory.newInstance().newXPath();

        return YahooRow.builder()
                .stockManagementRowId(new StockManagementRowId(symbol))
                .shortTerm(getPerformanceOutlook(xpath, doc, 1))
                .midTerm(getPerformanceOutlook(xpath, doc, 2))
                .longTerm(getPerformanceOutlook(xpath, doc, 3))
                .fairValue(getFairValue(xpath, doc))
                .estimatedReturn(getEstimatedReturn(xpath, doc))
                .build();
    }

    private PerformanceOutlook getPerformanceOutlook(XPath xpath, Document doc, int outlookIndex) throws XPathExpressionException {
        if(outlookIndex < 1 || outlookIndex > 3) {
            throw new UnsupportedOperationException("outlookIndex must be between 1 and 3, inclusive");
        }
        String xpathStr = String.format(PERFORMANCE_OUTLOOK_XPATH_FORMAT, outlookIndex);
        ElementImpl svgNode = (ElementImpl) xpath.compile(xpathStr).evaluate(doc, XPathConstants.NODE);
        return getPerformanceOutlookGivenClass(svgNode.getAttribute("class"));
    }

    private PerformanceOutlook getPerformanceOutlookGivenClass(String classAttributeValue) {
        if(classAttributeValue.contains("RotateZ(180deg)")) {
            return PerformanceOutlook.Bearish;
        } else if(classAttributeValue.contains("RotateZ(90deg)")) {
            return PerformanceOutlook.Neutral;
        } else if(classAttributeValue.contains("Cur (p)")) {
            // just making sure something is here that makes sense
            return PerformanceOutlook.Bullish;
        }
        return null;
    }

    private String getFairValue(XPath xpath, Document doc) throws XPathExpressionException {
        return (String) xpath.compile(FAIR_VALUE_XPATH).evaluate(doc, XPathConstants.STRING);
    }

    private Double getEstimatedReturn(XPath xpath, Document doc) throws XPathExpressionException {
        String estimatedReturnFullStr = (String) xpath.compile(ESTIMATED_RETURN_XPATH).evaluate(doc, XPathConstants.STRING);
        Pattern pattern = Pattern.compile("(-?[0-9.]+)%");
        Matcher matcher = pattern.matcher(estimatedReturnFullStr);
        Assert.isTrue(matcher.find(), "Could not find Estimated return");
        return Double.parseDouble(matcher.group(1));
    }
}
