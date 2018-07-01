package com.statistics.transactionstatistics.constroller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.statistics.transactionstatistics.TransactionStatisticsApplication;
import com.statistics.transactionstatistics.model.Transaction;
import java.util.Date;
import java.util.stream.IntStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TransactionStatisticsApplication.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class StatisticsControllerTest {

  ObjectMapper mapper = new ObjectMapper();
  String baseUri = "/transactions";
  @Autowired
  private WebApplicationContext context;
  private MockMvc mvc;

  @Before
  public void setup() {
    this.mvc = MockMvcBuilders.webAppContextSetup(this.context).build();
  }

  @Test
  @DisplayName("happy path test, with one transaction and one result")
  public void testHappyPath() throws Exception {

    Transaction transaction = new Transaction(new Date().getTime(), 5.4D);
    mvc.perform(post(baseUri)
        .content(mapper.writeValueAsString(transaction))
        .contentType(APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(header().string(LOCATION, containsString("/transactions")));

    mvc.perform(get("/statistics"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("sum").value(5.4D))
        .andExpect(jsonPath("max").value(5.4D))
        .andExpect(jsonPath("min").value(5.4D))
        .andExpect(jsonPath("avg").value(5.4D))
        .andExpect(jsonPath("count").value(1L));
  }

  @Test
  @DisplayName("executing 100000 calls in parallel")
  public void concurrentExecution() throws Exception {

    IntStream.range(0, 100000).parallel().forEach(i -> {

      Transaction transaction = new Transaction(new Date().getTime(), 5.4D);
      try {
        mvc.perform(post(baseUri)
            .content(mapper.writeValueAsString(transaction))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(header().string(LOCATION, containsString("/transactions")));
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    mvc.perform(get("/statistics"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("sum").value(540000D))
        .andExpect(jsonPath("max").value(5.4D))
        .andExpect(jsonPath("min").value(5.4D))
        .andExpect(jsonPath("avg").value(5.4D))
        .andExpect(jsonPath("count").value(100000L));
  }

  @Test
  @DisplayName("test old transactions")
  public void testOldTransactions() throws Exception {

    long time = new Date().getTime() - 50000;
    Transaction transaction = new Transaction(time, 5.4D);
    mvc.perform(post(baseUri)
        .content(mapper.writeValueAsString(transaction))
        .contentType(APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(header().string(LOCATION, containsString("/transactions")));

    mvc.perform(get("/statistics"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("sum").value(5.4D))
        .andExpect(jsonPath("max").value(5.4D))
        .andExpect(jsonPath("min").value(5.4D))
        .andExpect(jsonPath("avg").value(5.4D))
        .andExpect(jsonPath("count").value(1L));

    transaction = new Transaction(time - 10000, 5.4D);
    mvc.perform(post(baseUri)
        .content(mapper.writeValueAsString(transaction))
        .contentType(APPLICATION_JSON))
        .andExpect(status().isNoContent());

    mvc.perform(get("/statistics"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("sum").value(5.4D))
        .andExpect(jsonPath("max").value(5.4D))
        .andExpect(jsonPath("min").value(5.4D))
        .andExpect(jsonPath("avg").value(5.4D))
        .andExpect(jsonPath("count").value(1L));
  }


}