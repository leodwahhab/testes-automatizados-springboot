package com.example.sw_planet_api.web;

import com.example.sw_planet_api.domain.Planet;
import com.example.sw_planet_api.domain.PlanetService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.example.sw_planet_api.commons.PlanetConstants.INVALID_PLANET;
import static com.example.sw_planet_api.commons.PlanetConstants.PLANET;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlanetController.class)
public class PlanetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean // MockitoBean because PlanetService is a dependency of PlanetController
                // which is in the Spring context
    private PlanetService planetService;

    @Test
    void createPlanet_withValidData_shouldReturnCreated() throws Exception {
        when(planetService.create(PLANET)).thenReturn(PLANET);

        mockMvc.perform(post("/planets").content(objectMapper.writeValueAsString(PLANET))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(PLANET));
    }

    @Test
    void createPlanet_withInvalidData_shouldReturnBadRequest() throws Exception {
        Planet nullsPlanet = new Planet();
        Planet emptyPlanet = new Planet("", "", "");
        Planet blankPlanet = new Planet(" ", " ", " ");

        mockMvc.perform(post("/planets").content(objectMapper.writeValueAsString(nullsPlanet))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());   // retorno 400 Bad Request,
                                                        // pois o Spring não consegue processar a requisição devido à validação falha
                                                        // implementada pelo Spring Validation (JSR-380) com as anotações @NotBlank e @NotNull na classe Planet.
        mockMvc.perform(post("/planets").content(objectMapper.writeValueAsString(emptyPlanet))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/planets").content(objectMapper.writeValueAsString(blankPlanet))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPlanet_withExistingName_shouldThrowException() throws Exception {
        when(planetService.create(any())).thenThrow(DataIntegrityViolationException.class);

        mockMvc.perform(post("/planets").content(objectMapper.writeValueAsString(PLANET))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }
}
