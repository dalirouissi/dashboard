package com.mar.elasticsearch.repository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.mar.elasticsearch.document.Article;

public interface SampleArticleRepository extends ElasticsearchRepository<Article, String> {

}
