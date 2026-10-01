package com.wordprocessor.source;

import com.wordprocessor.pipeline.Source;
import java.util.List;

/** Text that is already in memory (for example, the body of an HTTP request). */
public class TextSource implements Source {

    private final String text;

    public TextSource(String text) {
        this.text = text;
    }

    @Override
    public List<String> read() {
        return text.lines().toList();
    }
}
