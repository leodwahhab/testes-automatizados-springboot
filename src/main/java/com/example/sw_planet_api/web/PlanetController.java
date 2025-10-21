package com.example.sw_planet_api.web;

import com.example.sw_planet_api.domain.Planet;
import com.example.sw_planet_api.domain.PlanetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/planet")
public class PlanetController {
    @Autowired
    PlanetService pLanetService;

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody @Valid Planet planet) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pLanetService.create(planet));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        return pLanetService.get(id).map(planet -> ResponseEntity.ok().body(planet))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<?> findByName(@PathVariable("name") String name) {
        return pLanetService.getByName(name).map(planet -> ResponseEntity.ok().body(planet))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<?> findByClimateAndTerrain(@RequestParam(value = "climate", required = false) String climate,
                                                     @RequestParam(value = "terrain", required = false) String terrain) {
        return ResponseEntity.ok().body(pLanetService.getByTerrainAndClimate(terrain, climate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") Long id) {
        pLanetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
