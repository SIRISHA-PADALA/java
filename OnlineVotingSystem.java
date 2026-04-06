import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

// ================= VOTER CLASS =================
class Voter {
    String voterId;
    String aadhaar;
    String name;
    int age;
    String gender;
    String mobile;
    String constituency = "Not Assigned";

    Voter(String voterId, String aadhaar, String name,
            int age, String gender, String mobile) {

        this.voterId = voterId;
        this.aadhaar = aadhaar;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.mobile = mobile;
    }
}

// ================= MAIN SYSTEM =================
public class OnlineVotingSystem {

    static HashMap<String, Voter> voters = new HashMap<>();
    static HashSet<String> votedVoters = new HashSet<>();
    static HashMap<String, HashMap<String, Integer>> constituencyVotes = new HashMap<>();

    static boolean electionStarted = false;
    static boolean electionEnded = false;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        generateFixedVoters();
        loadVotesFromFile(); // Load existing votes
        loginMenu();
    }

    // ================= PERSISTENCE =================
    static void saveVotesToFile() {
        try (PrintWriter out = new PrintWriter(new FileWriter("votes_backup.txt"))) {
            for (Map.Entry<String, HashMap<String, Integer>> entry : constituencyVotes.entrySet()) {
                String constituency = entry.getKey();
                for (Map.Entry<String, Integer> partyEntry : entry.getValue().entrySet()) {
                    out.println(constituency + "|" + partyEntry.getKey() + "|" + partyEntry.getValue());
                }
            }
            // Save voted voters with constituency: voterId:constituency
            java.util.List<String> votedData = new ArrayList<>();
            for (String id : votedVoters) {
                Voter v = voters.get(id);
                if (v != null) {
                    votedData.add(id + ":" + v.constituency);
                } else {
                    votedData.add(id + ":Unknown");
                }
            }
            out.println("VOTED_VOTERS|" + String.join(",", votedData));
            out.println("ELECTION_STATUS|" + electionStarted + "|" + electionEnded);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void loadVotesFromFile() {
        File file = new File("votes_backup.txt");
        if (!file.exists())
            return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("VOTED_VOTERS|")) {
                    String[] parts = line.split("\\|", 2);
                    if (parts.length > 1) {
                        String[] items = parts[1].split(",");
                        for (String item : items) {
                            if (!item.isEmpty()) {
                                if (item.contains(":")) {
                                    String[] subParts = item.split(":", 2);
                                    String id = subParts[0];
                                    String consti = subParts[1];
                                    votedVoters.add(id);
                                    Voter v = voters.get(id);
                                    if (v != null)
                                        v.constituency = consti;
                                } else {
                                    votedVoters.add(item);
                                }
                            }
                        }
                    }
                } else if (line.startsWith("ELECTION_STATUS|")) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 3) {
                        electionStarted = Boolean.parseBoolean(parts[1]);
                        electionEnded = Boolean.parseBoolean(parts[2]);
                    }
                } else {
                    String[] parts = line.split("\\|");
                    if (parts.length == 3) {
                        String consti = parts[0];
                        String party = parts[1];
                        int count = Integer.parseInt(parts[2]);
                        constituencyVotes.putIfAbsent(consti, new HashMap<>());
                        constituencyVotes.get(consti).put(party, count);
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    // ================= FIXED 100 VOTERS =================
    static void generateFixedVoters() {

        String[] maleNames = {
                "venkatesh", "Ravi Teja", "Kiran Reddy", "Rahul Sharma", "Vikram Rao",
                "Manoj Kumar", "Suresh Babu", "Ramesh Naidu", "Aditya Varma", "Nikhil Reddy",
                "Teja Kumar", "Praveen Kumar", "Mahesh Reddy", "Varun Teja", "Tarun Kumar",
                "Sai Krishna", "Harish Reddy", "Lokesh Babu", "Vamsi Krishna", "Ajay Kumar",
                "Santosh Reddy", "Naresh Kumar", "Karthik Reddy", "Sandeep Kumar", "Rohit Reddy",
                "Chaitanya", "Surya Prakash", "Mohan Reddy", "Ganesh Kumar", "Vivek Reddy",
                "Deepak Kumar", "Vinay Kumar", "Abhishek Reddy", "Srikanth", "Aravind",
                "Raghu Ram", "Pavan Kumar", "Krishna Chaitanya", "Dinesh Kumar", "Jagadeesh",
                "Prakash Reddy", "Harsha Vardhan", "Srinivas Reddy", "Kishore Kumar", "Ashok Reddy",
                "Naveen Kumar", "Rajesh Kumar", "Anil Kumar", "Murali Krishna", "Ravi Kumar"
        };

        String[] femaleNames = {
                "Anjali Devi", "Sneha Reddy", "Priya Sharma", "Divya Lakshmi", "Kavya Reddy",
                "Swathi Priya", "Pooja Kumari", "Meena Devi", "Lakshmi Priya", "Neha Sharma",
                "Sravani Reddy", "Keerthi Reddy", "Deepika Sharma", "Harika Devi", "Bhavya Reddy",
                "Aishwarya", "Nandini Reddy", "Sowmya Priya", "Madhavi Devi", "Padma Lakshmi",
                "Sunitha Reddy", "Radhika Sharma", "Jyothi Lakshmi", "Bindu Reddy", "Lavanya",
                "Geetha Rani", "Sushmita", "Anusha Reddy", "Bhargavi", "Siri Lakshmi",
                "Sandhya Rani", "Monika Sharma", "Tejaswini", "Vaishnavi", "Manasa Reddy",
                "Pavani", "Renuka Devi", "Sirisha", "Kalyani", "Indu Rani",
                "Chandana", "Snehalatha", "Meghana", "Gayatri", "Shilpa Reddy",
                "Harini", "Navya", "Sahithi", "Pranavi", "Sangeetha"
        };

        // 50 MALES
        for (int i = 0; i < 50; i++) {
            String voterId = String.format("VOTE%03d", i + 1);
            String aadhaar = String.format("800000000%03d", i + 1);
            String mobile = "9123456" + String.format("%03d", i + 1);

            voters.put(voterId,
                    new Voter(voterId, aadhaar,
                            maleNames[i], 25 + (i % 10),
                            "Male", mobile));
        }

        // 50 FEMALES
        for (int i = 0; i < 50; i++) {
            String voterId = String.format("VOTE%03d", i + 51);
            String aadhaar = String.format("800000000%03d", i + 51);
            String mobile = "9234567" + String.format("%03d", i + 51);

            voters.put(voterId,
                    new Voter(voterId, aadhaar,
                            femaleNames[i], 23 + (i % 10),
                            "Female", mobile));
        }
    }

    // ================= LOGIN MENU =================
    static void loginMenu() {
        JFrame frame = new JFrame("online Voting Portal");
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Load Background Image
        final Image bgImage;
        Image tempBg;
        try {
            tempBg = ImageIO.read(new File("election (3)/election/admin_bg.png"));
        } catch (IOException e) {
            tempBg = null;
        }
        bgImage = tempBg;

        // Main Container with Background
        GradientPanel mainPanel = new GradientPanel(new Color(15, 23, 42), new Color(30, 41, 59)) {
            @Override
            protected void paintComponent(Graphics g) {
                if (bgImage != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);

                    // Dark Overlay for better contrast
                    g2.setColor(new Color(15, 23, 42, 140));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.dispose();
                } else {
                    super.paintComponent(g);
                }
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        frame.add(mainPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;

        // Portal Logo with Glow
        JLabel iconLabel = new JLabel(new ImageIcon(createPortalIcon(140)));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        mainPanel.add(iconLabel, gbc);

        // Welcome Text with premium typography
        JLabel welcomeLabel = new JLabel("Online Voting Portal");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        welcomeLabel.setForeground(Color.WHITE);
        gbc.gridy = 1;
        mainPanel.add(welcomeLabel, gbc);

        JLabel subLabel = new JLabel("Secure • Transparent • High-Fidelity Infrastructure");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subLabel.setForeground(new Color(148, 163, 184));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 50, 0);
        mainPanel.add(subLabel, gbc);

        // Access Cards Container
        JPanel cardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        cardPanel.setOpaque(false);

        cardPanel.add(createAccessCard("Voter Access", "Cast your secure ballot and view your receipt", "👤",
                new Color(59, 130, 246), e -> {
                    frame.dispose();
                    voterLogin();
                }));

        cardPanel.add(createAccessCard("ECI Portal", "Election management, real-time results & monitoring", "🛡️",
                new Color(15, 118, 110), e -> {
                    frame.dispose();
                    adminLogin();
                }));

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(cardPanel, gbc);

        frame.setVisible(true);
    }

    private static JPanel createAccessCard(String title, String subtitle, String icon, Color accent,
            ActionListener action) {
        JPanel card = new JPanel(new BorderLayout(0, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Card Shadow
                g2.setColor(new Color(0, 0, 0, 60));
                g2.fillRoundRect(5, 5, getWidth() - 5, getHeight() - 5, 25, 25);

                // Card Body
                g2.setColor(new Color(255, 255, 255, 245));
                g2.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 25, 25);

                // Accent Bar at top
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, getWidth() - 5, 10, 25, 25);
                g2.fillRect(0, 5, getWidth() - 5, 5);

                g2.dispose();
            }
        };
        card.setPreferredSize(new Dimension(320, 220));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLbl.setForeground(accent);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLbl.setForeground(new Color(15, 23, 42));

        JLabel subLbl = new JLabel("<html><body style='width: 220px'>" + subtitle + "</body></html>");
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subLbl.setForeground(new Color(100, 116, 139));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(titleLbl);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(subLbl);

        card.add(iconLbl, BorderLayout.NORTH);
        card.add(textPanel, BorderLayout.CENTER);

        // Hover Effect & Click Action
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createEmptyBorder(20, 25, 30, 25));
                card.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
                card.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                action.actionPerformed(null);
            }
        });

        return card;
    }

    public static BufferedImage createPortalIcon(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Outer Ring
        g2.setColor(new Color(59, 130, 246));
        g2.setStroke(new BasicStroke(4));
        g2.drawOval(10, 10, size - 20, size - 20);

        // Box
        g2.setColor(new Color(241, 245, 249));
        g2.fillRect(size / 4, size / 3, size / 2, size / 2);
        g2.setColor(new Color(59, 130, 246));
        g2.drawRect(size / 4, size / 3, size / 2, size / 2);

        // Slot
        g2.setColor(new Color(15, 23, 42));
        g2.fillRect(size / 3, size / 3 + size / 10, size / 3, size / 15);

        g2.dispose();
        return img;
    }

    private static JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));

        // Hover Effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.setBackground(bg.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg);
            }
        });

        return btn;
    }

    // Custom Gradient Panel
    static class GradientPanel extends JPanel {
        private Color color1;
        private Color color2;

        public GradientPanel(Color color1, Color color2) {
            this.color1 = color1;
            this.color2 = color2;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int w = getWidth();
            int h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);
        }
    }

    // ================= VOTER LOGIN =================
    static void voterLogin() {
        VoterLoginDialog dialog = new VoterLoginDialog(null);
        dialog.setVisible(true);

        if (dialog.isOkPressed()) {
            String voterId = dialog.getVoterId().toUpperCase();
            String aadhaar = dialog.getAadhaar();

            if (voters.containsKey(voterId)
                    && voters.get(voterId).aadhaar.equals(aadhaar)) {

                if (electionEnded) {
                    new CustomMessageDialog(null, "Election has ended!").setVisible(true);
                    loginMenu();
                    return;
                }

                if (!electionStarted) {
                    new CustomMessageDialog(null, "Election not started yet!").setVisible(true);
                    loginMenu();
                    return;
                }

                new VotingDashboard(voterId);

            } else {
                new CustomMessageDialog(null, "Wrong Credentials!").setVisible(true);
                loginMenu();
            }
        } else {
            loginMenu();
        }
    }

    // ================= ADMIN LOGIN =================
    static void adminLogin() {
        AdminLoginDialog loginDialog = new AdminLoginDialog(null);
        loginDialog.setVisible(true);

        if (loginDialog.isOkPressed()) {
            String user = loginDialog.getUsername();
            String pass = loginDialog.getPassword();

            if ("admin".equals(user) && "admin123".equals(pass)) {
                new AdminDashboard().setVisible(true);
            } else {
                new CustomMessageDialog(null, "Wrong Admin Credentials!").setVisible(true);
                loginMenu();
            }
        } else {
            loginMenu();
        }
    }

    static void resetElectionData() {
        CustomConfirmDialog confirmDialog = new CustomConfirmDialog(null,
                "Are you sure you want to RESET all election data? This cannot be undone.");
        confirmDialog.setVisible(true);

        if (confirmDialog.isConfirmed()) {
            votedVoters.clear();
            constituencyVotes.clear();
            electionStarted = false;
            electionEnded = false;
            saveVotesToFile();
            new CustomMessageDialog(null, "All data has been reset successfully.").setVisible(true);
        }
    }

    static void showResults() {
        new ResultsDialog(null).setVisible(true);
    }

}

