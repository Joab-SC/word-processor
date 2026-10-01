package com.wordprocessor.filters;

import com.wordprocessor.pipeline.Filter;
import java.util.ArrayList;
import java.util.List;

/** Trims each line and collapses repeated spaces into one. */
public class NormalizeSpacesFilter implements Filter {

    @Override
    public List<String> process(List<String> lines) {
        List<String> result = new ArrayList<>();
        for (String line : lines) {
            result.add(line.trim().replaceAll("\\s+", " "));
        }
        return result;
    }
}
