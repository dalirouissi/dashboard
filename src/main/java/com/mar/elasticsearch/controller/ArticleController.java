package com.mar.elasticsearch.controller;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.search.facet.FacetBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.FacetedPage;
import org.springframework.data.elasticsearch.core.facet.request.NativeFacetRequest;
import org.springframework.data.elasticsearch.core.facet.request.RangeFacetRequestBuilder;
import org.springframework.data.elasticsearch.core.facet.request.TermFacetRequestBuilder;
import org.springframework.data.elasticsearch.core.facet.result.Range;
import org.springframework.data.elasticsearch.core.facet.result.RangeResult;
import org.springframework.data.elasticsearch.core.facet.result.Term;
import org.springframework.data.elasticsearch.core.facet.result.TermResult;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mar.elasticsearch.document.Article;
import com.mar.elasticsearch.document.ArticleBuilder;
import com.mar.elasticsearch.repository.SampleArticleRepository;


@RestController
@RequestMapping("/article")
public class ArticleController {
	
	@Autowired
	private SampleArticleRepository articleRepository;
	
	@Autowired
	private ElasticsearchTemplate elasticSearchTemplate;
	
	
    public static final String RIZWAN_IDREES = "Rizwan Idrees";
    public static final String MOHSIN_HUSEN = "Mohsin Husen";
    public static final String JONATHAN_YAN = "Jonathan Yan";
    public static final String ARTUR_KONCZAK = "Artur Konczak";
    public static final int YEAR_2002 = 2002;
    public static final int YEAR_2001 = 2001;
    public static final int YEAR_2000 = 2000;
	
	private static Logger logger = LoggerFactory.getLogger(ArticleController.class);
	
	private String facetName = "theAuthor";
	
