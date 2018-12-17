package com.blackbox.plog.pLogs.exporter.formatter;

@SuppressWarnings({"WeakerAccess", "unused"})
public class HtmlExceptionFormatter {

    private final HtmlAppender throwableHtmlAppender;

    public HtmlExceptionFormatter(final HtmlExceptionFormatOptions options) {
        throwableHtmlAppender = HtmlExceptionMessageFactory.createFormatter(options);
    }

    public void formatMessage(Appendable appendable, Message message) {
        throwableHtmlAppender.accept(appendable, message);
    }

    public void formatMessage(Appendable appendable, String title, String content, Throwable throwable) {
        formatMessage(appendable, new Message(title, content, throwable));
    }

    /**
     * @param appendable A appendable interface to append the output html to
     * @param throwable  the throwable instance
     */
    public void formatMessage(Appendable appendable, Throwable throwable) {
        formatMessage(appendable, "An Exception Occurred", throwable.getLocalizedMessage(), throwable);
    }

    /**
     * @param appendable A appendable interface to append the output html to
     * @param title      The html message title
     * @param content    The html message body
     */
    public void formatMessage(Appendable appendable, String title, String content) {
        formatMessage(appendable, title, content, null);
    }

    /**
     * @param title     The html message title
     * @param content   The html message body
     * @param throwable The throwable instance
     * @return A html string
     */
    public String toString(String title, String content, Throwable throwable) {
        final StringBuilder stringBuilder = new StringBuilder();
        formatMessage(stringBuilder, title, content, throwable);
        return stringBuilder.toString();
    }

    /**
     * @param title   The title to display on page.
     * @param content The message content to display on the html page.
     * @return A html string
     */
    public String toString(String title, String content) {
        final StringBuilder stringBuilder = new StringBuilder();
        formatMessage(stringBuilder, title, content);
        return stringBuilder.toString();
    }

    /**
     * @param throwable The throwable exception to turn into a html string
     * @return A html string
     */
    public String toString(Throwable throwable) {
        final StringBuilder stringBuilder = new StringBuilder();
        formatMessage(stringBuilder, throwable);
        return stringBuilder.toString();
    }
}
