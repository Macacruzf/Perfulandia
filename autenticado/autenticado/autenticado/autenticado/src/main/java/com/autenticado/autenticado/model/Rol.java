package com.autenticado.autenticado.model;


import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true) // para no tener problemas 
@Relation(itemRelation = "rol", collectionRelation = "roles") // Para colecciones HATEOAS
@Schema(description = "Representa un rol asignado a un usuario en el sistema")
public class Rol extends RepresentationModel<Rol> {

    @Schema(description = "Identificador único del rol", example = "1")
    private Long id;

    @Schema(description = "Nombre del rol", example = "ADMIN")
    private String nombreRol;
}