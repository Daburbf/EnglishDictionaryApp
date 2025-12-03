package main.ui;

import main.ui.components.BlurEffect;
import main.ui.components.CorruptionPopup;
import main.ui.components.ExplosionEffect;
import main.model.Dictionary;
import main.model.Word;
import main.model.RedBlackTree;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import main.ui.components.OldEffect;
import main.ui.components.MatrixRainEffect;

public class DictionaryPanel extends JPanel {
    private MainFrame mainFrame;
    private JTextArea resultArea;
    private JTextField searchField;
    private RedBlackTree englishToIndonesianTree;
    private RedBlackTree indonesianToEnglishTree;
    private Dictionary dictionaryModel;
    private JPanel contentPanel;
    private CardLayout contentCardLayout;
    private JPanel calculatorPanel;
    private JTextField calcDisplay;
    private double calcCurrentValue = 0;
    private String calcCurrentOperator = "";
    private JPanel gamePanel;
    private JLabel gameMessage;
    private JLabel coinLabel;
    private Timer flipTimer;
    private int flipCount = 0;
    private final int TOTAL_FLIPS = 20;
    private boolean isEnglishToIndonesian = true;
    private JToggleButton languageToggle;
    private JButton searchButton;
    private CorruptionPopup corruptionPopup;
    private Timer corruptionTimer;
    private JProgressBar loadingBar;
    private Timer loadingTimer;
    private int loadingProgress = 0;
    private Timer autoSearchTimer;
    private final int AUTO_SEARCH_DELAY = 300;

