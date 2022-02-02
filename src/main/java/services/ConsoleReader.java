package services;

import java.io.*;
import java.time.LocalDateTime;

/**
 * this class provides correct information from console
 */
public class ConsoleReader {
    private BufferedReader reader;

    public ConsoleReader(InputStream inputStream) {
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    String readString() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    int readInteger() {
        try {
            return Integer.parseInt(readString());
        } catch (NumberFormatException e) {
            System.out.println("your input is not a number! try again");
        }
        return 0;
    }

    LocalDateTime readLocaleDateTime() {
        LocalDateTime localDateTime;
        while (true) {
            try {
                System.out.println("enter the year in xxxx format");
                int year = readInteger();
                System.out.println("enter the month in number format(from 1 to 12)");
                int month = readInteger();
                System.out.println("enter the day of the month");
                int day = readInteger();
                System.out.println("enter the hour (from 00 to 23)");
                int hours = readInteger();
                System.out.println("enter the minutes (from 00 to 59)");
                int minutes = readInteger();
                localDateTime = LocalDateTime.of(year, month, day, hours, minutes);
                if (localDateTime.isBefore(LocalDateTime.now())) {
                    System.out.println("your deadline has already outdated!");
                    continue;
                }
                return localDateTime;

            } catch (Exception e) {
                System.out.println("your input is not correct! try again!");
            }
        }
    }
}
