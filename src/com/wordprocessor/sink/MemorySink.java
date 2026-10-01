package com.wordprocessor.sink;

import com.wordprocessor.pipeline.Sink;
import java.util.ArrayList;
import java.util.List;

/** Keeps the result in memory so the caller (the web server) can send it back. */
public class MemorySink implements Sink {

    private List<String> lines = new ArrayList<>();

    @Override
    public void write(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    public List<String> lines() {
        return lines;
    }
}
