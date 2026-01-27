package com.project.mobilebfflab.client;

import com.project.mobilebfflab.client.dto.UserClientDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserClient {
    private final WebClient.Builder webClientBuilder;

    public UserClientDto getUser() {
        log.debug("Запрос пользователя из user-service");

        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8081/user")
                .retrieve()
                .bodyToMono(UserClientDto.class)
                .block();
    }
}
