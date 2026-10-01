package com.wordprocessor.source;

import com.wordprocessor.pipeline.Source;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/** Reads text typed by the user until a line that says FIN. */
public class ConsoleSource implements Source {

    @Override
    public List<String> read() throws IOException {
        System.out.println("Escribe tu texto. Cuando termines, escribe FIN en una linea sola:");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null && !line.equalsIgnoreCase("FIN")) {
            lines.add(line);
        }
        return lines;
    }
}