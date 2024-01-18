package br.com.finance.manager.api.constants;

public class LogMessagesConstants {

    private LogMessagesConstants() {
        throw new IllegalStateException("Cannot instantiate LogMessagesConstants class");
    }

    public static final String INPUT_ENDPOINT = "%s endpoint input = %s";
    public static final String OUTPUT_ENDPOINT = "%s endpoint output = %s";
    public static final String OUTPUT_ENDPOINT_NO_CONTENT = "%s endpoint output = [no content]";
}
