package com.wordprocessor.sink;

import com.wordprocessor.pipeline.Sink;
import java.util.List;

public class ConsoleSink implements Sink {

    @Override
    public void write(List<String> lines) {
        lines.forEach(System.out::println);
    }
}
