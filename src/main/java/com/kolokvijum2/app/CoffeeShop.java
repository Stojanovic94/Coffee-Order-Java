package com.kolokvijum2.app;

import javax.swing.*;

import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;
import java.util.regex.Pattern;

public class CoffeeShop {
    private JFrame frame;
    JTextField nameField = new JTextField("");
    JComboBox<String> coffeeTypeField = new JComboBox<>(
            new String[] { "Espreso", "Kupućino", "Makijato", "Late", "Moka" });
    JRadioButton blendArabicaRadio = new JRadioButton("Arabica 100%");
    JRadioButton blendBalkanRadio = new JRadioButton("Balkan Blend");
    JCheckBox milkCheckBox = new JCheckBox("Mleko");
    JCheckBox creamCheckBox = new JCheckBox("Šlag");
    JCheckBox rumCheckBox = new JCheckBox("Rum");
    JCheckBox cinnamonCheckBox = new JCheckBox("Cimet");
    JCheckBox espressoShootCheckBox = new JCheckBox("Dodatni espreso šot");
    JLabel sugarLabel = new JLabel("Šečer");
    JTextField sugarField = new JTextField("0", 2);
    ButtonGroup blendGroup = new ButtonGroup();
    JLabel totalLabel = new JLabel("");
    ImageIcon logo = new ImageIcon("coffee.png");
    JLabel logoLabel = new JLabel(logo);

    public CoffeeShop() {
        frame = new JFrame("Akademska kafica");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 800);
        frame.setLayout(new BorderLayout());
        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(BorderFactory.createTitledBorder("Naručivanje kafe"));
        inputPanel.setLayout(new GridLayout(19, 1, 20, 0));

        JButton submitButton = new JButton("Nova poružbina");
        JButton resetButton = new JButton("Reset");
        submitButton.addActionListener(e -> submitOrder());
        resetButton.addActionListener(e -> resetFields());
        inputPanel.add(new JLabel("Ime"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Tip kafe"));
        inputPanel.add(coffeeTypeField);
        sugarField.setHorizontalAlignment(JTextField.CENTER);
        sugarField.setEditable(false);
        JButton minusButton = new JButton("−");
        JButton plusButton = new JButton("+");
        minusButton.addActionListener(e -> decreaseSugarAmount());
        plusButton.addActionListener(e -> increaseSugarAmount());

        JPanel sugarPanel = new JPanel(new GridLayout(1, 3, 0, 0));
        sugarPanel.add(sugarLabel);
        sugarPanel.add(minusButton);
        sugarPanel.add(sugarField);
        sugarPanel.add(plusButton);
        inputPanel.add(sugarPanel);
        inputPanel.add(new JLabel());

        blendGroup.add(blendArabicaRadio);
        blendGroup.add(blendBalkanRadio);
        inputPanel.add(new JLabel("Blend"));
        inputPanel.add(blendArabicaRadio);
        inputPanel.add(blendBalkanRadio);
        inputPanel.add(new JLabel("Dodaci za kafu")); // Section label
        inputPanel.add(milkCheckBox);
        inputPanel.add(creamCheckBox);
        inputPanel.add(rumCheckBox);
        inputPanel.add(cinnamonCheckBox);
        inputPanel.add(espressoShootCheckBox);
        inputPanel.add(new JLabel());
        inputPanel.add(submitButton);
        inputPanel.add(resetButton);
        inputPanel.add(totalLabel);
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.setVisible(true);
    }

    public void resetFields() {
        blendGroup.clearSelection();
        milkCheckBox.setSelected(false);
        creamCheckBox.setSelected(false);
        rumCheckBox.setSelected(false);
        cinnamonCheckBox.setSelected(false);
        espressoShootCheckBox.setSelected(false);
        coffeeTypeField.setSelectedIndex(0);
        totalLabel.setText("");
    }

    public void submitOrder() {
        Order order = new Order(nameField.getText(), getSelectedCoffeeType(), Double.parseDouble(sugarField.getText()),
                getSelectedBlend(), getSelectedAdditions());
        totalLabel.setText("Total " + order.submitOrder());
        OrderExporter txtExporter = OrderExporterFactory.createExporter("TXT");
        OrderExporter jsonExporter = OrderExporterFactory.createExporter("JSON");
        txtExporter.export(order);
        jsonExporter.export(order);

    }

    public String getSelectedBlend() {
        if (blendArabicaRadio.isSelected()) {
            return blendArabicaRadio.getText();
        } else if (blendBalkanRadio.isSelected()) {
            return blendBalkanRadio.getText();
        } else {
            return "";
        }
    }

    public void decreaseSugarAmount() {
        int sugarQuantity = Integer.parseInt(sugarField.getText());
        if (sugarQuantity > 0) {
            sugarQuantity--;
            sugarField.setText(String.valueOf(sugarQuantity));
        }
    }

    public void increaseSugarAmount() {
        int sugarQuantity = Integer.parseInt(sugarField.getText());
        if (sugarQuantity < 5) {
            sugarQuantity++;
            sugarField.setText(String.valueOf(sugarQuantity));
        }
    }

    public String getSelectedCoffeeType() {
        return (String) coffeeTypeField.getSelectedItem();
    }

    public ArrayList<String> getSelectedAdditions() {
        ArrayList<String> selectedOptions = new ArrayList<>();
        if (milkCheckBox.isSelected()) {
            selectedOptions.add("Mleko");
        }
        if (creamCheckBox.isSelected()) {
            selectedOptions.add("Šlag");
        }
        if (rumCheckBox.isSelected()) {
            selectedOptions.add("Rum");
        }
        if (cinnamonCheckBox.isSelected()) {
            selectedOptions.add("Cimet");
        }
        if (espressoShootCheckBox.isSelected()) {
            selectedOptions.add("Dodatni espreso šot");
        }
        return selectedOptions;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CoffeeShop::new);
    }
}

