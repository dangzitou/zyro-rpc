package io.dangzitou.examplespringbootconsumer;

import io.dangzitou.example.common.model.User;
import io.dangzitou.example.common.service.UserService;
import io.dangzitou.zyrorpc.springboot.starter.annotation.RpcReference;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl {
    @RpcReference
    private UserService userService;

    public void test() {
        User user = new User();
        user.setName("dangzitou");
        user.setJob("java");
        String result = userService.getInfo(user);
        System.out.println("Result from provider: " + result);
    }
}
