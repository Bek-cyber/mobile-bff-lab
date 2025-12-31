package com.project.mobilebfflab.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class HomeResponseDto {
    UserDto user;
    List<AccountDto> accounts;
    List<OfferDto> offers;

    boolean offersAvailable;

    @Value
    @Builder
    public static class UserDto {
        String id;
        String name;
    }

    @Value
    @Builder
    public static class AccountDto {
        String id;
        String type;
        String balance;
    }

    @Value
    @Builder
    public static class OfferDto {
        String id;
        String title;
    }
}
