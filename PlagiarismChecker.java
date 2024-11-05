package PlagiarismCheckerUI;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.net.URL;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PlagiarismChecker extends Frame implements ActionListener {
    TextArea inputText;
    Button checkButton;
    Label resultLabel;
    BufferedImage backgroundImage;

    // Sample reference text
    String referenceText = "When the big clock at the train station stopped, the leaves kept falling" +
            " This blew my mind in high school, and I wasn’t the only one." + " Knowledge is power";

    public PlagiarismChecker() {
        setTitle("Plagiarism Checker");
        setSize(500, 400);
        setLayout(new GridBagLayout()); // Use GridBagLayout for better control over component positioning
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding

        inputText = new TextArea(10, 40); // Increased size
        checkButton = new Button("Check for Plagiarism");
        resultLabel = new Label("");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Make result text bold and increase size

        // Load the background image from the provided URL
        try {
            URL url = new URL("http://www.bloggeron.net/wp-content/uploads/2020/11/Plagiarism-Checker.jpeg");
            backgroundImage = ImageIO.read(url);
            System.out.println("Image loaded successfully.");
        } catch (IOException e) {
            System.err.println("Error loading image: " + e.getMessage());
        }

        checkButton.addActionListener(this);
        checkButton.setBackground(Color.LIGHT_GRAY); // Button color
        checkButton.setFont(new Font("Arial", Font.BOLD, 14)); // Button font

        // Center the components in the GridBagLayout
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(inputText, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(checkButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(resultLabel, gbc);

        setVisible(true);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dispose();
            }
        });
    }

    @Override
    public void update(Graphics g) {
        paint(g); // Override update to ensure proper redrawing
    }

    @Override
    public void paint(Graphics g) {
        // Draw the background image
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        // Draw components after the background to ensure they appear on top
        paintComponents(g);
    }

    public void actionPerformed(ActionEvent e) {
        String text = inputText.getText();
        double plagiarismPercentage = checkForPlagiarism(text);

        // Update the result label based on the plagiarism percentage
        if (plagiarismPercentage > 25) {
            resultLabel.setText("Plagiarism detected: " + plagiarismPercentage + "%");
        } else {
            resultLabel.setText("No plagiarism: " + plagiarismPercentage + "%");
        }
    }

    private double checkForPlagiarism(String text) {
        Map<String, Integer> refWordCount = getWordFrequency(referenceText);
        Map<String, Integer> inputWordCount = getWordFrequency(text);

        double cosineSimilarity = calculateCosineSimilarity(refWordCount, inputWordCount);
        return cosineSimilarity * 100; // Convert to percentage
    }

    private Map<String, Integer> getWordFrequency(String text) {
        Map<String, Integer> wordCount = new HashMap<>();
        String[] words = text.toLowerCase().replaceAll("[^a-zA-Z0-9 ]", "").split("\\s+");
        for (String word : words) {
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }
        return wordCount;
    }

    private double calculateCosineSimilarity(Map<String, Integer> refCount, Map<String, Integer> inputCount) {
        double dotProduct = 0.0;
        double refMagnitude = 0.0;
        double inputMagnitude = 0.0;

        for (String word : refCount.keySet()) {
            int refFreq = refCount.get(word);
            int inputFreq = inputCount.getOrDefault(word, 0);
            dotProduct += refFreq * inputFreq;
            refMagnitude += Math.pow(refFreq, 2);
        }

        for (int freq : inputCount.values()) {
            inputMagnitude += Math.pow(freq, 2);
        }

        refMagnitude = Math.sqrt(refMagnitude);
        inputMagnitude = Math.sqrt(inputMagnitude);

        if (refMagnitude == 0 || inputMagnitude == 0) {
            return 0.0; // Avoid division by zero
        }

        return dotProduct / (refMagnitude * inputMagnitude);
    }

    public static void main(String[] args) {
        new PlagiarismChecker();
    }
}