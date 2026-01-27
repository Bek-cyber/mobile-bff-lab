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

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeService {
    private final UserClient userClient;
    private final OfferClient offerClient;
    private final AccountClient accountClient;

    public HomeResponseDto getHome() {
        log.info("Fan-out для Home-экрана");

        UserClientDto user = userClient.getUser();
        List<AccountClientDto> accounts = accountClient.getAccounts();

        boolean offersAvailable = true;
        List<OfferClientDto> offers;

        try {
            offers = offerClient.getOffers();
        } catch (Exception ex) {
            log.warn("Offers-service недоступен, деградация Home-экрана", ex);
            offersAvailable = false;
            offers = Collections.emptyList();
        }

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
    }
}
