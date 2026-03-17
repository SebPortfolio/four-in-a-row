package de.paulm.four_in_a_row.web.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import de.paulm.api.UserApiDelegate;
import de.paulm.four_in_a_row.domain.security.User;
import de.paulm.four_in_a_row.mapper.user.UserMapper;
import de.paulm.four_in_a_row.service.UserService;
import de.paulm.model.UserPatchRequestWdto;
import de.paulm.model.UserResponseWdto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserApiHandler implements UserApiDelegate {

    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public ResponseEntity<UserResponseWdto> patchUser(Long userId, UserPatchRequestWdto userPatchRequestWdto) {
        User patchedUser = userService.editEmail(userId, userPatchRequestWdto.getEmail());
        UserResponseWdto responseWdto = userMapper.toWdto(patchedUser);
        return ResponseEntity.ok(responseWdto);
    }

    @Override
    public ResponseEntity<Void> deleteUser(Long userId) {
        // TODO Auto-generated method stub
        return UserApiDelegate.super.deleteUser(userId);
    }

}
