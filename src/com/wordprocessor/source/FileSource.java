package com.wordprocessor.source;

import com.wordprocessor.pipeline.Source;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileSource implements Source {

    private final Path path;

    public FileSource(Path path) {
        this.path = path;
    }

    @Override
    public List<String> read() throws IOException {
        return Files.readAllLines(path);
    }
}
