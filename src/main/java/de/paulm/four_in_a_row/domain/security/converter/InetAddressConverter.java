package de.paulm.four_in_a_row.domain.security.converter;

import java.net.InetAddress;
import java.net.UnknownHostException;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class InetAddressConverter implements AttributeConverter<InetAddress, String> {

    @Override
    public String convertToDatabaseColumn(InetAddress inetAddress) {
        return inetAddress == null ? null : inetAddress.getHostAddress();
    }

    @Override
    public InetAddress convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty())
            return null;
        try {
            return InetAddress.getByName(dbData);
        } catch (UnknownHostException e) {
            throw new RuntimeException("Ungültige IP-Addresse in Datenbank", e); // spezielle Exception?
        }
    }
}
