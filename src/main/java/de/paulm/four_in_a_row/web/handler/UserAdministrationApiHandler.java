package de.paulm.four_in_a_row.web.handler;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import de.paulm.api.UserAdministrationApiDelegate;
import de.paulm.four_in_a_row.domain.security.Permission;
import de.paulm.four_in_a_row.domain.security.Role;
import de.paulm.four_in_a_row.mapper.SharedSecurityMapper;
import de.paulm.four_in_a_row.mapper.user.UserAdminMapper;
import de.paulm.four_in_a_row.service.UserAdministrationService;
import de.paulm.four_in_a_row.web.dtos.UserAdminCreateRequest;
import de.paulm.four_in_a_row.web.dtos.UserAdminPatchRequest;
import de.paulm.four_in_a_row.web.dtos.UserAdminResponse;
import de.paulm.four_in_a_row.web.util.ResourceLocationHelper;
import de.paulm.model.UserAdminCreateRequestWdto;
import de.paulm.model.UserAdminPatchRequestWdto;
import de.paulm.model.UserAdminWdto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserAdministrationApiHandler implements UserAdministrationApiDelegate {

    private final UserAdministrationService userAdministrationService;
    private final UserAdminMapper userAdminMapper;
    private final SharedSecurityMapper sharedSecurityMapper;

    @Override
    public ResponseEntity<List<UserAdminWdto>> getUsersAsAdmin() {
        List<UserAdminResponse> userAdminList = userAdministrationService.getUsersAsAdmin();
        List<UserAdminWdto> wdtos = userAdminMapper.toResponseWdtoList(userAdminList);
        return ResponseEntity.ok(wdtos);
    }

    @Override
    public ResponseEntity<UserAdminWdto> getUserByIdAsAdmin(Long userId) {
        UserAdminResponse userAdmin = userAdministrationService.getUserByIdAsAdmin(userId);
        UserAdminWdto wdto = userAdminMapper.toResponseWdto(userAdmin);
        return ResponseEntity.ok(wdto);
    }

    @Override
    public ResponseEntity<UserAdminWdto> createUserAsAdmin(UserAdminCreateRequestWdto requestWdto) {
        UserAdminCreateRequest request = userAdminMapper.fromCreateRequestWdto(requestWdto);
        UserAdminResponse response = userAdministrationService.createUser(request);
        UserAdminWdto responseWdto = userAdminMapper.toResponseWdto(response);

        URI location = ResourceLocationHelper.create(responseWdto.getId(), "userId");
        return ResponseEntity.created(location).body(responseWdto);
    }

    @Override
    public ResponseEntity<UserAdminWdto> patchUserAsAdmin(Long userId,
            UserAdminPatchRequestWdto requestWdto) {
        UserAdminPatchRequest request = userAdminMapper.fromPatchRequestWdto(requestWdto);
        UserAdminResponse response = userAdministrationService.patchUser(userId, request);
        UserAdminWdto responseWdto = userAdminMapper.toResponseWdto(response);

        return ResponseEntity.ok(responseWdto);
    }

    @Override
    public ResponseEntity<Void> deleteUserAsAdmin(Long userId) {
        // TODO Auto-generated method stub
        return UserAdministrationApiDelegate.super.deleteUserAsAdmin(userId);
    }

    @Override
    public ResponseEntity<List<String>> getAllRoles() {
        List<String> roles = sharedSecurityMapper.fromRoleArray(Role.values());
        return ResponseEntity.ok(roles);
    }

    @Override
    public ResponseEntity<List<String>> getAllPermissions() {
        List<String> permissions = sharedSecurityMapper.fromPermissionArray(Permission.values());
        return ResponseEntity.ok(permissions);
    }
}
