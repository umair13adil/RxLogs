package com.blackbox.plog.pLogs.exporter.formatter;

public class Message {

    private final String title;

    private final String content;

    private final Throwable throwable;

    public Message(final String title, final String content, final Throwable throwable) {
        this.title = title;
        this.content = content;
        this.throwable = throwable;
    }

    public static Message forThrowable(Throwable throwable) {
        return new Message(throwable.getLocalizedMessage(), null, throwable);
    }

    public static Message forThrowableWithTitle(Throwable throwable, String title) {
        return new Message(title, throwable.getLocalizedMessage(), throwable);
    }

    public static Message forMessage(String title, String content) {
        return new Message(title, content, null);
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
