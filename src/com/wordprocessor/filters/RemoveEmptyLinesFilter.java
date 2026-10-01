package com.wordprocessor.filters;

import com.wordprocessor.pipeline.Filter;
import java.util.ArrayList;
import java.util.List;

public class RemoveEmptyLinesFilter implements Filter {

    @Override
    public List<String> process(List<String> lines) {
        List<String> result = new ArrayList<>();
        for (String line : lines) {
            if (!line.isBlank()) {
                result.add(line);
            }
        }
        return result;
    }
}