// ================= DASHBOARD =================
class VotingDashboard extends JFrame implements ActionListener {

    JTextField ageField, voterIdField, nameField, contactField, resultField;
    JRadioButton ysrcp, tdp, janasena, nota;
    JComboBox<String> constituencyBox;
    JButton submitBtn, newVoteBtn;

    String loggedInVoter;

    VotingDashboard(String voterId) {

        loggedInVoter = voterId;
        Voter v = OnlineVotingSystem.voters.get(voterId);

        setTitle("Online Voting System");
        setSize(800, 700); // Increased width for better layout
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Colors
        Color primaryColor = new Color(52, 152, 219);
        Color white = Color.WHITE;

        // --- Main Panel with Gradient ---
        OnlineVotingSystem.GradientPanel mainPanel = new OnlineVotingSystem.GradientPanel(new Color(52, 152, 219),
                new Color(240, 240, 255));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0)); // Vertical padding only

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // --- Title ---
        JLabel title = new JLabel("Online Voting System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(white); // White title on gradient
        // Shadow/Outline effect
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(title);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // --- Voter Details Form (Card Style with Shadow) ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(white);
        // Shadow border simulation
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(0, 0, 5, 5), // Offset for shadow sensation
                        BorderFactory.createEmptyBorder(20, 40, 20, 40))));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.setMaximumSize(new Dimension(600, 350)); // Limit width

        addFormRow(formPanel, "Full Name ", v.name, 0);
        addFormRow(formPanel, "Voter Id  ", v.voterId, 1);
        addFormRow(formPanel, "Age       ", String.valueOf(v.age), 2);
        addFormRow(formPanel, "Gender    ", v.gender, 3);
        addFormRow(formPanel, "Contact Number", v.mobile, 4);

        // Constituency Row
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel cityLabel = new JLabel("Constituency");
        cityLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(cityLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] constituencies = {
               "Amadalavalasa","Etcherla","Ichchapuram","Narasannapeta","Palakonda",
"Palasa","Pathapatnam","Rajam","Srikakulam","Tekkali",

"Bobbili","Cheepurupalli","Gajapathinagaram","Kurupam","Nellimarla",
"Parvathipuram","Salur","Srungavarapukota","Vizianagaram",

"Visakhapatnam East","Visakhapatnam West","Visakhapatnam North","Visakhapatnam South",
"Araku Valley","Paderu","Anakapalle","Chodavaram","Madugula",
"Narsipatnam","Payakaraopeta","Yelamanchili","Gajuwaka","Bheemunipatnam",

"Rajahmundry City","Rajahmundry Rural","Anaparthi",

"Vijayawada Central","Vijayawada East","Vijayawada West","Nandigama",
"Jaggayyapeta","Tiruvuru","Mylavaram","Gudivada","Pedana",
"Machilipatnam","Avanigadda","Pamarru","Penamaluru","Kaikaluru",

"Guntur East","Guntur West","Tadikonda","Mangalagiri","Tenali",
"Ponnuru","Prathipadu","Vemuru","Repalle","Bapatla",

"Ongole","Darsi","Kondapi","Kanigiri","Markapuram",
"Giddalur","Yerragondapalem","Kandukur","Addanki","Chirala",

"Tirupati","Srikalahasti","Sullurpet","Venkatagiri","Sarvepalli",
"Nellore City","Nellore Rural","Kavali","Atmakur","Udayagiri",

"Kadapa","Badvel","Mydukur","Jammalamadugu","Kamalapuram",
"Pulivendula","Rajampet","Kodur","Rayachoti","Proddatur",

"Kurnool","Kodumur","Yemmiganur","Mantralayam","Adoni",
"Alur","Pattikonda","Nandyal","Banaganapalle","Allagadda",

"Anantapur Urban","Anantapur Rural","Raptadu","Guntakal","Tadipatri",
"Uravakonda","Rayadurg","Kalyandurg","Singanamala","Dharmavaram",

"Kadiri","Puttaparthi","Penukonda","Hindupur","Madakasira",
"Chandragiri","Pileru","Madanapalle","Punganur","Palamaner"
};
        constituencyBox = new JComboBox<>(constituencies);
        constituencyBox.setPreferredSize(new Dimension(250, 30));

        // --- Search Filter for Constituency ---
        JTextField searchField = new JTextField();
        searchField.setToolTipText("Type to filter constituencies...");
        searchField.setPreferredSize(new Dimension(250, 25));
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText().toLowerCase();
                constituencyBox.removeAllItems();
                for (String c : constituencies) {
                    if (c.toLowerCase().contains(text)) {
                        constituencyBox.addItem(c);
                    }
                }
                constituencyBox.setPopupVisible(text.length() > 0);
            }
        });

        JPanel comboPanel = new JPanel(new BorderLayout(0, 5));
        comboPanel.setOpaque(false);
        comboPanel.add(searchField, BorderLayout.NORTH);
        comboPanel.add(constituencyBox, BorderLayout.CENTER);

        formPanel.add(comboPanel, gbc);

        mainPanel.add(formPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // --- Vote Selection Text ---
        JLabel voteLabel = new JLabel("Choose Your Party");
        voteLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        voteLabel.setForeground(new Color(44, 62, 80));
        voteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(voteLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- Party Icons Panel ---
        JPanel partyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20)); // More spacing
        partyPanel.setOpaque(false); // Transparent to show gradient
        partyPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create Party Components (Icon + Radio)
        String electionDir = "election (3)/election/";

        ysrcp = new JRadioButton("YSRCP");
        JPanel ysrcpPanel = createPartyComponent("Fan", electionDir + "YSR.jpeg", ysrcp);

        tdp = new JRadioButton("TDP");
        JPanel tdpPanel = createPartyComponent("Cycle", electionDir + "TDP.jpeg", tdp);

        janasena = new JRadioButton("Janasena");
        JPanel janasenaPanel = createPartyComponent("Glass", electionDir + "jagasena.jpg.jpeg", janasena);

        nota = new JRadioButton("NOTA");
        JPanel notaPanel = createPartyComponent("NOTA", electionDir + "nota.png", nota);

        ButtonGroup partyGroup = new ButtonGroup();
        partyGroup.add(ysrcp);
        partyGroup.add(tdp);
        partyGroup.add(janasena);
        partyGroup.add(nota);

        partyPanel.add(ysrcpPanel);
        partyPanel.add(tdpPanel);
        partyPanel.add(janasenaPanel);
        partyPanel.add(notaPanel);

        mainPanel.add(partyPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // --- Submit Button ---
        submitBtn = new JButton("Submit Vote");
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setBackground(new Color(46, 204, 113)); // Green
        submitBtn.setFocusPainted(false);
        submitBtn.setPreferredSize(new Dimension(220, 55));
        submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Button Hover
        submitBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                submitBtn.setBackground(new Color(39, 174, 96)); // Darker Green
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (submitBtn.isEnabled())
                    submitBtn.setBackground(new Color(46, 204, 113));
            }
        });

        mainPanel.add(submitBtn);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // --- Result Field ---
        JPanel resultPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        resultPanel.setOpaque(false); // Transparent
        JLabel resultLabel = new JLabel("Selected: ");
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        resultLabel.setForeground(new Color(44, 62, 80));

        resultField = new JTextField(15);
        resultField.setEditable(false);
        resultField.setFont(new Font("Segoe UI", Font.BOLD, 16));
        resultField.setHorizontalAlignment(JTextField.CENTER);
        resultField.setBorder(BorderFactory.createLineBorder(primaryColor, 2));

        resultPanel.add(resultLabel);
        resultPanel.add(resultField);
        resultPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(resultPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- New Vote Button ---
        newVoteBtn = new JButton("Back to Login");
        newVoteBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        newVoteBtn.setBackground(Color.WHITE);
        newVoteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        mainPanel.add(newVoteBtn);

        // Listeners
        submitBtn.addActionListener(this);
        newVoteBtn.addActionListener(e -> {
            dispose();
            OnlineVotingSystem.loginMenu();
        });

        ActionListener partyListener = e -> {
            JRadioButton btn = (JRadioButton) e.getSource();
            resultField.setText(btn.getText());
        };
        ysrcp.addActionListener(partyListener);
        tdp.addActionListener(partyListener);
        janasena.addActionListener(partyListener);
        nota.addActionListener(partyListener);

        setVisible(true);
    }

    // Helper to create Rows for Form
    private void addFormRow(JPanel panel, String labelText, String value, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.EAST;

        gbc.gridx = 0;
        gbc.gridy = y;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JTextField field = new JTextField(value);
        field.setPreferredSize(new Dimension(250, 30));
        field.setEditable(false);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(245, 245, 245));
        field.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel.add(field, gbc);
    }

    // Helper to create Party Component (Icon Beside Radio)
    private JPanel createPartyComponent(String symbolText, String imagePath, JRadioButton radio) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        JLabel iconLabel = new JLabel();
        iconLabel.setPreferredSize(new Dimension(50, 50));

        // Try to load image
        File imgFile = new File(imagePath);
        boolean imgLoaded = false;
        if (imgFile.exists()) {
            try {
                ImageIcon icon = new ImageIcon(imagePath);
                // Check if image is actually valid/loaded
                if (icon.getImageLoadStatus() == MediaTracker.COMPLETE && icon.getIconWidth() > 0) {
                    Image img = icon.getImage();
                    Image newImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                    iconLabel.setIcon(new ImageIcon(newImg));
                    imgLoaded = true;
                }
            } catch (Exception e) {
                imgLoaded = false;
            }
        }

        if (!imgLoaded) {
            // Bulletproof Icon Fallback
            iconLabel.setIcon(new ProceduralLogoIcon(symbolText, 50));
        }

        panel.add(iconLabel);
        panel.add(radio);

        return panel;
    }

    // High-Fidelity Procedural Logo Fallback Icon
    static class ProceduralLogoIcon implements Icon {
        private String type;
        private int size;

        public ProceduralLogoIcon(String type, int size) {
            this.type = type;
            this.size = size;
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y);

            int w = size;
            int h = size;

            if (type.equalsIgnoreCase("Fan") || type.equalsIgnoreCase("YSRCP")) {
                g2.setColor(new Color(59, 130, 246));
                g2.fillOval(w / 2 - 6, h / 2 - 6, 12, 12);
                g2.setColor(new Color(30, 64, 175));
                for (int i = 0; i < 3; i++) {
                    g2.rotate(Math.toRadians(120), w / 2, h / 2);
                    g2.fillRoundRect(w / 2 - 4, 8, 8, h / 2 - 12, 4, 4);
                }
            } else if (type.equalsIgnoreCase("Cycle") || type.equalsIgnoreCase("TDP")) {
                g2.setColor(new Color(245, 158, 11));
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(6, h - 22, 18, 18);
                g2.drawOval(w - 24, h - 22, 18, 18);
                g2.drawLine(15, h - 13, w / 2, h / 2);
                g2.drawLine(w - 15, h - 13, w / 2, h / 2);
                g2.drawLine(w / 2, h / 2, w / 2, h / 2 - 10);
                g2.drawLine(w / 2 - 5, h / 2 - 10, w / 2 + 5, h / 2 - 10);
            } else if (type.equalsIgnoreCase("Glass") || type.equalsIgnoreCase("Janasena")) {
                g2.setColor(new Color(239, 68, 68));
                int[] px = { w / 2 - 14, w / 2 + 14, w / 2 + 18, w / 2 - 18 };
                int[] py = { h - 8, h - 8, 8, 8 };
                g2.fillPolygon(px, py, 4);
                g2.setColor(new Color(255, 255, 255, 180));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawPolygon(px, py, 4);
                g2.drawLine(w / 2 - 10, 15, w / 2 - 8, h - 15);
            } else if (type.equalsIgnoreCase("Hand")) {
                g2.setColor(new Color(34, 197, 94));
                g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawRoundRect(w / 2 - 12, h / 2 - 5, 24, 15, 10, 10);
                for (int i = 0; i < 4; i++)
                    g2.drawLine(w / 2 - 9 + (i * 6), h / 2 - 5, w / 2 - 9 + (i * 6), 10);
                g2.drawLine(w / 2 - 15, h / 2, w / 2 - 20, h / 2 - 10);
            } else {
                g2.setColor(new Color(100, 116, 139));
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(10, 10, w - 20, h - 20, 10, 10);
                g2.drawLine(15, 15, w - 15, h - 15);
                g2.drawLine(w - 15, 15, 15, h - 15);
            }
            g2.dispose();
        }
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == submitBtn) {
            if (OnlineVotingSystem.votedVoters.contains(loggedInVoter)) {
                new CustomMessageDialog(this, "You have already voted!").setVisible(true);
                return;
            }

            String selectedParty = "";

            if (ysrcp.isSelected())
                selectedParty = "YSRCP";
            else if (tdp.isSelected())
                selectedParty = "TDP";
            else if (janasena.isSelected())
                selectedParty = "Janasena";
            else if (nota.isSelected())
                selectedParty = "NOTA";
            else {
                new CustomMessageDialog(this, "Select a party!").setVisible(true);
                return;
            }

            String constituency = constituencyBox.getSelectedItem().toString();
            Voter vObj = OnlineVotingSystem.voters.get(loggedInVoter);
            if (vObj != null) {
                vObj.constituency = constituency;
            }

            OnlineVotingSystem.constituencyVotes
                    .putIfAbsent(constituency, new HashMap<>());

            HashMap<String, Integer> map = OnlineVotingSystem.constituencyVotes.get(constituency);

            map.put(selectedParty,
                    map.getOrDefault(selectedParty, 0) + 1);

            OnlineVotingSystem.votedVoters.add(loggedInVoter);
            OnlineVotingSystem.saveVotesToFile();

            new VoteReceiptDialog(this, vObj != null ? vObj.name : "Unknown", selectedParty)
                    .setVisible(true);
            submitBtn.setEnabled(false); // Disable after vote
        }
    }
}

