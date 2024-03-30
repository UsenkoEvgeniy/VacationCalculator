package com.example.vacationcalculator.controller;

import com.example.vacationcalculator.dto.VacationPayResponse;
import com.example.vacationcalculator.service.VacationPayService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Validated
public class VacationController {
    private final VacationPayService vacationPayService;

    @GetMapping("/calculate")
    public VacationPayResponse calculateVacationPay(@Min(value = 1, message = "Средняя зарплата должна быть положительным числом")
                                                    @NotNull @RequestParam BigDecimal avgWagePerMonth,
                                                    @Min(value = 1, message = "Продолжительность отпуска должна быть положительным числом")
                                                    @Max(value = 90, message = "Отпуск не более 90 дней")
                                                    @NotNull @RequestParam Integer vacationPeriod,
                                                    @RequestParam(required = false) LocalDate startDate) {
        if (Objects.nonNull(startDate)) {
            return vacationPayService.calculateVacationPayFromDate(avgWagePerMonth, vacationPeriod, startDate);
        }
        return vacationPayService.calculateVacationPay(avgWagePerMonth, vacationPeriod);
    }

    @ExceptionHandler({ConstraintViolationException.class, MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidation(Exception e) {
        return e.getMessage();
    }
}
