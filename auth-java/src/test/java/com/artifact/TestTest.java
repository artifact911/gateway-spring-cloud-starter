package com.artifact;

import com.artifact.entity.Role;
import com.artifact.service.RoleService;
import com.artifact.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

        Role admin = roleService.findByName("ADMIN");
        System.out.println();

        System.out.println();
    }
}
