import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Server {

    private static final Map<String, Socket> clients = Collections.synchronizedMap(new HashMap<>());
    private static List<String> chatHistory = Collections.synchronizedList(new ArrayList<>());
    private static List<String> accounts = new ArrayList<>();


    public static Map<String, Socket> getClientList(){
        return clients;
    }
    public static Socket getClientSocketByName(String clientName) {
        return clients.get(clientName);
    }
    public static List<String> getChatHistory(){
        return chatHistory;
    }
    public static Set<String> getClientNames() {
        return clients.keySet();
    }
    public static void setClients(String clientName, Socket socket){
        clients.put(clientName, socket);
    }


    public static void checkLogin(String username, String password){
        accounts = FileHandler.readFromFile("accounts.txt");
        if(!accounts.isEmpty() && accounts != null){
            for(String account : accounts){
                String[] parts = account.split(";");
                if(parts.length >= 3){
                    String dbUsername = parts[0];
                    String dbPassword = parts[1];
                    String clientName = parts[2];
                    if(username.equals(dbUsername) && password.equals(dbPassword)){
                        sendMessageToClient("loginSocket", "LOGIN_SUCCESS", clientName);
                        return;
                    }
                }
            }
            sendMessageToClient("loginSocket", "INVALID_ACCOUNT", "");
        }
    }
    public static void checkSignup(String username, String password, String clientName){
        List<String> allGroupName = FileHandler.readFromFile("groupChatList.txt");
        if(!accounts.isEmpty() && accounts != null){
            for(String account : accounts){
                String[] parts = account.split(";");
                if(parts.length >= 3) {
                    String dbUsername = parts[0];
                    String dbPassword = parts[1];
                    String dbClientName = parts[2];
                    if(username.equals(dbUsername)){
                        sendMessageToClient("signupSocket", "ACCOUNT_EXIST", "");
                        return;
                    } else if(clientName.equals(dbClientName)){
                        sendMessageToClient("signupSocket", "CLIENT_NAME_EXIST", "");
                        return;
                    }
                }
            }
            if(!allGroupName.isEmpty() && allGroupName != null){
                for(String groupName : allGroupName){
                    if(groupName.equals(clientName)){
                        sendMessageToClient("signupSocket", "CLIENT_NAME_EXIST", "");
                        return;
                    }
                }
            }
            FileHandler.writeToFile("accounts.txt", username + ";" + password + ";" + clientName);
            sendMessageToClient("signupSocket", "SIGNUP_SUCCESS", "");
        }
    }
    public static void sendMessageToClient(String destClientName, String message, String srcClientName) {
        Socket destClientSocket = clients.get(destClientName);
        if (destClientSocket != null) {
            try {
                PrintWriter writer = new PrintWriter(destClientSocket.getOutputStream(), true);
                writer.println(srcClientName+ ": " + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void sendFileToClient(String destClientName, String filePath, String srcClientname) {
        Socket destClientSocket = clients.get(destClientName);
        if (destClientSocket != null) {
            try {
                PrintWriter writer = new PrintWriter(destClientSocket.getOutputStream(), true);
                writer.println("SEND_FILE_ANNOUNCEMENT;" + srcClientname + ";" + filePath);

                // Gửi dữ liệu file
                try (BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(filePath))) {
                    int count;
                    byte[] buffer = new byte[1024];
                    OutputStream outputStream = destClientSocket.getOutputStream();
                    while ((count = fileInputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, count);
                        outputStream.flush();
                    }
                }
                // Close the output stream to signal the end of the file transmission
                destClientSocket.shutdownOutput();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void broadcastClientList() {
        StringBuilder clientListString = new StringBuilder();
        Set<String> clientsName = clients.keySet();
        for (String client : clientsName) {
            clientListString.append(client).append(",");
        }

        // Gửi danh sách cập nhật đến tất cả client
        for (Socket clientSocket : clients.values()) {
            try {
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                writer.println("UPDATE_CHAT_LIST");
                writer.println(clientListString.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void addGroupChatHandle(String groupName, String clientName){
        List<String> allGroupChat = FileHandler.readFromFile("groupChatList.txt");
        List<String> allClients = FileHandler.readFromFile("clientsName.txt");
        if (allClients != null && !allClients.isEmpty()){
            for(String dbClientName : allClients){
                if(dbClientName.equals(groupName)){
                    sendMessageToClient(clientName, "CREATE_GROUP_FAIL", groupName);
                    return;
                }
            }
            if(allGroupChat != null && !allGroupChat.isEmpty()){
                int count = 0;
                for(String groupChat : allGroupChat){
                    if(groupChat.equals(groupName)){
                        count++;
                    }
                }
                if(count == 0){
                    // viết tên group vào file tổng tất cả các group trong server
                    FileHandler.writeToFile("groupChatList.txt", groupName);

                    // viết tên group vào file chứa tất cả groupName của client đó
                    File file = new File("groupsName_" + clientName + ".txt");
                    if(file.exists()){
                        FileHandler.writeToFile("groupsName_" + clientName + ".txt", groupName);
                    } else {
                        FileHandler.writeToFile("groupsName_" + clientName + ".txt", groupName);
                    }

                    // viết tên các client có trong group đó
                    File groupFile = new File(groupName + ".txt");
                    if(file.exists()){
                        FileHandler.writeToFile(groupName + ".txt", clientName);
                    } else {
                        FileHandler.writeToFile(groupName + ".txt", clientName);
                    }
                    sendMessageToClient(clientName, "CREATE_GROUP_SUCCESS", groupName);
                } else {
                    sendMessageToClient(clientName, "CREATE_GROUP_FAIL", groupName);
                }
            } else {
                // viết tên group vào file tổng tất cả các group trong server
                FileHandler.writeToFile("groupChatList.txt", groupName);

                // viết tên group vào file chứa tất cả groupName của client đó
                File file = new File("groupsName_" + clientName + ".txt");
                if(file.exists()){
                    FileHandler.writeToFile("groupsName_" + clientName + ".txt", groupName);
                } else {
                    FileHandler.writeToFile("groupsName_" + clientName + ".txt", groupName);
                }

                // viết tên các client có trong group đó
                File groupFile = new File(groupName + ".txt");
                if(file.exists()){
                    FileHandler.writeToFile(groupName + ".txt", clientName);
                } else {
                    FileHandler.writeToFile(groupName + ".txt", clientName);
                }
                sendMessageToClient(clientName, "CREATE_GROUP_SUCCESS", groupName);
            }
        }
    }
    public static void deleteChatHistory (String receiver, String sender){
        String fileToDelete = sender + "_" + receiver + ".txt";
        Path filePath = Paths.get(fileToDelete);
        try {
            Files.delete(filePath);
            System.out.println("File deleted successfully!");
        } catch (IOException e) {
            System.err.println("Failed to delete the file: " + e.getMessage());
        }

        chatHistory.remove(fileToDelete);
        for(String chat : chatHistory){
            FileHandler.deleteChatFile("chatHistory.txt", chat);
        }
    }
    public static void addMemberToGroup(String groupName, String listMember, String clientName){
        String[] newMembers = listMember.split(",");
        List<String> existMembers = FileHandler.readFromFile(groupName + ".txt");
        for(String newMember : newMembers){
            int count = 0;
            for(String existMember : existMembers){
                if(newMember.equals(existMember)){
                    count++;
                }
            }
            if (count == 0){
                // viết tên group vào file chứa tất cả groupName của client đó
                File file = new File("groupsName_" + newMember + ".txt");
                if(file.exists()){
                    FileHandler.writeToFile("groupsName_" + newMember + ".txt", groupName);
                } else {
                    FileHandler.writeToFile("groupsName_" + newMember + ".txt", groupName);
                }

                // viết tên các client có trong group đó
                File groupFile = new File(groupName + ".txt");
                if(file.exists()){
                    FileHandler.writeToFile(groupName + ".txt", newMember);
                } else {
                    FileHandler.writeToFile(groupName + ".txt", newMember);
                }
                sendMessageToClient(clientName, "ADD_MEMBER_SUCCESS", newMember);
                sendMessageToClient(newMember, "UPDATE_GROUP_CHAT_LIST", groupName);
            } else {
                sendMessageToClient(clientName, "ADD_MEMBER_FAIL", newMember);
            }
        }
    }
    public static void sendMessageToGroupChat(String message, String groupName, String srcClientName) {
        List<String> allMemberInGroup = FileHandler.readFromFile(groupName + ".txt");
        if(!allMemberInGroup.isEmpty() && allMemberInGroup != null){
            for(String member : allMemberInGroup){
                if(!member.equals(srcClientName)) {
                    Socket destClientSocket = clients.get(member);
                    if (destClientSocket != null) {
                        try {
                            PrintWriter writer = new PrintWriter(destClientSocket.getOutputStream(), true);
                            writer.println("MESSAGE_FROM_GROUP: " + groupName + ": " + srcClientName+ ": " + message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }


    public static void main(String[] args) {
        chatHistory = FileHandler.readFromFile("chatHistory.txt");
        try {
            ServerSocket serverSocket = new ServerSocket(3200);
            System.out.println("Waiting for clients ...");

            while (true) {
                // Chấp nhận kết nối từ client và tạo một thread mới
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getPort());

                // Tạo một thread mới để xử lý thông tin từ client
                new Thread(new ClientHandler(clientSocket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
