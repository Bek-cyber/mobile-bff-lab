package com.project.mobilebfflab.controller;

import com.project.mobilebfflab.dto.HomeResponseDto;
import com.project.mobilebfflab.service.HomeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mobile/v1/home")
public class HomeController {
    private final HomeService homeService;

    public HomeResponseDto getHome() {
        log.info("Запрос Home-экрана от мобильного клиента");
        return homeService.getHome();
    }
}
