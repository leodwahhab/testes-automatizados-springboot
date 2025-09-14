package com.example.sw_planet_api.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;

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

    }
}