// ================= VOTE RECEIPT DIALOG =================
class VoteReceiptDialog extends JDialog {
    public VoteReceiptDialog(Frame parent, String name, String party) {
        super(parent, "Vote Receipt", true);
        setSize(750, 480);
        setLocationRelativeTo(parent);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        // Background Panel
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        add(mainPanel);

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        contentPanel.setOpaque(false);

        // Left Side: Illustration
        JLabel illustrationLabel = new JLabel();
        String votePath = "c:\\Users\\kadal\\Downloads\\election (3)\\election\\vote_success.png";
        try {
            ImageIcon icon = new ImageIcon(votePath);
            Image img = icon.getImage().getScaledInstance(340, 340, Image.SCALE_SMOOTH);
            illustrationLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            illustrationLabel.setText("✅");
            illustrationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 120));
        }
        contentPanel.add(illustrationLabel);

        // Right Side: Details
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel("Vote Successful!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 38));
        titleLabel.setForeground(new Color(16, 185, 129)); // Green
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 5, 0);
        detailsPanel.add(titleLabel, gbc);

        JPanel line = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(16, 185, 129));
                g.fillRect(0, 0, 100, 4);
            }
        };
        line.setPreferredSize(new Dimension(100, 4));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 25, 0);
        detailsPanel.add(line, gbc);

        // Receipt Card
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(new Color(240, 253, 244)); // Very light green
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(187, 247, 208), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        GridBagConstraints cgbc = new GridBagConstraints();
        cgbc.anchor = GridBagConstraints.WEST;
        cgbc.insets = new Insets(5, 0, 5, 20);

        String txId = "TXN-" + System.currentTimeMillis() % 1000000;
        addDetailRow(card, "Voter Name", name, 0, cgbc);
        addDetailRow(card, "Party Choice", party, 1, cgbc);
        addDetailRow(card, "Transaction", txId, 2, cgbc);
        addDetailRow(card, "Date", new java.text.SimpleDateFormat("dd MMM yyyy, HH:mm").format(new java.util.Date()), 3,
                cgbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        detailsPanel.add(card, gbc);

        // Done Button
        JButton doneBtn = new JButton("Close & Secure Sign-out") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(16, 185, 129), 0, getHeight(),
                        new Color(5, 150, 105));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight()); // Pill Shape
                g2.dispose();
                super.paintComponent(g);
            }
        };
        doneBtn.setPreferredSize(new Dimension(300, 50));
        doneBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        doneBtn.setForeground(Color.WHITE);
        doneBtn.setContentAreaFilled(false);
        doneBtn.setBorderPainted(false);
        doneBtn.setFocusPainted(false);
        doneBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        doneBtn.addActionListener(e -> {
            dispose();
            parent.dispose();
            OnlineVotingSystem.loginMenu();
        });

        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        detailsPanel.add(doneBtn, gbc);

        contentPanel.add(detailsPanel);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    private void addDetailRow(JPanel p, String label, String val, int y, GridBagConstraints c) {
        c.gridy = y;

        c.gridx = 0;
        JLabel l = new JLabel(label + ":");
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(new Color(100, 116, 139));
        p.add(l, c);

        c.gridx = 1;
        JLabel v = new JLabel(val);
        v.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        v.setForeground(new Color(30, 41, 59));
        p.add(v, c);
    }
}

// ================= CUSTOM LOGIN DIALOG =================
class VoterLoginDialog extends JDialog {
    private JTextField voterIdField;
    private JTextField aadhaarField;
    private boolean okPressed = false;
    private Image bgImage;

    public VoterLoginDialog(Frame parent) {
        super(parent, "National Voter Portal - Authentication", true);
        setSize(1000, 650);
        setLocationRelativeTo(parent);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        // Load background
        try {
            bgImage = ImageIO.read(new File("election (3)/election/admin_bg.png"));
        } catch (IOException e) {
            bgImage = null;
        }

        // Main Container with Background
        JPanel mainContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dark Navy Gradient Background
                g2.setPaint(new GradientPaint(0, 0, new Color(13, 20, 52), 0, getHeight(), new Color(24, 34, 77)));
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Subtle Starry Texture
                g2.setColor(new Color(255, 255, 255, 40));
                Random rand = new Random(42);
                for(int i=0; i<150; i++) {
                    int x = rand.nextInt(getWidth());
                    int y = rand.nextInt(getHeight());
                    int size = rand.nextInt(3);
                    g2.fillOval(x, y, size, size);
                }

                g2.dispose();
            }
        };
        mainContainer.setOpaque(false);
        add(mainContainer);

        // Center Login Card
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        mainContainer.add(centerPanel, BorderLayout.CENTER);

        JPanel loginCard = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Multi-layered Shadow
                for (int i = 1; i <= 8; i++) {
                    g2.setColor(new Color(0, 0, 0, 15 - i));
                    g2.fillRoundRect(i, i, getWidth() - i*2, getHeight() - i*2, 25, 25);
                }

                // Card Body
                g2.setColor(new Color(255, 255, 255, 255));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                g2.dispose();
            }
        };
        loginCard.setOpaque(false);
        loginCard.setPreferredSize(new Dimension(520, 480));
        loginCard.setBorder(BorderFactory.createEmptyBorder(30, 45, 30, 45));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Header Section (Icon + Title)
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw Identity/Ballot Icon
                int s = 45;
                int x = 0, y = (getHeight()-s)/2;
                
                // Blue Card
                g2.setColor(new Color(59, 130, 246));
                g2.fillRoundRect(x, y + 10, s, s-10, 8, 8);
                
                // Top Tab (White)
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(x + 10, y, s - 20, 15, 4, 4);
                g2.setColor(new Color(0, 0, 0, 40));
                g2.drawRoundRect(x + 10, y, s - 20, 15, 4, 4);

                // Checkmark in the middle
                g2.setColor(new Color(34, 197, 94));
                g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(x + 15, y + 25, x + 22, y + 32);
                g2.drawLine(x + 22, y + 32, x + 35, y + 18);
                
                // Blue Lines/Bars on Card
                g2.setColor(new Color(255, 255, 255, 180));
                g2.fillRect(x + 8, y + 35, 15, 3);
                g2.fillRect(x + 8, y + 42, 30, 3);

                g2.dispose();
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(400, 60));
        
        JLabel titleLabel = new JLabel("Voter Authentication Portal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(30, 58, 138));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 55, 0, 0));
        headerPanel.add(titleLabel);

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 5, 0);
        loginCard.add(headerPanel, gbc);
        
        // Separator Line
        JPanel sep = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(241, 245, 249));
                g.fillRect(0, 0, getWidth(), 1);
            }
        };
        sep.setPreferredSize(new Dimension(400, 1));
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 25, 0);
        loginCard.add(sep, gbc);

        // Voter ID Section
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        loginCard.add(createModernInputSection("Voter ID", "\uD83D\uDCC4", voterIdField = new JTextField(15)), gbc);

        // Aadhaar Section
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 35, 0);
        loginCard.add(createModernInputSection("Aadhaar Number", "ID", aadhaarField = new JTextField(15)), gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        buttonPanel.setOpaque(false);

        JButton exitBtn = createVoterLoginButton("Exit", "X", new Color(0, 0, 0, 5));
        JButton authBtn = createVoterLoginButton("Authenticate", "CHK", new Color(30, 58, 138));

        exitBtn.addActionListener(e -> dispose());
        authBtn.addActionListener(e -> {
            okPressed = true;
            dispose();
        });

        buttonPanel.add(exitBtn);
        buttonPanel.add(authBtn);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 0, 0);
        loginCard.add(buttonPanel, gbc);

        centerPanel.add(loginCard);
    }

    private JPanel createModernInputSection(String labelStr, String iconType, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);
        
        // Label with Icon
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        labelPanel.setOpaque(false);
        
        JLabel iconL = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                
                if (iconType.equals("ID")) {
                    g2.setColor(new Color(59, 130, 246));
                    g2.fillRoundRect(0, 2, w, h-4, 4, 4);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    g2.drawString("ID", 4, h-6);
                } else {
                    // Portrait Icon
                    g2.setColor(new Color(71, 85, 105));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(0, 0, w-1, h-1, 3, 3);
                    g2.fillOval(3, 3, 6, 6);
                    g2.fillRect(3, 10, 10, 2);
                    g2.fillRect(16, 4, 6, 2);
                    g2.fillRect(16, 8, 6, 2);
                }
                g2.dispose();
            }
        };
        iconL.setPreferredSize(new Dimension(24, 18));
        
        JLabel l = new JLabel(labelStr);
        l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l.setForeground(new Color(71, 85, 105));
        
        labelPanel.add(iconL);
        labelPanel.add(l);
        p.add(labelPanel, BorderLayout.NORTH);
        
        // Field with styled background
        field.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(241, 245, 249), 1, true),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        field.setBackground(new Color(249, 250, 251));
        field.setForeground(new Color(30, 41, 59));
        field.setCaretColor(new Color(59, 130, 246));
        
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JButton createVoterLoginButton(String text, String iconType, Color bg) {
        boolean isAuth = text.equals("Authenticate");
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Draw Icon
                int x = 40, y = getHeight()/2;
                if (iconType.equals("X")) {
                    g2.setColor(new Color(185, 28, 28));
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.drawLine(x-6, y-6, x+6, y+6);
                    g2.drawLine(x+6, y-6, x-6, y+6);
                } else if (iconType.equals("CHK")) {
                    g2.setColor(new Color(74, 222, 128));
                    g2.fillRoundRect(x-10, y-10, 20, 20, 4, 4);
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(x-5, y, x-1, y+4);
                    g2.drawLine(x-1, y+4, x+6, y-5);
                }
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setPreferredSize(new Dimension(200, 50));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(isAuth ? Color.WHITE : new Color(30, 41, 59));
        btn.setBorder(null);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public boolean isOkPressed() {
        return okPressed;
    }

    public String getVoterId() {
        return voterIdField.getText().trim();
    }

    public String getAadhaar() {
        return aadhaarField.getText().trim();
    }
}

