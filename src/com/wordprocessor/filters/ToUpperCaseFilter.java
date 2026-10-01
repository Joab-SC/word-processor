package com.wordprocessor.filters;

import com.wordprocessor.pipeline.Filter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ToUpperCaseFilter implements Filter {

    @Override
    public List<String> process(List<String> lines) {
        List<String> result = new ArrayList<>();
        for (String line : lines) {
            result.add(line.toUpperCase(Locale.ROOT));
        }
        return result;
    }
}
