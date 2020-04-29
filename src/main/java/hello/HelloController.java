package hello;

import hello.entity.User;
import hello.service.UserService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;

@RestController
public class HelloController {
    private UserService userService;

    @Inject
    public HelloController(UserService userService) {
        this.userService = userService;
    }

//    @RequestMapping("/")
//    public User index() {
//        return this.userService.getUserById(2);
//    }

}

//  ORM Object Relationship Mapping 对象关系映射
// 1.JPA        不需要写SQL或很少的SQL
// 2.MyBatis    需要写SQL

// 使用XmlConfiguration和application.properties进行xml配置
// 使用JavaConfiguration进行基于注解的配置

// JavaConfiguration中的@Bean标记的构造的UserService类可以改换成
// 在UserService类前标记@Service或@Component