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
    public static void main(String[] args) {

        try {
            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("file path and name");
            String inputLocation = bufferRead.readLine();
            System.out.println("output folder");
            String outputFolder = bufferRead.readLine();

            String content = Files.readString(Path.of(inputLocation));
            String inputFileName = new File(inputLocation).getName().replace(".drawio", "");
            Pattern regexPattern = Pattern.compile("name=\"([a-zA-Z0-9\\s-]*)\"");
            Matcher matcher = regexPattern.matcher(content);

            List<String> names = new ArrayList<>();
            Integer i = 0;

            while (matcher.find()) {
                i++;
                String counter = createPaddedCounter(i);
                names.add(counter + " - " + matcher.group(1) + ".jpg");
            }

            for (int j = 0; j < names.size(); j++) {
                String outputFileName = inputFileName + " - " + names.get(j);
                String command = String.format("\"\"C:\\Program Files\\draw.io\\draw.io.exe\" -f jpg -x \"%s\" -f jpg -o \"%s\" -p %s\""
                    , inputLocation
                    , outputFolder + "\\" + outputFileName
                    , j
                );
//                System.out.println(command);
                startCommand(command);
            }
        } catch (Exception e) {
            System.console().writer().println(e.getMessage());
        }
    }

    private static String createPaddedCounter(int i) {
        String padding = "";
        if (i < 100) { padding = "0"; }
        if (i < 10) { padding = "00"; }
        return padding + String.valueOf(i);
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
