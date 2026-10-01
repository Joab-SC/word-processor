package com.wordprocessor.filters;

import com.wordprocessor.pipeline.Filter;
import java.util.ArrayList;
import java.util.List;

public class LineNumberFilter implements Filter {

    @Override
    public List<String> process(List<String> lines) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            result.add((i + 1) + ": " + lines.get(i));
        }
        return result;
    }
}
