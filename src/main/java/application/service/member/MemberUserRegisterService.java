package application.service.member;

import application.service.UserRegisterService;
import domain.model.Member;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MemberUserRegisterService extends UserRegisterService<Member> {
}
