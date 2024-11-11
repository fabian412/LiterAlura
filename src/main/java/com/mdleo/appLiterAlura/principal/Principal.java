package com.mdleo.appLiterAlura.principal;

import com.mdleo.appLiterAlura.model.*;
import com.mdleo.appLiterAlura.repository.IAuthorRepository;
import com.mdleo.appLiterAlura.repository.IBookRepository;
import com.mdleo.appLiterAlura.service.ConsumoAPI;
import com.mdleo.appLiterAlura.service.ConvierteDatos;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class Principal {

    private Scanner lectura = new Scanner(System.in);
    private ConsumoAPI consumoAPI;
    private ConvierteDatos convierteDatos;
    private IBookRepository bookRepository;
    private IAuthorRepository authorRepository;
    private List<Book> books;
    private List<Author> authors;

    private final String URL_BASE = "https://gutendex.com/books?search=";

    // Constructor único con inyección de dependencias
    @Autowired
    public Principal(ConsumoAPI consumoAPI, ConvierteDatos convierteDatos, IBookRepository bookRepository, IAuthorRepository authorRepository) {
        this.consumoAPI = consumoAPI;
        this.convierteDatos = convierteDatos;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public void showMenu() {
        System.out.println("******************");
        System.out.println("* BIBLIOTECA LITERARIA *");
        System.out.println("******************");

        var option = -1;
        do {
            mostrarMenu();
            option = obtenerOpcionUsuario();
            manejarOpcionMenu(option);
        } while (option != 0);
    }

    private void mostrarMenu() {
        String menu = """
                1 - Buscar Libros
                2 - Listar Libros registrados
                3 - Listar Libros por idioma
                4 - Listar Autores registrados
                5 - Listar Autores por determinado año
                6 - Listar 10 Libros más descargados
                7 - Buscar Autores por nombre   
                8 - Analisis a los datos
                0 - Salir
                """;
        System.out.println(menu);
        System.out.print("Elige una opción: ");
    }

    private int obtenerOpcionUsuario() {
        try {
            return lectura.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Por favor, ingresa un número válido.");
            lectura.nextLine(); // Limpiar la entrada no válida
            return -1;
        }
    }

    private void manejarOpcionMenu(int option) {
        lectura.nextLine();  // Consumir salto de línea
        switch (option) {
            case 1 -> buscarBook();
            case 2 -> listarBooks();
            case 3 -> buscarIdioma();
            case 4 -> listarAuthors();
            case 5 -> buscarFechas();
            case 6 -> listarTop10();
            case 7 -> buscarAutor();
            case 8 -> analizarDatos();
            case 0 -> System.out.println("Gracias por usar el sistema. ¡Hasta pronto!");
            default -> System.out.println("Opción inválida. Intenta de nuevo.");
        }
    }


    private DataBook obtenerDatosDelUsuario() {
        System.out.print("Ingresa el título del libro que deseas buscar: ");
        var title = lectura.nextLine();
        String json = consumoAPI.obtenerDatos(URL_BASE + title.toLowerCase().replace(" ", "+"));

        DataResult dataResult = convierteDatos.obtenerDatos(json, DataResult.class);
        return dataResult.results().stream()
                .filter(b -> b.title().equalsIgnoreCase(title))
                .findFirst()
                .orElse(null);
    }

    @Transactional
    private void buscarBook() {
        try {
            DataBook dataBook = obtenerDatosDelUsuario();
            if (dataBook != null) {
                Book book = new Book(dataBook);
                for (DataAuthor dataAuthor : dataBook.authors()) {
                    Author author;
                    Optional<Author> existingAuthor = authorRepository.findAuthorByName(dataAuthor.name());
                    if (existingAuthor.isPresent()) {
                        author = existingAuthor.get(); // Autor existente
                    } else {
                        author = new Author(dataAuthor);
                        authorRepository.save(author);  // Guardar el nuevo autor
                    }
                    book.addAuthor(author);  // Agregar el autor al libro
                }
                bookRepository.save(book); // Guarda el libro
                System.out.println("Libro guardado exitosamente:\n" + formatBook(book));
            } else {
                System.out.println("Libro no encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Error al buscar o guardar el libro: " + e.getMessage());
        }
    }

    private void listarBooks() {
        books = bookRepository.findAll();
        if (books.isEmpty()) {
            System.out.println("No hay libros registrados.");
        } else {
            books.stream()
                    .sorted(Comparator.comparing(Book::getLanguage))
                    .forEach(book -> System.out.println(formatBook(book)));
        }
    }

    private void buscarIdioma() {
        System.out.print("Ingresa el idioma o lenguaje que deseas filtrar: ");
        var idioma = lectura.nextLine();
        var language = Language.fromTotalString(idioma.toLowerCase());

        List<Book> idiomBooks = bookRepository.findByLanguage(language);
        System.out.println("Libros en " + idioma + ":");
        idiomBooks.forEach(book -> System.out.println(formatBook(book)));
    }

    private void listarAuthors() {
        authors = authorRepository.findAllAuthorsWithBooks();
        if (authors.isEmpty()) {
            System.out.println("No hay autores registrados.");
        } else {
            authors.stream()
                    .sorted(Comparator.comparing(Author::getName))
                    .forEach(author -> System.out.println(formatAuthor(author)));
        }
    }

    private void buscarFechas() {
        System.out.println("Ingresa el año a buscar: ");
        var year = lectura.nextInt();
        List<Author> authorsAge = authorRepository.findAuthorsWithAge(year);
        if (authorsAge.isEmpty()) {
            System.out.println("No se han encontrado authores para ese año.");
        } else {
            authorsAge.stream()
                    .sorted(Comparator.comparing(Author::getBirthYear))
                    .forEach(author -> System.out.println(formatAuthor(author)));
        }
    }

    private void listarTop10() {
        List<Book> topBooks =bookRepository.findTop10ByOrderByDownloadCountDesc();
        topBooks.forEach(book -> System.out.println(formatBook(book)));
    }

    private void buscarAutor() {
        System.out.println("Ingresa el nombre del author a encontrar: ");
        var nameAuthor = lectura.nextLine();
        Optional<Author> author = authorRepository.findAuthors(nameAuthor);
        if (author.isPresent()) {
            Author foundAuthor = author.get();
            System.out.println(formatAuthor(foundAuthor));
        } else {
            System.out.println("No se encontró ningún resultado para el autor: " + nameAuthor);
        }
    }

    private void analizarDatos() {
        List<Book> books = bookRepository.findAll();
        DoubleSummaryStatistics est = books.stream()
                .filter(book -> book.getDownloadCount() > 0)
                .collect(Collectors.summarizingDouble(Book::getDownloadCount));
        System.out.println("---------------------------");
        System.out.println("Análisis de las descargas de los libros:");
        System.out.println("Promedio de descargas: " + Math.round(est.getAverage() * 100.00)/100.00);
        System.out.println("Máximo de descargas: " + est.getMax());
        System.out.println("Mínimo de descargas: " + est.getMin());
        System.out.println("Número de registros evaluados: " + est.getCount());
        System.out.println("---------------------------");

    }


    private String formatBook(Book book) {
        return """
                ---------------------------
                Título: %s
                Autor(es): %s
                Idioma: %s
                Número de descargas: %d
                ---------------------------
                """.formatted(
                book.getTitle(),
                book.getAuthors().stream().map(Author::getName).collect(Collectors.joining(", ")),
                book.getLanguage(),
                book.getDownloadCount()
        );
    }

    private String formatAuthor(Author author) {
        StringBuilder formatted = new StringBuilder("""
                ***************************
                Autor: %s
                Año de nacimiento: %s
                Año de fallecimiento: %s
                Libros escritos:
                """.formatted(
                author.getName(),
                Optional.ofNullable(author.getBirthYear()).map(String::valueOf).orElse("Desconocido"),
                Optional.ofNullable(author.getDeathYear()).map(String::valueOf).orElse("Desconocido")
        ));
        author.getBooks().forEach(book -> formatted.append(" - ").append(book.getTitle()).append("\n"));
        formatted.append("***************************");
        return formatted.toString();
    }


}



