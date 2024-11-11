package com.mdleo.appLiterAlura.repository;

import com.mdleo.appLiterAlura.model.Book;
import com.mdleo.appLiterAlura.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IBookRepository extends JpaRepository<Book, Long> {

    List<Book> findByLanguage(Language language);

    List<Book> findTop10ByOrderByDownloadCountDesc();
}
