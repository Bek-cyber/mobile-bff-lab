package com.project.mobilebfflab.service;

import com.project.mobilebfflab.client.AccountClient;
import com.project.mobilebfflab.client.OfferClient;
import com.project.mobilebfflab.client.UserClient;
import com.project.mobilebfflab.client.dto.AccountClientDto;
import com.project.mobilebfflab.client.dto.OfferClientDto;
import com.project.mobilebfflab.client.dto.UserClientDto;
import com.project.mobilebfflab.dto.HomeResponseDto;
import com.project.mobilebfflab.error.BffException;
import com.project.mobilebfflab.error.ErrorCode;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeService {
    private static final Duration USER_TIMEOUT = Duration.ofSeconds(2);
    private static final Duration ACCOUNTS_TIMEOUT = Duration.ofSeconds(2);
    private static final Duration OFFERS_TIMEOUT = Duration.ofSeconds(2);

    private final UserClient userClient;
    private final OfferClient offerClient;
    private final AccountClient accountClient;

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public HomeResponseDto getHome() {
        log.info("Параллельный fan-out для Home-экрана");

        CircuitBreaker userCb = circuitBreakerRegistry.circuitBreaker("userService");
        CircuitBreaker accountsCb = circuitBreakerRegistry.circuitBreaker("accountsService");
        CircuitBreaker offersCb = circuitBreakerRegistry.circuitBreaker("offersService");

        Mono<UserClientDto> userMono = Mono
                .fromCallable(userClient::getUser)
                .timeout(USER_TIMEOUT)
                .transformDeferred(CircuitBreakerOperator.of(userCb));

        Mono<List<AccountClientDto>> accountsMono = Mono
                .fromCallable(accountClient::getAccounts)
                .timeout(ACCOUNTS_TIMEOUT)
                .transformDeferred(CircuitBreakerOperator.of(accountsCb));

        Mono<List<OfferClientDto>> offersMono = Mono
                .fromCallable(offerClient::getOffers)
                .timeout(OFFERS_TIMEOUT)
                .transformDeferred(CircuitBreakerOperator.of(offersCb))
                .onErrorResume(ex -> {
                    // graceful degradation: офферы — необязательный блок
                    log.warn("Offers-service недоступен (CB/timeout/ошибка), деградация Home-экрана: {}", ex.toString());
                    return Mono.just(Collections.emptyList());
                });

        return Mono.zip(userMono, accountsMono, offersMono).map(tuple -> {
                    UserClientDto user = tuple.getT1();
                    List<AccountClientDto> accounts = tuple.getT2();
                    List<OfferClientDto> offers = tuple.getT3();

                    boolean offersAvailable = !offers.isEmpty();

                    return HomeResponseDto.builder()
                            .user(HomeResponseDto.UserDto.builder()
                                    .id(user.getId())
                                    .name(user.getFullName())
                                    .build())
                            .accounts(accounts.stream()
                                    .map(a -> HomeResponseDto.AccountDto.builder()
                                            .id(a.getId())
                                            .type(a.getType())
                                            .balance(a.getBalance())
                                            .build())
                                    .toList())
                            .offers(offers.stream()
                                    .map(o -> HomeResponseDto.OfferDto.builder()
                                            .id(o.getId())
                                            .title(o.getTitle())
                                            .build())
                                    .toList())
                            .offersAvailable(offersAvailable)
                            .build();
                })
                .onErrorMap(ex -> {
                    log.error("Критичный downstream недоступен", ex);
                    return new BffException(ErrorCode.DOWNSTREAM_UNAVAILABLE);
                })
                .block();

    }
}
