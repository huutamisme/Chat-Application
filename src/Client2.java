import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.List;


public class Client2 {
    private static CardLayout cardLayout;
    private static JPanel cardPanel;
    private static JList<String> chatList;
    private static JList<String> copyChatList;
    private static JList<String> groupChatList;
    private static HashMap<String, JTextArea> chatAreas;
    private static HashMap<String, JTextArea> groupChatAreas;
    private static JTextField messageField;
    private static String selectedFriend;
    private static String selectedGroup;
    private static JLabel groupNameLabel;
    private static List<String> friendsChatted;
    private static List<String> groupChatted;
    private static final List<String> friends = new ArrayList<>();
    private static List<String> onlineFriends = new ArrayList<>();
    private static String selectedFileName;

    private static void updateChatList(String clientListString) {
        DefaultListModel<String> updatedListModel = new DefaultListModel<>();
        for(String friend : friends){
            updatedListModel.addElement(friend);
        }
        List<String> allClients = FileHandler.readFromFile("clientsName.txt");
        if(allClients != null && !allClients.isEmpty()){
            for (String clientName : allClients) {
                int count = 0;
                if(!friends.isEmpty() && friends != null) {
                    for (String friend : friends) {
                        if (clientName.equals(friend)) {
                            count++;
                        }
                    }
                }
                if(count == 0){
                    updatedListModel.addElement(clientName);
                    friends.add(clientName);
                }
            }
        }
        if(clientListString != null && !clientListString.isEmpty()) {
            String[] onlClientList = clientListString.split(",");
            onlineFriends = new ArrayList<>();
            Collections.addAll(onlineFriends, onlClientList);
            chatList.setModel(updatedListModel);
            copyChatList.setModel(updatedListModel);
        }
    }
    private static void updateGroupChatList(String newGroupName){
        DefaultListModel<String> updatedListModel = new DefaultListModel<>();
        updatedListModel.addElement(newGroupName);
        for(String groupChat : groupChatted){
            updatedListModel.addElement(groupChat);
        }
        groupChatList.setModel(updatedListModel);
    }
    private static int updateStatusChatList(String selectedClientName){
        if(onlineFriends != null && !onlineFriends.isEmpty()){
            for(String client : onlineFriends){
                if(client.equals(selectedClientName)){
                    return 1;
                }
            }
        }
        return 0;
    }
    private static void initFriendList(String currentClientName){
        DefaultListModel<String> updatedListModel = new DefaultListModel<>();
        updatedListModel.addElement(currentClientName);
        friends.add(currentClientName);
        if(friendsChatted != null && !friendsChatted.isEmpty()){
            for(String friend : friendsChatted){
                updatedListModel.addElement(friend);
                friends.add(friend);
            }
        }
        chatList.setModel(updatedListModel);
        copyChatList.setModel(updatedListModel);
    }
    private static void initGroupChatList(){
        DefaultListModel<String> updatedListModel = new DefaultListModel<>();
        for(String groupChat : groupChatted){
            updatedListModel.addElement(groupChat);
        }
        groupChatList.setModel(updatedListModel);
    }
    private static void updateChatAreas(String chatAreasString){
        if(chatAreasString != null && !chatAreasString.isEmpty()){
            String[] clientNames = chatAreasString.split(",");
            for(String chatAreaString : clientNames){
                chatAreas.computeIfAbsent(chatAreaString, k -> {
                    JTextArea newTextArea = new JTextArea();
                    newTextArea.setEditable(false);
                    return newTextArea;
                });
            }
        }
    }
    private static void updateGroupChatAreas(String newGroupName){
        groupChatAreas.computeIfAbsent(newGroupName, k-> {
            JTextArea newTextArea = new JTextArea();
            newTextArea.setEditable(false);
            return newTextArea;
        });
    }
    private static void initChatAreas(){
        List<String> allClients = FileHandler.readFromFile("clientsName.txt");
        if(allClients != null && !allClients.isEmpty()){
            for(String chatAreaString : allClients){
                chatAreas.computeIfAbsent(chatAreaString, k -> {
                    JTextArea newTextArea = new JTextArea();
                    newTextArea.setEditable(false);
                    return newTextArea;
                });
            }
        }
    }
    private static void initGroupChatAreas(String clientName){
        if(groupChatted != null && !groupChatted.isEmpty()){
            for(String groupChatAreaString : groupChatted){
                groupChatAreas.computeIfAbsent(groupChatAreaString, k-> {
                    JTextArea newTextArea = new JTextArea();
                    newTextArea.setEditable(false);
                    return newTextArea;
                });
            }
        }
    }
    private static int addFriend(String friendName, String clientName){
        List<String> friendsChatted = FileHandler.readFromFile(clientName + ".txt");
        for(String friend : friendsChatted){
            if(friend.equals(friendName)){
                return 0;
            }
        }
        friendsChatted.add(friendName);
        return 1;
    }
    private static void reloadChatHistory(String clientName){
        List<String> chatHistory = FileHandler.readFromFile("chatHistory.txt");
        if(friends != null && !friends.isEmpty()){
            for(String friend : friends){
                for(String history : chatHistory){
                    String [] history_split = history.split("_");
                    if(history_split.length >= 2){
                        String client1 = history_split[0];
                        String client2 = history_split[1].split("\\.txt")[0];
                        if ((clientName.equals(client1) && friend.equals(client2))) {
                            chatAreas.put(friend, FileHandler.loadChatHistory(history));
                        }
                    }
                }
            }
        }
    }
    public static void addComponents(Container pane) {
        // Tạo CardLayout và JPanel để chứa các panel
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Thêm giao diện login và signup vào cardPanel
        cardPanel.add(createLoginPanel(), "login");
        cardPanel.add(createSignupPanel(), "signup");
        // Thêm cardPanel vào container
        pane.add(cardPanel);
    }
    private static JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel(new GridLayout(4, 1));

