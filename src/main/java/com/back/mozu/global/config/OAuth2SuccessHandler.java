package com.back.mozu.global.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    @Value("${frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String userId = oAuth2User.getAttribute("userId");
        String role = oAuth2User.getAttribute("role");
        Boolean isNewUser = oAuth2User.getAttribute("isNewUser");

        String token = jwtProvider.createToken(userId, role);

        String redirectUrl = frontendUrl + "/auth/callback"
                + "?token=" + token
                + "&isNewUser=" + isNewUser;

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
