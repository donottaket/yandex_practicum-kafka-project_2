package kz.yandex_practicum.kafka.filter_processor;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

public final class WordContext {
    private String from;
    private String to;
    private UUID messageId;
    private String replacement;
    private int order;
    private String original;

    public WordContext(String from, String to, UUID messageId, int order, String original) {
        this.from = from;
        this.to = to;
        this.messageId = messageId;
        this.order = order;
        this.original = original;
    }

    public WordContext(String from, String to, UUID messageId, int order, String original, String replacement) {
        this.from = from;
        this.to = to;
        this.messageId = messageId;
        this.order = order;
        this.original = original;
        this.replacement = replacement;
    }

    public WordContext() {
    }

    public String getOriginal() {
        return original;
    }

    @JsonIgnore
    public UUID getMessageKey() {
        return messageId;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public int getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return "WordContext[" +
                "from=" + from + ", " +
                "to=" + to + ", " +
                "messageId=" + messageId + ", " +
                "replacement=" + replacement + ", " +
                "order=" + order + ']';
    }
}