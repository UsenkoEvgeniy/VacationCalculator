## Приложение для расчета отпускных
Приложение принимает среднюю зарплату за 12 месяцев и количество календарных дней отпуска - отвечает суммой отпускных, которые придут сотруднику.

Пример запроса
```http request
### Expect code 200 and result of calculation
GET http://localhost:8080/calculate?vacationPeriod=9&avgWagePerMonth=175000.43
Content-Type: application/json
{
    "vacationPay": 53754.39
}
```

При указании даты начала отпуска, расчет учитывает нерабочие праздничные дни по ТК РФ
```http request
### Expect code 200 and result of calculation
GET http://localhost:8080/calculate?vacationPeriod=9&avgWagePerMonth=175000.43&startDate=2025-01-01
Content-Type: application/json
{
    "vacationPay": 5972.71
}
```