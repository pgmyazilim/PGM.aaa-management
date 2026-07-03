package com.company.aaamanagement.infrastructure;

import com.company.aaamanagement.domain.ClientRedirectUri;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRedirectUriRepository extends JpaRepository<ClientRedirectUri, Integer> {

    List<ClientRedirectUri> findByClient_ClientIdOrderByRedirectUriAsc(Integer clientId);

    boolean existsByClient_ClientIdAndRedirectUri(Integer clientId, String redirectUri);
}
