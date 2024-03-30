package com.example.vacationcalculator.service;

import com.example.vacationcalculator.dto.VacationPayResponse;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface VacationPayService {
    VacationPayResponse calculateVacationPay(BigDecimal avgWagePerMonth, Integer vacationPeriod);

    VacationPayResponse calculateVacationPayFromDate(BigDecimal avgWagePerMonth, Integer vacationPeriod, LocalDate startDate);
}
