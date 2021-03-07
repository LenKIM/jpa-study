package jpabook.jpashop.controller;

import static org.springframework.http.HttpHeaders.IF_UNMODIFIED_SINCE;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@WebMvcTest(HomeController.class)
public class HomeControllerTest {

  @Autowired
  public MockMvc mockMvc;

  @Test
  public void whenValidate_thenReturnCacheHeader() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add(IF_UNMODIFIED_SINCE, "Tue, 04 Feb 2020 19:57:25 GMT");
    this.mockMvc.perform(MockMvcRequestBuilders.get("/hello/aaa").headers(headers))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().is(304));
  }

  @Test
  public void _60초후에_요청하면_캐시안하고_새로운_데이터를_가져올까() throws Exception {
    this.mockMvc.perform(MockMvcRequestBuilders.get("/hello/name"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.header()
            .string("Cache-Control", "max-age=60, must-revalidate, no-transform"));

    Thread.sleep(5000);

    this.mockMvc.perform(MockMvcRequestBuilders.get("/hello/name"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isNotModified())
        .andExpect(MockMvcResultMatchers.header()
            .string("Cache-Control", "max-age=60, must-revalidate, no-transform"));

    Thread.sleep(1000 * 60);

    this.mockMvc.perform(MockMvcRequestBuilders.get("/hello/name"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.header()
            .string("Cache-Control", "max-age=60, must-revalidate, no-transform"));
  }
}