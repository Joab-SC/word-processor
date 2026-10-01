package com.wordprocessor;

import com.wordprocessor.filters.LineNumberFilter;
import com.wordprocessor.filters.NormalizeSpacesFilter;
import com.wordprocessor.filters.RemoveEmptyLinesFilter;
import com.wordprocessor.filters.RemovePunctuationFilter;
import com.wordprocessor.filters.ToUpperCaseFilter;
import com.wordprocessor.filters.WordCountFilter;
import com.wordprocessor.pipeline.Pipeline;
import com.wordprocessor.pipeline.Source;
import com.wordprocessor.sink.ConsoleSink;
import com.wordprocessor.source.ConsoleSource;
import com.wordprocessor.source.FileSource;
import java.io.IOException;
import java.nio.file.Path;

/**
 * (no arguments) -> asks for text in the console
 * file.txt       -> processes that file
 */
public class Main {

    public static void main(String[] args) throws IOException {
        Pipeline pipeline = new Pipeline()
                .add(new RemovePunctuationFilter())
                .add(new NormalizeSpacesFilter())
                .add(new RemoveEmptyLinesFilter())
                .add(new ToUpperCaseFilter())
                .add(new LineNumberFilter())
                .add(new WordCountFilter());

        Source source = args.length > 0 ? new FileSource(Path.of(args[0])) : new ConsoleSource();
        pipeline.run(source, new ConsoleSink());
    }
}