import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    public static void writeToFile(String filename, String message){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            if(message != null && !message.isEmpty()){
                writer.write(message);
                writer.newLine();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteChatFile(String filename, String message){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false))) {
            if(message != null && !message.isEmpty()){
                writer.write(message);
                writer.newLine();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addToAllClientsList(String filename, String clientName){
        List<String> allClients = readFromFile(filename);
        int count = 0;
        for(String client : allClients){
            if(client.equals(clientName)){
                count++;
            }
        }
        if(count == 0){
            writeToFile(filename, clientName);
        }
    }

    public static List<String> readFromFile(String filename){
        List<String> chatHistory = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                chatHistory.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return chatHistory;
    }

    public static JTextArea loadChatHistory(String filename){
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                chatArea.append(line + "\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chatArea;
    }

}
