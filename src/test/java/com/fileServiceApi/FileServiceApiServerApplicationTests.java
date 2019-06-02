package com.fileServiceApi;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class FileServiceApiServerApplicationTests extends AbstractTest {
	
	
   @Override
   @Before
   public void setUp() {
      super.setUp();
   }

	@Test
	public void downloadFile() throws Exception {
	// Note before running the test you have keep cute.jpeg file in fileStorage location which is mentioned in application.properties
	   String uri = "/api/downloadFile/cute.jpeg";
	   MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
	      .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();  
	   int status = mvcResult.getResponse().getStatus();
	   assertEquals(200, status);
	}
}
