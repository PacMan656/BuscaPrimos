### **Análise do Código**
O código consiste em uma aplicação cliente-servidor para calcular números primos em um intervalo determinado. O sistema permite três modos de cálculo: sequencial, paralelo e distribuído. Ele é composto por quatro arquivos principais:

1. **`Client.java`** – Cliente que se conecta ao servidor para buscar números primos.
2. **`Server.java`** – Servidor que recebe requisições dos clientes e responde com a lista de números primos.
3. **`BuscaPrimos.java`** – Classe responsável por calcular os números primos, tanto de forma sequencial quanto paralela.
4. **`Main.java`** – Interface gráfica que permite ao usuário selecionar o modo de execução.

---

## **1. `Client.java` (Cliente)**
Este código implementa um cliente que se conecta ao servidor na porta **9999** e solicita a lista de números primos dentro de um intervalo.

### **Métodos e Funcionalidade**
- **`main(String[] args)`**  
    - Cria uma instância do cliente e chama `getPrimes(10, 100)`, pedindo números primos entre **10 e 100**.
    - Exibe a lista de primos retornada pelo servidor.

- **`getPrimes(int start, int end)`**  
    - Abre um **socket** para `localhost` na porta `9999`.
    - Envia dois números inteiros (início e fim do intervalo).
    - Aguarda e recebe uma **lista de números primos** do servidor.
    - Retorna essa lista.

---
## **2. `Server.java` (Servidor)**
Este código implementa um servidor que aguarda conexões na porta **9999** e processa solicitações de clientes para encontrar números primos.

### **Métodos e Funcionalidade**
- **`main(String[] args)`**  
    - Cria um `ServerSocket` na porta **9999**.
    - Aguarda conexões dos clientes.
    - Para cada conexão, inicia uma nova **thread** `ClientHandler` para processar a requisição.

- **`ClientHandler (Runnable)`**  
    - Lê os valores **start** e **end** enviados pelo cliente.
    - Utiliza `BuscaPrimos` para calcular números primos sequencialmente.
    - Envia a lista de primos de volta para o cliente.

---
## **3. `BuscaPrimos.java` (Cálculo dos números primos)**
Essa classe contém os métodos para encontrar números primos de forma **sequencial** e **paralela**.

### **Métodos e Funcionalidade**
- **`calculateSequential(int start, int end)`**  
    - Percorre todos os números no intervalo e verifica se são primos usando `isPrime(int number)`.
    - Retorna a lista de primos encontrados.

- **`calculateParallel(int start, int end, int threads)`**  
    - Divide o intervalo entre múltiplas **threads**.
    - Cada thread verifica se os números atribuídos a ela são primos.
    - Os resultados são adicionados a uma lista de forma **síncrona** (uso de `synchronized`).
    - Espera todas as threads terminarem (`join()`).
    - Retorna a lista final de primos.

- **`isPrime(int number)`**  
    - Método auxiliar para verificar se um número é primo.
    - Usa otimização de `Math.sqrt(number)` para reduzir a quantidade de divisões.

---
## **4. `Main.java` (Interface Gráfica)**
Implementa uma **GUI** para o usuário definir o intervalo e o modo de execução.

### **Interface**
- **Selecionar Modo:** `"Sequencial"`, `"Paralelo"` ou `"Distribuído"`.
- **Definir Intervalo:** `start` e `end`.
- **Definir Número de Threads (se modo paralelo for escolhido).`
- **Botão de Calcular:** Calcula os primos conforme o modo selecionado.
- **Botão para ativar o servidor:** Se não estiver rodando, inicia o servidor.
- **Verificar Status do Servidor:** Indica se o servidor está ativo.

### **Métodos e Funcionalidade**
- **`isServerActive(String host, int port)`**  
    - Verifica se o servidor está rodando tentando abrir um **socket** na porta `9999`.

- **`startServer(JLabel serverStatusLabel)`**  
    - Inicia o servidor em uma nova thread.

- **`calculateButton.addActionListener()`**  
    - Executa o cálculo conforme o modo escolhido:
        - **Sequencial:** Usa `calculateSequential(start, end)`.
        - **Paralelo:** Usa `calculateParallel(start, end, threads)`.
        - **Distribuído:** Envia a requisição ao servidor (`Client.getPrimes(start, end)`).

---
## **Resumo**
- **Cliente (`Client.java`)** → Conecta-se ao servidor e solicita números primos.
- **Servidor (`Server.java`)** → Aguarda conexões, processa requisições e retorna os números primos.
- **BuscaPrimos (`BuscaPrimos.java`)** → Implementa os cálculos (sequencial e paralelo).
- **Interface Gráfica (`Main.java`)** → Permite ao usuário selecionar o intervalo e o método de cálculo.

O código é bem estruturado, utilizando **multithreading** no servidor e na busca paralela. A interface gráfica facilita a interação do usuário.
