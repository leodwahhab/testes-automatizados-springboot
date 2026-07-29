package com.example.sw_planet_api.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static com.example.sw_planet_api.commons.PlanetConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.*;

@DataJpaTest
public class PlanetRepositoryTest {
    @Autowired
    private PlanetRepository planetRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @BeforeEach
    void beforeEach() {
        PLANET.setId(null);
    }

    @Test
    void createPlanet_WithValidData_ReturnsPlanet() {
        // save() e find() compartilham o mesmo EntityManager/Session (persistence context)
        // dentro deste teste. Esse contexto é um cache de 1º nível (identity map por
        // tipo+id): save() devolve a própria instância de PLANET já gerenciada, e find()
        // com o mesmo id encontra essa instância no cache em vez de ir ao banco. Ou seja,
        // planet == sut == PLANET (mesma referência em memória) — os asserts abaixo
        // comparam o objeto com ele mesmo, não validam de fato a persistência dos campos.
        Planet planet = planetRepository.save(PLANET);
        Planet sut = testEntityManager.find(Planet.class, planet.getId());

        assertThat(sut.getName()).isEqualTo(PLANET.getName());
        assertThat(sut.getClimate()).isEqualTo(PLANET.getClimate());
        assertThat(sut.getTerrain()).isEqualTo(PLANET.getTerrain());
    }

    @Test
    void createPlanet_withInvalidData_ThrowsException() {
        Planet nullsPlanet = new Planet();
        Planet emptyPlanet = new Planet("", "", "");
        Planet blankPlanet = new Planet(" ", " ", " ");

        assertThatThrownBy(() -> planetRepository.save(nullsPlanet)).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> planetRepository.save(emptyPlanet)).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> planetRepository.save(blankPlanet)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void createPlanet_withExistingName_ThrowsException() {
        testEntityManager.persistAndFlush(PLANET);
        Planet existingNamePlanet = new Planet(PLANET.getName(), "climateEx", "terrainEx");

        assertThatThrownBy(() -> planetRepository.save(existingNamePlanet)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void getPlanetById_WithExistingId_ReturnsPlanet() {
        testEntityManager.persistAndFlush(PLANET);

        Optional<Planet> sut = planetRepository.findById(PLANET.getId());

        assertThat(sut).isPresent();
        assertThat(sut.get()).isEqualTo(PLANET);
    }

    @Test
    void getPlanetById_WithUnexistingId_ReturnsEmpty() {
        Optional<Planet> sut = planetRepository.findById(anyLong());

        assertThat(sut).isEmpty();
    }

    @Test
    void getPlanetById_WithExistingName_ReturnsPlanet() {
        testEntityManager.persistAndFlush(PLANET);

        Optional<Planet> sut = planetRepository.findByName(PLANET.getName());

        assertThat(sut).isPresent();
        assertThat(sut.get()).isEqualTo(PLANET);
    }

    @Test
    void getPlanetById_WithUnexistingName_ReturnsEmpty() {
        Optional<Planet> sut = planetRepository.findByName(anyString());

        assertThat(sut).isEmpty();
    }

    @Sql(scripts = "/import_planets.sql")
    @Test
    void listPlanets_ByExistingClimateAndTerrain_shouldReturnListWithPlanets() {
        Example<Planet> queryWithoutFilters = QueryBuilder.buildQuery(new Planet());
        Example<Planet> queryWithFilters = QueryBuilder.buildQuery(new Planet(TATOOINE.getClimate(), TATOOINE.getTerrain()));

        List<Planet> sutWithoutFilters = planetRepository.findAll(queryWithoutFilters);
        List<Planet> sutWithFilters = planetRepository.findAll(queryWithFilters);

        assertThat(sutWithoutFilters.isEmpty()).isFalse();
        assertThat(sutWithoutFilters.size()).isEqualTo(3);

        assertThat(sutWithFilters.isEmpty()).isFalse();
        assertThat(sutWithFilters.size()).isEqualTo(1);
        assertThat(sutWithFilters.getFirst()).isEqualTo(TATOOINE);
    }

    @Test
    void listPlanets_ByUnxistingClimateAndTerrain_shouldReturnEmptyList() {
        String unexistingClimate = "Unexisting Climate";
        String unexistingTerrain = "Unexisting Terrain";
        Example<Planet> query = QueryBuilder.buildQuery(new Planet(unexistingClimate, unexistingTerrain));

        List<Planet> sut = planetRepository.findAll(query);

        assertThat(sut.isEmpty()).isTrue();
    }

    @Test
    void removePlanet_withExistingId_shouldRemovePlanet() {
        testEntityManager.persistAndFlush(PLANET);

        assertThatCode(() -> planetRepository.deleteById(PLANET.getId())).doesNotThrowAnyException();
        assertThat(testEntityManager.find(Planet.class, PLANET.getId())).isNull();
    }
}
