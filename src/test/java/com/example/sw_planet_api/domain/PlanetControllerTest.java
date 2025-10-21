package com.example.sw_planet_api.domain;

import com.example.sw_planet_api.web.PlanetController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static com.example.sw_planet_api.commons.PlanetConstants.PLANET;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlanetController.class)
public class PlanetControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PlanetService planetService;

    @Test
    public void createPlanet_WithValidData_ReturnsCreated() throws Exception {
        when(planetService.create(any(Planet.class))).thenReturn(PLANET);

        mockMvc.perform(post("/planet").content(objectMapper.writeValueAsString(PLANET)).contentType("application/json"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(PLANET.getName()))
                .andExpect(jsonPath("$.climate").value(PLANET.getClimate()))
                .andExpect(jsonPath("$.terrain").value(PLANET.getTerrain()));
    }

    @Test
    public void createPlanet_WithInvalidData_ReturnsBadRequest() throws Exception {
        Planet emptyPlanet = new Planet();
        Planet invalidPlanet = new Planet("", "", "");

        mockMvc.perform(post("/planet").content(objectMapper.writeValueAsString(emptyPlanet)).contentType("application/json"))
                .andExpect(status().isUnprocessableEntity());
        mockMvc.perform(post("/planet").content(objectMapper.writeValueAsString(invalidPlanet)).contentType("application/json"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void createPlanet_WithExistingData_ReturnsConflict() throws Exception {
        when(planetService.create(any(Planet.class))).thenThrow(DataIntegrityViolationException.class);

        mockMvc.perform(post("/planet").content(objectMapper.writeValueAsString(PLANET)).contentType("application/json"))
                .andExpect(status().isConflict());
    }

    @Test
    public void getPlanet_ByExistingId_ReturnsPlanet() throws Exception {
        when(planetService.get(anyLong())).thenReturn(Optional.of(PLANET));

        mockMvc.perform(post("/planet").content(objectMapper.writeValueAsString(PLANET)).contentType("application/json"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(PLANET.getName()))
                .andExpect(jsonPath("$.climate").value(PLANET.getClimate()))
                .andExpect(jsonPath("$.terrain").value(PLANET.getTerrain()));
    }
}
