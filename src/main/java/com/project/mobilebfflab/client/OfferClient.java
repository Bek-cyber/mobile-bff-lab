package com.project.mobilebfflab.client;

import com.project.mobilebfflab.client.dto.OfferClientDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OfferClient {
    private final WebClient.Builder webClientBuilder;

    public List<OfferClientDto> getOffers() {
        log.debug("Запрос офферов из offers-service");

        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8083/offers")
                .retrieve()
                .bodyToFlux(OfferClientDto.class)
                .collectList()
                .block();
    }
}
