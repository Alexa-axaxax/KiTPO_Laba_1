import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.util.Scanner;

import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class GUI extends JFrame {
    private VerticalTree<PolarPoint> verticalTree;
    private JTextArea treeArea;
    private JTextArea outputArea;

    private JTextField polarDistanceTextField;
    private JTextField polarAngleTextField;
    private JTextField indexTextField;

    private JButton addButton;
    private JButton getButton;
    private JButton removeButton;
    private JButton balanceButton;
    private JButton savebutton;
    private JButton loadButton;
    public GUI() {
        setTitle("Polar point vertical tree");
        setPreferredSize(new Dimension(600, 600));
        setResizable(false);

        verticalTree = new VerticalTree<>();

        getContentPane().setLayout(new BorderLayout());
        treeArea = new JTextArea();
        treeArea.setEditable(false);
        treeArea.setBorder(BorderFactory.createLineBorder(Color.black));
        JScrollPane treeScrollPane = new JScrollPane(treeArea);
        treeScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        treeScrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(treeScrollPane, BorderLayout.CENTER);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setRows(5);
        outputArea.setBorder(BorderFactory.createLineBorder(Color.black));
        outputArea.setLineWrap(true);
        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        outputScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        outputScrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(outputScrollPane, BorderLayout.SOUTH);

        JPanel controlPanel = new JPanel();
        GridLayout gridLayout = new GridLayout(4, 1);
        gridLayout.setVgap(30);
        controlPanel.setLayout(gridLayout);
        controlPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        getContentPane().add(controlPanel, BorderLayout.EAST);


        JPanel inputButtonPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Polar distance:"));
        polarDistanceTextField = new JTextField();
        inputPanel.add(polarDistanceTextField);
        inputPanel.add(new JLabel("Polar angle:"));
        polarAngleTextField = new JTextField();
        inputPanel.add(polarAngleTextField);

        addButton = new JButton("Add point");
        inputButtonPanel.add(inputPanel, BorderLayout.CENTER);
        inputButtonPanel.add(addButton, BorderLayout.SOUTH);
        inputButtonPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        addButton.addActionListener(e -> {
            double distance = -1;
            try {
                distance = Double.parseDouble(polarDistanceTextField.getText());
            }
            catch (Exception ex) {
                addErrorMessage("Can not parse double from polar point distance");
                return;
            }

            double angle = -1;
            try {
                angle = Double.parseDouble(polarAngleTextField.getText());
            }
            catch (Exception ex) {
                addErrorMessage("Can not parse double from polar point angle");
                return;
            }
            PolarPoint p = new PolarPoint(distance, angle);
            addMessage("Point " + p + " was added to the tree");
            verticalTree.add(p);
            updateTreeView();
        });
        controlPanel.add(inputButtonPanel);

        JPanel elementPanel = new JPanel(new GridLayout(3, 1));
        indexTextField = new JTextField();
        indexTextField.setHorizontalAlignment(SwingConstants.CENTER);
        elementPanel.add(indexTextField);
        getButton = new JButton("Get at index");
        getButton.addActionListener(e -> {
            try {
                int index = java.lang.Integer.parseInt(indexTextField.getText());
                PolarPoint p = verticalTree.get(index);
                addMessage("Point " + p + " is at index " + index);
            }
            catch (IndexOutOfBoundsException ex) {
                addErrorMessage("Input index is out of bounds");
            }
            catch (Exception ex) {
                addErrorMessage("Can not parse index from input index field");
            }
        });
        elementPanel.add(getButton);
        removeButton = new JButton("Remove at index");
        removeButton.addActionListener(e -> {
            try {
                int index = java.lang.Integer.parseInt(indexTextField.getText());
                PolarPoint p = verticalTree.remove(index);
                updateTreeView();
                addMessage("Point " + p + " removed from index " + index);
            }
            catch (IndexOutOfBoundsException ex) {
                addErrorMessage("Input index is out of bounds");
            }
            catch (Exception ex) {
                addErrorMessage("Can not parse index from input index field");
            }
        });
        elementPanel.add(removeButton);
        elementPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        controlPanel.add(elementPanel);

        JPanel buttonPanel = new JPanel(new GridLayout(3,1));
        balanceButton = new JButton("Balance tree");
        buttonPanel.add(balanceButton);
        balanceButton.addActionListener(e -> {
            verticalTree.balance();
            updateTreeView();
            addMessage("Tree was balanced");
        });
        buttonPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        savebutton = new JButton("Save tree");
        buttonPanel.add(savebutton);
        savebutton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(".");
            fileChooser.showSaveDialog(this);
            try (PrintWriter writer = new PrintWriter(fileChooser.getSelectedFile())) {
                writer.print(verticalTree.serialize());
            } catch (Exception ex) {
                addErrorMessage("Can not save to file: " + ex.getMessage());
            }
        });
        loadButton = new JButton("Load tree");
        buttonPanel.add(loadButton);
        loadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(".");
            fileChooser.showOpenDialog(this);
            try (Scanner scanner = new Scanner(fileChooser.getSelectedFile())) {
                StringBuilder builder = new StringBuilder();
                while(scanner.hasNextLine()) {
                    builder.append(scanner.nextLine()).append(System.lineSeparator());
                }
                verticalTree = VerticalTree.deserialize(builder.toString(), PolarPoint.class);
                updateTreeView();
            } catch (Exception ex) {
                addErrorMessage("Can not load from file: " + ex.getMessage());
            }
        });
        controlPanel.add(buttonPanel);

        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
    }

    private void updateTreeView() {
        treeArea.setText(verticalTree.toString());
    }

    private void addMessage(String message) {
        outputArea.setText(outputArea.getText() + message + System.lineSeparator());
    }

    private void addErrorMessage(String message) {
        outputArea.setText(outputArea.getText() + "ERROR!: " + message + System.lineSeparator());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }
}
