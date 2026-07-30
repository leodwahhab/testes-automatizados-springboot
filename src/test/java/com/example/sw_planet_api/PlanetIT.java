package com.example.sw_planet_api;

import com.example.sw_planet_api.domain.Planet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static com.example.sw_planet_api.commons.PlanetConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("it")
@SpringBootTest(webEnvironment =  SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/import_planets.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/remove_planets.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class PlanetIT {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createPlanet_returnsCreated() {
        ResponseEntity<Planet> sut = restTemplate.postForEntity("/planets", PLANET, Planet.class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(sut.getBody().getId()).isNotNull();
        assertThat(sut.getBody().getName()).isEqualTo(PLANET.getName());
        assertThat(sut.getBody().getClimate()).isEqualTo(PLANET.getClimate());
        assertThat(sut.getBody().getTerrain()).isEqualTo(PLANET.getTerrain());
    }

    @Test
    void getPlanet_returnsPlanet() {
        ResponseEntity<Planet> sut = restTemplate.getForEntity("/planets/1", Planet.class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut.getBody()).isEqualTo(TATOOINE);
    }

    @Test
    void getPlanetByName_returnsPlanet() {
        ResponseEntity<Planet> sut = restTemplate.getForEntity(String.format("/planets/name/%s", TATOOINE.getName()), Planet.class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut.getBody()).isEqualTo(TATOOINE);
    }

    @Test
    void listPlanets_returnsAllPlanets() {
        ResponseEntity<Planet[]> sut = restTemplate.getForEntity("/planets", Planet[].class);
        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut).isNotNull();
        assertThat(sut.getBody().length).isEqualTo(3);
        assertThat(sut.getBody()[0]).isEqualTo(TATOOINE);
        assertThat(sut.getBody()[1]).isEqualTo(ALDERAAN);
        assertThat(sut.getBody()[2]).isEqualTo(YAVINIV);
    }

    @Test
    void listPlanets_byClimate_returnsPlanets() {
        ResponseEntity<Planet[]> sut = restTemplate.getForEntity(String.format("/planets?climate=%s", ALDERAAN.getClimate()), Planet[].class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut).isNotNull();
        assertThat(sut.getBody().length).isEqualTo(1);
        assertThat(sut.getBody()[0]).isEqualTo(ALDERAAN);
    }

    @Test
    void listPlanets_byTerrain_returnsPlanets() {
        ResponseEntity<Planet[]> sut = restTemplate.getForEntity(String.format("/planets?terrain=%s", YAVINIV.getTerrain()), Planet[].class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut).isNotNull();
        assertThat(sut.getBody().length).isEqualTo(1);
        assertThat(sut.getBody()[0]).isEqualTo(YAVINIV);
    }

    @Test
    void removePlanet_returnsNoContent() {
        ResponseEntity<Void> sut = restTemplate.exchange("/planets/1", HttpMethod.DELETE, null, Void.class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
