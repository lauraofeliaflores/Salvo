package com.mindhub.salvo;

import com.mindhub.salvo.models.Salvo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
// importamos y agregamos la anotación  @RepositoryRestResource y Spring Boot hizo el resto del trabajo.
//@RepositoryRestResource  convierte a  ShipRepository en un  repositorio de Rest.
// Spring Boot busca  captadores públicos tales como : Public String getFirstName ()
// y crea métodos para generar JSON para los campos asociados.

@RepositoryRestResource

public interface SalvoRepository extends JpaRepository<Salvo, Long> {


}
