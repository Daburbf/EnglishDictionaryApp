package main.service;

import main.ui.components.CorruptionPopup;
import javax.swing.*;
import java.awt.*;

public class CorruptionService {
    
    public static void triggerCorruptionEffect(Component parent) {
        Window window = null;
        if (parent instanceof Window) {
            window = (Window) parent;
        } else {
            window = SwingUtilities.getWindowAncestor(parent);
        }
        
        if (window != null) {
            CorruptionPopup popup = new CorruptionPopup(window);
            popup.showPopup();
        }
    }
}