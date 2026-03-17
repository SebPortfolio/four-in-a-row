package de.paulm.four_in_a_row.web.handler;

import org.springframework.web.bind.annotation.RestController;

import de.paulm.api.UserApiDelegate;
import de.paulm.four_in_a_row.mapper.user.UserMapper;
import de.paulm.four_in_a_row.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserApiHandler implements UserApiDelegate {

    private final UserService userService;
    private final UserMapper userMapper;

}
