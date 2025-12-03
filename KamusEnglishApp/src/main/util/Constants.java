package main.util;

import java.awt.Color;
import java.awt.Font;

public class Constants {
    public static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    public static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    public static final Color ACCENT_COLOR = new Color(231, 76, 60);
    public static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    public static final Color WARNING_COLOR = new Color(241, 196, 15);
    public static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    public static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 18);
    public static final Font NORMAL_FONT = new Font("Arial", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);
    
    public static final String APP_NAME = "Lexionary - English Indonesian Dictionary";
    public static final String VERSION = "2.0";
    
    public static final String DICTIONARY_FILE = "src/resources/data/dictionary.txt";
    public static final String CORRUPT_OFFICERS_FILE = "src/resources/data/corrupt_officers.txt";
    
    public static final String[] GIMMICK_TYPES = {
        "explosion", "blur", "matrix", "rain", "old", "mouse", "calculator", "game"
    };
}