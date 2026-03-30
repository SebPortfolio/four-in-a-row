package de.paulm.four_in_a_row.web.handler;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import de.paulm.api.UserAdministrationApiDelegate;
import de.paulm.four_in_a_row.domain.security.Permission;
import de.paulm.four_in_a_row.domain.security.Role;
import de.paulm.four_in_a_row.domain.security.User;
import de.paulm.four_in_a_row.mapper.SharedSecurityMapper;
import de.paulm.four_in_a_row.mapper.user.UserAdminMapper;
import de.paulm.four_in_a_row.mapper.user.UserAdminMasterDataMapper;
import de.paulm.four_in_a_row.mapper.user.UserAdminOverviewMapper;
import de.paulm.four_in_a_row.mapper.user.UserAuditMapper;
import de.paulm.four_in_a_row.service.AuditService;
import de.paulm.four_in_a_row.service.UserAdministrationService;
import de.paulm.four_in_a_row.web.dtos.Audit;
import de.paulm.four_in_a_row.web.dtos.UserAdminCreateRequest;
import de.paulm.four_in_a_row.web.dtos.UserAdminMasterDataResponse;
import de.paulm.four_in_a_row.web.dtos.UserAdminOverviewResponse;
import de.paulm.four_in_a_row.web.dtos.UserAdminPatchRequest;
import de.paulm.four_in_a_row.web.dtos.UserAdminResponse;
import de.paulm.four_in_a_row.web.util.ResourceLocationHelper;
import de.paulm.model.EmailRevealResponseWdto;
import de.paulm.model.UserAdminCreateRequestWdto;
import de.paulm.model.UserAdminMasterDataResponseWdto;
import de.paulm.model.UserAdminOverviewResponseWdto;
import de.paulm.model.UserAdminPatchRequestWdto;
import de.paulm.model.UserAdminResponseWdto;
import de.paulm.model.UserAuditWdto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserAdministrationApiHandler implements UserAdministrationApiDelegate {

    private final UserAdministrationService userAdministrationService;
    private final AuditService auditService;

    private final UserAdminMapper userAdminMapper;
    private final SharedSecurityMapper sharedSecurityMapper;
    private final UserAuditMapper userAuditMapper;
    private final UserAdminOverviewMapper userAdminOverviewMapper;
    private final UserAdminMasterDataMapper userAdminMasterDataMapper;

    @Override
    public ResponseEntity<List<UserAdminResponseWdto>> getUsersAsAdmin() {
        List<UserAdminResponse> userAdminList = userAdministrationService.getUsersAsAdmin();
        List<UserAdminResponseWdto> wdtos = userAdminMapper.toResponseWdtoList(userAdminList);
        return ResponseEntity.ok(wdtos);
    }

    @Override
    public ResponseEntity<UserAdminResponseWdto> getUserByIdAsAdmin(Long userId) {
        UserAdminResponse userAdmin = userAdministrationService.getUserByIdAsAdmin(userId);
        UserAdminResponseWdto wdto = userAdminMapper.toResponseWdto(userAdmin);
        return ResponseEntity.ok(wdto);
    }

    @Override
    public ResponseEntity<UserAdminResponseWdto> createUserAsAdmin(UserAdminCreateRequestWdto requestWdto) {
        UserAdminCreateRequest request = userAdminMapper.fromCreateRequestWdto(requestWdto);
        UserAdminResponse response = userAdministrationService.createUser(request);
        UserAdminResponseWdto responseWdto = userAdminMapper.toResponseWdto(response);

        URI location = ResourceLocationHelper.create(responseWdto.getId(), "userId");
        return ResponseEntity.created(location).body(responseWdto);
    }

    @Override
    public ResponseEntity<UserAdminResponseWdto> patchUserAsAdmin(Long userId,
            UserAdminPatchRequestWdto requestWdto) {
        UserAdminPatchRequest request = userAdminMapper.fromPatchRequestWdto(requestWdto);
        UserAdminResponse response = userAdministrationService.patchUser(userId, request);
        UserAdminResponseWdto responseWdto = userAdminMapper.toResponseWdto(response);

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

    @Override
    public ResponseEntity<List<UserAuditWdto>> getUserHistory(Long userId) {
        List<Audit<User>> history = auditService.getHistory(User.class, userId);
        List<UserAuditWdto> wdtos = userAuditMapper.toWdtoList(history);
        return ResponseEntity.ok(wdtos);
    }

    @Override
    public ResponseEntity<EmailRevealResponseWdto> getRevealedEmail(Long userId) {
        String revealedEmail = userAdministrationService.getClearTextEmail(userId);
        return ResponseEntity.ok(new EmailRevealResponseWdto(revealedEmail));
    }

    @Override
    public ResponseEntity<List<UserAdminOverviewResponseWdto>> getUserAdminOverview() {
        List<UserAdminOverviewResponse> response = userAdministrationService.getUsersForAdminOverview();
        List<UserAdminOverviewResponseWdto> responseWdto = userAdminOverviewMapper.toWdtoList(response);
        return ResponseEntity.ok(responseWdto);
    }

    @Override
    public ResponseEntity<UserAdminMasterDataResponseWdto> getUserAdminMasterData(Long userId) {
        UserAdminMasterDataResponse response = userAdministrationService.getUserMasterData(userId);
        UserAdminMasterDataResponseWdto responseWdto = userAdminMasterDataMapper.toWdto(response);
        return ResponseEntity.ok(responseWdto);
    }
}
