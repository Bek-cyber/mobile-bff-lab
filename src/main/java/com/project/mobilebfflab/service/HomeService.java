package com.project.mobilebfflab.service;

import com.project.mobilebfflab.dto.HomeResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class HomeService {

    public HomeResponseDto getHome() {
        log.info("Формирование Home-экрана");

        // Пока mock-данные
        HomeResponseDto.UserDto user = HomeResponseDto.UserDto.builder()
                .id("u-1")
                .name("Client One")
                .build();

        List<HomeResponseDto.AccountDto> accounts = List.of(
                HomeResponseDto.AccountDto.builder()
                        .id("a-1")
                        .type("CARD")
                        .balance("1000 ₽")
                        .build()
        );

        boolean offersAvailable = false;
        List<HomeResponseDto.OfferDto> offers = List.of();

        log.info("Home-экран сформирован (offersAvailable={})", offersAvailable);

        return HomeResponseDto.builder()
                .user(user)
                .accounts(accounts)
                .offers(offers)
                .offersAvailable(offersAvailable)
                .build();
    }
}
