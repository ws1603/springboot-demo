package hello.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class AuthControllerTest {
    private MockMvc mvc;

    @Mock
    private UserService userService;
    @Mock
    private AuthenticationManager authenticationManager;

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new AuthController(userService, authenticationManager)).build();
    }

    /**
     *  在JVM中的某个地方
     */ {

//        AuthControllerTest testInstance1 = new AuthControllerTest();
//        testInstance1.setUp();
//        testInstance1.test1();
//        AuthControllerTest testInstance2 = new AuthControllerTest();
//        testInstance2.test2();
//        testInstance2.setUp();
        // Junit每次测试创建实例来保证数据隔离
    }

    @Test
    void returnNotLoginByDefault() throws Exception {
        mvc.perform(get("/auth")).andExpect(status().isOk()).andExpect(result -> {
//            System.out.println("------------"+result.getResponse().getCharacterEncoding());
            result.getResponse().setCharacterEncoding("utf8");
            Assertions.assertTrue(result.getResponse().getContentAsString().contains("用户没有登录"));
        });
    }

    @Test
    void testLogin() throws Exception {
    /*
    未登录时，/auth接口返回未登录状态
    使用/auth/login登录
    检查/auth的返回值,处于登录状态
     */

        // 未登录时，/auth接口返回未登录状态
        mvc.perform(get("/auth")).andExpect(status().isOk()).andExpect(result -> {
            result.getResponse().setCharacterEncoding("utf8");
            Assertions.assertTrue(result.getResponse().getContentAsString().contains("用户没有登录"));
        });

        // 使用auth/login登录
        Map<String, String> usernamePassword = new HashMap<>();
        usernamePassword.put("username", "MyUser");
        usernamePassword.put("password", "MyPassword");

        Mockito.when(userService.loadUserByUsername("MyUser")).thenReturn(new User("MyUser", bCryptPasswordEncoder.encode("MyPassword"), Collections.emptyList()));
        Mockito.when(userService.getUserByUsername("MyUser")).thenReturn(new hello.entity.User(123, "MyUser", bCryptPasswordEncoder.encode("MyPassword")));

        MvcResult response = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(usernamePassword)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    result.getResponse().setCharacterEncoding("utf8");
                    Assertions.assertTrue(result.getResponse().getContentAsString().contains("登录成功"));
                })
                .andReturn();

        HttpSession session = response.getRequest().getSession();

        // 再次检查/auth的返回值,处于登录状态
        mvc.perform(get("/auth").session((MockHttpSession) session)).andExpect(status().isOk()).andExpect(result -> {
            result.getResponse().setCharacterEncoding("utf8");
            System.out.println(result.getResponse().getContentAsString());
            Assertions.assertTrue(result.getResponse().getContentAsString().contains("MyUser"));
        });
    }
}
