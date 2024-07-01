package com.literatura.challenge_2_back_literatura.config.iConfig;

public interface IConvertirDatos {
    //Este metodo es el generico que convierte el json datos Java, debido a que no se sabe que retornara
    <T> T convertirDatosJsonAJava(String json , Class<T> clase); 
}
