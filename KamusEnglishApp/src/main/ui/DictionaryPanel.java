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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private String currentCalcInput = "";
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

    public DictionaryPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.englishToIndonesianTree = new RedBlackTree();
        this.indonesianToEnglishTree = new RedBlackTree();
        this.dictionaryModel = new Dictionary();
        initializeDictionary();
        initializeUI();
        setupCorruptionEffect();
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

    private void initializeDictionary() {
        try {
            List<Word> allWords = dictionaryModel.getAllWords();
            if (allWords != null) {
                for (Word word : allWords) {
                    if (word.getEnglish() != null && word.getIndonesian() != null) {
                        englishToIndonesianTree.insert(word.getEnglish().toLowerCase(), word.getIndonesian());
                        indonesianToEnglishTree.insert(word.getIndonesian().toLowerCase(), word.getEnglish());
                    }
                }
            }
        } catch (Exception ex) {
        }

        englishToIndonesianTree.insert("blur", "Buram, Kabur");
        englishToIndonesianTree.insert("calculator", "Menampilkan kalkulator");
        englishToIndonesianTree.insert("game", "Memulai koin flip");
        englishToIndonesianTree.insert("explosion", "Ledakan, Letusan");
        englishToIndonesianTree.insert("boom", "Ledakan, Dentuman");
        englishToIndonesianTree.insert("rain", "Hujan");
        englishToIndonesianTree.insert("matrix", "Matriks");
        englishToIndonesianTree.insert("digital", "Digital");
        englishToIndonesianTree.insert("mouse", "Tikus");

        indonesianToEnglishTree.insert("buram", "Blurry");
        indonesianToEnglishTree.insert("ledakan", "Explosion");
        indonesianToEnglishTree.insert("lengkap", "Complete");
        indonesianToEnglishTree.insert("hujan", "Rain");
        indonesianToEnglishTree.insert("matriks", "Matrix");
        indonesianToEnglishTree.insert("digital", "Digital");
        indonesianToEnglishTree.insert("tikus", "Mouse");
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(25, 25, 35));
        setBorder(new EmptyBorder(10, 10, 10, 10));

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

        languageToggle = new JToggleButton("ENGLISH → INDONESIA");
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
                languageToggle.setText("ENGLISH → INDONESIA");
                searchButton.setText("SEARCH");
            } else {
                languageToggle.setText("INDONESIA → ENGLISH");
                searchButton.setText("CAR");
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

        JButton backButton = new JButton("← Kembali ke Dashboard");
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

    private void updateWelcomeMessage() {
        if (isEnglishToIndonesian) {
            resultArea.setText("Type the word in English then press ENTER or click the SEARCH button");
        } else {
            resultArea.setText("Ketik kata dalam bahasa Indonesia lalu tekan ENTER atau klik tombol CARI");
        }
    }

    private void performSearch() {
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

            if (isEnglishToIndonesian) {
                if (indonesianToEnglishTree.search(searchText) != null) {
                    resultArea.setText("ERROR: Mode English → Indonesia\nKata '" + searchText + "' adalah kata Indonesia\nSilahkan ubah ke mode Indonesia → English");
                    return;
                }
            } else {
                if (englishToIndonesianTree.search(searchText) != null) {
                    resultArea.setText("ERROR: Mode Indonesia → English\nKata '" + searchText + "' adalah kata Inggris\nSilahkan ubah ke mode English → Indonesia");
                    return;
                }
            }

            if (searchText.equals("explosion") || searchText.equals("ledakan") ||
                searchText.equals("boom") || searchText.equals("complete") ||
                searchText.equals("finished") || searchText.equals("selesai") ||
                searchText.equals("master") || searchText.equals("vocab") ||
                searchText.equals("dictionary") || searchText.equals("kamus")) {
                ExplosionEffect.triggerExplosion(mainFrame);
                displayExplosionEffect(searchText);
                return;
            }

            if (searchText.equals("mouse") || searchText.equals("tikus")) {
                showCorruptionEffect();
                displayCorruptionEffect(searchText);
                return;
            }

            if (searchText.equals("blur") || searchText.equals("buram")) {
                BlurEffect.showBlurEffect(mainFrame);
                displayBlurEffect(searchText);
                return;
            }

            if (searchText.equals("old") || searchText.equals("tua")) { 
                OldEffect.triggerOldEffect(mainFrame);
                displayOldEffect(searchText);
                return;
            }

            if (searchText.equals("rain") || searchText.equals("hujan")) {
                MatrixRainEffect.triggerMatrixRain(mainFrame);
                if (isEnglishToIndonesian && searchText.equals("rain")) {
                    displaySearchResult("rain", "Hujan");
                } else if (!isEnglishToIndonesian && searchText.equals("hujan")) {
                    displaySearchResult("hujan", "Rain");
                } else {
                    if (isEnglishToIndonesian) {
                        resultArea.setText("ERROR: Mode English → Indonesia\nKata '" + searchText + "' adalah kata Indonesia\nSilahkan ubah ke mode Indonesia → English");
                    } else {
                        resultArea.setText("ERROR: Mode Indonesia → English\nKata '" + searchText + "' adalah kata Inggris\nSilahkan ubah ke mode English → Indonesia");
                    }
                }
                return;
            }

            switch (searchText) {
                case "calculator":
                case "kalkulator":
                    contentCardLayout.show(contentPanel, "CALCULATOR");
                    resultArea.setText("");
                    return;

                case "game":
                case "permainan":
                    contentCardLayout.show(contentPanel, "GAME");
                    resultArea.setText("");
                    return;

                case "matrix":
                case "matriks":
                case "digital":
                    MatrixRainEffect.triggerMatrixRain(mainFrame);
                    if (isEnglishToIndonesian) {
                        if (searchText.equals("matrix")) displaySearchResult("matrix", "Matriks");
                        else if (searchText.equals("digital")) displaySearchResult("digital", "Digital");
                        else displaySearchResult("matriks", "Matrix");
                    } else {
                        if (searchText.equals("matriks")) displaySearchResult("matriks", "Matrix");
                        else if (searchText.equals("digital")) displaySearchResult("digital", "Digital");
                        else displaySearchResult("matrix", "Matriks");
                    }
                    return;

                default:
                    String meaning = null;
                    if (isEnglishToIndonesian) {
                        meaning = englishToIndonesianTree.search(searchText);
                        if (meaning != null) {
                            displaySearchResult(searchText, meaning);
                            return;
                        }
                    } else {
                        meaning = indonesianToEnglishTree.search(searchText);
                        if (meaning != null) {
                            displaySearchResult(searchText, meaning);
                            return;
                        }
                    }
                    displayNotFound(searchText);
            }
        } catch (Exception ex) {
            resultArea.setText("ERROR: Terjadi kesalahan internal. Coba lagi.");
        }
    }

    private void displayExplosionEffect(String searchText) {
        String word = searchText.toUpperCase();
        String languageFrom = isEnglishToIndonesian ? "English" : "Indonesian";
        String languageTo = isEnglishToIndonesian ? "Indonesian" : "English";
        String translation = isEnglishToIndonesian ? "Ledakan, Letusan" : "Explosion, Blast";
        String result = "DICTIONARY\n" +
                       "Word: " + word + "\n" +
                       languageFrom + ": " + searchText + "\n" +
                       languageTo + ": " + translation;
        resultArea.setText(result);
    }

    private void displayCorruptionEffect(String searchText) {
        String word = searchText.toUpperCase();
        String languageFrom = isEnglishToIndonesian ? "English" : "Indonesian";
        String languageTo = isEnglishToIndonesian ? "Indonesian" : "English";
        String translation = isEnglishToIndonesian ? "Tikus" : "Mouse";
        String result = "DICTIONARY\n" +
                       "Word: " + word + "\n" +
                       languageFrom + ": " + searchText + "\n" +
                       languageTo + ": " + translation;
        resultArea.setText(result);
    }

    private void displayBlurEffect(String searchText) {
        String word = searchText.toUpperCase();
        String languageFrom = isEnglishToIndonesian ? "English" : "Indonesian";
        String languageTo = isEnglishToIndonesian ? "Indonesian" : "English";
        String translation = isEnglishToIndonesian ? "Buram, Kabur" : "Blurry";
        String result = "DICTIONARY\n" +
                       "Word: " + word + "\n" +
                       languageFrom + ": " + searchText + "\n" +
                       languageTo + ": " + translation;
        resultArea.setText(result);
    }

    private void displaySearchResult(String searchText, String meaning) {
        String word = searchText.toUpperCase();
        String languageFrom = isEnglishToIndonesian ? "English" : "Indonesian";
        String languageTo = isEnglishToIndonesian ? "Indonesian" : "English";
        String result = "DICTIONARY\n" +
                       "Word: " + word + "\n" +
                       languageFrom + ": " + searchText + "\n" +
                       languageTo + ": " + meaning;
        resultArea.setText(result);
    }

    private void displayOldEffect(String searchText) {
        String word = searchText.toUpperCase();
        String languageFrom = isEnglishToIndonesian ? "English" : "Indonesian";
        String languageTo = isEnglishToIndonesian ? "Indonesian" : "English";
        String translation = isEnglishToIndonesian ? "Kuno" : "Old";
        String result = "DICTIONARY\n" +
                       "Word: " + word + "\n" +
                       languageFrom + ": " + searchText + "\n" +
                       languageTo + ": " + translation;
        resultArea.setText(result);
    }

    private void displayNotFound(String searchText) {
        String word = searchText.toUpperCase();
        if (isEnglishToIndonesian) {
            String result = "DICTIONARY\n" +
                          "Word: " + word + "\n" +
                          "ERROR: Kata '" + searchText + "' tidak ditemukan dalam kamus";
            resultArea.setText(result);
        } else {
            String result = "DICTIONARY\n" +
                          "Word: " + word + "\n" +
                          "ERROR: Word '" + searchText + "' not found in dictionary";
            resultArea.setText(result);
        }
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
        currentCalcInput = "";
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

        JLabel headerLabel = new JLabel("KOIN FLIP");
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