package com.mar.elasticsearch.document;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.elasticsearch.core.query.IndexQuery;

public class ArticleBuilder {
	
	private Article result;
	
	public ArticleBuilder(String id){
		result = new Article(id);
	}
	
	public ArticleBuilder title(String title){
		result.setTitle(title);
		return this;
	}
	
	public ArticleBuilder addAuthor(String author){
		result.getAuthors().add(author);
		return this;
	}
	
	public ArticleBuilder addPublishedYear(Integer year){
		result.getPublishedYear().add(year);
		return this;
	}
	
	public ArticleBuilder score(int score) {
		result.setScore(score);
		return this;
	}
	
	public Article build(){
		return result;
	}
	
	public ArticleBuilder addTag(String tag){
		List<String> tagsTmp = new ArrayList<>();
		if(result.getTags() == null){
			result.setTags(tagsTmp);
		}else {
			tagsTmp = (List<String>) result.getTags();
		}
		tagsTmp.add(tag);
		return this;
	}
	
	public IndexQuery buildIndex(){
		IndexQuery indexQuery = new IndexQuery();
		indexQuery.setId(result.getId());
		indexQuery.setObject(result);
		return indexQuery;
	}
}
