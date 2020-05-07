package org.dimdev;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Generator {
    private static final String PREFIX = "E:\\IdeaPojects\\DimDoors\\resources\\assets\\dimdoors\\blockstates\\";
    private static final String SUFFIX = "_fabric.json";
    private static final String[] COLORS = {
            "black",
            "blue",
            "brown",
            "cyan",
            "gray",
            "green",
            "light_blue",
            "light_gray",
            "lime",
            "magenta",
            "orange",
            "pink",
            "purple",
            "red",
            "silver",
            "white",
            "yellow"
    };

    public static void main(String[] args) throws IOException {
        String template = new String(Files.readAllBytes(Paths.get(PREFIX + "color" + SUFFIX)), StandardCharsets.UTF_8);

        for (String color : COLORS) {
            Files.write(Paths.get(PREFIX + "color" + SUFFIX), template.replace("color", color).getBytes());
        }
    }
}
