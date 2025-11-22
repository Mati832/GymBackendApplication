package application.service.member;

import application.service.UserService;
import domain.model.Member;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MemberUserService extends UserService<Member> {
}
