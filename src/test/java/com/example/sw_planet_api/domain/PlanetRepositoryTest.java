package com.example.sw_planet_api.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static com.example.sw_planet_api.commons.PlanetConstants.PLANET;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class PlanetRepositoryTest {
    @Autowired
    private PlanetRepository planetRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void createPlanet_WithValidData_ReturnsPlanet() {
        // Teste original do curso. identifiquei que no fim validava-se o mesmo objeto,
        // portanto, o teste sempre passaria

//        Planet planet = planetRepository.save(PLANET);
//        Planet sut = testEntityManager.find(Planet.class, planet.getId());
//
//        assertThat(sut.getName()).isEqualTo(PLANET.getName());
//        assertThat(sut.getClimate()).isEqualTo(PLANET.getClimate());
//        assertThat(sut.getTerrain()).isEqualTo(PLANET.getTerrain());

        // Teste corrigido comparando resultado com um objeto diferente, mas com os mesmos valores
        Planet expected = new Planet(1L, PLANET.getName(), PLANET.getClimate(), PLANET.getTerrain());

        planetRepository.save(PLANET);

        Planet sut = testEntityManager.find(Planet.class, expected.getId());
        assertThat(sut.getName()).isEqualTo(expected.getName());
        assertThat(sut.getClimate()).isEqualTo(expected.getClimate());
        assertThat(sut.getTerrain()).isEqualTo(expected.getTerrain());

    }
}
