package com.artifact;

import com.artifact.entity.Role;
import com.artifact.entity.User;
import com.artifact.service.AuthService;
import com.artifact.service.RoleService;
import com.artifact.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = AuthJavaApplication.class)
public class TestTest {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Test
    public void test() {
//        userService.findById(1L);
        System.out.println();

//        Role admin = roleService.findByName("ADMIN");
        System.out.println();

        List<User> byLogin = userService.findByLogin("123");

        System.out.println();
    }
}
