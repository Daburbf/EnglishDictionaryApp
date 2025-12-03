package main.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import main.service.CalculatorService;
import main.util.Constants;

public class CalculatorPanel extends JPanel {
    private CalculatorService calculatorService;
    private JTextField display;
    private String currentInput;
    private boolean newInput;
    
    public CalculatorPanel() {
        this.calculatorService = new CalculatorService();
        this.currentInput = "";
        this.newInput = true;
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Constants.BACKGROUND_COLOR);

        JPanel headerPanel = createHeaderPanel();

        JPanel calculatorPanel = createCalculatorPanel();
        
        add(headerPanel, BorderLayout.NORTH);
        add(calculatorPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Constants.SUCCESS_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("KALKULATOR");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        JButton backButton = new JButton("Kembali ke Dashboard");
        backButton.setFont(Constants.BUTTON_FONT);
        backButton.setBackground(Constants.WARNING_COLOR);
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof MainFrame) {
                ((MainFrame) window).showDashboard();
            }
        });
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(backButton, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createCalculatorPanel() {
        JPanel calculatorPanel = new JPanel(new BorderLayout());
        calculatorPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));
        calculatorPanel.setBackground(Constants.BACKGROUND_COLOR);

        display = new JTextField("0");
        display.setFont(new Font("Arial", Font.BOLD, 24));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setBackground(Color.WHITE);
        display.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JPanel buttonPanel = createButtonPanel();
        
        calculatorPanel.add(display, BorderLayout.NORTH);
        calculatorPanel.add(buttonPanel, BorderLayout.CENTER);
        
        return calculatorPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 10, 10));
        buttonPanel.setBackground(Constants.BACKGROUND_COLOR);
        
        String[] buttons = {
            "C", "Hapus", "%", "/",
            "7", "8", "9", "*",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "0", ".", "=", "±"
        };
        
        for (String text : buttons) {
            JButton button = createCalculatorButton(text);
            buttonPanel.add(button);
        }
        
        return buttonPanel;
    }
    
    private JButton createCalculatorButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (text.matches("[0-9.]")) {
            button.setBackground(Color.WHITE);
            button.setForeground(Color.BLACK);
        } else if (text.equals("=")) {
            button.setBackground(Constants.SUCCESS_COLOR);
            button.setForeground(Color.WHITE);
        } else if (text.equals("C")) {
            button.setBackground(Constants.ACCENT_COLOR);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(Constants.PRIMARY_COLOR);
            button.setForeground(Color.WHITE);
        }
        
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        button.addActionListener(new CalculatorButtonListener());
        
        return button;
    }
    
    private class CalculatorButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            
            switch (command) {
                case "C":
                    currentInput = "";
                    display.setText("0");
                    newInput = true;
                    break;
                    
                case "Hapus":
                    if (currentInput.length() > 0) {
                        currentInput = currentInput.substring(0, currentInput.length() - 1);
                        display.setText(currentInput.isEmpty() ? "0" : currentInput);
                    }
                    break;
                    
                case "=":
                    calculateResult();
                    break;
                    
                case "±":
                    if (!currentInput.isEmpty() && !currentInput.equals("0")) {
                        if (currentInput.startsWith("-")) {
                            currentInput = currentInput.substring(1);
                        } else {
                            currentInput = "-" + currentInput;
                        }
                        display.setText(currentInput);
                    }
                    break;
                    
                case "%":
                    if (!currentInput.isEmpty()) {
                        try {
                            double value = Double.parseDouble(currentInput);
                            value /= 100;
                            currentInput = String.valueOf(value);
                            display.setText(currentInput);
                        } catch (NumberFormatException ex) {
                            display.setText("Error");
                        }
                    }
                    break;
                    
                default:
                    if (newInput) {
                        currentInput = command;
                        newInput = false;
                    } else {
                        currentInput += command;
                    }
                    display.setText(currentInput);
                    break;
            }
        }
        
        private void calculateResult() {
            try {
                String result = calculatorService.evaluateExpression(currentInput);
                display.setText(result);
                currentInput = result;
                newInput = true;
            } catch (Exception ex) {
                display.setText("Error");
                currentInput = "";
                newInput = true;
            }
        }
    }
}