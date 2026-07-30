package com.example.sw_planet_api.web;

import com.example.sw_planet_api.domain.Planet;
import com.example.sw_planet_api.domain.PlanetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static com.example.sw_planet_api.commons.PlanetConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Test
    void getPlanet_byExistingId_shouldReturnPlanet() throws Exception {
        when(planetService.get(VALID_ID)).thenReturn(Optional.of(PLANET));

        mockMvc.perform(get("/planets/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(PLANET));
    }

    @Test
    void getPlanet_byUnexistingId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/planets/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPlanet_byExistingName_shouldReturnPlanet() throws Exception {
        when(planetService.getByName(anyString())).thenReturn(Optional.of(PLANET));

        mockMvc.perform(get("/planets/name/name").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(PLANET));
    }

    @Test
    void getPlanet_byUnexistingName_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/planets/name/name").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void listPlanets_ByExistingClimateAndTerrain_shouldReturnListWithPlanets() throws Exception {
        when(planetService.getByTerrainAndClimate(null, null)).thenReturn(PLANETS);
        when(planetService.getByTerrainAndClimate(TATOOINE.getClimate(), TATOOINE.getTerrain())).thenReturn(List.of(TATOOINE));

        mockMvc.perform(get("/planets").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.size()").value(3));

        // por algum motivo tem que se usar o string format pra testar os request parameters
        mockMvc.perform(get("/planets?" + String.format("climate=%s&terrain=%s", TATOOINE.getClimate(), TATOOINE.getTerrain())).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0]").value(TATOOINE));
    }

    @Test
    void listPlanets_ByUnxistingClimateAndTerrain_shouldReturnEmptyList() throws Exception {
        String unexistingClimate = "Unexisting Climate";
        String unexistingTerrain = "Unexisting Terrain";

        mockMvc.perform(get("/planets?" + String.format("climate=%s&terrain=%s", unexistingClimate, unexistingTerrain)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void removePlanet_withExistingId_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/planets/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void removePlanet_withUnexistingId_shouldReturn404() throws Exception {

    }
}
