/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server;

/**
 *
 * @author marco
 */
import java.io.*;
import java.net.*;
import java.util.List;

public class Client {

    public static void main(String[] args) {
        Client client = new Client();
        List<Integer> primes = client.getPrimes(10, 100); // Exemplos de intervalo
        if (primes != null) {
            System.out.println("Primos: " + primes);
        }
    }

    public List<Integer> getPrimes(int start, int end) {
        try (
                Socket socket = new Socket("localhost", 9999); ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream()); ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {
            // Envia os números para o servidor
            output.writeInt(start);
            output.writeInt(end);
            output.flush();

            // Recebe os números primos do servidor
            return (List<Integer>) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