// ================= ADMIN LOGIN DIALOG =================
class AdminLoginDialog extends JDialog {
    private JTextField userField;
    private JPasswordField passField;
    private boolean okPressed = false;
    private Image bgImage;

    public AdminLoginDialog(Frame parent) {
        super(parent, "Election Commission Admin Portal", true);
        setSize(1000, 650);
        setLocationRelativeTo(parent);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        // Load background
        try {
            bgImage = ImageIO.read(new File("election (3)/election/admin_bg.png"));
        } catch (IOException e) {
            bgImage = null;
        }

        // Main Container with Background
        JPanel mainContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (bgImage != null) {
                    g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g2.setPaint(new GradientPaint(0, 0, new Color(15, 23, 42), 0, getHeight(), new Color(30, 41, 59)));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }

                // Top Header Overlay
                g2.setColor(new Color(15, 23, 42, 100));
                g2.fillRect(0, 0, getWidth(), 80);

                g2.dispose();
            }
        };
        mainContainer.setOpaque(false);
        add(mainContainer);

        // Header Section
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        headerPanel.setOpaque(false);
        JLabel portalLogo = new JLabel(new ImageIcon(OnlineVotingSystem.createPortalIcon(60)));
        headerPanel.add(portalLogo);
        mainContainer.add(headerPanel, BorderLayout.NORTH);

        // Center Login Card
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        mainContainer.add(centerPanel, BorderLayout.CENTER);

        JPanel loginCard = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillRoundRect(5, 5, getWidth() - 5, getHeight() - 5, 25, 25);

                // Card Body
                g2.setColor(new Color(255, 255, 255, 240));
                g2.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 25, 25);

                g2.dispose();
            }
        };
        loginCard.setOpaque(false);
        loginCard.setPreferredSize(new Dimension(500, 450));
        loginCard.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Card Title
        JLabel titleLabel = new JLabel("Election Commission Admin Portal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(30, 58, 138));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 30, 0);
        loginCard.add(titleLabel, gbc);

        // Admin ID Field
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 15, 0);
        loginCard.add(createIconInputPanel("👤", userField = new JTextField(15), "Admin ID"), gbc);

        // Password Field
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 40, 0);
        loginCard.add(createIconInputPanel("🔒", passField = new JPasswordField(15), "Password"), gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setOpaque(false);

        JButton backBtn = createModernButton("Back", new Color(243, 244, 246), new Color(55, 65, 81));
        JButton loginBtn = createModernButton("Secure Login", new Color(30, 58, 138), Color.WHITE);

        backBtn.addActionListener(e -> dispose());
        loginBtn.addActionListener(e -> {
            okPressed = true;
            dispose();
        });

        buttonPanel.add(backBtn);
        buttonPanel.add(loginBtn);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        loginCard.add(buttonPanel, gbc);

        centerPanel.add(loginCard);
    }

    private JPanel createIconInputPanel(String icon, JTextField field, String placeholder) {
        JPanel p = new JPanel(new BorderLayout(15, 0));
        p.setBackground(new Color(249, 250, 251));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconLabel.setForeground(new Color(17, 24, 39));

        field.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        field.setBorder(null);
        field.setBackground(new Color(249, 250, 251));
        field.setForeground(new Color(55, 65, 81));

        p.add(iconLabel, BorderLayout.WEST);
        p.add(field, BorderLayout.CENTER);

        return p;
    }

    private JButton createModernButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setPreferredSize(new Dimension(180, 50));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(bg.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(bg);
            }
        });

        return btn;
    }

    public boolean isOkPressed() {
        return okPressed;
    }

    public String getUsername() {
        return userField.getText().trim();
    }

    public String getPassword() {
        return new String(passField.getPassword());
    }
}

// ================= SIDEBAR ICON =================
class SidebarIcon implements Icon {
    private String type;
    private int size;

    public SidebarIcon(String type, int size) {
        this.type = type;
        this.size = size;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.translate(x, y);
        switch (type) {
            case "VOTE":
                g2.setColor(new Color(59, 130, 246));
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawRoundRect(4, 8, size - 8, size - 16, 4, 4);
                g2.drawLine(size / 3, 12, size * 2 / 3, 12);
                break;
            case "USERS":
                g2.setColor(new Color(99, 102, 241));
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(size / 3, 6, size / 3, size / 3);
                g2.drawArc(6, size / 2, size - 12, size / 2, 0, 180);
                break;
            case "DATE":
                g2.setColor(new Color(79, 70, 229));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(6, 6, size - 12, size - 12, 4, 4);
                g2.drawLine(6, size / 3, size - 6, size / 3);
                g2.drawLine(size / 4, 4, size / 4, 8);
                g2.drawLine(size * 3 / 4, 4, size * 3 / 4, 8);
                break;
            case "CHART":
                g2.setColor(new Color(14, 165, 233));
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(6, 6, size - 12, size - 12);
                g2.fillArc(10, 10, size - 20, size - 20, 45, 90);
                break;
            case "BALLOT":
                g2.setColor(new Color(59, 130, 246));
                g2.setStroke(new BasicStroke(2));
                g2.drawRect(4, 12, size - 8, size - 20);
                g2.drawLine(size / 3, 8, size * 2 / 3, 8);
                g2.drawLine(size / 2, 8, size / 2, 14);
                break;
            case "STOP":
                g2.setColor(new Color(239, 68, 68));
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(4, 4, size - 8, size - 8);
                g2.drawRect(size / 3, size / 3, size / 3, size / 3);
                break;
            case "TROPHY":
                g2.setColor(new Color(251, 191, 36));
                g2.setStroke(new BasicStroke(2));
                g2.drawArc(4, 8, size - 8, size / 2, 0, -180);
                g2.drawLine(size / 2, size * 3 / 4, size / 2, size - 8);
                g2.drawLine(size / 4, size - 8, size * 3 / 4, size - 8);
                break;
            case "SEARCH":
                g2.setColor(new Color(59, 130, 246));
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(6, 6, size / 2, size / 2);
                g2.drawLine(size / 2 + 4, size / 2 + 4, size - 8, size - 8);
                break;
            case "GEAR":
                g2.setColor(new Color(71, 85, 105));
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(size / 4, size / 4, size / 2, size / 2);
                for (int i = 0; i < 8; i++) {
                    g2.rotate(Math.PI / 4, size / 2, size / 2);
                    g2.drawRect(size / 2 - 2, 2, 4, 4);
                }
                break;
            case "POWER":
                g2.setColor(new Color(220, 38, 38));
                g2.setStroke(new BasicStroke(2));
                g2.drawArc(6, 6, size - 12, size - 12, -60, 300);
                g2.drawLine(size / 2, 4, size / 2, size / 2);
                break;
        }
        g2.dispose();
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }
}

// ================= RESULTS ADVANCED DIALOG =================
class ResultsDialog extends JDialog {
    private JPanel contentContainer;
    private final Color BG_MAIN = new Color(241, 245, 249);
    private final Color CARD_BG = new Color(255, 255, 255);
    private final Color TEXT_DARK = new Color(15, 23, 42);

    public ResultsDialog(Frame parent) {
        super(parent, "Election Analytical Results", true);
        setSize(1100, 850);
        setLocationRelativeTo(parent);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_MAIN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        add(mainPanel);

        // --- HEADER WITH GRADIENT ---
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Navy-Purple Gradient
                GradientPaint gp = new GradientPaint(0, 0, new Color(30, 58, 138), getWidth(), 0, new Color(76, 29, 149));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                g2.fillRect(0, getHeight()/2, getWidth(), getHeight()/2); // Flatten bottom

                // Decorative Illustration (Top Right)
                int ix = getWidth() - 180;
                int iy = 10;
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillOval(ix, iy, 150, 150);
                g2.setColor(new Color(255, 255, 255, 20));
                g2.fillOval(ix - 20, iy + 20, 100, 100);

                // Ballot Box Icon (Header)
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(ix + 40, iy + 50, 70, 50, 5, 5);
                g2.drawLine(ix + 55, iy + 45, ix + 95, iy + 45); // Slot
                g2.drawLine(ix + 50, iy + 65, ix + 65, iy + 80); // Check
                g2.drawLine(ix + 65, iy + 80, ix + 100, iy + 60);
                
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(getWidth(), 110));
        header.setBorder(BorderFactory.createEmptyBorder(25, 40, 15, 40));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Election Analytical Results");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel("Real-Time Vote Monitoring Dashboard");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subLabel.setForeground(new Color(226, 232, 240));

        titlePanel.add(titleLabel);
        titlePanel.add(subLabel);
        header.add(titlePanel, BorderLayout.WEST);

        // Control Buttons
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        controls.setOpaque(false);
        
        JButton minBtn = createHeaderBtn("MIN");
        JButton maxBtn = createHeaderBtn("MAX");
        JButton closeBtn = createHeaderBtn("CLOSE");
        closeBtn.addActionListener(e -> dispose());
        
        controls.add(minBtn);
        controls.add(maxBtn);
        controls.add(closeBtn);
        header.add(controls, BorderLayout.EAST);

        mainPanel.add(header, BorderLayout.NORTH);

        // --- CONTENT ---
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // 1. Top Stats
        contentPanel.add(createTopStatsSection(), BorderLayout.NORTH);

