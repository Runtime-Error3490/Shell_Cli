package com.example.windows;

import java.io.*;

public class WindowsCmd {
    private Process process;
    private BufferedWriter writer;
    private BufferedReader reader;

    // Constructor: Start a persistent CMD process
    public WindowsCmd() {
        try {
            System.out.println("Starting CMD process...");
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/K"); // Keep CMD open
            builder.redirectErrorStream(true); // Merge error stream with output
            process = builder.start();
            writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            System.out.println("CMD started successfully.");
        } catch (IOException e) {
            throw new RuntimeException("Error starting CMD shell", e);
        }
    }

    // Execute a command inside the same CMD session using /K
    public void executeCommand(String command) {
        try {
            System.out.println("Executing command: " + command);
            writer.write(command + "\n"); // Send command
            writer.flush();
            readOutput(); // Read and print output
        } catch (IOException e) {
            throw new RuntimeException("Error executing command: " + command, e);
        }
    }

    // Read CMD output
    private void readOutput() {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[CMD OUTPUT]: " + line);
                if (line.contains(">")) { // CMD prompt indicates end of command
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading CMD output", e);
        }
    }

    // Close the CMD session
    public void close() {
        try {
            System.out.println("Closing CMD session...");
            executeCommand("exit"); // Exit CMD
            writer.close();
            reader.close();
            process.destroy();
            System.out.println("CMD session closed.");
        } catch (IOException e) {
            throw new RuntimeException("Error closing CMD shell", e);
        }
    }

    public static void main(String[] args) {
        WindowsCmd shell = new WindowsCmd();

        System.out.println("Creating folder 'shaleen'...");
        shell.executeCommand("mkdir shaleen");

        System.out.println("Creating index.js file inside 'shaleen'...");
        shell.executeCommand("echo console.log(\"Hello, World!\"); > shaleen\\index.js");

        System.out.println("All commands executed successfully!");

        shell.close();
    }
}
