package com.mar.elasticsearch.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mar.elasticsearch.document.Book;
import com.mar.elasticsearch.repository.SampleBookRepository;


@RestController
@RequestMapping("/book")
public class BookController {

	@Autowired
	private SampleBookRepository bookRepository;

	private static final Logger logger = Logger.getLogger(BookController.class);

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ResponseEntity<String> saveBook(@RequestBody Book book) {
		logger.info("save book  " + book);
		bookRepository.save(book);
		return new ResponseEntity<String>("The data book save", HttpStatus.OK);
	}

	@RequestMapping("/{name}")
	public List<Book> getBookByName(@PathVariable String name) {
		List<Book> books = bookRepository.findByName(name, new PageRequest(0, 10)).getContent();
		logger.info("The result size  " + books.size());
		return books;
	}

	@RequestMapping("/{name}/{price}")
	public List<Book> getBookByNameAndPrice(@PathVariable String name, @PathVariable Long price) {
		List<Book> books = bookRepository.findByNameAndPrice(name, price, new PageRequest(0, 10)).getContent();
		logger.info("The result size  " + books.size());
		return books;
	}

	@RequestMapping("/find/{name}/{price}")
	public List<Book> getBookNameOrPrice(@PathVariable String name, @PathVariable Long price) {
		List<Book> books = bookRepository.findByNameOrPrice(name, price, new PageRequest(0, 10)).getContent();
		logger.info("The result size  " + books.size());
		return books;
	}

}
