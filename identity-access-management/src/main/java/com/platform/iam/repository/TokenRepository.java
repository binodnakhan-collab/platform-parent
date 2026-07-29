package com.platform.iam.repository;

import com.platform.iam.entity.AuthorizationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<AuthorizationToken, String> {

    Optional<AuthorizationToken> findByAccessTokenValue(String accessToken);

    Optional<AuthorizationToken> findByRefreshTokenValue(String refreshToken);

    @Query("select a from AuthorizationToken a where a.accessTokenValue = :token or a.refreshTokenValue = :token")
    Optional<AuthorizationToken> findByStateOrAccessTokenValueOrRefreshTokenValue(
            @Param("token") String token);

}