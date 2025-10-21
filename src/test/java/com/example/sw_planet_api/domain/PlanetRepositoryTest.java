package com.example.sw_planet_api.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static com.example.sw_planet_api.commons.PlanetConstants.INVALID_PLANET;
import static com.example.sw_planet_api.commons.PlanetConstants.PLANET;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class PlanetRepositoryTest {
    @Autowired
    PlanetRepository planetRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void createPlanet_WithValidData_ReturnsPlanet() {
        Planet savedPlanet = planetRepository.save(PLANET);

        Planet sut = entityManager.find(Planet.class, savedPlanet.getId());

        assertNotNull(sut);
        assertEquals(sut.getName(), PLANET.getName());
        assertEquals(sut.getClimate(), PLANET.getClimate());
        assertEquals(sut.getTerrain(), PLANET.getTerrain());
    }

    @Test
    public void createPlanet_WithInvalidData_ThrowsException() {
        Planet emptyPlanet = new Planet(null, null, null);

        assertThrows(RuntimeException.class, () -> planetRepository.save(INVALID_PLANET));
        assertThrows(RuntimeException.class, () -> planetRepository.save(emptyPlanet));
    }

    @Test
    public void createPlanet_WithExistingData_ThrowsException() {
        Planet planet = entityManager.persistFlushFind(PLANET);
        entityManager.detach(planet);
        planet.setId(null);

        assertThrows(RuntimeException.class, () -> planetRepository.save(planet));
    }

    @Test
    public void getPlanet_ByExistingId_ReturnPlanet() {
        Planet planet = entityManager.persistFlushFind(PLANET);

        Optional<Planet> sut = planetRepository.findById(planet.getId());

        assertTrue(sut.isPresent());
        assertEquals(sut.get(), planet);
    }

    @Test
    public void getPlanet_ByUnexistingId_ReturnEmpty() {
        Optional<Planet> sut = planetRepository.findById(1L);

        assertTrue(sut.isEmpty());
    }
}
