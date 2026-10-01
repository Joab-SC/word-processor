package com.wordprocessor.pipeline;

import java.io.IOException;
import java.util.List;

/** Where the data comes from. */
public interface Source {

    List<String> read() throws IOException;
}
