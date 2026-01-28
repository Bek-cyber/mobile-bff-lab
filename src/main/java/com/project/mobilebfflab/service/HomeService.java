package com.project.mobilebfflab.service;

import com.project.mobilebfflab.client.AccountClient;
import com.project.mobilebfflab.client.OfferClient;
import com.project.mobilebfflab.client.UserClient;
import com.project.mobilebfflab.client.dto.AccountClientDto;
import com.project.mobilebfflab.client.dto.OfferClientDto;
import com.project.mobilebfflab.client.dto.UserClientDto;
import com.project.mobilebfflab.dto.HomeResponseDto;
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

    public HomeResponseDto getHome() {
        log.info("Параллельный fan-out для Home-экрана");

        Mono<UserClientDto> userMono = Mono
                .fromCallable(userClient::getUser)
                .timeout(USER_TIMEOUT);

        Mono<List<AccountClientDto>> accountsMono = Mono
                .fromCallable(accountClient::getAccounts)
                .timeout(ACCOUNTS_TIMEOUT);

        Mono<List<OfferClientDto>> offersMono = Mono
                .fromCallable(offerClient::getOffers)
                .timeout(OFFERS_TIMEOUT)
                .onErrorReturn(Collections.emptyList());

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
        }).block();

    }
}
