package com.example.sw_planet_api.commons;

import com.example.sw_planet_api.domain.Planet;

import java.util.ArrayList;
import java.util.List;

public class PlanetConstants {
    public static final Planet PLANET = new Planet("name", "climate", "terrain");
    public static final Planet INVALID_PLANET = new Planet("", "", "");

    public static final Long VALID_ID = 1L;
    public static final Long INVALID_ID = 0L;

    public static final Planet TATOOINE = new Planet(1L,"Tatooine", "arid", "desert");
    public static final Planet ALDERAAN = new Planet(2L,"Alderaan", "temperate", "grasslands, mountains");
    public static final Planet YAVINIV = new Planet(3L,"Yavin IV", "temperate, tropical", "jungle, rainforest");
    public static final List<Planet> PLANETS = List.of(TATOOINE, ALDERAAN, YAVINIV);
}
