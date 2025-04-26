package kz.yandex_practicum.kafka.filter_processor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kz.yandex_practicum.kafka.filter_processor.message.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class WordAccumulator {
    private String from;
    private String to;
    private final List<String> words = new ArrayList<>();

    public WordAccumulator() {
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public List<String> getWords() {
        return words;
    }

    @JsonIgnore
    public WordAccumulator addWord(WordContext context) {
        if (from == null) {
            from = context.getFrom();
        }

        if (to == null) {
            to = context.getTo();
        }

        // Добавляем слово и его позицию
        words.add(context.getReplacement() != null ? context.getReplacement() : context.getOriginal());

        return this;
    }

    @JsonIgnore
    public Message toMessage() {
        StringJoiner joiner = new StringJoiner(" ");

        for (String word : words) {
            joiner.add(word);
        }

        return new Message(from, to, joiner.toString(), null);
    }
}