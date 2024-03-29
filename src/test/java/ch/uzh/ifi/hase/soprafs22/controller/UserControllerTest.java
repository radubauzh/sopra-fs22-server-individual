package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.*;
/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST request without actually sending them over the network.
 * This tests if the UserController works.
 */



@WebMvcTest(UserController.class)
public class UserControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    /**
     * POST -> add User -> 201
     */
    @Test
    public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        // given
        User user = new User();
        user.setUsername("firstname@lastname");
        user.setStatus(Boolean.FALSE);

        List<User> allUsers = Collections.singletonList(user);

        // this mocks the UserService -> we define above what the userService should return when getUsers() is called
        given(userService.getUsers()).willReturn(allUsers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is(user.getUsername())))
                .andExpect(jsonPath("$[0].status", is(user.getStatus())));
    }

    /**
     * GET -> Users -> list
     */
    @Test
    public void createUser_validInput_userCreated() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(Boolean.TRUE);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testUsername");

        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus())));
    }

    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input can be processed
     * Input will look like this: {"name": "Test User", "username": "testUsername"}
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The request body could not be created.%s", e.toString()));
        }
    }

    //--------------------------------------------------------------------------- all new
    /**
     * Test a postRequest to users, http-method, http-status, content-type // resulting data
     * Same Username ->  add User failed because username already exists -> Conflict 409
     * @throws Exception
     */
    @Test
    public void postRequest_users_test_should_fail() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(Boolean.TRUE);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testUsername");
        userPostDTO.setPassword("Password");

        given(userService.createUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT));

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        MvcResult result = mockMvc.perform(postRequest).andReturn();

        // test the http status of the response
        assertEquals(HttpStatus.CONFLICT.value(), result.getResponse().getStatus());
    }

    /**
     * Test putRequest to users, http-method, http-status, content-type
     * update user profile -> 204 No Content
     * @throws Exception
     */
    @Test
    public void putRequest_login_test() throws Exception { // checks the 204 update user profile
        // given
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(Boolean.TRUE);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testUsername");

        given(userService.createUser(Mockito.any())).willReturn(user); // tells what userservice will return

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users/1") //new
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        MvcResult result = mockMvc.perform(putRequest).andReturn();

        MockHttpServletResponse response = result.getResponse();

        // test the http status of the response -> Response == No Content 204
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());


        // test the http method () -> PUT
        assertEquals(HttpMethod.PUT.name(), result.getRequest().getMethod());

        // test the content type of the response
        assertEquals(MediaType.APPLICATION_JSON.toString(), result.getRequest().getContentType());
    }

    /**
     * Test a getRequest to users/{userId}, http-method, http-status, content-type
     *
     * @throws Exception
     */
    @Test
    public void getRequest_users_userId_test() throws Exception { //works
        // given
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(Boolean.TRUE);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testUsername");

        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        MvcResult result = mockMvc.perform(getRequest).andReturn();

        MockHttpServletResponse response = result.getResponse();
        //System.out.println(result.getResponse());

        // test the http status of the response -> 200
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        //System.out.println(response.getStatus());
        //System.out.println("HELLO HERE");
        //System.out.println(response.getStatus());

        // test the http method () -> GET
        assertEquals(HttpMethod.GET.name(), result.getRequest().getMethod());
        //System.out.println(result.getRequest().getMethod());

        // test the content type of the response
        assertEquals(MediaType.APPLICATION_JSON.toString(), result.getRequest().getContentType());
    }

    /**
     * Test a getRequest to users/{userId}, http-method, http-status, content-type
     * get -> 404 not found
     * @throws Exception
     */
    @Test
    public void getRequest_users_userNotFound() throws Exception { //works
        // given
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(Boolean.TRUE);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testUsername");

        Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(userService).getUserById(Mockito.anyLong());

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/users/23")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        MvcResult result = mockMvc.perform(getRequest).andReturn();

        MockHttpServletResponse response = result.getResponse();
        //System.out.println(result.getResponse());

        // test the http status of the response -> 200
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        //System.out.println(response.getStatus());

        // test the http method () -> GET
        assertEquals(HttpMethod.GET.name(), result.getRequest().getMethod());
        //System.out.println(result.getRequest().getMethod());

        // test the content type of the response
        assertEquals(MediaType.APPLICATION_JSON.toString(), result.getRequest().getContentType());
    }


}