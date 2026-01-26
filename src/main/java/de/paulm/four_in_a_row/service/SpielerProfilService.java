package de.paulm.four_in_a_row.service;

import org.springframework.stereotype.Service;

import de.paulm.four_in_a_row.domain.exceptions.SpielerProfilNotFoundException;
import de.paulm.four_in_a_row.profil.SpielerProfil;
import de.paulm.four_in_a_row.profil.SpielerStatistik;
import de.paulm.four_in_a_row.repository.SpielerProfilRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpielerProfilService {

    private final SpielerProfilRepository repository;

    public SpielerProfil getSpielerProfilById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new SpielerProfilNotFoundException(id));
    }

    @Transactional
    public void changeBenutzername(Long spielerId, String neuerName) {
        SpielerProfil profil = this.getSpielerProfilById(spielerId);
        profil.benutzernameAendern(neuerName);
    }

    @Transactional
    public void changeEmail(Long spielerId, String neueEmail) {
        SpielerProfil profil = this.getSpielerProfilById(spielerId);
        profil.emailAendern(neueEmail);
    }

    public SpielerStatistik getSpielerStatistik(Long spielerId) {
        SpielerProfil profil = this.getSpielerProfilById(spielerId);
        return profil.getStatistik();
    }
}
