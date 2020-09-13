package com.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    private static List<Note> notes = new ArrayList<>();

    // Before Java 8
//    static class ShutdownHook extends Thread {
//        @Override
//        public void run() {
//            super.run();
//        }
//    }
    // Java 8
    static Thread shutdownHook = new Thread(() -> {
        saveToFile();
    });

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // register a shutdown hook
        Runtime.getRuntime().addShutdownHook(shutdownHook);
        // main
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("********** WELCOME TO NOTE APP **********");
            notes = loadFromFile();
            Note.setIndex(notes.size());
            while (true) {
                System.out.println("The are common commands used in various situations:");
                System.out.println("1. List all notes");
                System.out.println("2. Take a new note");
                System.out.println("3. Edit an existing note");
                System.out.println("4. Delete an existing note");
                System.out.println("5. Exit the app");
                System.out.println("Enter your choice: ");
                String input = scanner.nextLine();
                int number = 0;
                try {
                    number = Integer.parseInt(input);
                } catch (NumberFormatException ex) {

                }
                int noteId;
                List<Note> candidates;
                switch (number) {
                    case 1:
                        System.out.println("Listing all notes");
                        notes.forEach(note -> System.out.printf("[%d] %s\n", note.getId(), note.getContent()));
                        break;
                    case 2:
                        System.out.println("Enter your note:");
                        input = scanner.nextLine();
                        notes.add(new Note(input));
                        break;
                    case 3:
                        notes.forEach(note -> System.out.printf("[%d] %s\n", note.getId(), note.getContent()));
                        System.out.println("Enter your note ID:");
                        input = scanner.nextLine();
                        noteId = Integer.parseInt(input);
                        candidates = notes.stream().filter(note -> note.getId() == noteId).collect(Collectors.toList());
                        if (candidates.size() > 0) {
                            System.out.println("New new note:");
                            String newContent = scanner.nextLine();
                            candidates.get(0).setContent(newContent);
                        } else {
                            System.out.println("Invalid ID!");
                        }
                        break;
                    case 4:
                        notes.forEach(note -> System.out.printf("[%d] %s\n", note.getId(), note.getContent()));
                        System.out.println("Enter your note ID:");
                        noteId = scanner.nextInt();
                        scanner.nextLine(); // nextInt() doesn't consume the break line \n, this will throw it away
                        candidates = notes.stream().filter(note -> note.getId() == noteId).collect(Collectors.toList());
                        if (candidates.size() > 0) {
                            System.out.println("Are you sure you want to delete this note? Please type Y or y to confirm...");
                            String answer = scanner.next();
                            if ("Y".equals(answer) || "y".equals(answer)) {
                                notes.removeIf(note -> note.getId() == noteId);
                            }
                        } else {
                            System.out.println("Invalid ID!");
                        }
                        break;
                    case 5:
                        System.out.println("Are you sure you want to exit the app? Please type Y or y to confirm...");
                        String answer = scanner.next();
                        if ("Y".equals(answer) || "y".equals(answer)) {
                            System.exit(0);
                        }
                        break;
                    default:
                        System.out.println("We're expecting a number between 1 and 5. Please try again!");
                        break;
                }
            }
        }
    }

    static void saveObjectsToFile() {
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("notes"));
            for (Note note : notes) {
                os.writeObject(note);
            }
            os.close();
        } catch (IOException exception) {
            System.out.println("Couldn't write the notes out");
            exception.printStackTrace();
        }
    }

    static List<Note> loadObjectsFromFile() throws IOException, ClassNotFoundException {
        List<Note> notes = new ArrayList<>();
        ObjectInputStream is = new ObjectInputStream(new FileInputStream("notes"));
        while (true) {
            try {
                Note note = (Note) is.readObject();
                notes.add(note);
            } catch (EOFException ex) {
                break;
            }
        }
        return notes;
    }

    static void saveToFile() {
        if (notes.size() < 1) {
            return;
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("notes.csv"));
            writer.write("ID,Content\n");
            for (Note note : notes) {
                writer.write(String.format("%d,%s\n", note.getId(), note.getContent()));
            }
            writer.close();
        } catch (IOException ex) {
            System.out.println("Couldn't write the notes out");
            ex.printStackTrace();
        }
    }

    static List<Note> loadFromFile() throws IOException, ClassNotFoundException {
        List<Note> notes = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("notes.csv"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("ID")) { // skip header
                    continue;
                }
                String[] array = line.split(",");
                if (array.length != 2) {
                    continue;
                }
                int id = Integer.parseInt(array[0]);
                String content = array[1];
                notes.add(new Note(id, content));
            }
        } catch (IOException ex) {

        }
        return notes;
    }
}

class Note implements Serializable {
    private static int index;
    private int id;
    private String content;

    public Note(String content) {
        index++;
        this.id = index;
        this.content = content;
    }

    public Note(int id, String content) {
        this.id = id;
        this.content = content;
    }

    public static void setIndex(int index) {
        Note.index = index;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        System.out.printf("Updating note [%d] from %s to %s\n", this.id, this.content, content);
        this.content = content;
    }
}