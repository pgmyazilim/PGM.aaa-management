package com.company.aaamanagement.infrastructure;

import com.company.aaamanagement.crypto.CryptoService;
import com.company.aaamanagement.domain.Client;
import com.company.aaamanagement.domain.DatabaseCredential;
import com.company.aaamanagement.domain.DatabaseServer;
import com.company.aaamanagement.domain.ExternalUrl;
import com.company.aaamanagement.domain.ModuleDatabase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InfrastructureServiceTest {

    @Mock ProjectRepository projectRepository;
    @Mock ModuleRepository moduleRepository;
    @Mock ExternalUrlRepository externalUrlRepository;
    @Mock ClientRepository clientRepository;
    @Mock DatabaseServerRepository serverRepository;
    @Mock DatabaseCredentialRepository credentialRepository;
    @Mock ModuleDatabaseRepository moduleDatabaseRepository;
    @Mock CryptoService cryptoService;
    @InjectMocks InfrastructureService infrastructureService;

    @Test
    void saveDatabase_whenNameBlank_throwsIllegalArgument() {
        assertThatThrownBy(() -> infrastructureService.saveDatabase(null, null, null, "  ", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Veritabanı adı");

        verify(moduleDatabaseRepository, never()).save(any());
    }

    @Test
    void saveDatabase_whenCredentialBelongsToOtherServer_throwsIllegalArgument() {
        DatabaseServer selected = DatabaseServer.builder().databaseServerId(1).build();
        DatabaseServer other = DatabaseServer.builder().databaseServerId(2).build();
        when(serverRepository.findById(1)).thenReturn(Optional.of(selected));
        when(credentialRepository.findById(9)).thenReturn(Optional.of(
                DatabaseCredential.builder().databaseCredentialId(9).databaseServer(other).build()));

        assertThatThrownBy(() -> infrastructureService.saveDatabase(null, 1, 9, "AaaDb", null))
                .isInstanceOf(IllegalArgumentException.class);

        verify(moduleDatabaseRepository, never()).save(any());
    }

    @Test
    void saveDatabase_whenOnlyCredentialGiven_inferServerFromCredential() {
        DatabaseServer server = DatabaseServer.builder().databaseServerId(3).build();
        when(credentialRepository.findById(9)).thenReturn(Optional.of(
                DatabaseCredential.builder().databaseCredentialId(9).databaseServer(server).build()));
        when(moduleDatabaseRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ModuleDatabase saved = infrastructureService.saveDatabase(null, null, 9, " AaaDb ", "  ");

        assertThat(saved.getDatabaseServer()).isSameAs(server);
        assertThat(saved.getDatabaseName()).isEqualTo("AaaDb");
        assertThat(saved.getDatabaseAlias()).isNull();
        assertThat(saved.getModifiedAtUtc()).isNotNull();
    }

    @Test
    void saveExternalUrl_whenUrlBlank_throwsIllegalArgument() {
        ExternalUrl e = ExternalUrl.builder().name("Portal").url("  ").build();

        assertThatThrownBy(() -> infrastructureService.saveExternalUrl(e))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("URL");

        verify(externalUrlRepository, never()).save(any());
    }

    @Test
    void saveExternalUrl_trimsFieldsAndStampsModifiedAt() {
        when(externalUrlRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        ExternalUrl e = ExternalUrl.builder().name(" Portal ").url(" https://x.example ").build();

        ExternalUrl saved = infrastructureService.saveExternalUrl(e);

        assertThat(saved.getName()).isEqualTo("Portal");
        assertThat(saved.getUrl()).isEqualTo("https://x.example");
        assertThat(saved.getModifiedAtUtc()).isNotNull();
    }

    @Test
    void saveClient_whenNameBlank_throwsIllegalArgument() {
        Client c = Client.builder().name("  ").build();

        assertThatThrownBy(() -> infrastructureService.saveClient(c))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ad");

        verify(clientRepository, never()).save(any());
    }

    @Test
    void saveClient_new_trimsNameAndStampsModifiedAt() {
        when(clientRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        Client c = Client.builder().name(" Gocsis Arayüzü ").description("d").build();

        Client saved = infrastructureService.saveClient(c);

        assertThat(saved.getName()).isEqualTo("Gocsis Arayüzü");
        assertThat(saved.getModifiedAtUtc()).isNotNull();
        verify(clientRepository, never()).findById(any());
    }

    @Test
    void saveClient_existing_updatesOnlyNameAndDescription() {
        UUID key = UUID.randomUUID();
        LocalDateTime created = LocalDateTime.of(2026, 1, 1, 0, 0);
        Client existing = Client.builder().clientId(5).clientKey(key).createdAtUtc(created)
                .name("Eski").description("eski").build();
        when(clientRepository.findById(5)).thenReturn(Optional.of(existing));
        when(clientRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        Client form = Client.builder().clientId(5).name(" Yeni ").description("yeni").build();

        Client saved = infrastructureService.saveClient(form);

        assertThat(saved).isSameAs(existing);
        assertThat(saved.getName()).isEqualTo("Yeni");
        assertThat(saved.getDescription()).isEqualTo("yeni");
        assertThat(saved.getClientKey()).isEqualTo(key);
        assertThat(saved.getCreatedAtUtc()).isEqualTo(created);
        assertThat(saved.getModifiedAtUtc()).isNotNull();
    }
}