        JPanel titleLoginPanel = new JPanel();
        JLabel loginLabel = new JLabel("Log In");
        loginLabel.setFont(new Font("Serif", Font.BOLD, 50));
        titleLoginPanel.add(loginLabel);


        JPanel centerLoginPanel = new JPanel();
        JPanel usernameLoginPanel = new JPanel();
        JPanel passwordLoginPanel = new JPanel();
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(30);
        JLabel passwordLabel = new JLabel("Password:");
        JTextField passwordField = new JPasswordField(30);
        usernameLoginPanel.add(usernameLabel);
        usernameLoginPanel.add(usernameField);
        passwordLoginPanel.add(passwordLabel);
        passwordLoginPanel.add(passwordField);
        centerLoginPanel.add(usernameLoginPanel);
        centerLoginPanel.add(passwordLoginPanel);


        JPanel buttonLoginPanel = new JPanel();
        JButton login_btn = new JButton("Log in");
        JButton signup_btn = new JButton("Sign up");
        JButton exit_btn = new JButton("Exit");
        buttonLoginPanel.add(login_btn);
        buttonLoginPanel.add(signup_btn);
        buttonLoginPanel.add(exit_btn);

        loginPanel.add(titleLoginPanel);
        loginPanel.add(centerLoginPanel);
        loginPanel.add(buttonLoginPanel);


