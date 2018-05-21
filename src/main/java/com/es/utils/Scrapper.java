package com.es.utils;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Function;
import java.util.logging.Level;


@Component
public class Scrapper {

    private Stack<String> valueStack = new Stack<>();

    private HashMap<String,String> valueMap = new HashMap<>();

    private WebClient client;

    private HtmlPage htmlPage;

    private long javascriptWaitTimeDefault = 3000;

    public Scrapper() {
        this(BrowserVersion.CHROME);
    }

    public Scrapper(BrowserVersion version) {
        this.client = new WebClient(version);
    }

    public Scrapper setOptions(boolean enableCss, boolean enableJs, boolean throwJsExceptions,
                               boolean useInsecureSsl, boolean enableCookies, boolean enableAjax) {
        client.getOptions().setCssEnabled(enableCss);
        client.getOptions().setJavaScriptEnabled(enableJs);
        client.getOptions().setThrowExceptionOnScriptError(throwJsExceptions);
        client.getOptions().setUseInsecureSSL(useInsecureSsl);
        client.getCookieManager().setCookiesEnabled(enableCookies);
        if (enableAjax)
            client.setAjaxController(new NicelyResynchronizingAjaxController());

        java.util.logging.Logger logger = java.util.logging.Logger.getLogger("com.gargoylesoftware");
        logger.setLevel(Level.OFF);
        return this;


    }

    public Scrapper setOptions()  {
        return setOptions(true,true,false,
                true,true,true);
    }


    public Scrapper setOptionJavaScriptWaitTimeDefault(long ms) {
        this.javascriptWaitTimeDefault = ms;
        return this;
    }

    public Scrapper visit(String url) throws IOException {
        return this.visit(url,this.javascriptWaitTimeDefault);
    }

    public Scrapper visit(String url, long waitToLoad) throws IOException {
        this.htmlPage = client.getPage(url);
        this.client.waitForBackgroundJavaScript(waitToLoad);
        return this;
    }

    public Scrapper inputTextBox(String elementId, String text) {
        HtmlTextInput input = (HtmlTextInput)this.htmlPage.getElementById(elementId);
        input.setValueAttribute(text);
        return this;
    }

    public Scrapper inputTextBoxX(String xpath, String text) {
        HtmlTextInput input = this.htmlPage.getFirstByXPath(xpath);
        input.setValueAttribute(text);
        return this;
    }


    /**
     * input a value into the text box identified by the XPath; the value
     * however is taken after evaluating a function that takes the current HtmlPage as
     * it's input
     *
     * @param xpath The XPath to the input text field
     * @param textFunction the function that returns the value to fill in
     * @return
     */
    public Scrapper inputTextBoxX(String xpath,Function<HtmlPage,String> textFunction) {
        return this.inputTextBoxX(xpath, textFunction.apply(this.htmlPage));
    }

    public Scrapper inputTextBox(String elementId,Function<HtmlPage,String> textFunction) {
        return this.inputTextBox(elementId, textFunction.apply(this.htmlPage));
    }

    public Scrapper passwordTextBox(String elementId, String text) {
        HtmlPasswordInput input = (HtmlPasswordInput)this.htmlPage.getElementById(elementId);
        input.setValueAttribute(text);
        return this;
    }

    public Scrapper passwordTextBoxX(String xpath, String text) {
        HtmlPasswordInput input = this.htmlPage.getFirstByXPath(xpath);
        input.setValueAttribute(text);
        return this;
    }

    public Scrapper passwordTextBoxX(String xpath,Function<HtmlPage,String> textFunction) {
        return this.passwordTextBoxX(xpath, textFunction.apply(this.htmlPage));
    }

    public Scrapper passwordTextBox(String elementId,Function<HtmlPage,String> textFunction) {
        return this.passwordTextBox(elementId, textFunction.apply(this.htmlPage));
    }


