package hello;

public class WebhookResponse {
    private final String displayText;

    private final String source = "java-webhook";

    public WebhookResponse( String displayText) {
        this.displayText = displayText;
    }

   
    public String getDisplayText() {
        return displayText;
    }

    public String getSource() {
        return source;
    }
}
