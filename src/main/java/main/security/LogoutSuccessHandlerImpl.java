package main.security;

import com.fasterxml.jackson.databind.json.JsonMapper;
import main.api.response.auth.LogoutResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogoutSuccessHandlerImpl extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        LogoutResponse logoutResponse = new LogoutResponse();
        logoutResponse.setResult(true);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JsonMapper jsonMapper = new JsonMapper();
        String jsonData = jsonMapper.writeValueAsString(logoutResponse);
        response.getWriter().write(jsonData);
        response.getWriter().flush();
        super.onLogoutSuccess(request, response, authentication);
    }
}