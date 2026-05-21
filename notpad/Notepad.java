import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JFileChooser;
import java.io.FileWriter;
import java.io.IOException;




public class Notepad {
    public static void main(String[] args){

        JFrame frame = new JFrame("My Notepad");

        JTextArea textArea =  new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);

        frame.add(scrollPane);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");

        saveItem.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showSaveDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION){
            try{
                FileWriter writer = new FileWriter(chooser.getSelectedFile());

                writer.write(textArea.getText());
                writer.close();
                frame.setTitle("My Notepad - " + chooser.getSelectedFile().getName());
            }catch (IOException ex){
                System.out.println("Error saving: " + ex.getMessage());
            }
        }
        });

        openItem.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(frame);

            if (result == JFileChooser.APPROVE_OPTION){
                try{

                    java.io.FileReader reader = new java.io.FileReader(chooser.getSelectedFile());
                    java.io.BufferedReader br = new java.io.BufferedReader(reader);

                    textArea.setText("");

                    String line;
                    while((line = br.readLine()) != null){
                        textArea.append(line + "\n");
                    }

                    br.close();
                    reader.close();
                    frame.setTitle("My Notepad - " + chooser.getSelectedFile().getName());
                    

                } catch (IOException ex){

                    System.out.println("Error opening: " + ex.getMessage());
                }
            }
        });

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);


        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}