package com.wordprocessor.filters;

import com.wordprocessor.pipeline.Filter;
import java.util.ArrayList;
import java.util.List;

/** Keeps the lines and appends a final "Total words: N" line. */
public class WordCountFilter implements Filter {

    @Override
    public List<String> process(List<String> lines) {
        int total = 0;
        for (String line : lines) {
            // ignore the "N: " prefix added by LineNumberFilter
            String text = line.replaceFirst("^\\d+:\\s*", "").trim();
            if (!text.isEmpty()) {
                total += text.split("\\s+").length;
            }
        }
        List<String> result = new ArrayList<>(lines);
        result.add("Total words: " + total);
        return result;
    }
}
