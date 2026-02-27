package de.paulm.four_in_a_row.web.handler;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import de.paulm.api.UserApiDelegate;
import de.paulm.four_in_a_row.domain.security.UserProfileAggregate;
import de.paulm.four_in_a_row.mapper.UserMapper;
import de.paulm.four_in_a_row.service.UserProfileAggregateService;
import de.paulm.model.UserWdto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserApiHandler implements UserApiDelegate {

    private final UserProfileAggregateService userProfileAggregateService;
    private final UserMapper userMapper;

    @Override
    public ResponseEntity<List<UserWdto>> getUsers() {
        List<UserProfileAggregate> userProfileAggregates = userProfileAggregateService.getAllAggregates();
        List<UserWdto> wdtos = userMapper.toWdtoList(userProfileAggregates);
        return ResponseEntity.ok(wdtos);
    }
}