    public DictionaryPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.englishToIndonesianTree = new RedBlackTree();
        this.indonesianToEnglishTree = new RedBlackTree();
        this.dictionaryModel = new Dictionary();
        initializeUI();
        setupCorruptionEffect();
        startLoadingDictionary();
    }

    private void startLoadingDictionary() {
        loadingBar.setVisible(true);
        loadingTimer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadingProgress += 2;
                loadingBar.setValue(loadingProgress);
                
                if (loadingProgress >= 100) {
                    loadingTimer.stop();
                    loadingBar.setVisible(false);
                    initializeDictionary();
                }
            }
        });
        loadingTimer.start();
    }

    private void initializeDictionary() {
        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                List<Word> allWords = dictionaryModel.getAllWords();
                int total = allWords.size();
                
                for (int i = 0; i < total; i++) {
                    Word word = allWords.get(i);
                    englishToIndonesianTree.insert(word.getEnglish().toLowerCase(), word);
                    indonesianToEnglishTree.insert(word.getIndonesian().toLowerCase(), word);
                    
                    publish((i * 100) / total);
                    Thread.sleep(1);
                }
                return null;
            }
            
            @Override
            protected void process(List<Integer> chunks) {
                if (!chunks.isEmpty()) {
                    int progress = chunks.get(chunks.size() - 1);
                    loadingBar.setValue(progress);
                }
            }
            
            @Override
            protected void done() {
                loadingBar.setValue(100);
                loadingBar.setVisible(false);
            }
        };
        worker.execute();
    }

    private void setupCorruptionEffect() {
        corruptionPopup = new CorruptionPopup(mainFrame);
        corruptionTimer = new Timer(8000, e -> {
            try {
                corruptionPopup.hidePopup();
                java.awt.event.MouseMotionListener[] listeners = mainFrame.getMouseMotionListeners();
                for (java.awt.event.MouseMotionListener listener : listeners) {
                    if (listener != null) {
                        mainFrame.removeMouseMotionListener(listener);
                    }
                }
            } catch (Exception ex) {
            }
        });
        corruptionTimer.setRepeats(false);
    }

    private void showCorruptionEffect() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - corruptionPopup.getWidth()) / 2;
        int y = (screenSize.height - corruptionPopup.getHeight()) / 2;
        corruptionPopup.setLocation(x, y);
        corruptionPopup.showPopup();
        corruptionTimer.restart();
        mainFrame.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                corruptionPopup.followCursor(e.getXOnScreen(), e.getYOnScreen());
            }
            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                corruptionPopup.followCursor(e.getXOnScreen(), e.getYOnScreen());
            }
        });
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(25, 25, 35));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel loadingPanel = createLoadingPanel();
        add(loadingPanel, BorderLayout.NORTH);

        contentCardLayout = new CardLayout();
        contentPanel = new JPanel(contentCardLayout);
        contentPanel.setBackground(new Color(25, 25, 35));

        JPanel dictionaryPanel = createDictionaryPanel();
        calculatorPanel = createCalculatorPanel();
        gamePanel = createGamePanel();

        contentPanel.add(dictionaryPanel, "DICTIONARY");
        contentPanel.add(calculatorPanel, "CALCULATOR");
        contentPanel.add(gamePanel, "GAME");

        add(contentPanel, BorderLayout.CENTER);
        contentCardLayout.show(contentPanel, "DICTIONARY");
        
        setupAutoSearch();
    }

    private JPanel createLoadingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(25, 25, 35));
        panel.setBorder(new EmptyBorder(5, 20, 5, 20));
        panel.setVisible(false);

        JLabel loadingLabel = new JLabel("Loading Dictionary...");
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        loadingLabel.setForeground(new Color(100, 180, 255));

        loadingBar = new JProgressBar(0, 100);
        loadingBar.setForeground(new Color(100, 180, 255));
        loadingBar.setBackground(new Color(40, 40, 50));
        loadingBar.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        panel.add(loadingLabel, BorderLayout.NORTH);
        panel.add(loadingBar, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDictionaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(25, 25, 35));
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 25, 35));
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel headerLabel = new JLabel("DICTIONARY");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerLabel.setForeground(new Color(100, 180, 255));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setBorder(new EmptyBorder(5, 0, 5, 0));

        JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        togglePanel.setBackground(new Color(25, 25, 35));
        togglePanel.setBorder(new EmptyBorder(8, 0, 0, 0));

        languageToggle = new JToggleButton("ENGLISH - INDONESIA");
        languageToggle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        languageToggle.setBackground(new Color(50, 130, 200));
        languageToggle.setForeground(Color.WHITE);
        languageToggle.setFocusPainted(false);
        languageToggle.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        languageToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        languageToggle.setSelected(true);
        isEnglishToIndonesian = true;

        languageToggle.addActionListener(e -> {
            isEnglishToIndonesian = languageToggle.isSelected();
            if (isEnglishToIndonesian) {
                languageToggle.setText("ENGLISH - INDONESIA");
                searchButton.setText("SEARCH");
            } else {
                languageToggle.setText("INDONESIA - ENGLISH");
                searchButton.setText("CARI");
            }
            updateWelcomeMessage();
        });

        togglePanel.add(languageToggle);
        headerPanel.add(headerLabel, BorderLayout.NORTH);
        headerPanel.add(togglePanel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(25, 25, 35));
        centerPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBackground(new Color(25, 25, 35));
        searchPanel.setBorder(new EmptyBorder(0, 0, 12, 0));
        searchPanel.setMaximumSize(new Dimension(600, 45));

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBackground(new Color(40, 40, 50));
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(Color.WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 160, 240), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        searchField.setColumns(30);

        searchButton = new JButton("SEARCH");
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchButton.setBackground(new Color(60, 140, 220));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.setPreferredSize(new Dimension(90, 45));

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        resultArea = new JTextArea();
        resultArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resultArea.setBackground(new Color(35, 35, 45));
        resultArea.setForeground(Color.WHITE);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 80), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        resultArea.setRows(4);
        resultArea.setPreferredSize(new Dimension(150, 40));
        resultArea.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));

        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBackground(new Color(25, 25, 35));
        resultPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        resultPanel.add(resultArea, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(new Color(25, 25, 35));
        footerPanel.setBorder(new EmptyBorder(12, 0, 0, 0));

        JButton backButton = new JButton("Kembali ke Dashboard");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        backButton.setBackground(new Color(60, 60, 80));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        footerPanel.add(backButton);

        centerPanel.add(searchPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        centerPanel.add(resultPanel);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(footerPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch());
        backButton.addActionListener(e -> mainFrame.showDashboard());

        updateWelcomeMessage();

        return panel;
    }

    private void setupAutoSearch() {
        autoSearchTimer = new Timer(AUTO_SEARCH_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autoSearchTimer.stop();
                performAutoSearch();
            }
        });
        autoSearchTimer.setRepeats(false);
        
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                autoSearchTimer.restart();
            }
            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                autoSearchTimer.restart();
            }
            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                autoSearchTimer.restart();
            }
        });
    }

    private void updateWelcomeMessage() {
        if (isEnglishToIndonesian) {
            resultArea.setText("Type the word in English then press ENTER or click the SEARCH button");
        } else {
            resultArea.setText("Ketik kata dalam bahasa Indonesia lalu tekan ENTER atau klik tombol CARI");
        }
    }

    private void performAutoSearch() {
        String raw = searchField.getText();
        if (raw == null) raw = "";
        String searchText = raw.trim().toLowerCase();

        if (searchText.isEmpty()) {
            updateWelcomeMessage();
            return;
        }

        if (searchText.length() >= 1) {
            Word wordResult = null;
            
            if (isEnglishToIndonesian) {
                wordResult = englishToIndonesianTree.search(searchText);
            } else {
                wordResult = indonesianToEnglishTree.search(searchText);
            }

            if (wordResult != null) {
                if (wordResult.hasGimmick()) {
                    executeGimmick(wordResult.getGimmickType(), searchText);
                }
                
                displayWordResult(wordResult);
            } else {
                showPartialMatches(searchText);
            }
        }
    }

    private void performSearch() {
        autoSearchTimer.stop();
        
        try {
            String raw = searchField.getText();
            if (raw == null) raw = "";
            String searchText = raw.trim().toLowerCase();

            if (searchText.isEmpty()) {
                if (isEnglishToIndonesian) {
                    resultArea.setText("ERROR: Masukkan kata dalam bahasa Inggris");
                } else {
                    resultArea.setText("ERROR: Masukkan kata dalam bahasa Indonesia");
                }
                return;
            }

            Word wordResult = null;
            
            if (isEnglishToIndonesian) {
                wordResult = englishToIndonesianTree.search(searchText);
            } else {
                wordResult = indonesianToEnglishTree.search(searchText);
            }

            if (wordResult != null) {
                if (wordResult.hasGimmick()) {
                    executeGimmick(wordResult.getGimmickType(), searchText);
                }
                
                displayWordResult(wordResult);
            } else {
                showPartialMatches(searchText);
            }
        } catch (Exception ex) {
            resultArea.setText("ERROR: Terjadi kesalahan internal. Coba lagi.");
        }
    }

    private void showPartialMatches(String searchText) {
        List<Word> allWords = dictionaryModel.getAllWords();
        StringBuilder result = new StringBuilder();
        result.append("Searching for: ").append(searchText).append("\n\n");
        
        int count = 0;
        for (Word word : allWords) {
            if (isEnglishToIndonesian) {
                if (word.getEnglish().toLowerCase().contains(searchText)) {
                    result.append(word.getEnglish()).append(" - ").append(word.getIndonesian()).append("\n");
                    count++;
                }
            } else {
                if (word.getIndonesian().toLowerCase().contains(searchText)) {
                    result.append(word.getIndonesian()).append(" - ").append(word.getEnglish()).append("\n");
                    count++;
                }
            }
            
            if (count >= 10) {
                result.append("\n... and more");
                break;
            }
        }
        
        if (count == 0) {
            result.append("No matches found.\n");
            result.append("Try typing more letters or check spelling.");
        } else {
            result.append("\nFound ").append(count).append(" matches.");
        }
        
        resultArea.setText(result.toString());
    }

    private void executeGimmick(String gimmickType, String searchText) {
        if (gimmickType == null) return;
        
        switch (gimmickType) {
            case "explosion":
                ExplosionEffect.triggerExplosion(mainFrame);
                break;
                
            case "blur":
                BlurEffect.showBlurEffect(mainFrame);
                break;
                
            case "matrix":
                MatrixRainEffect.triggerMatrixRain(mainFrame);
                break;
                
            case "rain":
                MatrixRainEffect.triggerMatrixRain(mainFrame);
                break;
                
            case "old":
                OldEffect.triggerOldEffect(mainFrame);
                break;
                
            case "mouse":
                showCorruptionEffect();
                break;
                
            case "calculator":
                contentCardLayout.show(contentPanel, "CALCULATOR");
                break;
                
            case "game":
                contentCardLayout.show(contentPanel, "GAME");
                break;
        }
    }

    private void displayWordResult(Word word) {
        StringBuilder result = new StringBuilder();
        
        if (isEnglishToIndonesian) {
            result.append("English: ").append(word.getEnglish()).append("\n");
            result.append("Indonesian: ").append(word.getIndonesian()).append("\n");
            result.append("Category: ").append(word.getCategory()).append("\n");
        } else {
            result.append("Indonesian: ").append(word.getIndonesian()).append("\n");
            result.append("English: ").append(word.getEnglish()).append("\n");
            result.append("Category: ").append(word.getCategory()).append("\n");
        }
        
        if (word.hasDefinition()) {
            result.append("\nEnglish Definition:\n");
            result.append(word.getDefinitionEn()).append("\n\n");
            result.append("Indonesian Definition:\n");
            result.append(word.getDefinitionId()).append("\n");
        }
        
        resultArea.setText(result.toString());
    }

    private JPanel createCalculatorPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(new Color(25, 25, 35));
        panel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel headerLabel = new JLabel("KALKULATOR");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(new Color(255, 200, 60));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        calcDisplay = new JTextField("0");
        calcDisplay.setFont(new Font("Segoe UI", Font.BOLD, 18));
        calcDisplay.setBackground(new Color(40, 40, 50));
        calcDisplay.setForeground(Color.WHITE);
        calcDisplay.setHorizontalAlignment(SwingConstants.RIGHT);
        calcDisplay.setEditable(false);
        calcDisplay.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 200, 60), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        calcDisplay.setPreferredSize(new Dimension(200, 40));

        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 6, 6));
        buttonPanel.setBackground(new Color(25, 25, 35));
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        String[] labels = {
            "C", "Hapus", "%", "/",
            "7", "8", "9", "X",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "0", ".", "=", "←"
        };

        for (String text : labels) {
            JButton b = new JButton(text);
            b.setFont(new Font("Segoe UI", Font.BOLD, 16));
            b.setFocusPainted(false);
            b.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            if (text.equals("=")) {
                b.setBackground(new Color(60, 180, 100));
                b.setForeground(Color.WHITE);
            } else if (text.matches("[0-9\\.]")) {
                b.setBackground(new Color(60, 60, 80));
                b.setForeground(Color.WHITE);
            } else if (text.equals("←")) {
                b.setBackground(new Color(220, 80, 70));
                b.setForeground(Color.WHITE);
            } else if (text.equals("C") || text.equals("Hapus")) {
                b.setBackground(new Color(255, 160, 50));
                b.setForeground(Color.WHITE);
            } else {
                b.setBackground(new Color(80, 80, 100));
                b.setForeground(new Color(255, 200, 60));
            }

            if (text.matches("[0-9]")) {
                b.addActionListener(e -> appendNumber(text));
            } else if (text.equals(".")) {
                b.addActionListener(e -> appendDecimal());
            } else if (text.equals("C")) {
                b.addActionListener(e -> clearCalc());
            } else if (text.equals("Hapus")) {
                b.addActionListener(e -> backspaceCalc());
            } else if (text.equals("=")) {
                b.addActionListener(e -> calculateResult());
            } else if (text.equals("←")) {
                b.addActionListener(e -> contentCardLayout.show(contentPanel, "DICTIONARY"));
            } else if (text.equals("%")) {
                b.addActionListener(e -> percentCalc());
            } else if (text.equals("/") || text.equals("X") || text.equals("-") || text.equals("+")) {
                String op = text.equals("X") ? "*" : text;
                b.addActionListener(e -> setOperator(op));
            }
            buttonPanel.add(b);
        }

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(25, 25, 35));
        top.add(headerLabel, BorderLayout.NORTH);
        top.add(calcDisplay, BorderLayout.CENTER);

        panel.add(top, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);

        return panel;
    }

    private void appendNumber(String num) {
        String text = calcDisplay.getText();
        if ("0".equals(text) || "ERROR".equals(text)) {
            calcDisplay.setText(num);
        } else {
            calcDisplay.setText(text + num);
        }
    }

    private void appendDecimal() {
        String text = calcDisplay.getText();
        if ("ERROR".equals(text)) {
            calcDisplay.setText("0.");
            return;
        }
        if (!text.contains(".")) {
            calcDisplay.setText(text + ".");
        }
    }

    private void clearCalc() {
        calcCurrentValue = 0;
        calcCurrentOperator = "";
        calcDisplay.setText("0");
    }

    private void backspaceCalc() {
        String text = calcDisplay.getText();
        if (text == null || text.length() <= 1) {
            calcDisplay.setText("0");
            return;
        }
        calcDisplay.setText(text.substring(0, text.length() - 1));
    }

    private void percentCalc() {
        try {
            double val = Double.parseDouble(calcDisplay.getText());
            val = val / 100.0;
            calcDisplay.setText(String.valueOf(val));
        } catch (Exception ex) {
            calcDisplay.setText("ERROR");
        }
    }

    private void setOperator(String op) {
        try {
            calcCurrentValue = Double.parseDouble(calcDisplay.getText());
            calcCurrentOperator = op;
            calcDisplay.setText("0");
        } catch (Exception ex) {
            calcDisplay.setText("ERROR");
            calcCurrentOperator = "";
        }
    }

    private void calculateResult() {
        try {
            double second = Double.parseDouble(calcDisplay.getText());
            double result = 0;
            switch (calcCurrentOperator) {
                case "+": result = calcCurrentValue + second; break;
                case "-": result = calcCurrentValue - second; break;
                case "*": result = calcCurrentValue * second; break;
                case "/":
                    if (second == 0) {
                        calcDisplay.setText("ERROR");
                        calcCurrentOperator = "";
                        return;
                    }
                    result = calcCurrentValue / second;
                    break;
                default:
                    result = second;
            }
            if (Double.isInfinite(result) || Double.isNaN(result)) {
                calcDisplay.setText("ERROR");
            } else {
                String out = result % 1 == 0 ? String.valueOf((long) result) : String.valueOf(result);
                calcDisplay.setText(out);
            }
            calcCurrentOperator = "";
            calcCurrentValue = 0;
        } catch (Exception ex) {
            calcDisplay.setText("ERROR");
            calcCurrentOperator = "";
        }
    }

    private JPanel createGamePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(25, 25, 35));
        panel.setBorder(new EmptyBorder(25, 60, 25, 60));

        JLabel headerLabel = new JLabel("FLIP KOIN");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerLabel.setForeground(new Color(80, 220, 120));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel gameContent = new JPanel(new BorderLayout(10, 10));
        gameContent.setBackground(new Color(25, 25, 35));

        JPanel coinPanel = new JPanel(new BorderLayout());
        coinPanel.setBackground(new Color(25, 25, 35));
        coinPanel.setBorder(new EmptyBorder(15, 0, 20, 0));

        coinLabel = new JLabel("🪙", SwingConstants.CENTER);
        coinLabel.setFont(new Font("Segoe UI", Font.PLAIN, 100));
        coinLabel.setForeground(new Color(255, 200, 60));

        JPanel coinContainer = new JPanel(new GridBagLayout());
        coinContainer.setBackground(new Color(25, 25, 35));
        coinContainer.add(coinLabel);

        coinPanel.add(coinContainer, BorderLayout.CENTER);

        gameMessage = new JLabel("Klik 'LEMPAR KOIN' untuk memulai!");
        gameMessage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gameMessage.setForeground(Color.WHITE);
        gameMessage.setHorizontalAlignment(SwingConstants.CENTER);

        JButton flipButton = new JButton("LEMPAR KOIN!");
        flipButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        flipButton.setBackground(new Color(80, 220, 120));
        flipButton.setForeground(Color.WHITE);
        flipButton.setFocusPainted(false);
        flipButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        flipButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton backButton = new JButton("← Kembali ke Kamus");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        backButton.setBackground(new Color(60, 60, 80));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(new Color(25, 25, 35));
        buttonPanel.add(flipButton);
        buttonPanel.add(backButton);

        gameContent.add(gameMessage, BorderLayout.NORTH);
        gameContent.add(coinPanel, BorderLayout.CENTER);
        gameContent.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(headerLabel, BorderLayout.NORTH);
        panel.add(gameContent, BorderLayout.CENTER);

        flipTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flipCount++;
                switch (flipCount % 4) {
                    case 0:
                        coinLabel.setText("🪙");
                        coinLabel.setForeground(new Color(255, 200, 60));
                        break;
                    case 1:
                        coinLabel.setText("⚫");
                        coinLabel.setForeground(Color.WHITE);
                        break;
                    case 2:
                        coinLabel.setText("💰");
                        coinLabel.setForeground(new Color(255, 215, 0));
                        break;
                    case 3:
                        coinLabel.setText("◎");
                        coinLabel.setForeground(new Color(200, 200, 200));
                        break;
                }
                double scale = 1.0 + 0.3 * Math.sin(flipCount * 0.4);
                int fontSize = (int)(100 * scale);
                coinLabel.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));

                if (flipCount >= TOTAL_FLIPS) {
                    flipTimer.stop();
                    boolean finalResult = Math.random() > 0.5;
                    if (finalResult) {
                        coinLabel.setText("🪙");
                        coinLabel.setForeground(new Color(255, 200, 60));
                        coinLabel.setFont(new Font("Segoe UI", Font.PLAIN, 100));
                        gameMessage.setText("HASIL: HEAD!");
                    } else {
                        coinLabel.setText("🪙");
                        coinLabel.setForeground(new Color(80, 160, 240));
                        coinLabel.setFont(new Font("Segoe UI", Font.PLAIN, 100));
                        gameMessage.setText("HASIL: TAIL!");
                    }
                    flipButton.setEnabled(true);
                    flipCount = 0;
                }
            }
        });

        flipButton.addActionListener(e -> startCoinFlip(flipButton));
        backButton.addActionListener(e -> {
            contentCardLayout.show(contentPanel, "DICTIONARY");
        });

        return panel;
    }

    private void startCoinFlip(JButton flipButton) {
        if (gamePanel == null) {
            Component[] comps = contentPanel.getComponents();
            for (Component c : comps) {
                if (c instanceof JPanel) {
                    JPanel p = (JPanel) c;
                    if (p.getComponents().length > 0) {
                        gamePanel = p;
                        break;
                    }
                }
            }
        }
        if (flipButton != null) flipButton.setEnabled(false);
        flipCount = 0;
        gameMessage.setText("Koin sedang berputar...");
        coinLabel.setFont(new Font("Segoe UI", Font.PLAIN, 100));
        flipTimer.start();
    }
}