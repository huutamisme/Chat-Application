import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.*;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private String clientName;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        handleClient();
    }


    private static String generateSenderFileName(String sender, String receiver, List<String> chatHistory){
        for(String file : chatHistory){
            String[] file_name = file.split("_");
            if(file_name.length >= 2){
                String client1 = file_name[0];
                String client2 = file_name[1].split("\\.txt")[0];
                if(sender.equals(client1) && receiver.equals(client2)){
                    return file;
                }
            }
        }
        String filename = sender + "_" + receiver + ".txt";
        chatHistory.add(filename);
        FileHandler.writeToFile("chatHistory.txt", filename);
        return filename;
    }

    private static String generateReceiverFileName(String sender, String receiver, List<String> chatHistory){
        for(String file : chatHistory){
            String[] file_name = file.split("_");
            if(file_name.length >= 2){
                String client1 = file_name[0];
                String client2 = file_name[1].split("\\.txt")[0];
                if(sender.equals(client2) && receiver.equals(client1)){
                    return file;
                }
            }
        }
        String filename = receiver + "_" + sender + ".txt";
        chatHistory.add(filename);
        FileHandler.writeToFile("chatHistory.txt", filename);
        return filename;
    }

    private void handleClient() {
        try {

            // Đọc tên từ client
            BufferedReader nameReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String clientName = nameReader.readLine();


            System.out.println("Client's name is: " + clientName);

            // Lưu thông tin client vào HashMap
            Server.setClients(clientName, clientSocket);
            // Gán tên cho client
            this.clientName = clientName;

            if(!clientName.equals("loginSocket") && !clientName.equals("signupSocket")){
                FileHandler.addToAllClientsList("clientsName.txt", clientName);
                Server.broadcastClientList();
            }


            // Tạo một thread riêng để xử lý tin nhắn từ client
            Thread readerThread = new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(clientName + ": " + line);

                        // Kiểm tra nếu người dùng nhập "quit", thì đóng kết nối
                        if (line.equalsIgnoreCase("quit")) {
                            System.out.println(clientName + " has left!");
                            Server.broadcastClientList();
                            break;
                        }

                        // Kiểm tra nếu là lệnh gửi tin nhắn đến một client cụ thể
                        if (line.contains(";;;")) {
                            String[] parts = line.split(";;;");
                            if (parts.length == 2) {
                                String destClientName = parts[0];
                                String message = parts[1];
                                String senderFileName = generateSenderFileName(clientName, destClientName, Server.getChatHistory());
                                String receiverFileName = generateReceiverFileName(clientName, destClientName, Server.getChatHistory());
                                FileHandler.writeToFile(senderFileName, "You: " + message);
                                FileHandler.writeToFile(receiverFileName, clientName + ": " + message);
                                Server.sendMessageToClient(destClientName, message, clientName);
                            }
                        }

                        if(line.startsWith("SEND_FILE_ANNOUNCEMENT")){
                            String[] parts = line.split(";");
                            if(parts.length == 3){
                                String destClientName = parts[1];
                                String filePath = parts[2];
                                Server.sendFileToClient(destClientName, filePath, clientName);
                            }
                        }

                        if(line.contains(":::")){
                            String[] parts = line.split(":::");
                            if(parts.length == 2){
                                String username = parts[0];
                                String password = parts[1];
                                Server.checkLogin(username, password);
                            } else if(parts.length == 3){
                                String username = parts[0];
                                String password = parts[1];
                                String newClientName = parts[2];
                                Server.checkSignup(username, password, newClientName);
                            }
                        }

                        if(line.startsWith("CREATE_GROUP_CHAT")){
                            String[] parts = line.split(";");
                            if(parts.length == 3){
                                String groupName = parts[1];
                                String creator = parts[2];
                                Server.addGroupChatHandle(groupName, creator);
                            }
                        }

                        if(line.startsWith("ADD_MEMBER")){
                            String[] parts = line.split(";");
                            if(parts.length == 4){
                                String listMember = parts[1];
                                String groupName = parts[2];
                                String addMemberClient = parts[3];
                                Server.addMemberToGroup(groupName, listMember, addMemberClient);
                            }
                        }

                        if(line.startsWith("CLIENT_CHAT_TO_GROUP")){
                            String[] parts = line.split(";");
                            if(parts.length == 4){
                                String message = parts[1];
                                String groupName = parts[2];
                                String srcClientName = parts[3];
                                Server.sendMessageToGroupChat(message, groupName, srcClientName);
                            }
                        }

                        if(line.startsWith("DELETE_CHAT_HISTORY")){
                            String[] parts = line.split(";");
                            if(parts.length == 3){
                                String receiver = parts[1];
                                String sender = parts[2];
                                Server.deleteChatHistory(receiver, sender);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        clientSocket.close();
                        Server.getClientList().remove(clientName);
                        Server.broadcastClientList();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            readerThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
