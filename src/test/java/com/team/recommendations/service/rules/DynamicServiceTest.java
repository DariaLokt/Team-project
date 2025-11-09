package com.team.recommendations.service.rules;

import com.team.recommendations.model.dynamic.DynamicProduct;
import com.team.recommendations.model.dynamic.DynamicProductDto;
import com.team.recommendations.model.dynamic.DynamicRule;
import com.team.recommendations.model.dynamic.DynamicRuleDto;
import com.team.recommendations.repository.DynamicProductRepository;
import com.team.recommendations.repository.DynamicRuleRepository;
import com.team.recommendations.service.mapper.DynamicProductMapper;
import com.team.recommendations.service.mapper.DynamicRuleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DynamicServiceTest {

    @Mock
    private DynamicProductRepository dynamicProductRepository;

    @Mock
    private DynamicRuleRepository dynamicRuleRepository;

    @Mock
    private DynamicProductMapper dynamicProductMapper;

    @Mock
    private DynamicRuleMapper dynamicRuleMapper;

    private DynamicService dynamicService;

    @BeforeEach
    void setUp() {
        dynamicService = new DynamicService(
                dynamicProductRepository,
                dynamicRuleRepository,
                dynamicProductMapper,
                dynamicRuleMapper
        );
    }

    @Test
    @DisplayName("Получает все продукты")
    void testGetAllProducts() {
        // given
        DynamicProduct product1 = new DynamicProduct();
        DynamicProduct product2 = new DynamicProduct();
        List<DynamicProduct> products = Arrays.asList(product1, product2);

        DynamicProductDto dto1 = new DynamicProductDto();
        DynamicProductDto dto2 = new DynamicProductDto();

        when(dynamicProductRepository.findAll()).thenReturn(products);
        when(dynamicProductMapper.toDto(product1)).thenReturn(dto1);
        when(dynamicProductMapper.toDto(product2)).thenReturn(dto2);

        // when
        Collection<DynamicProductDto> result = dynamicService.getAllProducts();

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));

        verify(dynamicProductRepository, times(1)).findAll();
        verify(dynamicProductMapper, times(2)).toDto(product1);
        verify(dynamicProductMapper, times(2)).toDto(product2);
    }

    @Test
    @DisplayName("Добавляет новый продукт")
    void testAddProduct() {
        // given
        DynamicRuleDto ruleDto = new DynamicRuleDto();
        DynamicProductDto productDto = new DynamicProductDto();
        productDto.setRule(List.of(ruleDto));

        DynamicProduct product = new DynamicProduct();
        DynamicRule rule = new DynamicRule();

        when(dynamicProductMapper.toEntity(productDto)).thenReturn(product);
        when(dynamicRuleMapper.toEntity(ruleDto)).thenReturn(rule);
        when(dynamicRuleRepository.save(any(DynamicRule.class))).thenReturn(rule);
        when(dynamicProductRepository.save(any(DynamicProduct.class))).thenReturn(product);

        // when
        DynamicProductDto result = dynamicService.addProduct(productDto);

        // then
        assertNotNull(result);
        assertEquals(productDto, result);

        verify(dynamicProductMapper, times(1)).toEntity(productDto);
        verify(dynamicRuleMapper, times(1)).toEntity(ruleDto);
        verify(dynamicRuleRepository, times(1)).save(rule);
        verify(dynamicProductRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("Удаляет продукт")
    void testDeleteProduct() {
        // given
        UUID productId = UUID.randomUUID();
        DynamicProduct product = new DynamicProduct();
        DynamicRule rule1 = new DynamicRule();
        DynamicRule rule2 = new DynamicRule();
        product.setRule(Arrays.asList(rule1, rule2));

        when(dynamicProductRepository.findById(productId)).thenReturn(java.util.Optional.of(product));

        // when
        dynamicService.deleteProduct(productId);

        // then
        verify(dynamicProductRepository, times(1)).findById(productId);
        verify(dynamicRuleRepository, times(1)).deleteAll(Arrays.asList(rule1, rule2));
        verify(dynamicProductRepository, times(1)).delete(product);
    }
}