package com.literatura.challenge_2_back_literatura.Libreria;
import com.literatura.challenge_2_back_literatura.config.ConsumoApiGutendex;
import com.literatura.challenge_2_back_literatura.config.ConvertirDatos;
import com.literatura.challenge_2_back_literatura.models.Autor;
import com.literatura.challenge_2_back_literatura.models.Books;
import com.literatura.challenge_2_back_literatura.models.BooksAnswer;
import com.literatura.challenge_2_back_literatura.models.records.BooksData;
import com.literatura.challenge_2_back_literatura.repository.iAutorRepository;
import com.literatura.challenge_2_back_literatura.repository.iBooksRepository;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

public class Libreria {

    private Scanner sc = new Scanner(System.in);
    private ConsumoApiGutendex consumoApi = new ConsumoApiGutendex();
    private ConvertirDatos convertir = new ConvertirDatos();
    private static String API_BASE = "https://gutendex.com/books/?search=";
    private List<Books> datosLibro = new ArrayList<>();
    private iBooksRepository bookRepository;
    private iAutorRepository autorRepository;
    public Libreria(iBooksRepository bookRepository, iAutorRepository autorRepository) {
        this.bookRepository = bookRepository;
        this.autorRepository = autorRepository;
    }

    public void consumo(){
        var opcion = -1;
        while (opcion != 0){
            var menu = """
                    __Iniciando la Aplicación__
                    ____________________________
                    __Selecciona la opción que desea realizar__

                
                    1) - Agregar Libro por Nombre
                    2) - Libros buscados
                    3) - Buscar libro por Nombre
                    4) - Buscar todos los Autores de libros buscados
                    5) - Buscar Autores por año
                    6) - Buscar Libros por Idioma
                    7) - Top 10 Libros mas Descargados
                    8) - Buscar Autor por Nombre                                   
                    0) - Salir                  
                    """;

            try {
                System.out.println(menu);
                opcion = sc.nextInt();
                sc.nextLine();
            } catch (InputMismatchException e) {

                System.out.println("""
                        
                __La opción seleccionada no es validad, ingrese un número de las opciones__
                        """);
                sc.nextLine();
                continue;
            }



            switch (opcion){
                case 1:
                    buscarLibroEnLaWeb();
                    break;
                case 2:
                    librosBuscados();
                    break;
                case 3:
                    buscarLibroPorNombre();
                    break;
                case 4:
                    BuscarAutores();
                    break;
                case 5:
                    buscarAutoresPorAnio();
                    break;
                case 6:
                    buscarLibrosPorIdioma();
                    break;
                case 7:
                    top10LibrosMasDescargados();
                    break;
                case 8:
                    buscarAutorPorNombre();
                    break;
                case 0:
                    opcion = 0;
                    System.out.println("--Cerrando Aplicación--\n");
                    break;
                default:
                    System.out.println("Ha seleccionado una opción incorreta.\n");
                    System.out.println("Selecciona una de las siguientes opciónes.\n");
                    consumo();
                    break;
            }
        }
    }

    private Books getDatosLibro(){
        System.out.println("Ingrese el nombre del libro: ");
        var nombreLibro = sc.nextLine().toLowerCase();
        var json = consumoApi.obtenerDatos(API_BASE + nombreLibro.replace(" ", "%20"));
        //System.out.println("JSON INICIAL: " + json);
        BooksAnswer datos = convertir.convertirDatosJsonAJava(json, BooksAnswer.class);

            if (datos != null && datos.getResultadoLibros() != null && !datos.getResultadoLibros().isEmpty()) {
                BooksData primerLibro = datos.getResultadoLibros().get(0); // Obtener el primer libro de la lista
                return new Books(primerLibro);
            } else {
                System.out.println("resultados no encontrado.");
                return null;
            }
    }


    private void buscarLibroEnLaWeb() {
        Books libro = getDatosLibro();

        if (libro == null){
            System.out.println("Libro no encontrado. el valor es null");
            return;
        }

        //datosLibro.add(libro);
        try{
            boolean libroExists = bookRepository.existsByTitulo(libro.getTitulo());
            if (libroExists){
                System.out.println("¡El libro ya existe en la base de datos!");
            }else {
                bookRepository.save(libro);
                System.out.println(libro.toString());
            }
        }catch (InvalidDataAccessApiUsageException e){
            System.out.println("¡No se puede persisitir el libro buscado!");
        }
    }