interface Coffee {
    double cost();
}

class EspressoCoffee implements Coffee {
    public double cost() {
        return 120;
    }
}

class CappuccinoCoffee implements Coffee {
    public double cost() {
        return 130;
    }
}

class MochaCoffee implements Coffee {
    public double cost() {
        return 140;
    }
}

class LatteCoffee implements Coffee {

    public double cost() {
        return 150;
    }
}

class Order {
    private String name;
    private double SUGAR_COST = 0;
    private String coffeeType;
    private double sugarAmount;
    private String blend;
    private Coffee coffee;
    private ArrayList<String> additions;

    public Order(String name, String coffeeType, double sugarAmount, String blend, ArrayList<String> additions) {
        this.name = name;
        this.coffeeType = coffeeType;
        this.sugarAmount = sugarAmount;
        this.blend = blend;
        this.additions = additions;

    }

    public double submitOrder() {
        double totalCost = 0 + this.sugarAmount * SUGAR_COST;
        coffee = CoffeeFactory.createCoffee(coffeeType);
        totalCost += coffee.cost();
        Blend blendObject = BlendFactory.createBlend(blend);
        totalCost += blendObject.cost();

        for (String topping : additions) {
            Topping toppingObject = ToppingFactory.createTopping(topping);
            totalCost += toppingObject.cost();
        }
        return totalCost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoffeeType() {
        return coffeeType;
    }

    public void setCoffeeType(String coffeeType) {
        this.coffeeType = coffeeType;
    }

    public double getSugarAmount() {
        return sugarAmount;
    }

    public void setSugarAmount(double sugarAmount) {
        this.sugarAmount = sugarAmount;
    }

    public String getBlend() {
        return blend;
    }

    public void setBlend(String blend) {
        this.blend = blend;
    }

    public ArrayList<String> getAdditions() {
        return additions;
    }

    public void setAdditions(ArrayList<String> additions) {
        this.additions = additions;
    }
}

interface OrderExporter {
    void export(Order order);
}

class TxtExporter implements OrderExporter {
    @Override
    public void export(Order order) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("text-orders.txt"))) {
            writer.write("Detalji:\n");
            writer.write("Ime naručioca: " + order.getName() + "\n");
            writer.write("Tip " + order.getCoffeeType() + "\n");
            writer.write("Količina šećara: " + order.getSugarAmount() + "\n");
            writer.write("Blend: " + order.getBlend() + "\n");
            writer.write("Dodaci: " + String.join(", ", order.getAdditions()) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class JsonExporter implements OrderExporter {
    @Override
    public void export(Order order) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", order.getName());
        jsonObject.put("coffeeType", order.getCoffeeType());
        jsonObject.put("sugarAmount", order.getSugarAmount());
        jsonObject.put("blend", order.getBlend());
        jsonObject.put("additions", order.getAdditions());
        try {
            Files.write(Paths.get("order.json"), jsonObject.toString(4).getBytes());
        } catch (IOException e) {
            System.out.println("Greska prilikom upista: " + e.getMessage());
        }

    }
}

class XmlExporter implements OrderExporter {
    @Override
    public void export(Order order) {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<order>\n");

        xmlBuilder.append("  <ime_narucioca>").append(order.getName()).append("</ime_narucioca>\n");
        xmlBuilder.append("  <tip_kafe>").append(order.getCoffeeType()).append("</tip_kafe>\n");
        xmlBuilder.append("  <kolicina_secera>").append(order.getSugarAmount()).append("</kolicina_secera>\n");
        xmlBuilder.append("  <blend>").append(order.getBlend()).append("</blend>\n");

        xmlBuilder.append("  <dodaci>\n");
        for (String addition : order.getAdditions()) {
            xmlBuilder.append("    <dodatak>").append(addition).append("</dodatak>\n");
        }
        xmlBuilder.append("  </dodaci>\n");

        xmlBuilder.append("</order>");
        try {
            Files.write(Paths.get("order.xml"), xmlBuilder.toString().getBytes());
        } catch (IOException e) {
            System.out.println("Greska prilikom upista: " + e.getMessage());
        }
    }
}

class OrderExporterFactory {
    public static OrderExporter createExporter(String type) {
        switch (type) {
            case "XML":
                return new XmlExporter();
            case "JSON":
                return new JsonExporter();
            case "TXT":
                return new TxtExporter();
            default:
                throw new IllegalArgumentException("Nepoznat tip exporta");
        }
    }
}

class CoffeeFactory {
    public static Coffee createCoffee(String type) {
        switch (type) {
            case "Espreso":
                return new EspressoCoffee();
            case "Kapućino":
                return new CappuccinoCoffee();
            case "Makijato":
                return new MochaCoffee();
            case "Late":
                return new LatteCoffee();
            case "Moka":
                return new MochaCoffee();
            default:
                throw new IllegalArgumentException("Nepoznat tip kafe");
        }
    }
}

interface Blend {
    public abstract double cost();
}

class ArabicaBlend implements Blend {
    @Override
    public double cost() {
        return 80;
    }
}

class BalkanBlend implements Blend {
    @Override
    public double cost() {
        return 100;
    }
}

interface Topping {
    public abstract double cost();
}

class MilkTopping implements Topping {
    @Override
    public double cost() {
        return 50;
    }
}

class CreamTopping implements Topping {
    @Override
    public double cost() {
        return 60;
    }
}

class RumTopping implements Topping {
    @Override
    public double cost() {
        return 70;
    }
}

class CinnamonTopping implements Topping {
    @Override
    public double cost() {
        return 50;
    }
}

class EspressoShotTopping implements Topping {
    @Override
    public double cost() {
        return 100;
    }
}

class ToppingFactory {
    public static Topping createTopping(String toppingType) {
        switch (toppingType) {
            case "Mleko":
                return new MilkTopping();
            case "Šlag":
                return new CreamTopping();
            case "Rum":
                return new RumTopping();
            case "Cimet":
                return new CinnamonTopping();
            case "Dodatni espreso šot":
                return new EspressoShotTopping();
            default:
                throw new IllegalArgumentException("Nepoznat tip dodatka");
        }
    }
}

class BlendFactory {
    public static Blend createBlend(String toppingType) {
        switch (toppingType) {
            case "Arabica 100%":
                return new ArabicaBlend();
            case "Balkan Blend":
                return new BalkanBlend();
            default:
                throw new IllegalArgumentException("Nepoznat tip blenda");
        }
    }
}