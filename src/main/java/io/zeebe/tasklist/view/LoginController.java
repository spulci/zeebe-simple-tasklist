package io.zeebe.tasklist.view;

import java.util.Map;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Controller
public class LoginController {

  @GetMapping("/login")
  public String login(Map<String, Object> model) {

    model.put("_csrf", getCsrfToken());
    return "login";
  }

  @GetMapping("/login-error")
  public String loginError(Map<String, Object> model) {

    model.put("error", "Username or password is invalid.");
    model.put("_csrf", getCsrfToken());

    return "login";
  }

  private CsrfToken getCsrfToken(){
    ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    CsrfToken csrf = (CsrfToken) attr.getRequest().getAttribute(CsrfToken.class.getName());

    return csrf;
  }
}