    @Transactional(readOnly = true)
    private void librosBuscados(){
        //datosLibro.forEach(System.out::println);
        List<Books> libros = bookRepository.findAll();
        if (libros.isEmpty()) {
            System.out.println("En la base de datos no se encontraron los libros.");
        } else {
            System.out.println("Libros encontrados en la base de datos:");
            for (Books libro : libros) {
                System.out.println(libro.toString());
            }
        }
    }

    private void buscarLibroPorNombre() {
        System.out.println("Ingrese Titulo libro que quiere buscar: ");
        var titulo = sc.nextLine();
        Books libroBuscado = bookRepository.findByTituloContainsIgnoreCase(titulo);
        if (libroBuscado != null) {
            System.out.println("El libro buscado es: " + libroBuscado);
        } else {
            System.out.println("El libro: " + titulo.toUpperCase() + "' no se encontró.");
        }
    }

    private  void BuscarAutores(){
        //LISTAR AUTORES DE LIBROS BUSCADOS
        List<Autor> autores = autorRepository.findAll();

        if (autores.isEmpty()) {
            System.out.println("En la base de datos no se encontraron libros.\n");
        } else {
            System.out.println("Libros encontrados en la base de datos: \n");
            Set<String> autoresUnicos = new HashSet<>();
            for (Autor autor : autores) {
                // add() retorna true si el nombre no estaba presente y se añade correctamente
                if (autoresUnicos.add(autor.getNombre())){
                    System.out.println(autor.getNombre()+'\n');
                }
            }
        }
    }

    private void  buscarLibrosPorIdioma(){
        System.out.println("Ingrese Idioma en el que quiere buscar: \n");
        System.out.println("--|--|");
        System.out.println("--Para libros en español = es |");
        System.out.println("--Para libros en ingles= en");
        System.out.println("--|--\n");

        var idioma = sc.nextLine();
        List<Books> librosPorIdioma = bookRepository.findByIdioma(idioma);

        if (librosPorIdioma.isEmpty()) {
            System.out.println("En la base de datos no se encontraron libros.\n");
        } else {
            System.out.println("En la base de datos se encontraron los libros:\n");
            for (Books libro : librosPorIdioma) {
                System.out.println(libro.toString());
            }
        }

    }

    private void buscarAutoresPorAnio() {
//        //BUSCAR AUTORES POR ANIO

        System.out.println("Digite el año de consulta para consultar que autores estan vivos: \n");
        var anioBuscado = sc.nextInt();
        sc.nextLine();

        List<Autor> autoresVivos = autorRepository.findByCumpleaniosLessThanOrFechaFallecimientoGreaterThanEqual(anioBuscado, anioBuscado);

        if (autoresVivos.isEmpty()) {
            System.out.println("La base de datos no se encontraron autores que estuvieran vivos en el año " + anioBuscado + ".");
        } else {
            System.out.println("Los autores que estaban vivos en el año " + anioBuscado + " son:");
            Set<String> autoresUnicos = new HashSet<>();

            for (Autor autor : autoresVivos) {
                if (autor.getCumpleanios() != null && autor.getFechaFallecimiento() != null) {
                    if (autor.getCumpleanios() <= anioBuscado && autor.getFechaFallecimiento() >= anioBuscado) {
                        if (autoresUnicos.add(autor.getNombre())) {
                            System.out.println("Autor: " + autor.getNombre());
                        }
                    }
                }
            }
        }
    }

    private void top10LibrosMasDescargados(){
        List<Books> top10Libros = bookRepository.findTop10ByTituloByCantidadDescargas();
        if (!top10Libros.isEmpty()){
            int index = 1;
            for (Books libro: top10Libros){
                System.out.printf("Libro %d: %s Autor: %s Descargas: %d\n",
                        index, libro.getTitulo(), libro.getAutores().getNombre(), libro.getCantidadDescargas());
                index++;
            }
            //top10Libros.forEach(l-> System.out.printf("Libro: %s Autor: %s Descargas: %s\n",
            //       l.getTitulo(), l.getAutores().getNombre(), l.getCantidadDescargas()));
        }
    }


    private void buscarAutorPorNombre() {
        System.out.println("Ingrese nombre del escritor que quiere buscar: ");
        var escritor = sc.nextLine();
        Optional<Autor> escritorBuscado = autorRepository.findFirstByNombreContainsIgnoreCase(escritor);
        if (escritorBuscado != null) {
            System.out.println("\nEl escritor buscado fue: " + escritorBuscado.get().getNombre());

        } else {
            System.out.println("\nEl escritor con el titulo '" + escritor + "' no se encontró.");
        }
    }
}
