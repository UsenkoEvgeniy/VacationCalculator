package com.example.vacationcalculator.service;

import com.example.vacationcalculator.dto.VacationPayResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RuVacationPayServiceTest {

    private final VacationPayService vacationPayService = new RuVacationPayService();
    private final BigDecimal avgWagePerMonth = new BigDecimal(29_300);
    private final int vacationPeriod = 10;

    @Test
    void calculateVacationPay_whenCorrectParams_thenGetResult() {
        VacationPayResponse payResponse = vacationPayService.calculateVacationPay(avgWagePerMonth, vacationPeriod);
        assertEquals(new BigDecimal("10000.00"), payResponse.vacationPay);
    }

    @Test
    void calculateVacationPayFromDate_whenNoHolidays_thenGetSameResponse() {
        VacationPayResponse payResponse = vacationPayService.calculateVacationPayFromDate(avgWagePerMonth,
                vacationPeriod, LocalDate.of(2024, 2, 13));
        assertEquals(new BigDecimal("10000.00"), payResponse.vacationPay);
        VacationPayResponse responseOfCalculateVacationPay = vacationPayService.calculateVacationPay(avgWagePerMonth,
                vacationPeriod);
        assertEquals(responseOfCalculateVacationPay.vacationPay, payResponse.vacationPay,
                "При отсутствии отпуска метод ответ методов должен совпадать");
    }

    @Test
    void calculateVacationPayFromDate_whenOneHolidays_thenPayLessOneDay() {
        VacationPayResponse payResponse = vacationPayService.calculateVacationPayFromDate(avgWagePerMonth,
                vacationPeriod, LocalDate.of(2024, 2, 14));
        assertEquals(new BigDecimal("9000.00"), payResponse.vacationPay);
    }
}