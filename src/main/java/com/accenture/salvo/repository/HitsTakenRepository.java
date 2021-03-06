package com.accenture.salvo.repository;

import com.accenture.salvo.model.games.HitsTaken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface HitsTakenRepository extends JpaRepository<HitsTaken, Long> {
}
