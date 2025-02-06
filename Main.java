/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package buscaprimos;

/**
 *
 * @author marco
 */
import Server.Client;
import java.awt.Color;
import java.io.EOFException;
import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Main {

    private static boolean isServerRunning = false; // Estado do servidor
    private static Thread serverThread; // Thread do servidor

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Prime Number Calculator");
            frame.setSize(500, 500);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(null);

            JLabel labelMode = new JLabel("Escolha o modo de execução:");
            labelMode.setBounds(20, 20, 200, 25);
            frame.add(labelMode);

            String[] modes = {"Sequencial", "Paralelo", "Distribuído"};
            JComboBox<String> modeSelection = new JComboBox<>(modes);
            modeSelection.setBounds(220, 20, 200, 25);
            frame.add(modeSelection);

            JLabel labelStart = new JLabel("Início do intervalo:");
            labelStart.setBounds(20, 60, 200, 25);
            frame.add(labelStart);

            JTextField startField = new JTextField();
            startField.setBounds(220, 60, 200, 25);
            frame.add(startField);

            JLabel labelEnd = new JLabel("Fim do intervalo:");
            labelEnd.setBounds(20, 100, 200, 25);
            frame.add(labelEnd);

            JTextField endField = new JTextField();
            endField.setBounds(220, 100, 200, 25);
            frame.add(endField);

            JLabel labelThreads = new JLabel("Número de Threads (somente paralelo):");
            labelThreads.setBounds(20, 140, 250, 25);
            frame.add(labelThreads);

            JTextField threadField = new JTextField("4"); // Valor padrão
            threadField.setBounds(270, 140, 150, 25);
            frame.add(threadField);

            JLabel serverStatusLabel = new JLabel("Servidor inativo");
            serverStatusLabel.setBounds(20, 440, 300, 25);
            serverStatusLabel.setForeground(Color.RED);
            frame.add(serverStatusLabel);

            JButton startServerButton = new JButton("Ativar Servidor");
            startServerButton.setBounds(20, 400, 200, 25);
            frame.add(startServerButton);

            JButton checkServerButton = new JButton("Verificar Servidor");
            checkServerButton.setBounds(240, 400, 200, 25);
            frame.add(checkServerButton);

            JTextArea resultArea = new JTextArea();
            resultArea.setEditable(false);
            resultArea.setLineWrap(true);
            resultArea.setWrapStyleWord(true);

            JScrollPane scrollPane = new JScrollPane(resultArea);
            scrollPane.setBounds(20, 230, 440, 150);
            frame.add(scrollPane);

            JButton calculateButton = new JButton("Calcular");
            calculateButton.setBounds(20, 190, 440, 25);
            frame.add(calculateButton);

            // Listener para ativar o servidor
            startServerButton.addActionListener(e -> {
                if (!isServerRunning) {
                    startServer(serverStatusLabel);
                    serverStatusLabel.setText("Servidor está ativo");
                    serverStatusLabel.setForeground(Color.GREEN);
                } else {
                    JOptionPane.showMessageDialog(frame, "Servidor já está ativo!", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                }
            });

            // Listener para verificar o servidor
            checkServerButton.addActionListener(e -> {
                if (isServerActive("localhost", 9999)) {
                    serverStatusLabel.setText("Servidor está ativo");
                    serverStatusLabel.setForeground(Color.GREEN);
                } else {
                    serverStatusLabel.setText("Servidor inativo");
                    serverStatusLabel.setForeground(Color.RED);
                }
            });

            calculateButton.addActionListener(e -> {
                try {
                    int start = Integer.parseInt(startField.getText());
                    int end = Integer.parseInt(endField.getText());
                    String mode = (String) modeSelection.getSelectedItem();

                    BuscaPrimos calculator = new BuscaPrimos();
                    List<Integer> primes;

                    long startTime = System.nanoTime();

                    switch (mode) {
                        case "Sequencial":
                            primes = calculator.calculateSequential(start, end);
                            break;
                        case "Paralelo":
                            int threads = Integer.parseInt(threadField.getText());
                            primes = calculator.calculateParallel(start, end, threads);
                            break;
                        case "Distribuído":
                            if (!isServerActive("localhost", 9999)) {
                                JOptionPane.showMessageDialog(frame, "Servidor inativo. Ative o servidor e tente novamente!", "Erro", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            Client client = new Client();
                            primes = client.getPrimes(start, end);
                            break;
                        default:
                            throw new IllegalArgumentException("Modo inválido selecionado!");
                    }

                    long endTime = System.nanoTime();
                    double executionTime = (endTime - startTime) / 1e6; // Tempo em ms

                    // Formatar o texto para exibição
                    StringBuilder resultText = new StringBuilder();
                    resultText.append("Modo de Execução: ").append(mode).append("\n");
                    resultText.append("Intervalo: ").append(start).append(" - ").append(end).append("\n");
                    resultText.append("Números Primos Encontrados: ").append(primes.size()).append("\n");
                    resultText.append("Tempo de Execução: ").append(String.format("%.2f", executionTime)).append(" ms\n");
                    resultText.append("Primos: \n");

                    // Adicionar números primos em colunas para melhor exibição
                    int count = 0;
                    for (int prime : primes) {
                        resultText.append(String.format("%6d", prime)); // Formata o número com 6 espaços
                        count++;
                        if (count % 10 == 0) { // Quebra de linha a cada 10 números
                            resultText.append("\n");
                        }
                    }

                    resultArea.setText(resultText.toString());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Por favor, insira valores válidos!", "Erro", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            });

            frame.setVisible(true);
        });
    }

    // Método para verificar se o servidor está ativo
    private static boolean isServerActive(String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // Método para iniciar o servidor em uma thread separada
    private static void startServer(JLabel serverStatusLabel) {
        serverThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(9999)) {
                isServerRunning = true;
                System.out.println("Servidor iniciado na porta 9999...");
                while (true) {
                    Socket socket = serverSocket.accept();
                    new Thread(new ClientHandler(socket)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
                serverStatusLabel.setText("Erro ao iniciar o servidor");
                serverStatusLabel.setForeground(Color.RED);
            }
        });
        serverThread.start();
    }

    // Classe interna para lidar com clientes no servidor
    static class ClientHandler implements Runnable {

        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    var input = new ObjectInputStream(socket.getInputStream()); var output = new ObjectOutputStream(socket.getOutputStream())) {
                int start = input.readInt();
                int end = input.readInt();

                BuscaPrimos calculator = new BuscaPrimos();
                List<Integer> primes = calculator.calculateSequential(start, end);

                // Envia os resultados de volta para o cliente
                output.writeObject(primes);
                output.flush();
            } catch (EOFException e) {
                System.err.println("Conexão encerrada inesperadamente pelo cliente.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
