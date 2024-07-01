package com.literatura.challenge_2_back_literatura.repository;
import com.literatura.challenge_2_back_literatura.models.Books;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface iBooksRepository extends JpaRepository<Books, Long> {

    boolean existsByTitulo(String titulo);

    Books findByTituloContainsIgnoreCase(String titulo);

    List<Books> findByIdioma(String idioma);



    @Query("SELECT l FROM Libro l ORDER BY l.cantidadDescargas DESC LIMIT 10")
    List<Books> findTop10ByTituloByCantidadDescargas();


}
