import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import io.github.cdimascio.dotenv.Dotenv;

public class ConversorMoedas {

    public static void main(String[] args) throws IOException, InterruptedException {

        Dotenv dotenv = Dotenv.load();
        String API_KEY = dotenv.get("API_KEY");

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println("\n===== CONVERSOR DE MOEDAS =====");
            System.out.println("1 - Dólar → Real");
            System.out.println("2 - Real → Dólar");
            System.out.println("3 - Euro → Real");
            System.out.println("4 - Real → Euro");
            System.out.println("5 - Libra → Real");
            System.out.println("6 - Real → Libra");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();

            if (opcao == 0) {
                System.out.println("Encerrando programa...");
                break;
            }

            System.out.print("Digite o valor: ");
            double valor = scanner.nextDouble();

            String base = "";
            String destino = "";

            switch (opcao) {
                case 1 -> { base = "USD"; destino = "BRL"; }
                case 2 -> { base = "BRL"; destino = "USD"; }
                case 3 -> { base = "EUR"; destino = "BRL"; }
                case 4 -> { base = "BRL"; destino = "EUR"; }
                case 5 -> { base = "GBP"; destino = "BRL"; }
                case 6 -> { base = "BRL"; destino = "GBP"; }
                default -> {
                    System.out.println("Opção inválida!");
                    continue;
                }
            }

            double taxa = buscarTaxa(API_KEY, base, destino);
            double resultado = valor * taxa;

            System.out.printf("Resultado: %.2f %s\n", resultado, destino);
        }

        scanner.close();
    }

    public static double buscarTaxa(String API_KEY, String base, String destino)
            throws IOException, InterruptedException {

        String url = "https://v6.exchangerate-api.com/v6/" +
                API_KEY + "/latest/" + base;

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        Gson gson = new Gson();

        Map dados = gson.fromJson(response.body(), Map.class);
        Map taxas = (Map) dados.get("conversion_rates");

        return (double) taxas.get(destino);
    }
}
