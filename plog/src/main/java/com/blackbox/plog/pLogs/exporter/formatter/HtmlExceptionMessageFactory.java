package com.blackbox.plog.pLogs.exporter.formatter;


import android.text.TextUtils;

import com.blackbox.plog.utils.DateTimeUtils;
import com.blackbox.plog.utils.Utils;

import org.slieb.throwables.ConsumerWithThrowable;
import org.slieb.throwables.FunctionWithThrowable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static android.text.Html.escapeHtml;

class HtmlExceptionMessageFactory {

    private static final HtmlAppender ESCAPED_TITLE_APPENDER = createEscapedAppender(Message::getTitle);

    private static final HtmlAppender BR_APPENDER = createStaticAppender("<br />");

    private static final HtmlAppender HR_APPENDER = createStaticAppender("<hr />");

    private static final HtmlAppender NULL_APPENDER = (v1, v2) -> {
    };

    static HtmlAppender createFormatter(final HtmlExceptionFormatOptions options) {
        final HtmlAppender htmlAppender = createTagAppender("html", null, createHeadAppender(options).andThenAppend(createBodyAppender(options)));
        return createStaticAppender("<!DOCTYPE html>").andThenAppend(htmlAppender);
    }

    private static HtmlAppender createHeadAppender(final HtmlExceptionFormatOptions options) {
        boolean printDetails = options.printDetails();
        final HtmlAppender titleAppender = createTagAppender("title", null, createEscapedAppender(Message::getTitle));
        final HtmlAppender themeAppender = createThemeAppender(options);
        return createTagAppender("head", null, printDetails ? titleAppender.andThenAppend(themeAppender) : themeAppender);
    }

    private static HtmlAppender createThemeAppender(HtmlExceptionFormatOptions options) {
        String finalText = Utils.INSTANCE.readAssetsXML("styles/" + options.getTheme().getName() + ".css", options.getContext());
        return (appendable, type) -> {
            appendTag(appendable, type, "style", null, (a, t) -> {
                a.append(finalText);
            });
        };
    }

    private static HtmlAppender createBodyAppender(final HtmlExceptionFormatOptions options) {
        return createTagAppender("body", null, createHeadingAppender(options)
                .andThenAppend(HR_APPENDER)
                .andThenAppend(bodyContentAppender(options))
                .andThenAppend(HR_APPENDER)
                .andThenAppend(createFooterAppender(options)));
    }

    private static HtmlAppender bodyContentAppender(final HtmlExceptionFormatOptions options) {

        HtmlAppender messageAppender =
                createTagAppender("div", new String[]{"message"}, createEscapedAppender(Message::getContent));

        boolean printCauses = options.printDetails() && options.printCauses();
        HtmlAppender causesAppender = createCausesAppender(options);

        boolean printStack = options.printDetails() && options.printStacktrace();
        HtmlAppender stacktraceAppender = createStacktraceAppender();

        boolean hasTabs = options.getTheme() == Theme.GRAY;

        return (appender, message) -> {
            messageAppender.acceptWithThrowable(appender, message);

            boolean hasThrowable = message.getThrowable() != null;

            if (hasThrowable && (printCauses || printStack)) {
                appender.append("<div class=container>");

                if (hasTabs && printCauses) {
                    appender.append("<input id=\"tab-1\" type=radio name=\"tab-group\" checked=checked />");
                    appender.append("<label for=\"tab-1\">Causes</label>");
                }

                if (hasTabs && printStack) {
                    appender.append("<input id=\"tab-2\" type=radio name=\"tab-group\"");
                    if (!printCauses) {
                        appender.append(" checked=checked");
                    }
                    appender.append("/>");
                    appender.append("<label for=\"tab-2\">Stacktrace</label>");
                }

                appender.append("<div class=content>");

                if (printCauses) {

                    appender.append("<div class=content-1>");

                    if (!hasTabs) {
                        appender.append("<h2>Causes</h2>");
                    }

                    causesAppender.acceptWithThrowable(appender, message);
                    appender.append("</div>");
                }

                if (printStack) {
                    appender.append("<div class=content-2>");
                    if (!hasTabs) {
                        appender.append("<h2>Stacktrace</h2>");
                    }
                    stacktraceAppender.acceptWithThrowable(appender, message);
                    appender.append("</div>");
                }

                appender.append("</div>");
            }
        };
    }

    private static HtmlAppender createCausesAppender(final HtmlExceptionFormatOptions options) {
        final boolean printStacktrace = options.printStacktrace();
        final String throwableTagName = printStacktrace ? "details" : "div";
        final String messageTagName = printStacktrace ? "summary" : "span";
        return createTagAppender("div", new String[]{"causes"}, (appendable, message) -> {
            Throwable current = message.getThrowable();
            if (current != null) {
                while (current != null) {
                    appendCauseEntry(printStacktrace, throwableTagName, messageTagName, appendable, message, current);
                    current = current.getCause();
                }
            }
        });
    }

