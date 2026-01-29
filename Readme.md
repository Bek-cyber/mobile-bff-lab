# Mobile BFF — Back-End For Front-End (Spring Boot)

## Описание

**Mobile BFF** — backend-сервис, реализующий паттерн **Back-End For Front-End (BFF)**
для мобильного клиента (Android).

Сервис выступает **edge-слоем** между мобильным приложением и backend-системами,
инкапсулируя сложность микросервисной архитектуры и предоставляя **screen-oriented API**.

Проект сфокусирован на:
- агрегации данных с нескольких downstream-сервисов,
- fan-out запросах и параллельной оркестрации,
- отказоустойчивости и graceful degradation,
- стабильном API-контракте для мобильного клиента,
- наблюдаемости (traceId, логирование).

---

## Цель проекта

Проект используется для:
- демонстрации корректной реализации паттерна Back-End For Front-End,
- практики orchestration-логики и fan-out запросов,
- изучения resilience-паттернов на edge-уровне,
- примера production-grade BFF без реальных downstream-сервисов,
- использования как шаблона для мобильных BFF.

---

## Технологии
- Java 21
- Spring Boot 3
- Spring Web (MVC)
- Spring WebClient
- Resilience4j (Circuit Breaker)
- Lombok
- Gradle

---

## Архитектура проекта
``` 
com.project.mobilebfflab
├── controller        // HTTP edge-слой
├── service           // orchestration и fan-out
├── client            // WebClient + downstream DTO
├── dto               // API DTO (screen-oriented)
├── error             // error-контракт и exception handling
├── config            // WebClient, traceId, resilience
└── MobileBffLabApplication
```
---

## Реализованный функционал
- Screen-oriented API для мобильного клиента
- Единый endpoint под экран (/mobile/v1/home)
- Fan-out запросы к нескольким downstream-источникам
- Параллельное выполнение запросов (Mono.zip)
- Таймауты для каждого downstream
- Circuit Breaker на каждый внешний вызов
- Graceful degradation для некритичных данных
- Единый error-контракт для клиента
- TraceId (end-to-end)
- Production-grade логирование

---

## Screen-oriented API
BFF предоставляет API, ориентированное на экран, а не на доменные сущности.

### Пример:

```
GET /mobile/v1/home
```

Один запрос формирует весь набор данных, необходимый для отображения экрана **Home**
в мобильном приложении.

---

## Fan-out и параллельная оркестрация

Один входящий HTTP-запрос порождает несколько исходящих запросов:
- user-service
- accounts-service
- offers-service

Все запросы выполняются **параллельно**, что снижает общую latency ответа.

---

## Graceful Degradation

Данные разделены по критичности:

- Критичные: user, accounts
- Некритичные: offers

Если некритичный downstream недоступен:
- экран возвращается корректно,
- клиент получает флаг деградации,
- UI адаптируется без ошибки.

---

## Error-контракт

Все ошибки возвращаются в стабильном формате, независимом от downstream-сервисов:

```
{
    "errorCode": "DOWNSTREAM_UNAVAILABLE",
    "message": "Временная ошибка. Попробуйте позже",
    "traceId": "9f1c2a8d3b7a4e11"
}
```


### Особенности:
- предсказуемые errorCode,
- контролируемые HTTP-статусы,
- отсутствие утечек внутренних ошибок.

---

## TraceId и наблюдаемость

- TraceId принимается из X-Trace-Id или генерируется автоматически
- Прокидывается через MDC
- Возвращается в response header
- Используется во всех логах

Это упрощает:
- диагностику ошибок,
- поддержку,
- анализ деградаций.

---

### Anti-Corruption Layer

API-DTO, используемые BFF, не совпадают с DTO downstream-сервисов.

Это позволяет:
- защитить мобильный контракт от изменений backend-сервисов,
- развивать backend независимо от клиента,
- избежать протекания доменной модели наружу.