	@RequestMapping("/save")
	public ResponseEntity<String> saveArticle(@RequestBody Article article){
		ResponseEntity<String> response = null;
		if(article != null){
			logger.info("process saving article with id   "+article.getId());
			articleRepository.save(article);
			response = new ResponseEntity<String>("Success saving article", HttpStatus.OK);
		} else {
			response = new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		return response;
	}
	
	@RequestMapping("/find/{articleId}")
	public ResponseEntity<?> findArticleById(@PathVariable String articleId){
		ResponseEntity<?> response;
		Article article = null;
		logger.info("find the article with id == "+articleId);
		if(articleId != null){
			article = articleRepository.findOne(articleId);
		}
		if (article != null){
			response = new ResponseEntity<Article>(article, HttpStatus.OK);
		} else {
			response = new ResponseEntity<String>("article not found for the given id", 
					HttpStatus.NOT_FOUND);
		}
		return response;
	}
	
	@RequestMapping("/remove/{articleId}")
	public ResponseEntity<String> remove(@PathVariable String articleId){
		ResponseEntity<String> response ;
		if(articleId != null){
			articleRepository.delete(articleId);
			response = new ResponseEntity<String>("remove article success", HttpStatus.OK);
		} else {
			response = new ResponseEntity<String>("remove article fail",HttpStatus.OK);
		}
		return response;
	}

	@RequestMapping("/removeAll")
	public ResponseEntity<String> removeAll(){
		logger.info("process removing all articles");
		articleRepository.deleteAll();
		return new ResponseEntity<String>("processed removing all articles", HttpStatus.OK);
	}

	@RequestMapping("/createIndex")
	public ResponseEntity<String> createIndex(@RequestBody Article article){
		elasticSearchTemplate.createIndex(Article.class);
		elasticSearchTemplate.putMapping(Article.class);
		ArticleBuilder articleBuilder = new ArticleBuilder(article.getId());
		articleBuilder.title(article.getTitle()).score(article.getScore());
		
		for(String author : article.getAuthors()){
			articleBuilder.addAuthor(author);
		}
		for(String tag : article.getTags()){
			articleBuilder.addAuthor(tag);
		}
		IndexQuery articleIndex = articleBuilder.buildIndex();
		elasticSearchTemplate.index(articleIndex);
		elasticSearchTemplate.refresh(Article.class, true);
		return new ResponseEntity<String>("Index has been created", HttpStatus.OK);
	}

	@RequestMapping("/fauthor")
	public ResponseEntity<String> getData(){
		
		SearchQuery query = new NativeSearchQueryBuilder()
				.withQuery(matchAllQuery()).withFacet(new TermFacetRequestBuilder(facetName).fields("author.untouched").build()).build();
	FacetedPage<Article> facet = elasticSearchTemplate.queryForPage(query, Article.class);
	TermResult result = (TermResult)facet.getFacet(facetName);
	logger.info("The result size  is  "+result.getTerms().size());	
		return new ResponseEntity<String>("Result count is "+result.getTerms().size(), HttpStatus.OK);
	}
	
	@RequestMapping("/fauthor/filtered")
	public ResponseEntity<String> getFilteredQuery(){
		SearchQuery query = new NativeSearchQueryBuilder().withQuery(matchAllQuery())
				.withFilter(FilterBuilders.notFilter(FilterBuilders.termFilter("title", "four")))
				.withFacet(new TermFacetRequestBuilder(facetName).applyQueryFilter().fields("authors.untouched").build()).build();
		
		FacetedPage<Article> facet = elasticSearchTemplate.queryForPage(query, Article.class);
		TermResult result = (TermResult) facet.getFacet(facetName);
		logger.info("result found for filtered author "+result.getTerms().size());
		return new ResponseEntity<String>("result count "+result.getTerms().size(), HttpStatus.OK);
	}
	
	@RequestMapping("/fauthor/filtered/exclude")
	public ResponseEntity<String> getFilteredAndExcludeTermsQuery(){
		SearchQuery query = new NativeSearchQueryBuilder().withQuery(matchAllQuery())
							.withFilter(FilterBuilders.notFilter(FilterBuilders.termFilter("titile", "four")))
							.withFacet(new TermFacetRequestBuilder(facetName).applyQueryFilter().fields("authors.untouched").excludeTerms(RIZWAN_IDREES, ARTUR_KONCZAK).build()).build();
		FacetedPage<Article> facet = elasticSearchTemplate.queryForPage(query, Article.class);
		TermResult result = (TermResult)facet.getFacet(facetName);
		logger.info("Result found  ==  "+result.getTerms().size());
		return new ResponseEntity<String>("The result count "+result.getTerms().size(), HttpStatus.OK);
	}
	
	@RequestMapping("/fauthor/ordered")
	public ResponseEntity<?> queryOrderedByTerm() {
		SearchQuery query = new NativeSearchQueryBuilder().withQuery(matchAllQuery())
				.withFacet(new TermFacetRequestBuilder(facetName).fields("authors.untouched").ascTerm().build())
				.build();

		FacetedPage<Article> facet = elasticSearchTemplate.queryForPage(query, Article.class);
		TermResult result = (TermResult) facet.getFacet(facetName);
		List<String> data = handleTerm(result.getTerms());
		
		logger.info("The result count   " + result.getTerms().size());
		return new ResponseEntity<String>("names ars  " + data, HttpStatus.OK);
	}	
	
	@RequestMapping("/fauthor/orderByCount")
	public ResponseEntity<String> queryOrderedByCount(){
		SearchQuery query = new NativeSearchQueryBuilder().withQuery(matchAllQuery())
				.withFacet(new TermFacetRequestBuilder(facetName).fields("authors.untouched").ascCount().build()).build();
		
		FacetedPage<Article> facet = elasticSearchTemplate.queryForPage(query, Article.class);
		TermResult result = (TermResult) facet.getFacet(facetName);
		List<String> data = handleTerm(result.getTerms());
		logger.info("The result count   " + result.getTerms().size());
		return new ResponseEntity<String>("Names  =  " + data, HttpStatus.OK);
	}

	@RequestMapping("year/orderByYear")
	public ResponseEntity<String> queryForOrderYear() {
		facetName = "fyear";
		SearchQuery query = new NativeSearchQueryBuilder().withQuery(matchAllQuery())
				.withFacet(new TermFacetRequestBuilder(facetName).fields("publishedYear").descCount().build()).build();
		FacetedPage<Article> facet = elasticSearchTemplate.queryForPage(query, Article.class);
		TermResult result = (TermResult) facet.getFacet(facetName);
		logger.info("The result count is   " + result.getTerms().size());
		List<String> data = handleTerm(result.getTerms());
		return new ResponseEntity<String>("The data " + data, HttpStatus.OK);
	}	
	
	@RequestMapping("year/byYearAndAuthor")
	public ResponseEntity<String> singleFacetOverYearsAndAuthors(){
		facetName = "fyear";
		SearchQuery query = new NativeSearchQueryBuilder().withQuery(matchAllQuery())
			.withFacet(new TermFacetRequestBuilder(facetName).fields("publishedYear", "fauthors.untouched").ascTerm().build()).build();	
		FacetedPage<Article> facet = elasticSearchTemplate.queryForPage(query, Article.class);
		TermResult result = (TermResult)facet.getFacet(facetName);
		logger.info("The result  Term  size   "+result.getTerms().size());
		List<String> data = handleTerm(result.getTerms());
		return new ResponseEntity<String>("The data  "+data, HttpStatus.OK);
	}
	
	@RequestMapping("year/facetYearAndauthor")
	public ResponseEntity<String> facetedYearAndFactedAuthorQuery(){
		String facetYear = "fyear";
		SearchQuery query = new NativeSearchQueryBuilder().withQuery(matchAllQuery())
				.withFacet(new TermFacetRequestBuilder(facetYear).fields("publishedYear").ascTerm().build())
				.withFacet(new TermFacetRequestBuilder(facetName).fields("authors.untouched").ascTerm().build())
				.build();
		FacetedPage<Article> facet = elasticSearchTemplate.queryForPage(query, Article.class);
		TermResult resultNames = (TermResult)facet.getFacet(facetName);
		logger.info("The Result Names are  "+resultNames);
		TermResult resultYear = (TermResult) facet.getFacet(facetYear);
		logger.info("The result years  ars "+resultYear);
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	@RequestMapping("year/nativefacet")
	public ResponseEntity<String> facetedYearsForNativeFacet(){
		String facetYear = "fyear";
		SearchQuery query = new NativeSearchQueryBuilder().withQuery(matchAllQuery())
			.withFacet(new NativeFacetRequest(FacetBuilders.termsFacet(facetYear).field("publishedYear"))).build();	
		FacetedPage<Article> facet = elasticSearchTemplate.queryForPage(query, Article.class);
		
		TermResult result = (TermResult)facet.getFacet(facetYear);
		logger.info("The result is  =  "+result);
		List<String> data = handleTerm(result.getTerms());
		return new ResponseEntity<String>("The data result is   "+data, HttpStatus.OK);
	}
	
	@RequestMapping("/author/reggex")
	public ResponseEntity<String> filterResultByReggex(){
		facetName = "reggex_author";
		SearchQuery query = new NativeSearchQueryBuilder().withQuery(matchAllQuery())
				.withFilter(FilterBuilders.notFilter(FilterBuilders.termFilter("title","four")))
				.withFacet(new TermFacetRequestBuilder(facetName).applyQueryFilter().fields("authors.untouched").regex("Art.*").build())
				.build();
		FacetedPage<Article> facet = elasticSearchTemplate.queryForPage(query, Article.class);
		TermResult result = (TermResult) facet.getFacet(facetName);
		logger.info("The facet result is   "+result.getTerms().size());
		List<String> data = handleTerm(result.getTerms());
		return new ResponseEntity<String>("The data result "+data, HttpStatus.OK);
	}
	
	@RequestMapping("/range")
	public ResponseEntity<String> queryWithRange(){
		facetName = "rangeYears";
		SearchQuery query = new NativeSearchQueryBuilder().withQuery(matchAllQuery())
				.withFacet(new RangeFacetRequestBuilder(facetName)
							.fields("publishedYear", "score").to(YEAR_2000)
							.range(YEAR_2000, YEAR_2002).from(YEAR_2002).build())
							.build();
		FacetedPage<Article> facet = elasticSearchTemplate.queryForPage(query, Article.class);
		RangeResult result = (RangeResult)facet.getFacet(facetName);
		
		logger.info("The result for range is  =  "+result);

		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	@RequestMapping("/keyValue")
	public ResponseEntity<String> keyValueRangeFacet() {
		facetName = "rangeScoreOverYears";
		SearchQuery query = new NativeSearchQueryBuilder().withQuery(matchAllQuery())
				.withFacet(new RangeFacetRequestBuilder(facetName).fields("publishedYear", "score").to(YEAR_2000)
						.range(YEAR_2000, YEAR_2002).from(YEAR_2002).build())
				.build();

		FacetedPage<Article> facet = elasticSearchTemplate.queryForPage(query, Article.class);
		RangeResult range = (RangeResult) facet.getFacet(facetName);

		logger.info("The range result is  " + range);
		List<String> details = new ArrayList<>();
		for (Range r : range.getRanges()) {
			StringBuffer sb = new StringBuffer();
			sb.append("The range  " + r.getFrom() + " - " + r.getTo());
			sb.append("total score  " + r.getTotal());
			sb.append("for total count of elements   " + r.getCount());
			details.add(sb.toString());
		}
		return new ResponseEntity<String>("request made for keyvalue  " + details, HttpStatus.OK);
	}	
	
	private List<String> handleTerm(List<Term> terms){
		List<String> data = terms.stream().map(term -> term.getTerm())
				.collect(Collectors.toList());
		return data;
	}
}
