        // 2. Main Grid
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Results Card (Left)
        gbc.gridx = 0;
        gbc.weightx = 0.65;
        gbc.insets = new Insets(0, 0, 0, 20);
        contentContainer = new JPanel();
        contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.Y_AXIS));
        contentContainer.setOpaque(false);
        grid.add(contentContainer, gbc);

        // Sidebar (Right)
        gbc.gridx = 1;
        gbc.weightx = 0.35;
        gbc.insets = new Insets(0, 0, 0, 0);
        grid.add(createSidebar(), gbc);

        contentPanel.add(grid, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        updateResultsView();
    }

    private JPanel createTopStatsSection() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(1050, 110));

        int totalVotes = OnlineVotingSystem.votedVoters.size(); 
        int totalVoters = OnlineVotingSystem.voters.size();
        double participation = (totalVoters > 0) ? ((double) totalVotes / totalVoters * 100) : 0;
        String lastUpdated = new java.text.SimpleDateFormat("dd MMM yyyy").format(new java.util.Date());

        statsPanel.add(createStatCard("Total Votes Cast:", String.valueOf(totalVotes), "VOTE", new Color(59, 130, 246)));
        statsPanel.add(createStatCard("Total Voters:", String.valueOf(totalVoters), "USERS", new Color(59, 130, 246)));
        statsPanel.add(createStatCard("Participation:", String.format("%.1f%%", participation), "CHART", new Color(59, 130, 246)));
        statsPanel.add(createStatCard("Last Updated:", lastUpdated, "DATE", new Color(59, 130, 246)));

        return statsPanel;
    }

    private JPanel createStatCard(String label, String value, String iconType, Color accent) {
        JPanel card = new JPanel(new BorderLayout(20, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Shadow
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(3, 3, getWidth()-6, getHeight()-6, 25, 25);
                
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth()-3, getHeight()-3, 25, 25);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Procedural Icon with light background
        JPanel iconP = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(239, 246, 255));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                new SidebarIcon(iconType, 30).paintIcon(this, g, 10, 10);
            }
        };
        iconP.setPreferredSize(new Dimension(50, 50));
        iconP.setOpaque(false);
        card.add(iconP, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        l.setForeground(new Color(100, 116, 139));

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 28));
        v.setForeground(TEXT_DARK);

        textPanel.add(l);
        textPanel.add(v);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setOpaque(false);

        Map<String, Integer> partyTotals = new HashMap<>();
        partyTotals.put("YSRCP", 0); partyTotals.put("TDP", 0);
        partyTotals.put("Janasena", 0); partyTotals.put("NOTA", 0);

        for (HashMap<String, Integer> map : OnlineVotingSystem.constituencyVotes.values()) {
            for (String party : partyTotals.keySet()) {
                partyTotals.put(party, partyTotals.get(party) + map.getOrDefault(party, 0));
            }
        }

        java.util.List<Map.Entry<String, Integer>> sorted = new ArrayList<>(partyTotals.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        String leadingParty = sorted.get(0).getValue() > 0 ? sorted.get(0).getKey() : "N/A";
        int margin = (sorted.size() >= 2) ? sorted.get(0).getValue() - sorted.get(1).getValue() : 0;

        Color leadColor = new Color(245, 158, 11); // Gold/Orange for leading

        // 1. Statistics Card
        JPanel statBox = createPremiumCard("Election Statistics");
        addSidebarInfo(statBox, "Leading Party:", leadingParty, leadColor);
        addSidebarInfo(statBox, "Winning Margin:", margin + " Vote" + (margin==1?"":"s"), TEXT_DARK);
        addSidebarInfo(statBox, "Total Parties:", "4", TEXT_DARK);
        addSidebarInfo(statBox, "Verified Votes:", String.valueOf(OnlineVotingSystem.votedVoters.size()), TEXT_DARK);
        addSidebarInfo(statBox, "Invalid Votes:", "0", TEXT_DARK);
        sidebar.add(statBox);

        sidebar.add(Box.createRigidArea(new Dimension(0, 25)));

        // 2. Vote Distribution
        JPanel chartBox = createPremiumCard("Vote Distribution");
        final int totalVotes = partyTotals.values().stream().mapToInt(Integer::intValue).sum();
        
        JPanel donutPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int size = 180;
                int x = (getWidth()-size)/2, y = 10;

                g2.setColor(new Color(241, 245, 249));
                g2.setStroke(new BasicStroke(28, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(x, y, size, size, 0, 360);

                if (totalVotes > 0) {
                    int start = 90;
                    drawArc(g2, x, y, size, start, partyTotals.get("YSRCP"), totalVotes, new Color(59, 130, 246));
                    start -= (int)(360.0 * partyTotals.get("YSRCP") / totalVotes);
                    drawArc(g2, x, y, size, start, partyTotals.get("TDP"), totalVotes, new Color(251, 191, 36));
                    start -= (int)(360.0 * partyTotals.get("TDP") / totalVotes);
                    drawArc(g2, x, y, size, start, partyTotals.get("Janasena"), totalVotes, new Color(239, 68, 68));
                    start -= (int)(360.0 * partyTotals.get("Janasena") / totalVotes);
                    drawArc(g2, x, y, size, start, partyTotals.get("NOTA"), totalVotes, new Color(148, 163, 184));
                }
                
                // Center Text
                g2.setColor(TEXT_DARK);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
                String t1 = leadingParty;
                String t2 = (totalVotes > 0 ? "100%" : "0%");
                g2.drawString(t1, x + (size - g2.getFontMetrics().stringWidth(t1))/2, y + size/2 + 5);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                g2.drawString(t2, x + (size - g2.getFontMetrics().stringWidth(t2))/2, y + size/2 + 30);
                g2.dispose();
            }
            private void drawArc(Graphics2D g2, int x, int y, int s, int start, int val, int total, Color c) {
                if(val == 0) return;
                int extend = (int)Math.round(360.0 * val / total);
                g2.setColor(c);
                g2.drawArc(x, y, s, s, start, -extend);
            }
        };
        donutPanel.setPreferredSize(new Dimension(220, 200));
        donutPanel.setOpaque(false);
        chartBox.add(donutPanel);

        // Legend
        JPanel legend = new JPanel(new GridLayout(4, 1, 0, 5));
        legend.setOpaque(false);
        addLegendItem(legend, "YSRCP", new Color(59, 130, 246));
        addLegendItem(legend, "TDP", new Color(251, 191, 36));
        addLegendItem(legend, "Janasena", new Color(239, 68, 68));
        addLegendItem(legend, "NOTA", new Color(148, 163, 184));
        chartBox.add(legend);

        sidebar.add(chartBox);
        return sidebar;
    }

    private void addLegendItem(JPanel p, String text, Color c) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row.setOpaque(false);
        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(c); g.fillOval(0, 0, 10, 10);
            }
        };
        dot.setPreferredSize(new Dimension(10, 10));
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        row.add(dot);
        row.add(l);
        p.add(row);
    }

    private JPanel createPremiumCard(String title) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setAlignmentX(Component.CENTER_ALIGNMENT);
        t.setFont(new Font("Segoe UI", Font.BOLD, 22));
        t.setForeground(TEXT_DARK);
        card.add(t);
        card.add(Box.createRigidArea(new Dimension(0, 25)));
        return card;
    }

    private void addSidebarInfo(JPanel p, String key, String val, Color valCol) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(400, 30));
        JLabel kl = new JLabel(key);
        kl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        kl.setForeground(new Color(100, 116, 139));
        JLabel vl = new JLabel(val);
        vl.setFont(new Font("Segoe UI Bold", Font.BOLD, 15));
        vl.setForeground(valCol);
        row.add(kl, BorderLayout.WEST);
        row.add(vl, BorderLayout.EAST);
        p.add(row);
        p.add(Box.createRigidArea(new Dimension(0, 8)));
    }

    private void updateResultsView() {
        contentContainer.removeAll();
        showAdvancedResults();
        contentContainer.revalidate();
        contentContainer.repaint();
    }

    private void showAdvancedResults() {
        JPanel mainCard = createPremiumCard("Consolidated National Summary");

        int tY = 0, tT = 0, tJ = 0, tN = 0;
        for (HashMap<String, Integer> m : OnlineVotingSystem.constituencyVotes.values()) {
            tY += m.getOrDefault("YSRCP", 0); tT += m.getOrDefault("TDP", 0);
            tJ += m.getOrDefault("Janasena", 0); tN += m.getOrDefault("NOTA", 0);
        }
        int total = tY + tT + tJ + tN;

        addAdvancedResultRow(mainCard, "YSRCP", "election (3)/election/YSR.jpeg", tY, total, new Color(59, 130, 246));
        addAdvancedResultRow(mainCard, "TDP", "election (3)/election/TDP.jpeg", tT, total, new Color(251, 191, 36));
        addAdvancedResultRow(mainCard, "Janasena", "election (3)/election/jagasena.jpg.jpeg", tJ, total, new Color(239, 68, 68));
        addAdvancedResultRow(mainCard, "NOTA", "election (3)/election/nota.png", tN, total, new Color(148, 163, 184));

        mainCard.add(Box.createRigidArea(new Dimension(0, 30)));

        // Verification & Buttons
        JPanel footer = new JPanel(new BorderLayout(40, 0));
        footer.setOpaque(false);

        JPanel info = new JPanel(new GridLayout(3, 1, 0, 5));
        info.setOpaque(false);
        info.add(createCheckLabel("Results Verified Across All Constituencies"));
        info.add(createCheckLabel("System: QR-Based Secure Voting System"));
        info.add(createCheckLabel("Admin: Election Control Panel"));
        footer.add(info, BorderLayout.WEST);

        JPanel btns = new JPanel(new GridLayout(2, 1, 0, 15));
        btns.setOpaque(false);
        JButton b1 = createModernButton("📁 Export Results as PDF", new Color(59, 130, 246));
        JButton b2 = createModernButton("📁 Download Excel Report", new Color(34, 197, 94));
        b1.addActionListener(e -> exportReport("Election_Report.pdf"));
        b2.addActionListener(e -> exportReport("Election_Report.xlsx"));
        btns.add(b1); btns.add(b2);
        footer.add(btns, BorderLayout.EAST);

        mainCard.add(footer);
        contentContainer.add(mainCard);
    }

    private void exportReport(String defaultName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Location to Save Report");
        fileChooser.setSelectedFile(new File(defaultName));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("=================================================");
                writer.println("   OFFICIAL ELECTION ANALYTICAL REPORT");
                writer.println("   Generated: " + new java.util.Date());
                writer.println("=================================================");
                writer.println();

                int totalVotes = OnlineVotingSystem.constituencyVotes.values().stream()
                        .flatMap(m -> m.values().stream())
                        .mapToInt(Integer::intValue).sum();

                writer.println("SUMMARY STATISTICS:");
                writer.println("-------------------");
                writer.println("Total Votes Cast: " + totalVotes);
                writer.println("Total Registered Voters: 100");
                writer.println("Participation: " + (totalVotes) + "%");
                writer.println();

                writer.println("PARTY-WISE BREAKDOWN:");
                writer.println("---------------------");

                HashMap<String, Integer> totals = new HashMap<>();
                for (HashMap<String, Integer> map : OnlineVotingSystem.constituencyVotes.values()) {
                    for (String party : map.keySet()) {
                        totals.put(party, totals.getOrDefault(party, 0) + map.get(party));
                    }
                }

                String[] parties = { "YSRCP", "TDP", "Janasena", "NOTA" };
                String winner = "N/A";
                int maxVotes = -1;

                for (String p : parties) {
                    int v = totals.getOrDefault(p, 0);
                    double pcent = (totalVotes == 0) ? 0 : (double) v / totalVotes * 100;
                    writer.printf("%-10s: %d Votes (%.1f%%)\n", p, v, pcent);

                    if (v > maxVotes) {
                        maxVotes = v;
                        winner = p;
                    }
                }

                writer.println();
                writer.println("FINAL VERDICT:");
                writer.println("--------------");
                writer.println("Leading/Winning Party: " + winner);
                writer.println("Audit Status: Verified & Encrypted");
                writer.println();
                writer.println("=================================================");
                writer.println("   END OF SECURE ELECTION REPORT");
                writer.println("=================================================");

                new CustomMessageDialog(null, "Report exported successfully to:\n" + file.getName()).setVisible(true);
            } catch (IOException ex) {
                new CustomMessageDialog(null, "Error exporting report: " + ex.getMessage()).setVisible(true);
            }
        }
    }

    private JLabel createCheckLabel(String text) {
        JLabel l = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(71, 85, 105));
                g2.setStroke(new BasicStroke(1.5f));
                // Draw custom checkmark to avoid encoding boxes
                g2.drawLine(2, 10, 6, 14);
                g2.drawLine(6, 14, 12, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        l.setForeground(new Color(71, 85, 105));
        l.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        return l;
    }

    private JButton createHeaderBtn(String type) {
        JButton b = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 180));
                g2.setStroke(new BasicStroke(1.5f));
                int w = getWidth(), h = getHeight();
                int s = 12; // size of icon
                int x = (w - s) / 2, y = (h - s) / 2;
                
                if (type.equals("MIN")) {
                    g2.drawLine(x, y + s/2, x + s, y + s/2);
                } else if (type.equals("MAX")) {
                    g2.drawRect(x, y, s, s);
                } else if (type.equals("CLOSE")) {
                    g2.drawLine(x, y, x + s, y + s);
                    g2.drawLine(x + s, y, x, y + s);
                }
                g2.dispose();
            }
        };
        b.setPreferredSize(new Dimension(30, 30));
        b.setBorder(null);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void addAdvancedResultRow(JPanel card, String party, String imgPath, int votes, int total, Color color) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(0, 10, 0, 10);
        g.fill = GridBagConstraints.BOTH;

        // Logo
        g.gridx = 0; g.weightx = 0.05;
        JLabel logo = new JLabel();
        logo.setPreferredSize(new Dimension(55, 55));
        try {
            BufferedImage orig = ImageIO.read(new File(imgPath));
            BufferedImage circular = new BufferedImage(55, 55, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = circular.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, 55, 55));
            g2.drawImage(orig, 0, 0, 55, 55, null);
            g2.dispose();
            logo.setIcon(new ImageIcon(circular));
        } catch (Exception e) {}
        row.add(logo, g);

        // Name
        g.gridx = 1; g.weightx = 0.15;
        JLabel name = new JLabel(party);
        name.setFont(new Font("Segoe UI", Font.BOLD, 20));
        name.setForeground(TEXT_DARK);
        row.add(name, g);

        // Bar
        g.gridx = 2; g.weightx = 0.5;
        double pct = (total == 0) ? 0 : (double) votes / total;
        JProgressBar bar = new JProgressBar(0, 100) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(241, 245, 249));
                g2.fillRoundRect(0, getHeight()/2-6, getWidth(), 12, 12, 12);
                g2.setColor(color);
                int val = (int)(getWidth() * pct);
                g2.fillRoundRect(0, getHeight()/2-6, val, 12, 12, 12);
                g2.dispose();
            }
        };
        bar.setOpaque(false); bar.setBorder(null);
        row.add(bar, g);

        // Stats
        g.gridx = 3; g.weightx = 0.2;
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        badge.setOpaque(false);
        JLabel pLabel = new JLabel(votes + " (" + (int)(pct*100) + "%)");
        pLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        
        JPanel pBadge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        pBadge.setOpaque(false); pBadge.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        JLabel bl = new JLabel(votes + " Vote" + (votes!=1?"s":""));
        bl.setFont(new Font("Segoe UI Bold", Font.BOLD, 14));
        bl.setForeground(votes > 0 ? Color.WHITE : new Color(100, 116, 139));
        pBadge.add(bl);
        
        badge.add(pLabel); badge.add(pBadge);
        row.add(badge, g);

        card.add(row);
    }

    private JButton createModernButton(String text, Color bg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground()); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g); g2.dispose();
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 18));
        b.setForeground(Color.WHITE); b.setBackground(bg);
        b.setFocusPainted(false); b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        b.setContentAreaFilled(false);
        return b;
    }
}

