package com.tkahrs.retroexchange.Repositories;

import com.tkahrs.retroexchange.Models.VideoGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;
import java.util.Optional;

@RestResource
public interface VideoGameJpaRepository extends JpaRepository<VideoGame, Integer> {
    VideoGame findByGameName(String name);

    VideoGame findFirstByGameName(String name);

    VideoGame getByGameName(String name);
    List<VideoGame> findAllByGameName(String search);
}
