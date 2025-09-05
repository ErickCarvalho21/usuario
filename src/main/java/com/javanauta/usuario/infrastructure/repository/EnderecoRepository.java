package com.javanauta.usuario.infrastructure.repository;

import com.javanauta.apredendospring.infrastructure.entity.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnderecoRepository extends JpaRepository <Endereco, Long> {
}
