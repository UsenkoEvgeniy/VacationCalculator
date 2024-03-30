package com.example.vacationcalculator.service;

import com.example.vacationcalculator.dto.VacationPayResponse;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.MonthDay;

import static com.example.vacationcalculator.utils.RuHolidays.HOLIDAY_LIST;

@Service
@NoArgsConstructor
public class RuVacationPayService implements VacationPayService {

    /**
     * Среднее количество дней в месяце для расчета отпускных (Постановление Правительства РФ от 24.12.2007 N 922
     * "Об особенностях порядка исчисления средней заработной платы")
     */
    public static final BigDecimal AVG_DAY_IN_MONTH = new BigDecimal("29.3");

    /**
     * Метод рассчитывает сумму отпускных за период в календарных днях
     *
     * @param avgWagePerMonth Средняя зарплата за последние 12 месяцев
     * @param vacationPeriod  Количество дней отпуска в календарных днях
     * @return Сумма отпускных в BigDecimal со scale 2
     */
    @Override
    public VacationPayResponse calculateVacationPay(BigDecimal avgWagePerMonth, Integer vacationPeriod) {
        BigDecimal pay = avgWagePerMonth.divide(AVG_DAY_IN_MONTH, 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(vacationPeriod));
        return new VacationPayResponse(pay);
    }

    /**
     * Метод рассчитывает сумму отпускных с учетом нерабочих праздничных дней
     *
     * @param avgWagePerMonth Средняя зарплата за последние 12 месяцев
     * @param vacationPeriod  Количество дней отпуска в календарных днях
     * @param startDate       Дата начала отпуска
     * @return Сумма отпускных в BigDecimal со scale 2
     */
    @Override
    public VacationPayResponse calculateVacationPayFromDate(BigDecimal avgWagePerMonth, Integer vacationPeriod, LocalDate startDate) {
        LocalDate lastDate = startDate.plusDays(vacationPeriod);
        for (MonthDay holiday : HOLIDAY_LIST) {
            if (isHolidayInDates(holiday, startDate, lastDate)) {
                vacationPeriod--;
            }
        }
        return calculateVacationPay(avgWagePerMonth, vacationPeriod);
    }

    /**
     * Метод проверяет, входит ли праздничный день в заданный диапазон
     *
     * @param holiday День и месяц праздничного дня
     * @param start   Дата начала периода, входит в диапазон проверки
     * @param end     Дата окончания периода, не входит в диапазон
     * @return true, если дата входит в диапазон, false, если не входит
     */
    private boolean isHolidayInDates(MonthDay holiday, LocalDate start, LocalDate end) {
        LocalDate holidayWithStartDateYear = holiday.atYear(start.getYear());
        LocalDate holidayWithEndDateYear = holiday.atYear(end.getYear());
        start = start.minusDays(1);
        return holidayWithStartDateYear.isAfter(start) && holidayWithStartDateYear.isBefore(end)
                || holidayWithEndDateYear.isAfter(start) && holidayWithEndDateYear.isBefore(end);
    }
}
