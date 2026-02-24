package de.paulm.four_in_a_row.mapper;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import de.paulm.four_in_a_row.domain.security.UserSession;
import de.paulm.model.UserSessionWdto;

@Mapper(componentModel = "spring")
public interface UserSessionMapper {

    @Mapping(target = "ipAddress", expression = "java(toInetAddress(wdto.getIpAddress()))")
    @Mapping(target = "userId", ignore = true) // Felder, die im WDTO fehlen
    @Mapping(target = "refreshToken", ignore = true) // Sicherheitskritisch, nicht mappen
    @Mapping(target = "invalidated", ignore = true)
    @Mapping(target = "userAgent", source = "deviceName")
    UserSession fromWdto(UserSessionWdto wdto);

    @Mapping(target = "ipAddress", expression = "java(entity.getIpAddress() != null ? entity.getIpAddress().getHostAddress() : null)")
    @Mapping(target = "isCurrent", expression = "java(isCurrentSession(entity, currentRefreshToken))")
    @Mapping(target = "deviceName", source = "userAgent")
    UserSessionWdto toWdto(UserSession entity, @Context String currentRefreshToken);

    List<UserSession> fromWdtoList(List<UserSessionWdto> wdtos);

    List<UserSessionWdto> toWdtoLost(List<UserSession> entities, @Context String currentRefreshToken);

    default InetAddress toInetAddress(String ip) {
        try {
            return ip != null ? InetAddress.getByName(ip) : null;
        } catch (UnknownHostException e) {
            return null;
        }
    }

    default boolean isCurrentSession(UserSession entity, String currentRefreshToken) {
        if (entity == null || entity.getRefreshToken() == null || currentRefreshToken == null) {
            return false;
        }
        return entity.getRefreshToken().equals(currentRefreshToken);
    }
}
