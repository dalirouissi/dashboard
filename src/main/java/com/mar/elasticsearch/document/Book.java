package com.mar.elasticsearch.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "lib", type = "book")
public class Book {
	
	@Id
	private String id;
	private String name;
	private Long price;
	@Version
	private Long version;
	
	public Book() {
	}

	public Book(String id, String name, Long price, Long version) {
		super();
		this.id = id;
		this.name = name;
		this.price = price;
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
}
