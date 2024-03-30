package com.example.vacationcalculator.controller;

import com.example.vacationcalculator.dto.VacationPayResponse;
import com.example.vacationcalculator.service.RuVacationPayService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = VacationController.class)
class VacationControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    private final BigDecimal avgWagePerMonth = new BigDecimal(29_300);
    private final Integer vacationPeriod = 10;
    @MockBean
    private RuVacationPayService payService;

    @Test
    void calculateVacationPay_whenCorrectWithoutDate_thenSendResponse() throws Exception {
        BigDecimal expected = new BigDecimal("100.00");
        Mockito.when(payService.calculateVacationPay(avgWagePerMonth, vacationPeriod))
                .thenReturn(new VacationPayResponse(expected));

        String body = mvc.perform(get("/calculate")
                        .param("avgWagePerMonth", "29300")
                        .param("vacationPeriod", vacationPeriod.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        VacationPayResponse response = mapper.readValue(body, VacationPayResponse.class);

        assertEquals(expected, response.vacationPay);
        verify(payService, only()).calculateVacationPay(avgWagePerMonth, vacationPeriod);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-29300"})
    void calculateVacationPay_whenWrongWage_thenBadRequest(String avgWage) throws Exception {

        mvc.perform(get("/calculate")
                        .param("avgWagePerMonth", avgWage)
                        .param("vacationPeriod", vacationPeriod.toString()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(payService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-3", "91"})
    void calculateVacationPay_whenWrongPeriod_thenBadRequest(String period) throws Exception {
        mvc.perform(get("/calculate")
                        .param("avgWagePerMonth", "29300")
                        .param("vacationPeriod", period))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(payService);
    }

    @Test
    void calculateVacationPay_whenPeriodNotSet_thenBadRequest() throws Exception {
        mvc.perform(get("/calculate")
                        .param("avgWagePerMonth", "29300"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(payService);
    }

    @Test
    void calculateVacationPay_whenWageNotSet_thenBadRequest() throws Exception {
        mvc.perform(get("/calculate")
                        .param("vacationPeriod", "7"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(payService);
    }

    @Test
    void calculateVacationPay_whenDateSet_callMethodFromDate() throws Exception {
        BigDecimal expected = new BigDecimal("100.00");
        Mockito.when(payService.calculateVacationPayFromDate(any(), any(), any()))
                .thenReturn(new VacationPayResponse(expected));
        mvc.perform(get("/calculate")
                .param("avgWagePerMonth", "29300")
                .param("vacationPeriod", "7")
                .param("startDate", "2021-10-10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        verify(payService, only()).calculateVacationPayFromDate(any(), any(), any());
    }
}