package com.team.recommendations.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.recommendations.model.recommendations.Recommendation;
import com.team.recommendations.model.recommendations.RecommendationResponse;
import com.team.recommendations.repository.RecommendationsRepository;
import com.team.recommendations.service.Invest500Service;
import com.team.recommendations.service.SimpleCreditService;
import com.team.recommendations.service.TopSavingService;
import netscape.javascript.JSObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("removal")
@WebMvcTest
class RecommendationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private Invest500Service invest500Service;

    @SpyBean
    private SimpleCreditService simpleCreditService;

    @SpyBean
    private TopSavingService topSavingService;

    @MockBean
    private RecommendationsRepository recommendationsRepository;

    @InjectMocks
    private RecommendationController recommendationController;

    @Test
    @DisplayName("Выводит рекомендации")
    void getUserRecommendations() throws Exception {
//        given
        UUID id = UUID.randomUUID();
        UUID recID = UUID.randomUUID();
        Recommendation recommendationTop = new Recommendation("Top",recID,"1");
        Recommendation recommendationSimp = new Recommendation("Simp",recID,"2");
        Recommendation recommendationInv = new Recommendation("Inv",recID,"3");
        Collection<Recommendation> recommendations = new ArrayList<>();
        recommendations.add(recommendationInv);
        recommendations.add(recommendationTop);
        recommendations.add(recommendationSimp);

//        when
        when(topSavingService.getRecommendation(any(UUID.class))).thenReturn(Optional.of(recommendationTop));
        when(simpleCreditService.getRecommendation(any(UUID.class))).thenReturn(Optional.of(recommendationSimp));
        when(invest500Service.getRecommendation(any(UUID.class))).thenReturn(Optional.of(recommendationInv));

//        then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/recommendation/{user_id}",String.valueOf(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(String.valueOf(id)))
                .andExpect(jsonPath("$.recommendations.size()").value(3))
                .andExpect(jsonPath("$.recommendations[0].id").value(String.valueOf(recID)))
                .andExpect(jsonPath("$.recommendations[1].id").value(String.valueOf(recID)))
                .andExpect(jsonPath("$.recommendations[2].id").value(String.valueOf(recID)));
        verify(invest500Service,times(2)).getRecommendation(id);
        verify(topSavingService,times(2)).getRecommendation(id);
        verify(simpleCreditService,times(2)).getRecommendation(id);
    }
}