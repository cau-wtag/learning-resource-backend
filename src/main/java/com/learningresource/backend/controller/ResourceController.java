package com.learningresource.backend.controller;

import com.learningresource.backend.dto.CreateResourceRequest;
import com.learningresource.backend.model.Resource;
import com.learningresource.backend.model.ResourceSummary;
import com.learningresource.backend.repository.ResourceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceRepository resourceRepository;

    public ResourceController(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    // GET /api/resources -> fetch all resources joined with links
    @GetMapping
    public List<ResourceSummary> getAllResources() {
        return resourceRepository.findAllSummaries();
    }

    // GET /api/resources/user/{userId} -> fetch resources by a specific user
    @GetMapping("/user/{userId}")
    public List<ResourceSummary> getResourcesByUser(@PathVariable Integer userId) {
        return resourceRepository.findByUserId(userId);
    }

    // POST /api/resources -> create a new resource with detail link
    @PostMapping
    public ResponseEntity<Integer> createResource(@RequestBody CreateResourceRequest request) {
        if (request.title() == null || request.userId() == null || request.link() == null) {
            return ResponseEntity.badRequest().build();
        }

        Integer newResourceId = resourceRepository.createWithDetail(
            request.title(),
            request.userId(),
            request.link()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(newResourceId);
    }

    // DELETE /api/resources/{id} -> delete resource and cascade details
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable Integer id) {
        boolean deleted = resourceRepository.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // PUT /api/resources/{id} -> update resource and its link
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateResource(
            @PathVariable Integer id,
            @RequestBody CreateResourceRequest request) {
        if (request.title() == null || request.link() == null || request.userId() == null) {
            return ResponseEntity.badRequest().build();
        }

        boolean updated = resourceRepository.updateWithDetail(id, request.userId(), request.title(), request.link());
        return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}