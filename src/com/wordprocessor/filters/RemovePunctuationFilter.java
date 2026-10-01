package com.wordprocessor.filters;

import com.wordprocessor.pipeline.Filter;
import java.util.ArrayList;
import java.util.List;

/** Replaces punctuation with spaces (letters, digits and apostrophes are kept). */
public class RemovePunctuationFilter implements Filter {

    @Override
    public List<String> process(List<String> lines) {
        List<String> result = new ArrayList<>();
        for (String line : lines) {
            result.add(line.replaceAll("[^\\p{L}\\p{N}\\s']", " "));
        }
        return result;
    }
}
