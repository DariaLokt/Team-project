package com.team.recommendations.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.recommendations.config.TestConfig;
import com.team.recommendations.model.dynamic.DynamicProductDto;
import com.team.recommendations.model.dynamic.DynamicRuleDto;
import com.team.recommendations.service.rules.DynamicService;
import com.team.recommendations.service.rules.StatsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestConfig.class)
@WebMvcTest(DynamicController.class)
class DynamicControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DynamicService dynamicService;

    @MockBean
    private StatsService statsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Выводит все продукты")
    void getAllProducts() throws Exception {
        // given
        DynamicProductDto productDto = createTestProductDto();
        Collection<DynamicProductDto> products = Collections.singletonList(productDto);

        // when
        when(dynamicService.getAllProducts()).thenReturn(products);

        // then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/rule")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].productName").value("Test Product"));

        verify(dynamicService, times(1)).getAllProducts();
    }

    @Test
    @DisplayName("Добавляет новые продукты")
    void addProduct() throws Exception {
        // given
        DynamicProductDto productDto = createTestProductDto();

        // when
        when(dynamicService.addProduct(any(DynamicProductDto.class))).thenReturn(productDto);

        // then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/rule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Test Product"));

        verify(dynamicService, times(1)).addProduct(any(DynamicProductDto.class));
    }

    @Test
    @DisplayName("Удаляет продукт")
    void deleteProduct() throws Exception {
        // given
        UUID id = UUID.randomUUID();

        // when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/rule/{product_id}", id.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(dynamicService, times(1)).deleteProduct(id);
    }

    private DynamicProductDto createTestProductDto() {
        DynamicProductDto productDto = new DynamicProductDto();
        productDto.setId(UUID.randomUUID());
        productDto.setProductName("Test Product");
        productDto.setProductId(UUID.randomUUID());
        productDto.setProductText("Test Description");

        DynamicRuleDto ruleDto = new DynamicRuleDto();
        ruleDto.setQuery("USER_OF");
        ruleDto.setArguments(List.of("DEBIT"));
        ruleDto.setNegate(false);

        productDto.setRule(Collections.singletonList(ruleDto));
        return productDto;
    }
}