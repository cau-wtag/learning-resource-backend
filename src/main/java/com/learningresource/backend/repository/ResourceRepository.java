package com.learningresource.backend.repository;

import com.learningresource.backend.model.Resource;
import com.learningresource.backend.model.ResourceDetail;
import com.learningresource.backend.model.ResourceSummary;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class ResourceRepository {

    private final JdbcClient jdbcClient;

    public ResourceRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    // 1. Fetch all resources with their link in a single SQL JOIN
    public List<ResourceSummary> findAllSummaries() {
        return jdbcClient.sql("""
                SELECT r.id, r.title, r.user_id, rd.link, r.created_at
                FROM resources r
                LEFT JOIN resource_details rd ON r.id = rd.resource_id
                ORDER BY r.id DESC
                """)
                .query(ResourceSummary.class)
                .list();
    }

    // 2. Fetch resources by user ID
    public List<ResourceSummary> findByUserId(Integer userId) {
        return jdbcClient.sql("""
                SELECT r.id, r.title, r.user_id, rd.link, r.created_at
                FROM resources r
                LEFT JOIN resource_details rd ON r.id = rd.resource_id
                WHERE r.user_id = :userId
                ORDER BY r.id DESC
                """)
                .param("userId", userId)
                .query(ResourceSummary.class)
                .list();
    }

    // 3. Atomic create: Inserts both the Resource and its ResourceDetail in one transaction
    @Transactional
    public Integer createWithDetail(String title, Integer userId, String link) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // Insert into resources
        jdbcClient.sql("""
                INSERT INTO resources (title, user_id)
                VALUES (:title, :userId)
                """)
                .param("title", title)
                .param("userId", userId)
                .update(keyHolder, "id");

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to retrieve generated id for resource");
        }
        Integer resourceId = key.intValue();

        // Insert into resource_details
        jdbcClient.sql("""
                INSERT INTO resource_details (resource_id, link)
                VALUES (:resourceId, :link)
                """)
                .param("resourceId", resourceId)
                .param("link", link)
                .update();

        return resourceId;
    }

    // 4. Delete resource by ID (ON DELETE CASCADE in Postgres auto-removes resource_details)
    public boolean deleteById(Integer id) {
        int rowsAffected = jdbcClient.sql("DELETE FROM resources WHERE id = :id")
                .param("id", id)
                .update();
        return rowsAffected > 0;
    }

    @Transactional
    public boolean updateWithDetail(Integer resourceId, Integer userId, String title, String link) {
        // 1. Update the parent resource title (ensure user owns it)
        int rows = jdbcClient.sql("""
                UPDATE resources 
                SET title = :title 
                WHERE id = :id AND user_id = :userId
                """)
                .param("title", title)
                .param("id", resourceId)
                .param("userId", userId)
                .update();

        if (rows == 0) return false;

        // 2. Update the link in resource_details
        jdbcClient.sql("""
                UPDATE resource_details 
                SET link = :link 
                WHERE resource_id = :resourceId
                """)
                .param("link", link)
                .param("resourceId", resourceId)
                .update();

        return true;
    }
}