    private static void appendCauseEntry(final boolean printStacktrace,
                                         final String throwableTagName,
                                         final String messageTagName,
                                         final Appendable appendable,
                                         final Message message,
                                         final Throwable current) throws IOException {
        final ConsumerWithThrowable<Appendable, IOException> consumer = throwableAppender(current);
        final ConsumerWithThrowable<Appendable, IOException> consumerStack = throwableStackAppender(current);
        appendTag(appendable, message, throwableTagName, new String[]{"throwable"}, (a, b) -> {
            appendTag(a, b, messageTagName, new String[]{"title"}, (a1, b2) -> consumer.acceptWithThrowable(a1));
            BR_APPENDER.acceptWithThrowable(a, b);
            if (printStacktrace) {
                consumerStack.acceptWithThrowable(a);
            }
        });
        BR_APPENDER.acceptWithThrowable(appendable, message);
    }

    private static HtmlAppender createStacktraceAppender() {
        return (appendable, message) -> {
            final Throwable throwable = message.getThrowable();
            if (throwable != null) {
                final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                final PrintStream printStream = new PrintStream(byteArrayOutputStream);
                throwable.printStackTrace(printStream);
                appendable.append("<div class=\"stacktrace\">");
                appendable.append("<pre>")
                        .append(escapeHtml(byteArrayOutputStream.toString()))
                        .append("</pre>");
                appendable.append("</div>");
            }
        };
    }

    private static ConsumerWithThrowable<Appendable, IOException> throwableStackAppender(Throwable current) {
        return (appendable) -> {
            for (final StackTraceElement element : current.getStackTrace()) {
                appendable
                        .append("<span class=line>")
                        .append("&nbsp;at ")
                        .append("<span class=classname>")
                        .append(escapeHtml(element.getClassName()))
                        .append("</span>")
                        .append("<span class=filename>(")
                        .append(escapeHtml(element.getFileName()))
                        .append(":")
                        .append(escapeHtml(String.valueOf(element.getLineNumber())))
                        .append(")</span>")
                        .append("</span>")
                        .append("<br/>");
            }
        };
    }

    private static ConsumerWithThrowable<Appendable, IOException> throwableAppender(Throwable current) {
        return (appendable) -> {
            appendable.append(escapeHtml(current.getClass().getName()));
            final String localizedMessage = current.getLocalizedMessage();
            if (localizedMessage != null) {
                appendable.append(": ")
                        .append(escapeHtml(localizedMessage));
            }
        };
    }

    private static HtmlAppender createFooterAppender(final HtmlExceptionFormatOptions options) {
        if (options.printFooter()) {
            boolean promote = options.promoteLibrary();
            return (v1, v2) -> {
                v1.append("<center><small>")
                        .append("Message generated at ")
                        .append(escapeHtml(DateTimeUtils.INSTANCE.getTimeFormatted()));
                if (promote) {
                    v1.append(" by ")
                            .append("<a href=\"http://github.com/StefanLiebenberg/html-exception-formatter\">")
                            .append("Html Exception Formatter.")
                            .append("</a>");
                }
                v1.append("</small></center>");
            };
        } else {
            return NULL_APPENDER;
        }
    }

    private static HtmlAppender createHeadingAppender(HtmlExceptionFormatOptions options) {
        final HtmlAppender withThrowableTitle = ESCAPED_TITLE_APPENDER.andThenAppend((a, m) -> {
            Throwable t = m.getThrowable();
            if (t != null) {
                String localizedMessage = t.getLocalizedMessage();
                if (localizedMessage != null && localizedMessage.length() < 50) {
                    a.append(": ").append(escapeHtml(localizedMessage));
                }
            }
        });
        return createTagAppender("h1", null, options.printDetails() ? withThrowableTitle : ESCAPED_TITLE_APPENDER);
    }

    private static HtmlAppender createEscapedAppender(FunctionWithThrowable<Message, String, IOException> functionWithThrowable) {
        return (v1, v2) -> {
            final String str = functionWithThrowable.applyWithThrowable(v2);
            if (str != null) {
                v1.append(escapeHtml(str));
            }
        };
    }

    private static HtmlAppender createTagAppender(final String tagName, final String[] classNames, final HtmlAppender contentAppender) {
        return (appendable, type) -> appendTag(appendable, type, tagName, classNames, contentAppender);
    }

    private static HtmlAppender createStaticAppender(final String csq) {
        return (v1, v2) -> v1.append(csq);
    }

    private static void appendTag(final Appendable appendable, final Message type, final String tagName, final String[] classNames,
                                  final HtmlAppender contentAppender) throws IOException {
        appendable.append("<").append(tagName);

        if (classNames != null && classNames.length > 0) {
            appendable.append(" class=\"")
                    .append(TextUtils.join(" ", classNames)).append("\"");
        }

        appendable.append(">");
        contentAppender.accept(appendable, type);
        appendable.append("</").append(tagName).append(">");
    }
}