        login_btn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            try {
                login(username, password);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        signup_btn.addActionListener(actionEvent -> cardLayout.show(cardPanel, "signup"));

        exit_btn.addActionListener(e -> System.exit(0));


        return loginPanel;
    }
    public static void login(String username, String password) throws ClassNotFoundException {
        try{
            Socket loginSocket = new Socket("localhost", 3200);
            PrintWriter out = new PrintWriter(loginSocket.getOutputStream(), true);

            out.println("loginSocket");
            Thread.sleep(500);
            out.println(username + ":::" + password);
            Thread readerThread = new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(loginSocket.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("INVALID_ACCOUNT")){
                            JOptionPane.showMessageDialog(null, "Wrong Username or Password", "Error", JOptionPane.ERROR_MESSAGE);
                        } else if(line.contains("LOGIN_SUCCESS")){
                            createChatUI(line.split(":")[0]);
                            out.println("quit");
                            loginSocket.close();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            readerThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private static JPanel createSignupPanel() {
        JPanel signupPanel = new JPanel();

        GroupLayout layout = new GroupLayout(signupPanel);
        signupPanel.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel loginLabel = new JLabel("Sign Up");
        loginLabel.setFont(new Font("Serif", Font.BOLD, 50));

        JLabel clientNameLabel = new JLabel("Your client name:");
        JTextField clientNameField = new JTextField(30);
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(30);
        JLabel passwordLabel = new JLabel("Password:");
        JTextField passwordField = new JPasswordField(30);
        JLabel cfpwLabel = new JLabel("Confirm password:");
        JTextField cfpwField = new JPasswordField(30);

        JButton signup_btn = new JButton("Sign up");
        JButton exit_btn = new JButton("Exit");

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(loginLabel)
                .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup()
                                .addComponent(clientNameLabel)
                                .addComponent(usernameLabel)
                                .addComponent(passwordLabel)
                                .addComponent(cfpwLabel))
                        .addGroup(layout.createParallelGroup()
                                .addComponent(clientNameField)
                                .addComponent(usernameField)
                                .addComponent(passwordField)
                                .addComponent(cfpwField)))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(signup_btn)
                        .addComponent(exit_btn)));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(loginLabel)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(clientNameLabel)
                        .addComponent(clientNameField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(usernameLabel)
                        .addComponent(usernameField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(passwordLabel)
                        .addComponent(passwordField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(cfpwLabel)
                        .addComponent(cfpwField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(signup_btn)
                        .addComponent(exit_btn)));

        signup_btn.addActionListener(actionEvent -> {
            String clientName = clientNameField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();
            String cfpw = cfpwField.getText();
            if (clientName.isEmpty() || username.isEmpty() || password.isEmpty() || cfpw.isEmpty()) {
                // Hiển thị dialog thông báo lỗi
                JOptionPane.showMessageDialog(null, "Please fill in all the information.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                // Tiếp tục xử lý dữ liệu đăng ký
                signup(clientName, username, password, cfpw);
            }
        });

        exit_btn.addActionListener(actionEvent -> cardLayout.show(cardPanel, "login"));

        return signupPanel;
    }
    public static void signup(String name, String username, String password, String cfpw){
        if(password.equals(cfpw))
        {
            try{
                Socket signupSocket = new Socket("localhost", 3200);
                PrintWriter out = new PrintWriter(signupSocket.getOutputStream(), true);
                out.println("signupSocket");
                Thread.sleep(500);
                out.println(username + ":::" + password + ":::" + name);

                Thread readerThread = new Thread(() -> {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(signupSocket.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.contains("ACCOUNT_EXIST")){
                                JOptionPane.showMessageDialog(null, "Username already exists. Please choose other username.", "Error", JOptionPane.ERROR_MESSAGE);
                            } else if (line.contains("CLIENT_NAME_EXIST")) {
                                JOptionPane.showMessageDialog(null, "Client Name already exists. Please choose other Client Name.", "Error", JOptionPane.ERROR_MESSAGE);
                            }else if(line.contains("SIGNUP_SUCCESS")){
                                JOptionPane.showMessageDialog(null, "Account registration successful. ");
                                cardLayout.show(cardPanel, "login");
                                out.println("quit");
                                signupSocket.close();
                                Thread.sleep(500);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
                readerThread.start();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Confirm password must match the password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private static void createChatUI(String clientName) {

        // nếu chưa có thì tạo chatAreas
        if (chatAreas == null) {
            chatAreas = new HashMap<>();
        }
        if(groupChatAreas == null){
            groupChatAreas = new HashMap<>();
        }
        SwingUtilities.getWindowAncestor(cardPanel).dispose();
        JFrame frame = new JFrame("ChatApp");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // chat list
        JPanel listPanel = new JPanel(new BorderLayout());
        FlowLayout flowLayout = new FlowLayout();
        JPanel navBar = new JPanel(flowLayout);
        JButton privateChatButton = new JButton("1-on-1 Chat");
        JButton groupChatButton = new JButton("Group Chat");
        navBar.add(privateChatButton);
        navBar.add(groupChatButton);
        listPanel.add(navBar, BorderLayout.NORTH);

        DefaultListModel<String> privateChatListModel = new DefaultListModel<>();
        chatList = new JList<>(privateChatListModel);
        chatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        copyChatList = new JList<>(privateChatListModel);

        DefaultListModel<String> groupChatListModel = new DefaultListModel<>();
        groupChatList = new JList<>(groupChatListModel);
        groupChatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton addGroupButton = new JButton("Add Group");
        JButton addMemberButton = new JButton("Add Member");

        // Adjust JList
        chatList.setCellRenderer(new CustomJList.BorderedAndCenteredTextListCellRenderer());
        chatList.setFixedCellHeight(50);
        copyChatList.setCellRenderer(new CustomJList.BorderedAndCenteredTextListCellRenderer());
        copyChatList.setFixedCellHeight(50);
        groupChatList.setCellRenderer(new CustomJList.GroupChatListCellRender());
        groupChatList.setFixedCellHeight(50);


        // chat Panel
        JPanel chatPanel = new JPanel(new BorderLayout());

        JTextArea defaultTextArea = new JTextArea();
        defaultTextArea.setEditable(false);
        JScrollPane defaultScrollPane = new JScrollPane(defaultTextArea);

        JPanel inputPanel = new JPanel();
        messageField = new JTextField(30);
        JButton sendButton = new JButton("Send");
        JButton fileButton = new JButton("file");
        JButton sendGroupButton = new JButton("Send");
        JButton deleteChatButton = new JButton("Delete Chat");

        chatPanel.removeAll();
        String welcometo3CMessage = "<html><div style='text-align: center; margin-left: 80'><font face='serif' size='50'>Welcome to 3C Chat App<br>~ Chat Chu Connect ~</font></div></html>";
        JLabel slogan3CLabel = new JLabel(welcometo3CMessage);

        slogan3CLabel.setBackground(new Color(148, 187, 233, 100));  // Set your desired background color
        slogan3CLabel.setOpaque(true);

        chatPanel.add(slogan3CLabel, BorderLayout.CENTER);

        privateChatButton.addActionListener(e -> {
            chatList.clearSelection();
            listPanel.remove(groupChatList);
            listPanel.add(chatList, BorderLayout.CENTER);

            chatPanel.removeAll();
            String welcomeMessage = "<html><div style='text-align: center; margin-left: 80; font-family: serif; font-size: 30px;'>\"Ahh, you know ...\"<br>Welcome to Private Chat<br><span style='font-size: 20px; font-weight: bold;'> ~ Where Privacy Meets Connection ~</span></div></html>";
            JLabel sloganLabel = new JLabel(welcomeMessage);

            sloganLabel.setBackground(new Color(148, 187, 233, 100));  // Set your desired background color
            sloganLabel.setOpaque(true);

            chatPanel.add(sloganLabel, BorderLayout.CENTER);

            frame.revalidate();
            frame.repaint();
        });

        groupChatButton.addActionListener(e -> {
            groupChatList.clearSelection();
            listPanel.remove(chatList);
            listPanel.add(groupChatList, BorderLayout.CENTER);
            listPanel.add(addGroupButton, BorderLayout.SOUTH);

            chatPanel.removeAll();
            String welcomeMessage = "<html><div style='text-align: center; margin-left: 80'><font face='serif' size='50'>\"Hi! Can I join?\"<br>Welcome to Group Chat<br> ~ The more, the merrier ~</font></div></html>";
            JLabel sloganLabel = new JLabel(welcomeMessage);

            sloganLabel.setBackground(new Color(148, 187, 233, 100));  // Set your desired background color
            sloganLabel.setOpaque(true);

            chatPanel.add(sloganLabel, BorderLayout.CENTER);


            frame.revalidate();
            frame.repaint();
        });


        // Create split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listPanel, chatPanel);
        splitPane.setDividerLocation(220);

        frame.add(splitPane);
        frame.setVisible(true);


        try {
            // Kết nối đến server
            Socket socket = new Socket("localhost", 3200);

            //tạo luồng gửi dữ liệu lên server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(clientName);

            File file = new File(clientName + ".txt");
            if(file.exists()){
                friendsChatted = FileHandler.readFromFile(clientName + ".txt");
                if (friendsChatted == null) {
                    friendsChatted = new ArrayList<>();
                    FileHandler.writeToFile(clientName + ".txt", null);
                }
            } else {
                friendsChatted = new ArrayList<>();
                FileHandler.writeToFile(clientName + ".txt", null);
            }

            File groupFile = new File("groupsName_" + clientName + ".txt");
            if(file.exists()){
                groupChatted = FileHandler.readFromFile("groupsName_" + clientName + ".txt");
                if (groupChatted == null) {
                    groupChatted = new ArrayList<>();
                    FileHandler.writeToFile("groupsName_" + clientName + ".txt", null);
                }
            } else {
                groupChatted = new ArrayList<>();
                FileHandler.writeToFile("groupsName_" + clientName + ".txt", null);
            }

            initFriendList(clientName);
            initChatAreas();
            reloadChatHistory(clientName);

            initGroupChatList();
            initGroupChatAreas(clientName);

            // Tạo một thread để đọc và in tin nhắn từ server
            Thread readerThread = new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.equals("UPDATE_CHAT_LIST")) {

                            String clientListString = reader.readLine();
                            updateChatAreas(clientListString);
                            updateChatList(clientListString);
                        } else if (line.startsWith("SEND_FILE_ANNOUNCEMENT")) {

                            String[] parts = line.split(";");
                            String sender = parts[1];
                            String fileName = parts[2];
                            // Thông báo cho người nhận rằng có file đến
                            int choice = JOptionPane.showConfirmDialog(null, sender + " sent you a file: " + fileName + ". Do you want to save it?", "Confirm Save File", JOptionPane.YES_NO_OPTION);

                            if (choice == JOptionPane.YES_OPTION) {
                                JFileChooser fileChooser = new JFileChooser();
                                fileChooser.setDialogTitle("Save File: " + fileName);

                                File suggestedFile = new File(fileName);
                                fileChooser.setSelectedFile(suggestedFile);

                                int userSelection = fileChooser.showSaveDialog(null);

                                if (userSelection == JFileChooser.APPROVE_OPTION) {
                                    File selectedFile = fileChooser.getSelectedFile();
                                    try (BufferedOutputStream fileOutputStream = new BufferedOutputStream(new FileOutputStream(selectedFile))) {
                                        int count;
                                        byte[] buffer = new byte[1024];
                                        InputStream inputStream = socket.getInputStream();
                                        while ((count = inputStream.read(buffer)) > 0) {
                                            fileOutputStream.write(buffer, 0, count);
                                            fileOutputStream.flush();
                                        }

                                        // Thông báo rằng file đã được lưu xuống
                                        JOptionPane.showMessageDialog(null, "File saved successfully: " + selectedFile.getAbsolutePath(), "File Saved", JOptionPane.INFORMATION_MESSAGE);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } else if (line.contains("CREATE_GROUP_SUCCESS")) {
                            String[] parts = line.split(":");
                            if(parts.length == 2){
                                String groupName = parts[0];
                                JOptionPane.showMessageDialog(frame, "Group created: " + groupName);
                                updateGroupChatAreas(groupName);
                                updateGroupChatList(groupName);
                            }
                        }else if (line.contains("CREATE_GROUP_FAIL")) {
                            String[] parts = line.split(":");
                            if(parts.length == 2){
                                String groupName = parts[0];
                                JOptionPane.showMessageDialog(null, "Group name: ' " + groupName  + " ' has already existed ! Please try other groupName", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }else if (line.contains("ADD_MEMBER_SUCCESS")) {
                            String[] parts = line.split(":");
                            if(parts.length == 2){
                                String member = parts[0];
                                JOptionPane.showMessageDialog(null, "Client: '" +  member + "' has been added to " + selectedGroup );
                                int numberOfMembers = FileHandler.readFromFile(selectedGroup + ".txt").size();
                                groupNameLabel.setText("<html><left>" + selectedGroup + "<br><font color='gray'>" + numberOfMembers + " members" + "</font></left></html>");
                            }
                        }else if (line.contains("ADD_MEMBER_FAIL")) {
                            String[] parts = line.split(":");
                            if(parts.length == 2){
                                String existMember = parts[0];
                                JOptionPane.showMessageDialog(null, "Client: ' " + existMember  + " ' has already existed in this group !", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }else if (line.contains("UPDATE_GROUP_CHAT_LIST")){
                            String[] parts = line.split(":");
                            if(parts.length == 2){
                                String groupName = parts[0];
                                updateGroupChatList(groupName);
                                updateGroupChatAreas(groupName);
                            }
                        }else if (line.startsWith("MESSAGE_FROM_GROUP")) {
                            String[] parts = line.split(": ");
                            if(parts.length == 4){
                                JTextArea recipientTextArea = groupChatAreas.get(parts[1]);
                                if (recipientTextArea != null) {
                                    SwingUtilities.invokeLater(() -> recipientTextArea.append(parts[2] + ": " + parts[3] + "\n"));
                                }
                            }
                        }else {
                            String finalLine = line;
                            String[] receivedMessage = line.split(": ");
                            if(addFriend(receivedMessage[0], clientName) == 1){
                                FileHandler.writeToFile(clientName + ".txt", receivedMessage[0]);
                            }
                            JTextArea recipientTextArea = chatAreas.get(receivedMessage[0]);
                            if (recipientTextArea != null) {
                                SwingUtilities.invokeLater(() -> recipientTextArea.append(finalLine + "\n"));
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            readerThread.start();

            chatList.addListSelectionListener(e -> {
                String preSelectedFriend = selectedFriend;

                selectedFriend = chatList.getSelectedValue();

                if(selectedFriend == null) {
                    selectedFriend = preSelectedFriend;
                }

                JTextArea selectedFriendTextArea = chatAreas.get(selectedFriend);
                int status = updateStatusChatList(selectedFriend);
                JScrollPane scrollPane = new JScrollPane(selectedFriendTextArea);

                chatPanel.removeAll();

                JPanel statusBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

                JLabel statusBar = new JLabel();
                if(status == 1){
                    statusBar.setText("<html><left>" + selectedFriend + "<br><font color='green'>Online</font></left></html>");
                    fileButton.setEnabled(true);
                } else {
                    statusBar.setText("<html><left>" + selectedFriend + "<br><font color='gray'>Offline</font></left></html>");
                    fileButton.setEnabled(false);
                }

                statusBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 410));

                statusBarPanel.add(statusBar);
                statusBarPanel.add(deleteChatButton);


                JPanel statusBar_scrollPane = new JPanel(new BorderLayout());
                statusBar_scrollPane.add(statusBarPanel, BorderLayout.NORTH);
                statusBar_scrollPane.add(scrollPane, BorderLayout.CENTER);
                inputPanel.removeAll();
                inputPanel.add(messageField);
                inputPanel.add(fileButton);
                inputPanel.add(sendButton);
                chatPanel.add(statusBar_scrollPane, BorderLayout.CENTER);
                chatPanel.add(inputPanel, BorderLayout.SOUTH);
                // Cập nhật giao diện
                chatPanel.revalidate();
                chatPanel.repaint();
            });

            groupChatList.addListSelectionListener(e ->{
                String preSelectedGroup = selectedGroup;

                selectedGroup = groupChatList.getSelectedValue();

                if(selectedGroup == null){
                    selectedGroup = preSelectedGroup;
                }

                JTextArea selectedGroupTextArea = groupChatAreas.get(selectedGroup);
                JScrollPane scrollPane = new JScrollPane(selectedGroupTextArea);

                chatPanel.removeAll();
                JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));

                int numberOfMembers = FileHandler.readFromFile(selectedGroup + ".txt").size();
                groupNameLabel = new JLabel();
                if(numberOfMembers > 1){
                    groupNameLabel.setText("<html><left>" + selectedGroup + "<br><font color='gray'>" + numberOfMembers + " members" + "</font></left></html>");
                } else {
                    groupNameLabel.setText("<html><left>" + selectedGroup + "<br><font color='gray'>" + numberOfMembers + " member" + "</font></left></html>");
                }

                groupNameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 383));


                statusBar.add(groupNameLabel);
                statusBar.add(addMemberButton);


                JPanel statusBar_scrollPane = new JPanel(new BorderLayout());
                statusBar_scrollPane.add(statusBar, BorderLayout.NORTH);
                statusBar_scrollPane.add(scrollPane, BorderLayout.CENTER);

                inputPanel.removeAll();
                inputPanel.add(messageField);
                inputPanel.add(sendGroupButton);
                chatPanel.add(statusBar_scrollPane, BorderLayout.CENTER);
                chatPanel.add(inputPanel, BorderLayout.SOUTH);


                // Cập nhật giao diện
                chatPanel.revalidate();
                chatPanel.repaint();
            });

            addMemberButton.addActionListener(actionEvent -> {
                JFrame addMemberFrame = new JFrame("Add Member");
                addMemberFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                addMemberFrame.setSize(220,400);

                addMemberFrame.setLayout(new BorderLayout());
                copyChatList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                JScrollPane scrollPane = new JScrollPane(copyChatList);
                JPanel buttonPanel = new JPanel();

                JButton addButton = new JButton("Add");
                JButton cancelButton = new JButton("Cancel");


                Dimension buttonSize = new Dimension(100, 30);
                addButton.setPreferredSize(buttonSize);
                cancelButton.setPreferredSize(buttonSize);

                addButton.setEnabled(copyChatList.getSelectedValue() != null);


                buttonPanel.add(addButton);
                buttonPanel.add(cancelButton);

                addMemberFrame.add(scrollPane, BorderLayout.CENTER);
                addMemberFrame.add(buttonPanel, BorderLayout.SOUTH);

                cancelButton.addActionListener(e -> {
                    copyChatList.clearSelection();
                    addMemberFrame.dispose();
                });

                copyChatList.addListSelectionListener(e -> {
                    addButton.setEnabled(true);
                });

                addButton.addActionListener(e -> {
                    List<String> selectedValues = copyChatList.getSelectedValuesList();
                    StringBuilder friendAdded = new StringBuilder();
                    for(String selectedValue : selectedValues){
                        friendAdded.append(selectedValue).append(",");
                    }
                    out.println("ADD_MEMBER;" + friendAdded.toString() + ";" + selectedGroup + ";" + clientName);
                    copyChatList.clearSelection();
                    addMemberFrame.dispose();
                });

                addMemberFrame.setVisible(true);
            });

            addGroupButton.addActionListener(e -> {

                JDialog addGroupDialog = new JDialog(frame, "Create Group Chat", true);
                addGroupDialog.setSize(200,200);
                addGroupDialog.setLayout(new BorderLayout());

                JLabel groupNameLabel = new JLabel("Enter your group name: ");
                JTextField groupNameField = new JTextField(20);

                JButton createButton = new JButton("Create");
                JButton cancelButton = new JButton("Cancel");

                createButton.addActionListener(createEvent -> {

                    String groupName = groupNameField.getText();
                    if (!groupName.trim().isEmpty()) {
                        out.println("CREATE_GROUP_CHAT;" + groupName + ";" + clientName);
                        addGroupDialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Please enter a valid group name.");
                    }
                });

                cancelButton.addActionListener(cancelEvent -> addGroupDialog.dispose());

                JPanel userInputPanel = new JPanel(new FlowLayout());
                userInputPanel.add(groupNameLabel);
                userInputPanel.add(groupNameField);

                JPanel buttonPanel = new JPanel(new FlowLayout());
                buttonPanel.add(createButton);
                buttonPanel.add(cancelButton);


                addGroupDialog.add(userInputPanel, BorderLayout.CENTER);
                addGroupDialog.add(buttonPanel, BorderLayout.SOUTH);

                addGroupDialog.setSize(300, 150);
                addGroupDialog.setLocationRelativeTo(frame);
                addGroupDialog.setVisible(true);
            });

            sendButton.addActionListener(actionEvent -> {
                String message = messageField.getText();

                if(chatAreas.get(selectedFriend) != null) {
                    out.println(selectedFriend + ";;;" + message);
                    chatAreas.get(selectedFriend).append("You: " + message + "\n");
                    messageField.setText("");
                    if (addFriend(selectedFriend, clientName) == 1) {
                        FileHandler.writeToFile(clientName + ".txt", selectedFriend);
                        File friendFile = new File(selectedFriend + ".txt");
                        if (friendFile.exists()) {
                            List<String> friendFriend = FileHandler.readFromFile(selectedFriend + ".txt");
                            if (friendFriend == null) {
                                FileHandler.writeToFile(selectedFileName + ".txt", clientName);
                            } else {
                                int count = 0;
                                for (String friend : friendFriend) {
                                    if (friend.equals(clientName)) {
                                        count++;
                                    }
                                }
                                if (count == 0) {
                                    FileHandler.writeToFile(selectedFriend + ".txt", clientName);
                                }
                            }
                        } else {
                            friendsChatted = new ArrayList<>();
                            FileHandler.writeToFile(clientName + ".txt", null);

                        }
                    }
                }
            });

            sendGroupButton.addActionListener(e -> {
                String message = messageField.getText();
                groupChatAreas.get(selectedGroup).append(clientName + ": " + message + "\n");
                out.println("CLIENT_CHAT_TO_GROUP;" + message + ";" + selectedGroup + ";" + clientName);
                messageField.setText("");
            });

            fileButton.addActionListener(actionEvent -> {
                JFrame sendFileFrame = new JFrame("Send File");
                sendFileFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                sendFileFrame.setSize(400,400);
                sendFileFrame.setLayout(new GridLayout(3,1));

                JPanel chooseFilePanel = new JPanel(new FlowLayout());
                JLabel chooseFileLabel = new JLabel("Choose a file");
                chooseFilePanel.add(chooseFileLabel);

                JPanel fileChosenPanel = new JPanel(new FlowLayout());
                JLabel fileChosenLabel = new JLabel("Selected file: ");
                fileChosenPanel.add(fileChosenLabel);

                JButton chooseFileBtn = new JButton("Choose File");
                JButton sendFileBtn = new JButton("Send File");
                JPanel btnPanel = new JPanel(new FlowLayout());
                btnPanel.add(chooseFileBtn);
                btnPanel.add(sendFileBtn);

                sendFileFrame.add(chooseFilePanel);
                sendFileFrame.add(fileChosenPanel);
                sendFileFrame.add(btnPanel);
                sendFileFrame.setVisible(true);

                chooseFileBtn.addActionListener(actionEvent1 -> {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
                    int result = fileChooser.showOpenDialog(frame);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        selectedFileName = fileChooser.getSelectedFile().getName();
                        fileChosenLabel.setText("Selected File: " + selectedFileName);
                    }
                });
                sendFileBtn.addActionListener(actionEvent12 -> {
                    String selectedFilePath = selectedFileName;

                    if (selectedFilePath != null && !selectedFilePath.isEmpty()) {
                        File selectedFile = new File(selectedFilePath);

                        try {
                            // Mở luồng để gửi yêu cầu gửi file đến server
                            out.println("SEND_FILE_ANNOUNCEMENT;" + selectedFriend + ";" + selectedFile.getName());

                            // Mở luồng để đọc và gửi dữ liệu file đến server
                            try (BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(selectedFile))) {
                                int count;
                                byte[] buffer = new byte[1024];
                                OutputStream socketOutputStream = socket.getOutputStream();
                                while ((count = fileInputStream.read(buffer)) > 0) {
                                    socketOutputStream.write(buffer, 0, count);
                                    socketOutputStream.flush();
                                }
                            }
                            // Thông báo rằng file đã được gửi đi
                            JOptionPane.showMessageDialog(frame, "File sent successfully: " + selectedFileName, "File Sent", JOptionPane.INFORMATION_MESSAGE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        sendFileFrame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Please choose a file before sending.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });


            });

            deleteChatButton.addActionListener(e -> {
                int dialogResult = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this chat?", "Confirmation", JOptionPane.YES_NO_OPTION);

                if (dialogResult == JOptionPane.YES_OPTION) {
                    out.println("DELETE_CHAT_HISTORY;" + selectedFriend + ";" + clientName);
                    chatAreas.get(selectedFriend).setText("");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Chat App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);

        addComponents(frame.getContentPane());

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client2::createAndShowGUI);
    }
}

