package be.ucll.cliapp;

import org.springframework.web.reactive.function.client.WebClient;

public class ApiClient {
    protected final WebClient webClient;

    public ApiClient() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8080")
                .build();
    }
}

