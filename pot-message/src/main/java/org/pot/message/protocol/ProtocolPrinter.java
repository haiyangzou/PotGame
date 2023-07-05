package org.pot.message.protocol;

import com.google.common.collect.Sets;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import com.google.protobuf.util.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.Constants;

import java.util.Set;
import java.util.function.Function;

@Slf4j
public class ProtocolPrinter {
    private static final TextFormat.Printer textFormatPrinter = TextFormat.printer().escapingNonAscii(false);
    private static final JsonFormat.Printer jsonFormatPrinter = JsonFormat.printer().preservingProtoFieldNames().includingDefaultValueFields().omittingInsignificantWhitespace();
    @Getter
    @Setter
    private static volatile boolean disabledPrint = !Constants.Env.isDebug();
    private static final Set<String> disablePrintSet = Sets.newConcurrentHashSet();

    public static boolean isDisabledPrint(MessageOrBuilder messageOrBuilder) {
        if (disabledPrint) return true;
        if (messageOrBuilder == null) return false;
        return disablePrintSet.contains(ProtocolSupport.name(messageOrBuilder.getDefaultInstanceForType().getClass()));
    }

    private static String printIterable(Iterable<? extends MessageOrBuilder> iterable, Function<MessageOrBuilder, String> printer) {
        if (iterable == null) return "[]";
        int index = 0;
        StringBuilder builder = new StringBuilder(24 * 16);
        for (MessageOrBuilder messageOrBuilder : iterable) {
            if (index++ > 0) builder.append(",");
            builder.append(printer.apply(messageOrBuilder));
        }
        return builder.toString();
    }

    public static String printRestrictedJson(MessageOrBuilder messageOrBuilder) {
        if (isDisabledPrint(messageOrBuilder)) return "DISABLED";
        return printJson(messageOrBuilder);
    }

    public static String printRestrictedJson(Iterable<? extends MessageOrBuilder> iterable) {
        return printIterable(iterable, ProtocolPrinter::printRestrictedJson);
    }

    public static String printRestrictedText(MessageOrBuilder messageOrBuilder) {
        if (isDisabledPrint(messageOrBuilder)) return "DISABLED";
        return printText(messageOrBuilder);
    }

    public static String printRestrictedText(Iterable<? extends MessageOrBuilder> iterable) {
        return printIterable(iterable, ProtocolPrinter::printText);
    }

    public static String printJson(MessageOrBuilder messageOrBuilder) {
        if (messageOrBuilder == null) return "NULL";
        try {
            return jsonFormatPrinter.print(messageOrBuilder);
        } catch (Throwable e) {
            log.error("print proto json fail {}", messageOrBuilder, e);
        }
        return "ERROR_PROTO";
    }

    public static String printJson(Iterable<? extends MessageOrBuilder> iterable) {
        return printIterable(iterable, ProtocolPrinter::printJson);
    }

    public static String printText(MessageOrBuilder messageOrBuilder) {
        if (messageOrBuilder == null) return "NULL";
        try {
            return textFormatPrinter.shortDebugString(messageOrBuilder);
        } catch (Throwable e) {
            log.error("print proto text fail {}", messageOrBuilder, e);
        }
        return "ERROR_PROTO";
    }

    public static String printText(Iterable<? extends MessageOrBuilder> iterable) {
        return printIterable(iterable, ProtocolPrinter::printText);
    }

}
