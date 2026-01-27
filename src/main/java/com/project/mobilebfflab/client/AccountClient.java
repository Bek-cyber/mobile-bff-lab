package com.project.mobilebfflab.client;

import com.project.mobilebfflab.client.dto.AccountClientDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountClient {
    private final WebClient.Builder webClientBuilder;

    public List<AccountClientDto> getAccounts() {
        log.debug("Запрос счетов из account-service");

        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8082/accounts")
                .retrieve()
                .bodyToFlux(AccountClientDto.class)
                .collectList()
                .block();
    }
}
