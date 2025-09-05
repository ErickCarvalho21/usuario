package com.javanauta.usuario.infrastructure.repository;

import com.javanauta.apredendospring.infrastructure.entity.Telefone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelefoneRepository extends JpaRepository<Telefone, Long> {
}
