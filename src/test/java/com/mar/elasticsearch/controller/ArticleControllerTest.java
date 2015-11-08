package com.mar.elasticsearch.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.mar.application.ApplicationTests;
import com.mar.elasticsearch.document.Article;
import com.mar.elasticsearch.repository.SampleArticleRepository;

 



@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTests.class)
@WebAppConfiguration
public class ArticleControllerTest {

	
	@Mock
	private SampleArticleRepository articleRepository;
	
	@InjectMocks
	private ArticleController articleController;
		
	@SuppressWarnings({ "unused", "rawtypes" })
	@Autowired
	private HttpMessageConverter httpMessageConverter;
	
	private MockMvc mockMVC;
	
	private MediaType contentType = new MediaType(
			MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	
	
	private static final String ARTICLEID = "1";
	
	private Article article;
	
	private static final Logger log = Logger.getLogger(ArticleControllerTest.class); 
	
	@Before
	public void initTests(){
		MockitoAnnotations.initMocks(this);
		article = new Article(ARTICLEID);
		this.mockMVC = MockMvcBuilders.standaloneSetup(articleController).build();
		article.setScore(2015);
	}
	
	@Test
	public void findArticleByIdTest_Success() throws Exception{
		when(articleRepository.findOne(ARTICLEID)).thenReturn(article);
		
		mockMVC.perform(post("/article/find/"+ARTICLEID)).andExpect(status().isOk())
		.andExpect(jsonPath("$.id", is(article.getId())))
		.andExpect(jsonPath("$.title", is(article.getTitle())));
	
		verify(articleRepository, timeout(1)).findOne(anyString());
	}
	
	
	@Test
	public void findArticleByIdTest_ArticleNotFound() throws Exception{
		when(articleRepository.findOne(ARTICLEID)).thenReturn(null);
		
		mockMVC.perform(post("/article/find/"+ARTICLEID))
		.andExpect(status().isNotFound());
		
		verify(articleRepository, timeout(1)).findOne(anyString());
	}

	@Test
	public void findArticleByIdTest_ArticlePathError() throws Exception{
		when(articleRepository.findOne(ARTICLEID)).thenReturn(null);
		
		mockMVC.perform(post("/article/find/"))
		.andExpect(status().isNotFound());
		verify(articleRepository, never()).findOne(anyString());
	}

	@Test
	public void saveArticle_Success() throws Exception{
		
		when(articleRepository.save(article)).thenReturn(article);
		mockMVC.perform(post("/article/save").content(convertToJon(article)).contentType(contentType))
				.andExpect(status().isOk());
		verify(articleRepository, times(1)).save(any(Article.class));
	
	}
	
	public void saveArticle_ArticleIsNull() throws HttpMessageNotWritableException, IOException, Exception{
		when(articleRepository.save(article)).thenReturn(article);
		
		mockMVC.perform(post("/article/save").content(convertToJon(null)).contentType(contentType))
		.andExpect(status().isNotFound());
		verify(articleRepository, never()).save(any(Article.class));
	}
	
	
	@SuppressWarnings("unchecked")
	private String convertToJon(Object o) throws HttpMessageNotWritableException, IOException{
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		this.httpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		log.info("The data json  ==   "+mockHttpOutputMessage.getBodyAsString());
		return mockHttpOutputMessage.getBodyAsString();
	}
}
