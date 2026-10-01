package com.wordprocessor.pipeline;

import java.io.IOException;
import java.util.List;

/** Where the result goes. */
public interface Sink {

    void write(List<String> lines) throws IOException;
}
