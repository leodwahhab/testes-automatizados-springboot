package com.example.sw_planet_api.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static com.example.sw_planet_api.commons.PlanetConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
//@SpringBootTest(classes = {PlanetService.class})
class PlanetServiceTest {
    @InjectMocks
//    @Autowired
    private PlanetService planetService;

    @Mock
//    @MockitoBean
    private PlanetRepository planetRepository;

    @Test
    public void createPlanet_WithValidData_ReturnsPlanet() {
        // AAA
        // Arrange
        when(planetRepository.save(PLANET)).thenReturn(PLANET);

        // Act
        //System Under Test
        Planet sut = planetService.create(PLANET);

        // Assert
        assertEquals(PLANET, sut);
    }

    @Test
    public void createPlanet_withInvalidData_ThrowsException() {
        when(planetService.create(INVALID_PLANET)).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> planetService.create(INVALID_PLANET));
    }

    @Test
    public void getPlanet_ByExistingId_ReturnPlanet() {
        when(planetService.get(anyLong())).thenReturn(Optional.of(PLANET));

        Optional<Planet> sut = planetService.get(1L);

        assertTrue(sut.isPresent());
        assertEquals(PLANET, sut.get());
    }

    @Test
    public void getPLanet_ByUnexistingId_ReturnEmpty() {
        when(planetService.get(anyLong())).thenReturn(Optional.empty());

        Optional<Planet> sut = planetService.get(1L);

        assertTrue(sut.isEmpty());
    }

    @Test
    public void getPlanet_ByExistingName_ReturnPlanet() {
        when(planetRepository.findByName(anyString())).thenReturn(Optional.of(PLANET));

        Optional<Planet> sut = planetService.getByName(PLANET.getName());

        assertTrue(sut.isPresent());
        assertEquals(PLANET, sut.get());
    }

    @Test
    public void getPLanet_ByUnexistingName_ReturnEmpty() {
        String unexistingName = "Unexisting Name";

        when(planetService.getByName(anyString())).thenReturn(Optional.empty());

        Optional<Planet> sut = planetService.getByName(unexistingName);

        assertTrue(sut.isEmpty());
    }

    @Test
    public void getPlanets_ByExistingClimateAndTerrain_ReturnPlanets() {
        when(planetRepository.findAll(any(Example.class))).thenReturn(List.of(PLANET));

        List<Planet> sut = planetService.getByTerrainAndClimate(PLANET.getClimate(), PLANET.getTerrain());
        assertFalse(sut.isEmpty());
        assertEquals(PLANET, sut.getFirst());
        assertEquals(1, sut.size());
    }

    @Test
    public void getPlanets_ByUnexistingClimateAndTerrain_ReturnEmpty() {
        String unexistingClimate = "Unexisting Climate";
        String unexistingTerrain = "Unexisting Terrain";

        when(planetRepository.findAll(any(Example.class))).thenReturn(List.of());

        List<Planet> sut = planetService.getByTerrainAndClimate(unexistingClimate, unexistingTerrain);
        assertTrue(sut.isEmpty());
    }

    @Test
    public void deletePlanet_ByExistingId_ReturnTrue() {
        assertDoesNotThrow(() -> planetService.delete(1L));
    }

    @Test
    public void deletePlanet_ByUnexistingId_ThrowsException() {
        doThrow(RuntimeException.class).when(planetRepository).deleteById(anyLong());
        assertThrows(RuntimeException.class, () -> planetService.delete(1L));
    }
}