package com.eventtickets.eventtickets.repositories;

import com.eventtickets.eventtickets.model.SectionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionTypeRepository extends JpaRepository<SectionType, Long> {
    SectionType findByName(String name); // Buscar tipo de sección por nombre
}
