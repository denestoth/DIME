package com.dnstth.dime;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final String DRAW_IO_EXECUTABLE_LOCATION = "\"\"C:\\Program Files\\draw.io\\draw.io.exe\"";
    private static final String SLIDE_NAME_PATTERN = "name=\"([a-zA-Z0-9\\s-]*)\"";

    public static void main(String[] args) {

        try {
            String inputLocation = getSourceFileLocation();
            String outputFolder = getOutputLocation();

            List<String> names = getSlideNames(inputLocation);

            String inputFileName = getOriginalFileNameWithoutExtension(inputLocation);

            for (int j = 0; j < names.size(); j++) {
                String outputFileName = String.format("%s - %03d - %s.jpg"
                    , inputFileName
                    , j + 1
                    , names.get(j));

                String command = String.format("%s -f jpg -x \"%s\" -f jpg -o \"%s\" -p %s\""
                    , DRAW_IO_EXECUTABLE_LOCATION
                    , inputLocation
                    , outputFolder + "\\" + outputFileName
                    , j
                );

                startCommand(command);
            }
        } catch (Exception e) {
            System.console().writer().println(e.getMessage());
        }
    }

    private static String getSourceFileLocation() throws IOException {
        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("file path and name");

        return bufferRead.readLine();
    }

    private static String getOutputLocation()  throws IOException {
        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("output folder");

        return bufferRead.readLine();
    }

    private static List<String> getSlideNames(String inputLocation) throws IOException {
        List<String> names = new ArrayList<>();
        String content = Files.readString(Path.of(inputLocation));
        Pattern regexPattern = Pattern.compile(SLIDE_NAME_PATTERN);
        Matcher matcher = regexPattern.matcher(content);

        while (matcher.find()) {
            names.add(matcher.group(1));
        }
        return names;
    }

    private static String getOriginalFileNameWithoutExtension(String inputLocation) {
        return new File(inputLocation).getName().replace(".drawio", "");
    }

    private static void startCommand(String command) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(
            "cmd.exe", "/c", command);
        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while (true) {
            line = r.readLine();
            if (line == null) { break; }
            System.out.println(line);
        }
    }
}
