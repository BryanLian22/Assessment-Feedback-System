package App;

import GUI.LoginFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting application...");
        System.out.println("Launching GUI...");
        
        // Ensure GUI runs on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Creating LoginFrame...");
                LoginFrame loginFrame = new LoginFrame();
                System.out.println("LoginFrame created successfully");
                
                System.out.println("Setting frame visible...");
                loginFrame.setVisible(true);
                loginFrame.toFront();
                loginFrame.requestFocus();
                loginFrame.setAlwaysOnTop(true);
                loginFrame.setAlwaysOnTop(false); // Remove always on top after bringing to front
                System.out.println("GUI should now be visible!");
                
            } catch (Exception e) {
                System.err.println("ERROR: Could not launch GUI!");
                System.err.println("Error message: " + e.getMessage());
                e.printStackTrace();
                
                javax.swing.JOptionPane.showMessageDialog(null, 
                    "Error launching GUI application:\n" + e.getMessage() + 
                    "\n\nCheck console for details.", 
                    "Application Error", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });
        
        System.out.println("Main method completed. GUI should appear shortly...");
    }
}
