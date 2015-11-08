package com.mar.elasticsearch.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.mar.elasticsearch.document.Book;



public interface SampleBookRepository extends ElasticsearchRepository<Book, String> { 

	
	Page<Book> findByName(String name, Pageable page);
	Page<Book> findByNameAndPrice(String name, Long price, Pageable pageable);
	Page<Book> findByNameOrPrice(String name, Long price, Pageable pageable);
}

