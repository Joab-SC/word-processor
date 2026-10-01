package com.wordprocessor.pipeline;

import java.util.List;

public interface Filter {

    List<String> process(List<String> lines);
}
