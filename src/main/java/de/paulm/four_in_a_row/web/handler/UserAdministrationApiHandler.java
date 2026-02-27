package de.paulm.four_in_a_row.web.handler;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import de.paulm.api.UserAdministrationApiDelegate;
import de.paulm.four_in_a_row.domain.security.UserProfileAggregate;
import de.paulm.four_in_a_row.mapper.UserAdminMapper;
import de.paulm.four_in_a_row.service.UserProfileAggregateService;
import de.paulm.model.BanPermanentRequestWdto;
import de.paulm.model.BanRequestWdto;
import de.paulm.model.UnbanRequestWdto;
import de.paulm.model.UserAdminCreateRequestWdto;
import de.paulm.model.UserAdminUpdateRequestWdto;
import de.paulm.model.UserAdminWdto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserAdministrationApiHandler implements UserAdministrationApiDelegate {

    private final UserProfileAggregateService userProfileAggregateService;
    private final UserAdminMapper userMapper;

    @Override
    public ResponseEntity<List<UserAdminWdto>> getUsersAsAdmin() {
        List<UserProfileAggregate> users = userProfileAggregateService.getAllAggregates();
        log.debug("User aggregiert: {}", users.size());
        List<UserAdminWdto> wdtos = userMapper.toWdtoList(users);
        log.debug("Users gemappt");
        return ResponseEntity.ok(wdtos);
    }

    @Override
    public ResponseEntity<UserAdminWdto> getUserByIdAsAdmin(Long userId) {
        UserProfileAggregate user = userProfileAggregateService.getAggregateByUserId(userId);
        UserAdminWdto wdto = userMapper.toWdto(user);
        return ResponseEntity.ok(wdto);
    }

    @Override
    public ResponseEntity<UserAdminWdto> createUserAsAdmin(UserAdminCreateRequestWdto userAdminCreateRequestWdto) {
        // TODO Auto-generated method stub
        return UserAdministrationApiDelegate.super.createUserAsAdmin(userAdminCreateRequestWdto);
    }

    @Override
    public ResponseEntity<UserAdminWdto> updateUserAsAdmin(Long userId,
            UserAdminUpdateRequestWdto userAdminUpdateRequestWdto) {
        // TODO Auto-generated method stub
        return UserAdministrationApiDelegate.super.updateUserAsAdmin(userId, userAdminUpdateRequestWdto);
    }

    @Override
    public ResponseEntity<Void> deleteUserAsAdmin(Long userId) {
        // TODO Auto-generated method stub
        return UserAdministrationApiDelegate.super.deleteUserAsAdmin(userId);
    }

    @Override
    public ResponseEntity<UserAdminWdto> banUser(Long userId, BanRequestWdto banRequestWdto) {
        // TODO Auto-generated method stub
        return UserAdministrationApiDelegate.super.banUser(userId, banRequestWdto);
    }

    @Override
    public ResponseEntity<UserAdminWdto> banUserPermanent(Long userId,
            BanPermanentRequestWdto banPermanentRequestWdto) {
        // TODO Auto-generated method stub
        return UserAdministrationApiDelegate.super.banUserPermanent(userId, banPermanentRequestWdto);
    }

    @Override
    public ResponseEntity<UserAdminWdto> unbanUser(Long userId, UnbanRequestWdto unbanRequestWdto) {
        // TODO Auto-generated method stub
        return UserAdministrationApiDelegate.super.unbanUser(userId, unbanRequestWdto);
    }
}
