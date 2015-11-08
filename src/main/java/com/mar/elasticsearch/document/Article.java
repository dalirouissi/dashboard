package com.mar.elasticsearch.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.NestedField;

@Document(indexName = "articles", type = "article", shards = 4, replicas = 2)
public class Article {

	
	@Id
	private String id;
	
	
	private String title;
	
	@MultiField(mainField = @Field(type = FieldType.String, index = FieldIndex.analyzed),
			otherFields = {@NestedField(dotSuffix = "untouched", type = FieldType.String, store = true, index = FieldIndex.not_analyzed),
						   @NestedField(dotSuffix = "sort", type = FieldType.String, store = true, indexAnalyzer = "keyword")})
	private List<String> authors = new ArrayList<>();
	
	@Field(type = FieldType.Integer, store = true)
	private List<Integer> publishedYear = new ArrayList<>();
	
	@Field(type = FieldType.String, store = true)
	private Collection<String> tags;
	
	private int score;
	
	public Article(){
		
	}
	
	public Article(String id){
		this.id = id;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Integer> getPublishedYear() {
		return publishedYear;
	}

	public void setPublishedYear(List<Integer> publishedYear) {
		this.publishedYear = publishedYear;
	}

	public Collection<String> getTags() {
		return tags;
	}

	public void setTags(Collection<String> tags) {
		this.tags = tags;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public List<String> getAuthors() {
		return authors;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}
}
