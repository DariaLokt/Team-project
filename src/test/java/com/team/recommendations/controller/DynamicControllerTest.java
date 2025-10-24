package com.team.recommendations.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.recommendations.config.TestConfig;
import com.team.recommendations.model.dynamic.DynamicProduct;
import com.team.recommendations.model.dynamic.DynamicRule;
import com.team.recommendations.repository.DynamicProductRepository;
import com.team.recommendations.repository.DynamicRuleRepository;
import com.team.recommendations.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestConfig.class)
@WebMvcTest(DynamicController.class)
class DynamicControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private DynamicService dynamicService;

    @MockBean
    private DynamicProductRepository dynamicProductRepository;

    @MockBean
    private DynamicRuleRepository dynamicRuleRepository;

    @InjectMocks
    private DynamicController dynamicController;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Выводит все продукты")
    void getAllProducts() throws Exception {
//        given
        DynamicRule rule1 = new DynamicRule();
        rule1.setId(UUID.randomUUID());
        rule1.setQuery("dfjgkh");
        rule1.setArguments(new ArrayList<>());
        rule1.setNegate(true);
        DynamicProduct product1 = new DynamicProduct();
        product1.setProduct_id(UUID.randomUUID());
        product1.setProduct_name("sdhttfg");
        product1.setProduct_id(UUID.randomUUID());
        product1.setProduct_text("rftyguhu");
        Collection<DynamicRule> rules = new ArrayList<>();
        rules.add(rule1);
        product1.setRule(rules);
        rule1.setProduct(product1);

//        when
        when(dynamicProductRepository.findAll()).thenReturn(List.of(product1));

//        then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/rule")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
        verify(dynamicService,only()).getAllProducts();
        verify(dynamicProductRepository,only()).findAll();
    }

    @Test
    @DisplayName("Добавляет новые продукты")
    void addProduct() throws Exception{
        //        given
        DynamicRule rule1 = new DynamicRule();
        rule1.setId(UUID.randomUUID());
        rule1.setQuery("dfjgkh");
        rule1.setArguments(new ArrayList<>());
        rule1.setNegate(true);
        DynamicProduct product1 = new DynamicProduct();
        product1.setProduct_id(UUID.randomUUID());
        product1.setProduct_name("sdhttfg");
        product1.setProduct_id(UUID.randomUUID());
        product1.setProduct_text("rftyguhu");
        Collection<DynamicRule> rules = new ArrayList<>();
        rules.add(rule1);
        product1.setRule(rules);
        rule1.setProduct(product1);

//        when
        when(dynamicProductRepository.save(any(DynamicProduct.class))).thenReturn(product1);

//        then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/rule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(dynamicService,only()).addProduct(product1);
        verify(dynamicProductRepository,only()).save(product1);
    }

    @Test
    @DisplayName("Удаляет продукт")
    void deleteProduct() throws Exception {
//        given
        UUID id = UUID.randomUUID();

//        when
        doNothing().when(dynamicService).deleteProduct(id);
        doNothing().when(dynamicProductRepository).deleteById(id);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/rule/{product_id}",id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

//        then
        verify(dynamicService,only()).deleteProduct(id);
    }
}