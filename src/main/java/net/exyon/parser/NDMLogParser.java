package net.exyon.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class NDMLogParser {
    public static void main(String[] args) {
        File logDirectory = new File("./logs");
//        for (File logFile : logDirectory.listFiles()) {
//            if (logFile.isFile() && logFile.getName().endsWith(".log")) {
//                processLogFile(logFile);
//            }
//        }
        File[] logFiles = logDirectory.listFiles((dir, name) -> name.matches(".*\\.\\d+"));
//        Arrays.sort(logFiles, Comparator.comparingInt(f -> Integer.parseInt(f.getName().replaceAll(".*\\.", ""))));
        if (logFiles != null) {
            for (File logFile : logFiles) {
                if (logFile.isFile()) {
                    processLogFile(logFile);
                }
            }
        }
    }

    private static void processLogFile(File logFile) {
        try (Scanner scanner = new Scanner(logFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("STAR")) {
                    String[] fields = line.split("\\|");
                    String startTime = "";
                    String stopTime = "";
                    String protocol = "";
                    String sourceNode = "";
                    String destinationNode = "";
                    String senderUser = "";
                    String receiverUser = "";
                    String sourceFleName = "";
                    String destFileName = "";
                    String message = "";
                    for (String field : fields) {
                        String[] keyVal = field.split("-");
                        if (keyVal.length == 2) {
                            String key = keyVal[0];
                            String val = keyVal[1];
                            switch (key) {
                                case "SSTA":
                                    startTime = val;
                                    break;
                                case "STOP":
                                    stopTime = val;
                                    break;
                                case "PNAM":
                                    protocol = val.substring(0, 3);
                                    break;
                                case "SNOD":
                                    sourceNode = val;
                                    break;
                                case "SUBM":
                                    senderUser = val.split("@")[0];
                                    break;
                                case "SBID":
                                    receiverUser = val;
                                    break;
                                case "SFIL":
                                    sourceFleName = val;
                                    break;
                                case "DFIL":
                                    destFileName = val;
                                    break;
                                case "MSGT":
                                    message = val;
                                    break;

                                default:
                                    break;
                            }
                        }
                    }
                    System.out.printf("Route found: %s - %s. %s\n", sourceFleName, destFileName, message);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
