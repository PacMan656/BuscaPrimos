/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package buscaprimos;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author marco
 */
public class BuscaPrimos {
// Método Sequencial
    public List<Integer> calculateSequential(int start, int end) {
        List<Integer> primes = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            if (isPrime(i)) {
                primes.add(i);
            }
        }
        return primes;
    }

    // Método Paralelo
    public List<Integer> calculateParallel(int start, int end, int threads) {
        List<Integer> primes = new ArrayList<>();
        int range = (end - start + 1) / threads;
        List<Thread> threadList = new ArrayList<>();
        
        for (int t = 0; t < threads; t++) {
            int threadStart = start + t * range;
            int threadEnd = (t == threads - 1) ? end : threadStart + range - 1;

            Thread thread = new Thread(() -> {
                for (int i = threadStart; i <= threadEnd; i++) {
                    if (isPrime(i)) {
                        synchronized (primes) {
                            primes.add(i);
                        }
                    }
                }
            });
            threadList.add(thread);
            thread.start();
        }

        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return primes;
    }

    // Método para verificar se um número é primo
    private boolean isPrime(int number) {
        if (number <= 1) return false;
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) return false;
        }
        return true;
    }
}
