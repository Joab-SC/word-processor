package com.wordprocessor.pipeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Pipeline {

    private final List<Filter> filters = new ArrayList<>();

    public Pipeline add(Filter filter) {
        filters.add(filter);
        return this;
    }

    /** Source -> filters -> Sink */
    public void run(Source source, Sink sink) throws IOException {
        sink.write(execute(source.read()));
    }

    public List<String> execute(List<String> input) {
        List<String> data = input;              // "data" plays the role of the pipe
        for (Filter filter : filters) {
            data = filter.process(data);
        }
        return data;
    }
}