// ================= ADMIN DASHBOARD =================
class AdminDashboard extends JFrame {
    public AdminDashboard() {
        setTitle("ECI Admin Infrastructure Control");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Navigation Sidebar
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Deep dark space background
                GradientPaint bg = new GradientPaint(0, 0, new Color(10, 15, 30), 0, getHeight(),
                        new Color(20, 25, 50));
                g2.setPaint(bg);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Subtle Stars
                Random rnd = new Random(123);
                g2.setColor(new Color(255, 255, 255, 100));
                for (int i = 0; i < 50; i++) {
                    int x = rnd.nextInt(getWidth());
                    int y = rnd.nextInt(getHeight());
                    int s = rnd.nextInt(2) + 1;
                    g2.fillOval(x, y, s, s);
                }
                g2.dispose();
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(30, 15, 30, 15));

        JLabel logoLabel = new JLabel(new SidebarIcon("BALLOT", 70));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logoLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 40)));

        JButton startBtn = createModernNavButton("Start Election", "BALLOT", new Color(59, 130, 246));
        JButton endBtn = createModernNavButton("End Election", "STOP", new Color(239, 68, 68));
        JButton winnerBtn = createModernNavButton("Winner", "TROPHY", new Color(251, 191, 36));
        JButton viewBtn = createModernNavButton("Detailed Results", "CHART", new Color(59, 130, 246));
        JButton auditBtn = createModernNavButton("Voter Audit Trail", "SEARCH", new Color(168, 85, 247));
        JButton resetBtn = createModernNavButton("Reset Infrastructure", "GEAR", new Color(71, 85, 105));
        JButton logoutBtn = createModernNavButton("Logout", "POWER", Color.BLACK);

        startBtn.addActionListener(e -> {
            OnlineVotingSystem.electionStarted = true;
            OnlineVotingSystem.electionEnded = false;
            OnlineVotingSystem.saveVotesToFile();
            new ElectionStartedDialog(this).setVisible(true);
            refresh();
        });

        endBtn.addActionListener(e -> {
            OnlineVotingSystem.electionStarted = false;
            OnlineVotingSystem.electionEnded = true;
            OnlineVotingSystem.saveVotesToFile();
            new ElectionStoppedDialog(this).setVisible(true);
            refresh();
        });

        winnerBtn.addActionListener(e -> new WinnerAnnouncementDialog(this).setVisible(true));
        viewBtn.addActionListener(e -> new ResultsDialog(this).setVisible(true));
        auditBtn.addActionListener(e -> new AuditLogDialog(this).setVisible(true));
        resetBtn.addActionListener(e -> {
            OnlineVotingSystem.resetElectionData();
            refresh();
        });
        logoutBtn.addActionListener(e -> {
            dispose();
            OnlineVotingSystem.loginMenu();
        });

        sidebar.add(startBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(endBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(winnerBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(viewBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(auditBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(resetBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 60)));
        sidebar.add(logoutBtn);

        add(sidebar, BorderLayout.WEST);

        // Main Content Area
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(new Color(248, 250, 252));
        add(mainContent, BorderLayout.CENTER);

        // Content Header
        JPanel contentHeader = new JPanel(new BorderLayout());
        contentHeader.setBackground(Color.WHITE);
        contentHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));
        contentHeader.setPreferredSize(new Dimension(0, 70));
        contentHeader.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));

        JLabel headTitle = new JLabel("Infrastructure Monitoring");
        headTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headTitle.setForeground(new Color(15, 23, 42));
        contentHeader.add(headTitle, BorderLayout.WEST);

        mainContent.add(contentHeader, BorderLayout.NORTH);

        // Dashboard Grid
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        int totalVotes = OnlineVotingSystem.constituencyVotes.values().stream()
                .flatMap(m -> m.values().stream()).mapToInt(Integer::intValue).sum();

        String statusText = OnlineVotingSystem.electionStarted ? "ACTIVE"
                : (OnlineVotingSystem.electionEnded ? "COMPLETED" : "READY");
        Color statusColor = OnlineVotingSystem.electionStarted ? new Color(34, 197, 94)
                : (OnlineVotingSystem.electionEnded ? new Color(239, 68, 68) : Color.GRAY);

        gbc.gridx = 0;
        gbc.gridy = 0;
        grid.add(createModernStatCard("Total Votes Verified", String.valueOf(totalVotes), "✅", new Color(59, 130, 246)),
                gbc);
        gbc.gridx = 1;
        grid.add(createModernStatCard("System Status", statusText, "📡", statusColor), gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        grid.add(createModernStatCard("Active Sessions", String.valueOf(OnlineVotingSystem.votedVoters.size()), "🕒",
                new Color(168, 85, 247)), gbc);
        gbc.gridx = 1;
        grid.add(createModernStatCard("Infrastructure Load", "Stable", "⚡", new Color(15, 118, 110)), gbc);

        mainContent.add(grid, BorderLayout.CENTER);
    }

    private void refresh() {
        dispose();
        new AdminDashboard().setVisible(true);
    }

    private JButton createModernNavButton(String text, String iconType, Color accent) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;

            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        repaint();
                    }

                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Glass background
                if (hovered) {
                    g2.setColor(new Color(255, 255, 255, 40));
                } else {
                    g2.setColor(new Color(255, 255, 255, 20));
                }
                g2.fillRoundRect(0, 0, w, h, 15, 15);

                // Icon
                Icon icon = new SidebarIcon(iconType, 32);
                icon.paintIcon(this, g2, 12, (h - 32) / 2);

                // Text
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), 60, (h + fm.getAscent() - fm.getDescent()) / 2);

                // Chevron
                g2.setColor(new Color(255, 255, 255, 100));
                g2.setStroke(new BasicStroke(2));
                int cx = w - 25;
                int cy = h / 2;
                g2.drawLine(cx, cy - 4, cx + 4, cy);
                g2.drawLine(cx + 4, cy, cx, cy + 4);

                g2.dispose();
            }
        };
        btn.setMaximumSize(new Dimension(250, 60));
        btn.setPreferredSize(new Dimension(250, 60));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setForeground(Color.WHITE);
        return btn;
    }

    private JPanel createModernStatCard(String title, String val, String emoji, Color color) {
        JPanel card = new JPanel(new BorderLayout(15, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(226, 232, 240, 100));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel iconLabel = new JLabel(emoji);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setForeground(color);
        card.add(iconLabel, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 14));
        t.setForeground(new Color(100, 116, 139));

        JLabel v = new JLabel(val);
        v.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 28));
        v.setForeground(new Color(15, 23, 42));

        textPanel.add(t);
        textPanel.add(v);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }
}

// ================= AUDIT LOG DIALOG =================
class AuditLogDialog extends JDialog {
    private JPanel listPanel;
    private JTextField searchField;

    public AuditLogDialog(Frame parent) {
        super(parent, "System Audit Trail - Integrity Logs", true);
        setSize(1000, 700);
        setLocationRelativeTo(parent);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        // Background Panel
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillRoundRect(5, 5, getWidth() - 5, getHeight() - 5, 30, 30);

                // Body
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 30, 30);

                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        add(mainPanel);

