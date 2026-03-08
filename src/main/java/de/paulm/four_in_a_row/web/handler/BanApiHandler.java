package de.paulm.four_in_a_row.web.handler;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import de.paulm.api.BanApiDelegate;
import de.paulm.api.UserAdministrationApiDelegate;
import de.paulm.four_in_a_row.domain.security.Ban;
import de.paulm.four_in_a_row.mapper.ban.BanChangeMapper;
import de.paulm.four_in_a_row.mapper.ban.BanMapper;
import de.paulm.four_in_a_row.service.BanService;
import de.paulm.four_in_a_row.web.dtos.ban.BanCreateRequest;
import de.paulm.four_in_a_row.web.dtos.ban.BanPermanentCreateRequest;
import de.paulm.four_in_a_row.web.dtos.ban.BanUpdateRequest;
import de.paulm.four_in_a_row.web.dtos.ban.CancelBanRequest;
import de.paulm.four_in_a_row.web.util.ResourceLocationHelper;
import de.paulm.model.BanCreateRequestWdto;
import de.paulm.model.BanPermanentCreateRequestWdto;
import de.paulm.model.BanUpdateRequestWdto;
import de.paulm.model.BanWdto;
import de.paulm.model.CancelBanRequestWdto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BanApiHandler implements BanApiDelegate {

    private final BanService banService;
    private final BanMapper banMapper;
    private final BanChangeMapper banChangeMapper;

    @Override
    public ResponseEntity<BanWdto> banUser(Long userId, BanCreateRequestWdto requestWdto) {
        BanCreateRequest mappedRequest = banChangeMapper.fromBanCreateRequestWdto(requestWdto);
        Ban ban = banService.createBan(userId, mappedRequest);
        BanWdto responseWdto = banMapper.toBanWdto(ban);
        URI location = ResourceLocationHelper
                .createFromMethod(on(UserAdministrationApiDelegate.class).getUserByIdAsAdmin(ban.getUser().getId()));
        // FIXME no request Mapping in delegate Fehler
        return ResponseEntity.created(location).body(responseWdto);
    }

    @Override
    public ResponseEntity<BanWdto> banUserPermanent(Long userId,
            BanPermanentCreateRequestWdto requestWdto) {
        BanPermanentCreateRequest mappedRequest = banChangeMapper.fromBanCreatePermanentRequestWdto(requestWdto);
        Ban ban = banService.createPermaBan(userId, mappedRequest);
        BanWdto responseWdto = banMapper.toBanWdto(ban);
        return ResponseEntity.ok(responseWdto);
    }

    @Override
    public ResponseEntity<BanWdto> updateBan(Long userId, Long banId, BanUpdateRequestWdto requestWdto) {
        BanUpdateRequest mappedRequest = banChangeMapper.fromBanUpdateRequestWdto(requestWdto);
        Ban ban = banService.updateBan(userId, banId, mappedRequest);
        BanWdto responseWdto = banMapper.toBanWdto(ban);
        return ResponseEntity.ok(responseWdto);
    }

    @Override
    public ResponseEntity<BanWdto> cancelBan(Long userId, Long banId, CancelBanRequestWdto requestWdto) {
        CancelBanRequest mappedRequest = banChangeMapper.fromCancelBanRequestWdto(requestWdto);
        Ban ban = banService.cancelBan(userId, banId, mappedRequest);
        BanWdto responseWdto = banMapper.toBanWdto(ban);
        return ResponseEntity.ok(responseWdto);
    }
}
