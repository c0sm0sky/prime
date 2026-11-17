package pl.demo.prime;

import jakarta.faces.context.FacesContext;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;
import org.springframework.web.context.annotation.SessionScope;

@Component("loginView")
@SessionScope
@Profile("wildfly | docker | springboot")
public class LoginView {
    @Getter @Setter
    private String username;
    @Getter @Setter
    private String password;
    @Getter
    private String error;

    public boolean isLoggedIn() {
        return Boolean.TRUE.equals(FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("loggedIn"));
    }

    public String login() {
        if ("prime".equals(username) && "prime123".equals(password)) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("loggedIn", true);
            error = null;
            password = null;
            return null;
        }
        error = "Nieprawidłowy login lub hasło.";
        return null;
    }
    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("loggedIn");
        username = null;
        password = null;
        error = null;
        return null;
    }

}