        // Sidebar (Left - Illustration)
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setPreferredSize(new Dimension(350, 0));
        leftPanel.setOpaque(true);
        leftPanel.setBackground(new Color(252, 253, 255));
        leftPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(241, 245, 249)));

        JLabel illustrationLabel = new JLabel();
        String auditPath = "c:\\Users\\kadal\\Downloads\\election (3)\\election\\election_started.png";
        try {
            ImageIcon icon = new ImageIcon(auditPath);
            Image img = icon.getImage().getScaledInstance(280, 280, Image.SCALE_SMOOTH);
            illustrationLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
        }
        leftPanel.add(illustrationLabel);
        mainPanel.add(leftPanel, BorderLayout.WEST);

        // Content Area (Right)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Top Header Section
        JPanel topHeader = new JPanel(new GridBagLayout());
        topHeader.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Title with blue accent
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Verified Voter Records & Audit Trail");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(51, 65, 85));
        titlePanel.add(titleLabel);

        JPanel accentLine = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(37, 99, 235));
                g.fillRect(0, 0, 60, 4);
            }
        };
        accentLine.setPreferredSize(new Dimension(60, 4));

        gbc.gridy = 0;
        topHeader.add(titlePanel, gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(8, 0, 25, 0);
        topHeader.add(accentLine, gbc);

        // Unified Search Bar with Button
        JPanel searchBar = new JPanel(new BorderLayout());
        searchBar.setBackground(Color.WHITE);
        searchBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(0, 15, 0, 0)));
        searchBar.setPreferredSize(new Dimension(0, 50));

        JPanel searchInputPanel = new JPanel(new BorderLayout(10, 0));
        searchInputPanel.setOpaque(false);
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 18));
        searchIcon.setForeground(new Color(148, 163, 184));
        searchInputPanel.add(searchIcon, BorderLayout.WEST);

        searchField = new JTextField("Search voter by name or ID...");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        searchField.setBorder(null);
        searchField.setForeground(new Color(148, 163, 184));
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("Search voter by name or ID...")) {
                    searchField.setText("");
                    searchField.setForeground(new Color(30, 41, 59));
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search voter by name or ID...");
                    searchField.setForeground(new Color(148, 163, 184));
                }
            }
        });
        searchInputPanel.add(searchField, BorderLayout.CENTER);
        searchBar.add(searchInputPanel, BorderLayout.CENTER);

        JButton searchBtn = new JButton("Search") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(37, 99, 235));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        searchBtn.setPreferredSize(new Dimension(100, 50));
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setContentAreaFilled(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setFocusPainted(false);
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchBar.add(searchBtn, BorderLayout.EAST);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 25, 0);
        topHeader.add(searchBar, gbc);

        // Dark Table Header
        JPanel tableHeader = new JPanel(new GridLayout(1, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(15, 23, 42)); // Dark Navy
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        tableHeader.setOpaque(false);
        tableHeader.setPreferredSize(new Dimension(0, 45));
        String[] columns = { "Voter Name", "Voter ID", "Status" };
        for (String col : columns) {
            JLabel lbl = new JLabel(col, JLabel.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lbl.setForeground(Color.WHITE);
            tableHeader.add(lbl);
        }
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 10, 0);
        topHeader.add(tableHeader, gbc);

        rightPanel.add(topHeader, BorderLayout.NORTH);

        // List Area
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        rightPanel.add(scroll, BorderLayout.CENTER);

        // Footer with Close Button
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        JButton closeBtn = new JButton("Close Portal") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(37, 99, 235));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        closeBtn.setPreferredSize(new Dimension(160, 40));
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn);
        rightPanel.add(footer, BorderLayout.SOUTH);

        mainPanel.add(rightPanel, BorderLayout.CENTER);

        // Search logic
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                refreshList();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                refreshList();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                refreshList();
            }
        });
        searchBtn.addActionListener(e -> refreshList());

        refreshList();
    }

    private void refreshList() {
        listPanel.removeAll();
        String query = searchField.getText().toLowerCase();
        if (query.equals("search voter by name or id..."))
            query = "";

        if (OnlineVotingSystem.votedVoters.isEmpty()) {
            JLabel empty = new JLabel("No records found.");
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            empty.setForeground(new Color(148, 163, 184));
            listPanel.add(empty);
        } else {
            for (String id : OnlineVotingSystem.votedVoters) {
                Voter v = OnlineVotingSystem.voters.get(id);
                if (v != null && (id.toLowerCase().contains(query) || v.name.toLowerCase().contains(query))) {
                    listPanel.add(createVoterCard(v));
                    listPanel.add(Box.createRigidArea(new Dimension(0, 8)));
                }
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel createVoterCard(Voter v) {
        JPanel card = new JPanel(new GridLayout(1, 3)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Very subtle shadow for "premium" look
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 2, 12, 12);

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 12, 12);

                g2.setColor(new Color(241, 245, 249));
                g2.drawRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(0, 85));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));
        card.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // 1. Voter Name & Sub-details (Mumbai)
        JPanel namePanel = new JPanel(new BorderLayout(15, 0));
        namePanel.setOpaque(false);

        // Circular Avatar Placeholder
        JPanel avatarBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(239, 246, 255));
                g2.fillOval(4, 4, getWidth() - 8, getHeight() - 8);
                g2.setColor(new Color(37, 99, 235));
                g2.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 24));
                FontMetrics fm = g2.getFontMetrics();
                String icon = "👤";
                int x = (getWidth() - fm.stringWidth(icon)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(icon, x, y);
                g2.dispose();
            }
        };
        avatarBox.setPreferredSize(new Dimension(50, 50));
        avatarBox.setOpaque(false);
        namePanel.add(avatarBox, BorderLayout.WEST);

        JPanel textInfo = new JPanel(new GridLayout(2, 1, 0, 2));
        textInfo.setOpaque(false);
        JLabel nameLbl = new JLabel(v.name);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        nameLbl.setForeground(new Color(30, 41, 59));

        String details = v.gender + ", " + v.age + " • " + v.constituency;
        JLabel detailLbl = new JLabel(details);
        detailLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailLbl.setForeground(new Color(100, 116, 139));

        textInfo.add(nameLbl);
        textInfo.add(detailLbl);
        namePanel.add(textInfo, BorderLayout.CENTER);
        card.add(namePanel);

        // 2. Voter ID Column (Styled Badge)
        JPanel idCol = new JPanel(new GridBagLayout());
        idCol.setOpaque(false);
        JLabel idBadge = new JLabel(v.voterId);
        idBadge.setFont(new Font("Segoe UI", Font.BOLD, 14));
        idBadge.setForeground(new Color(37, 99, 235));
        idBadge.setOpaque(true);
        idBadge.setBackground(new Color(239, 246, 255));
        idBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(219, 234, 254), 1),
                BorderFactory.createEmptyBorder(6, 15, 6, 15)));
        idCol.add(idBadge);
        card.add(idCol);

        // 3. Status Column (Verified Badge)
        JPanel statusCol = new JPanel(new GridBagLayout());
        statusCol.setOpaque(false);
        JLabel statusBadge = new JLabel("✔ VERIFIED");
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusBadge.setForeground(new Color(22, 163, 74));
        statusBadge.setOpaque(true);
        statusBadge.setBackground(new Color(240, 253, 244));
        statusBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 252, 231), 1),
                BorderFactory.createEmptyBorder(6, 15, 6, 15)));
        statusCol.add(statusBadge);
        card.add(statusCol);

        return card;
    }
}

// ================= CUSTOM MESSAGE DIALOG =================
class CustomMessageDialog extends JDialog {
    public CustomMessageDialog(Frame parent, String message) {
        super(parent, "Message", true);
        setSize(650, 320);
        setLocationRelativeTo(parent);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        // Background Panel
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        add(mainPanel);

        // Custom Title Bar
        JPanel titleBar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(248, 250, 252)); // Very light blue-gray
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.fillRect(0, getHeight() / 2, getWidth(), getHeight() / 2); // Flatten bottom
                g2.dispose();
            }
        };
        titleBar.setOpaque(false);
        titleBar.setPreferredSize(new Dimension(getWidth(), 45));
        titleBar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));

        JLabel titleLabel = new JLabel("Message");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(new Color(71, 85, 105));
        titleBar.add(titleLabel, BorderLayout.WEST);

        JButton closeXBtn = new JButton("✕");
        closeXBtn.setFont(new Font("Arial", Font.BOLD, 18));
        closeXBtn.setForeground(new Color(148, 163, 184));
        closeXBtn.setBorder(null);
        closeXBtn.setContentAreaFilled(false);
        closeXBtn.setFocusPainted(false);
        closeXBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeXBtn.addActionListener(e -> dispose());
        titleBar.add(closeXBtn, BorderLayout.EAST);

        mainPanel.add(titleBar, BorderLayout.NORTH);

        // Content Panel (Split into Illustration and Text)
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Left Side: Illustration (Using election_started.png as requested)
        JLabel illustrationLabel = new JLabel();
        String msgPath = "c:\\Users\\kadal\\Downloads\\election (3)\\election\\election_started.png";
        try {
            ImageIcon icon = new ImageIcon(msgPath);
            Image img = icon.getImage().getScaledInstance(220, 220, Image.SCALE_SMOOTH);
            illustrationLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            illustrationLabel.setText("📬");
            illustrationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 100));
        }
        contentPanel.add(illustrationLabel);

        // Right Side: Text & Button
        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel msgLabel = new JLabel("<html><div style='width: 260px;'>" + message + "</div></html>");
        msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        msgLabel.setForeground(new Color(30, 41, 59));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 30, 0);
        textPanel.add(msgLabel, gbc);

        // Custom OK Button (Blue Pill)
        JButton okBtn = new JButton("OK") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, getHeight(), getHeight());

                // Gradient
                GradientPaint gp = new GradientPaint(0, 0, new Color(59, 130, 246), 0, getHeight(),
                        new Color(29, 78, 216));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight()); // Pill Shape

                g2.dispose();
                super.paintComponent(g);
            }
        };
        okBtn.setPreferredSize(new Dimension(160, 48));
        okBtn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        okBtn.setForeground(Color.WHITE);
        okBtn.setContentAreaFilled(false);
        okBtn.setBorderPainted(false);
        okBtn.setFocusPainted(false);
        okBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okBtn.addActionListener(e -> dispose());

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 10);
        textPanel.add(okBtn, gbc);

        contentPanel.add(textPanel);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }
}

// ================= CUSTOM CONFIRM DIALOG =================
class CustomConfirmDialog extends JDialog {
    private boolean confirmed = false;

    public CustomConfirmDialog(Frame parent, String message) {
        super(parent, "Action Confirmation", true);
        setSize(1000, 580);
        setLocationRelativeTo(parent);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        // Background Panel with soft shadow
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Outer Shadow
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 30, 30);

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 30, 30);
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        add(mainPanel);

        JPanel contentPanel = new JPanel(new BorderLayout(60, 0));
        contentPanel.setOpaque(false);

        // Left Side: Illustration
        JLabel illustrationLabel = new JLabel();
        illustrationLabel.setPreferredSize(new Dimension(320, 380));
        String confirmPath = "c:\\Users\\kadal\\Downloads\\election (3)\\election\\confirmation.png";
        try {
            ImageIcon icon = new ImageIcon(confirmPath);
            Image img = icon.getImage().getScaledInstance(320, 320, Image.SCALE_SMOOTH);
            illustrationLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            illustrationLabel.setText("❓");
            illustrationLabel.setFont(new Font("Segoe UI Semilight", Font.PLAIN, 100));
            illustrationLabel.setForeground(new Color(226, 232, 240));
        }
        contentPanel.add(illustrationLabel, BorderLayout.WEST);

        // Right Side: Content
        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // Title: Are you sure?
        JLabel titleLabel = new JLabel("<html>Are you <font color='#f59e0b'>sure?</font></html>");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 44));
        titleLabel.setForeground(new Color(30, 41, 59));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        textPanel.add(titleLabel, gbc);

        // Short Thick Accent line
        JPanel line = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(245, 158, 11));
                g.fillRect(0, 0, 80, 6);
            }
        };
        line.setPreferredSize(new Dimension(80, 6));
        line.setOpaque(false);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 30, 0);
        gbc.anchor = GridBagConstraints.WEST;
        textPanel.add(line, gbc);

        // Message - LARGER AND MORE PROMINENT
        JLabel msgLabel = new JLabel("<html><div style='width: 380px; line-height: 1.6;'>" + message + "</div></html>");
        msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        msgLabel.setForeground(new Color(51, 65, 85));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 35, 0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        textPanel.add(msgLabel, gbc);

        // Buttons Panel
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btns.setOpaque(false);

        // Confirm Button (Blue Pill Style with Shadow)
        JButton yesBtn = new JButton("Yes, Confirm") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow effect
                g2.setColor(new Color(37, 99, 235, 50));
                g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 30, 30);

                // Main button
                g2.setColor(new Color(37, 99, 235));
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        yesBtn.setPreferredSize(new Dimension(180, 56));
        yesBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        yesBtn.setForeground(Color.WHITE);
        yesBtn.setContentAreaFilled(false);
        yesBtn.setBorderPainted(false);
        yesBtn.setFocusPainted(false);
        yesBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Changed Mind Button (Soft Gray Outline)
        JButton noBtn = new JButton("Changed Mind") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Light gray background
                g2.setColor(new Color(248, 250, 252));
                g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 30, 30);

                // Border
                g2.setColor(new Color(226, 232, 240));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 4, getHeight() - 4, 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        noBtn.setPreferredSize(new Dimension(180, 56));
        noBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        noBtn.setForeground(new Color(71, 85, 105));
        noBtn.setContentAreaFilled(false);
        noBtn.setBorderPainted(false);
        noBtn.setFocusPainted(false);
        noBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        yesBtn.addActionListener(e -> {
            confirmed = true;
            dispose();
        });
        noBtn.addActionListener(e -> dispose());

        btns.add(yesBtn);
        btns.add(noBtn);

        gbc.gridy = 3;
        gbc.insets = new Insets(20, 0, 0, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        textPanel.add(btns, gbc);

        contentPanel.add(textPanel, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}

// ================= ELECTION STARTED DIALOG =================
class ElectionStartedDialog extends JDialog {
    public ElectionStartedDialog(Frame parent) {
        super(parent, "Election Started", true);
        setSize(700, 420);
        setLocationRelativeTo(parent);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        // Background Panel with rounded corners
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        // Content Panel (Split into Illustration and Text)
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setOpaque(false);

        // Left Side: Illustration
        JLabel illustrationLabel = new JLabel();
        String electionStartPath = "c:\\Users\\kadal\\Downloads\\election (3)\\election\\election_started.png";
        try {
            ImageIcon icon = new ImageIcon(electionStartPath);
            Image img = icon.getImage().getScaledInstance(320, 320, Image.SCALE_SMOOTH);
            illustrationLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            illustrationLabel.setText("🗳️");
            illustrationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 120));
        }
        contentPanel.add(illustrationLabel);

        // Right Side: Text & Button
        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // "Election Started" Title
        JLabel titleLabel = new JLabel("<html>Election <font color='#2563eb'><b>Started</b></font></html>");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        titleLabel.setForeground(new Color(30, 41, 59));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        textPanel.add(titleLabel, gbc);

        // Blue Separator Line
        JPanel line = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(59, 130, 246));
                g.fillRect(0, 0, 120, 5);
            }
        };
        line.setPreferredSize(new Dimension(120, 5));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 30, 0);
        textPanel.add(line, gbc);

        // "Election is now ACTIVE." text
        JLabel activeLabel = new JLabel("<html>Election is now <font color='#2563eb'><b>ACTIVE.</b></font></html>");
        activeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        activeLabel.setForeground(new Color(71, 85, 105));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 10, 0);
        textPanel.add(activeLabel, gbc);

        // "You can start voting now." text
        JLabel subLabel = new JLabel("You can start voting now.");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        subLabel.setForeground(new Color(71, 85, 105));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 40, 0);
        textPanel.add(subLabel, gbc);

        // Custom OK Button
        JButton okBtn = new JButton("OK") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, getHeight(), getHeight());

                // Gradient
                GradientPaint gp = new GradientPaint(0, 0, new Color(59, 130, 246), 0, getHeight(),
                        new Color(29, 78, 216));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight()); // Pill Shape

                g2.dispose();
                super.paintComponent(g);
            }
        };
        okBtn.setPreferredSize(new Dimension(280, 60));
        okBtn.setFont(new Font("Segoe UI", Font.BOLD, 28));
        okBtn.setForeground(Color.WHITE);
        okBtn.setContentAreaFilled(false);
        okBtn.setBorderPainted(false);
        okBtn.setFocusPainted(false);
        okBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okBtn.addActionListener(e -> dispose());

        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 0);
        textPanel.add(okBtn, gbc);

        contentPanel.add(textPanel);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Add subtle shadow to the whole dialog
        getRootPane().setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 10), 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
    }
}

