package hello.service;

import hello.entity.User;
import hello.mapper.UserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collections;

@Service
public class UserService implements UserDetailsService {
    private UserMapper userMapper;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
//    private Map<String, User> users = new ConcurrentHashMap<>();

    @Inject
    public UserService(BCryptPasswordEncoder bCryptPasswordEncoder, UserMapper userMapper) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userMapper = userMapper;
//        save("user","password");
    }

    public void save(String username, String password) {
        userMapper.save(username,bCryptPasswordEncoder.encode(password));
//        users.put(username, new User(1,username,bCryptPasswordEncoder.encode(password)));
    }

    public User getUserByUsername(String username) {
        return userMapper.findUserByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username + "不存在！");
        }

//       User user = users.get(username);

        return new org.springframework.security.core.userdetails.User(username, user.getEncryptedPassword(), Collections.emptyList());
    }
}

//  警告:  绝对不可以存明文的密码
//  原因： 1.如果数据库被黑客盗走了，所有用户名密码就都泄露了
//         2.DMA(数据库管理人员)看到这些用户名密码他可能会拿去做坏事
//  因此我们在默认情况下在spring中会要求提供一个PasswordEncoder
//  当用户注册的时候他给你用户名密码时，你必须把他的用户名、加密后的密码存到数据库里
//  当用户登录时对它使用的密码进行一次相同的加密过程，在于数据库存的密文比对
//  所以  1.一定要加密
//        2.加密是不可逆的
//        3.加密必须是一致的
//        4.不要尝试自己设计加密算法
//  在spring中使用BCryptPasswordEncoder加密

//  MySql中主键id不能自增的问题
//  sql语句中先把id 设为not null auto_increment,最后再设为主键 primary key(id)
//  create table user (
//  id int  not null auto_increment,
//  username varchar(10) ,
//  encrypted_password varchar(100) ,
//  avatar varchar(100) ,
//  created_at datetime ,
//  updated_at datetime,
//  primary key(id));

//  解决SpringBoot中驼峰形式的sql和MySql中下划线形式的sql不匹配的问题
//  在application.properties中加入mybatis.configuration.mapUnderscoreToCamelCase=true
