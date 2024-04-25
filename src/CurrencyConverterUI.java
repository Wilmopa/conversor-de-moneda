import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONException; // Agregar esta importación
import org.json.JSONObject; // Agregar esta importación

public class CurrencyConverterUI {
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/54538d274f63d776d9198aad/latest/USD";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CurrencyConverterUI().createUI());
    }

    private void createUI() {
        JFrame frame = new JFrame("Currency Converter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Crear componentes de la interfaz de usuario
        JPanel inputPanel = new JPanel(new FlowLayout());
        JTextField amountTextField = new JTextField(10);
        JComboBox<String> fromCurrencyComboBox = new JComboBox<>(new String[]{"USD", "EUR", "GBP", "JPY", "AUD", "CAD"});
        JComboBox<String> toCurrencyComboBox = new JComboBox<>(new String[]{"USD", "EUR", "GBP", "JPY", "AUD", "CAD"});
        JButton convertButton = new JButton("Convertir");
        JLabel resultLabel = new JLabel("");

        // Agregar componentes al panel
        inputPanel.add(new JLabel("Cantidad:"));
        inputPanel.add(amountTextField);
        inputPanel.add(new JLabel("De:"));
        inputPanel.add(fromCurrencyComboBox);
        inputPanel.add(new JLabel("A:"));
        inputPanel.add(toCurrencyComboBox);
        inputPanel.add(convertButton);

        // Agregar el panel de entrada y el resultado al marco
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(resultLabel, BorderLayout.CENTER);

        // Agregar un ActionListener al botón de conversión
        convertButton.addActionListener(e -> convertCurrency(amountTextField, fromCurrencyComboBox, toCurrencyComboBox, resultLabel));

        // Configurar el tamaño y la visibilidad del marco
        frame.pack();
        frame.setVisible(true);
    }

    private void convertCurrency(JTextField amountTextField, JComboBox<String> fromCurrencyComboBox,
                                 JComboBox<String> toCurrencyComboBox, JLabel resultLabel) {
        // Obtener la cantidad a convertir del campo de texto
        String amountStr = amountTextField.getText().trim();
        if (amountStr.isEmpty()) {
            resultLabel.setText("Ingrese una cantidad válida");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            resultLabel.setText("Ingrese una cantidad válida");
            return;
        }

        try {
            // Construir la URL de la API con las monedas seleccionadas
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Leer la respuesta JSON de la API
            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            // Analizar la respuesta JSON para obtener las tasas de cambio
            String jsonResponse = response.toString();
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject rates = jsonObject.getJSONObject("conversion_rates");

            // Obtener la tasa de cambio para la moneda de origen
            String fromCurrency = (String) fromCurrencyComboBox.getSelectedItem();
            double fromRate = rates.getDouble(fromCurrency);

            // Obtener la tasa de cambio para la moneda de destino
            String toCurrency = (String) toCurrencyComboBox.getSelectedItem();
            double toRate = rates.getDouble(toCurrency);

            // Realizar la conversión de la moneda
            double convertedAmount = amount / fromRate * toRate;

            // Actualizar el resultado en resultLabel
            resultLabel.setText(String.format("%.2f %s = %.2f %s", amount, fromCurrency, convertedAmount, toCurrency));
        } catch (IOException | JSONException e) {
            resultLabel.setText("Error al obtener tasas de cambio: " + e.getMessage());
        }
    }
}