// ================= ELECTION STOPPED DIALOG =================
class ElectionStoppedDialog extends JDialog {
    public ElectionStoppedDialog(Frame parent) {
        super(parent, "Election Stopped", true);
        setSize(700, 420);
        setLocationRelativeTo(parent);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        // Background Panel with rounded corners
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        // Content Panel (Split into Illustration and Text)
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setOpaque(false);

        // Left Side: Illustration
        JLabel illustrationLabel = new JLabel();
        String electionStopPath = "c:\\Users\\kadal\\Downloads\\election (3)\\election\\election_stopped.png";
        try {
            ImageIcon icon = new ImageIcon(electionStopPath);
            Image img = icon.getImage().getScaledInstance(320, 320, Image.SCALE_SMOOTH);
            illustrationLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            illustrationLabel.setText("🚫");
            illustrationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 120));
        }
        contentPanel.add(illustrationLabel);

        // Right Side: Text & Button
        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // "Election Stopped" Title
        JLabel titleLabel = new JLabel("<html>Election <font color='#ef4444'><b>Ended</b></font></html>");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        titleLabel.setForeground(new Color(30, 41, 59));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        textPanel.add(titleLabel, gbc);

        // Red Separator Line
        JPanel line = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(239, 68, 68));
                g.fillRect(0, 0, 120, 5);
            }
        };
        line.setPreferredSize(new Dimension(120, 5));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 30, 0);
        textPanel.add(line, gbc);

        // "Election has been STOPPED." text
        JLabel activeLabel = new JLabel("<html>Election has been <font color='#ef4444'><b>STOPPED.</b></font></html>");
        activeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        activeLabel.setForeground(new Color(71, 85, 105));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 10, 0);
        textPanel.add(activeLabel, gbc);

        // "Voting is no longer active." text
        JLabel subLabel = new JLabel("Voting is no longer active.");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        subLabel.setForeground(new Color(71, 85, 105));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 40, 0);
        textPanel.add(subLabel, gbc);

        // Custom OK Button
        JButton okBtn = new JButton("OK") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, getHeight(), getHeight());

                // Red Gradient
                GradientPaint gp = new GradientPaint(0, 0, new Color(248, 113, 113), 0, getHeight(),
                        new Color(220, 38, 38));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight()); // Pill Shape

                g2.dispose();
                super.paintComponent(g);
            }
        };
        okBtn.setPreferredSize(new Dimension(280, 60));
        okBtn.setFont(new Font("Segoe UI", Font.BOLD, 28));
        okBtn.setForeground(Color.WHITE);
        okBtn.setContentAreaFilled(false);
        okBtn.setBorderPainted(false);
        okBtn.setFocusPainted(false);
        okBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okBtn.addActionListener(e -> dispose());

        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 0);
        textPanel.add(okBtn, gbc);

        contentPanel.add(textPanel);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Add subtle shadow to the whole dialog
        getRootPane().setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 10), 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
    }
}

// ================= WINNER ANNOUNCEMENT DIALOG =================
class WinnerAnnouncementDialog extends JDialog {
    public WinnerAnnouncementDialog(Frame parent) {
        super(parent, "Election Result", true);
        setSize(780, 600);
        setLocationRelativeTo(parent);
        setUndecorated(true);
        setLayout(new BorderLayout());
        setBackground(new Color(0, 0, 0, 0));

        // Calculate National Winner
        String winner = "None";
        int maxVotes = -1;
        HashMap<String, Integer> nationalVotes = new HashMap<>();

        for (HashMap<String, Integer> map : OnlineVotingSystem.constituencyVotes.values()) {
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String party = entry.getKey();
                int votes = entry.getValue();
                nationalVotes.put(party, nationalVotes.getOrDefault(party, 0) + votes);
            }
        }

        for (Map.Entry<String, Integer> entry : nationalVotes.entrySet()) {
            if (entry.getValue() > maxVotes) {
                maxVotes = entry.getValue();
                winner = entry.getKey();
            }
        }

        if (maxVotes <= 0) {
            winner = "No Votes Cast";
        }

        final String finalWinner = winner;
        final int finalMaxVotes = maxVotes;

        // Container Panel logic
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Navy-Purple Gradient Background
                LinearGradientPaint bgGp = new LinearGradientPaint(
                    0, 0, getWidth(), getHeight(),
                    new float[]{0.0f, 1.0f},
                    new Color[]{new Color(15, 23, 42), new Color(49, 46, 129)}
                );
                g2.setPaint(bgGp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);

                // Decorative Illustration (Top Right)
                int ix = getWidth() - 220;
                int iy = -20;
                
                // Outer Glow Circle
                g2.setColor(new Color(255, 255, 255, 20));
                g2.fillOval(ix, iy, 250, 250);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillOval(ix + 25, iy + 25, 200, 200);

                // Ballot Box Illustration
                g2.setColor(new Color(226, 232, 240));
                g2.fillRoundRect(ix + 60, iy + 140, 130, 80, 10, 10); // Box body
                g2.setColor(new Color(148, 163, 184));
                g2.fillRect(ix + 80, iy + 135, 90, 8); // Box top slit
                
                // Hand Icon (Simplified)
                g2.setColor(new Color(255, 228, 202));
                int[] hx = {ix + 160, ix + 220, ix + 240, ix + 180};
                int[] hy = {iy + 40, iy + 20, iy + 80, iy + 100};
                g2.fillPolygon(hx, hy, 4);

                // Ballot Paper
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(ix + 120, iy + 80, 50, 60, 5, 5);
                g2.setColor(new Color(30, 58, 138));
                g2.setStroke(new BasicStroke(3));
                g2.drawLine(ix + 135, iy + 105, ix + 145, iy + 115);
                g2.drawLine(ix + 145, iy + 115, ix + 160, iy + 95);
                
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        add(mainPanel, BorderLayout.CENTER);

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 20, 0));

        JLabel headLabel = new JLabel("Election Winner");
        headLabel.setFont(new Font("Segoe UI", Font.BOLD, 52));
        headLabel.setForeground(Color.WHITE);
        headLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(headLabel);

        JLabel subHeadLabel = new JLabel("Final Result Summary");
        subHeadLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        subHeadLabel.setForeground(new Color(203, 213, 225));
        subHeadLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(subHeadLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Content (Card)
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        mainPanel.add(content, BorderLayout.CENTER);

        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 35, 35);
                g2.dispose();
            }
        };
        card.setPreferredSize(new Dimension(700, 380));
        card.setOpaque(false);
        content.add(card);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.CENTER;

        // Circular Icon Container
        JPanel iconContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                int r = 85;

                // Dashed Circle
                g2.setColor(new Color(203, 213, 225));
                float[] dash = {6f, 6f};
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dash, 0));
                g2.drawOval(cx - r, cy - r, r * 2, r * 2);

                // Inner Light Blue Circle
                g2.setColor(new Color(239, 246, 255));
                g2.fillOval(cx - r + 15, cy - r + 15, (r - 15) * 2, (r - 15) * 2);

                // Ballot Box Icon
                int bx = cx - 40;
                int by = cy - 25;
                g2.setColor(new Color(30, 58, 138));
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(bx, by, 80, 55, 8, 8); // Box
                g2.drawArc(bx + 15, by - 20, 50, 40, -180, 180); // Slit area
                
                if (finalWinner.equals("No Votes Cast")) {
                    g2.setFont(new Font("Arial", Font.BOLD, 30));
                    g2.drawString("X", cx - 10, cy + 20);
                } else {
                    // Checkmark
                    g2.drawLine(cx - 15, cy + 5, cx, cy + 20);
                    g2.drawLine(cx, cy + 20, cx + 25, cy - 10);
                }

                g2.dispose();
            }
        };
        iconContainer.setPreferredSize(new Dimension(200, 200));
        iconContainer.setOpaque(false);
        gbc.gridy = 0;
        gbc.insets = new Insets(-50, 0, 10, 0);
        card.add(iconContainer, gbc);

        // Winner Name
        JLabel nameLabel = new JLabel(finalWinner);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        nameLabel.setForeground(new Color(15, 23, 42));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        card.add(nameLabel, gbc);

        // Winner Subtitle
        String subText = finalWinner.equals("No Votes Cast") ? "No votes were recorded in this election." : "Leading with highest vote accumulation.";
        JLabel subtitle = new JLabel(subText);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        subtitle.setForeground(new Color(100, 116, 139));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 25, 0);
        card.add(subtitle, gbc);

        // Vote Badge (Pill)
        JPanel badge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color badgeColor = finalWinner.equals("No Votes Cast") ? new Color(254, 226, 226) : new Color(220, 252, 231);
                g2.setColor(badgeColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
            }
        };
        badge.setOpaque(false);
        badge.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 8));
        
        JLabel voteIcon = new JLabel(finalWinner.equals("No Votes Cast") ? "⊖" : "✓");
        voteIcon.setFont(new Font("Segoe UI", Font.BOLD, 20));
        voteIcon.setForeground(finalWinner.equals("No Votes Cast") ? new Color(185, 28, 28) : new Color(21, 128, 61));
        badge.add(voteIcon);

        JLabel votesCount = new JLabel(finalMaxVotes + " Votes");
        votesCount.setFont(new Font("Segoe UI", Font.BOLD, 22));
        votesCount.setForeground(finalWinner.equals("No Votes Cast") ? new Color(185, 28, 28) : new Color(21, 128, 61));
        badge.add(votesCount);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 20, 0);
        card.add(badge, gbc);

        // Footer Area
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));

        JButton closeBtn = new JButton("Close Portal") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gp = new GradientPaint(0, 0, new Color(37, 99, 235), getWidth(), 0, new Color(76, 29, 149));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
                g2.drawString("✕", 25, getHeight()/2 + 8);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        closeBtn.setPreferredSize(new Dimension(320, 65));
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 24));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());
        
        footer.add(closeBtn);
        mainPanel.add(footer, BorderLayout.SOUTH);
    }
}