    public Scrapper clickSubmit(String elementId, long waitToLoad) throws IOException {
        //HtmlSubmitInput
        //HtmlButton button = (HtmlButton)
        DomElement submit = this.htmlPage.getElementById(elementId);
        //client.waitForBackgroundJavaScript(waitToLoad);
        this.htmlPage = (HtmlPage)submit.click();
        client.waitForBackgroundJavaScriptStartingBefore(waitToLoad);
        return this;
    }

    public Scrapper clickSubmitX(String xpath, long waitToLoad) throws IOException {
        //HtmlButton button =
        DomElement submit = this.htmlPage.getFirstByXPath(xpath);
        client.waitForBackgroundJavaScript(waitToLoad);
        this.htmlPage = (HtmlPage)submit.click();
        return this;
    }


    public Scrapper clickLink(String elementId, long waitToLoad) throws IOException {
        HtmlAnchor anchor = (HtmlAnchor)this.htmlPage.getElementById(elementId);
        client.waitForBackgroundJavaScript(waitToLoad);
        this.htmlPage = anchor.click();
        return this;
    }

    public Scrapper clickLinkX(String xpath, long waitToLoad) throws IOException {
        HtmlAnchor anchor = this.htmlPage.getFirstByXPath(xpath);
        client.waitForBackgroundJavaScript(waitToLoad);
        this.htmlPage = anchor.click();
        return this;
    }


    public Scrapper visitAnchor(String elementId, long waitToLoad) throws IOException {
        String url = this.htmlPage.getBaseURL() +
                ((HtmlAnchor)this.htmlPage.getElementById(elementId)).getHrefAttribute();
        return this.visit(url,waitToLoad);
    }

    public Scrapper visitAnchorX(String xpath, long waitToLoad) throws IOException {
        String url =
                this.htmlPage.getBaseURL().toString().substring(0,
                        this.htmlPage.getBaseURL().toString().indexOf(this.htmlPage.getBaseURL().getFile())) +
                        ((HtmlAnchor)this.htmlPage.getFirstByXPath(xpath)).getHrefAttribute();
        System.out.println("Visiting Anchor at: " + url);
        return this.visit(url,waitToLoad);
    }


    public Scrapper validateElement(String elementId) throws Exception {
        if (this.htmlPage.getElementById(elementId)==null)
            throw new Exception("Validation failed: Couldn't find element by id: " + elementId);
        return this;
    }

    public Scrapper validateElementX(String xpath) throws Exception {
        if (this.htmlPage.getFirstByXPath(xpath)==null)
            throw new Exception("Validation failed: Couldn't find element for path: " + xpath);
        return this;
    }



    private String getTextContentX(String xpath) {
        DomElement ele = ((DomElement)this.htmlPage.getFirstByXPath(xpath));
        String value  = ele.getTextContent();
        return value;
    }

    private String getTextContent(String elementId) {
        return this.htmlPage.getElementById(elementId).getTextContent();
    }

    public Scrapper pushValueToStackX(String xpath) {
        this.valueStack.push(this.getTextContentX(xpath));
        return this;
    }

    public Scrapper pushValueToStack(String elementId) {
        this.valueStack.push(this.getTextContent(elementId));
        return this;
    }

    public Scrapper putValueInMapX(String xpath, String key) {
        this.valueMap.put(key, this.getTextContentX(xpath));
        return this;
    }

    public Scrapper putValueInMap(String elementId, String key) {
        this.valueMap.put(key, this.getTextContent(elementId));
        return this;
    }

    public Optional<String> popValueFromStack() {
        if (!this.valueStack.empty())
            return Optional.of(this.valueStack.pop());
        else
            return Optional.ofNullable(null);
    }

    public Optional<String> getValueFromMap(String key) {
        if (this.valueMap.containsKey(key)) {
            return  Optional.of(this.valueMap.get(key));
        } else
            return Optional.ofNullable(null);
    }

    public Stack<String> getValueStack() {
        return this.valueStack;
    }

    public HashMap<String,String> getValueMap() {
        return this.valueMap;
    }

    public Scrapper printCurrentPage() {
        int a =1;
        System.out.println(this.htmlPage.asText());
        return this;
    }


    public HtmlPage getCurrentPage() {
        return this.htmlPage;
    }



}
