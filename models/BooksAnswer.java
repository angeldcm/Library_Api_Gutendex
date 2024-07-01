package com.literatura.challenge_2_back_literatura.models;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.literatura.challenge_2_back_literatura.models.records.BooksData;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BooksAnswer {

    @JsonAlias("results")
    List<BooksData> resultadoLibros;

    public List<BooksData> getResultadoLibros() {
        return resultadoLibros;
    }

    public void setResultadoLibros(List<BooksData> resultadoLibros) {
        this.resultadoLibros = resultadoLibros;
    }
}
