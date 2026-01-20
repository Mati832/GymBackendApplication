package adapter.in.Links;

import adapter.in.DTOs.ResponseDTOs.LoginUserResponse;
import adapter.in.controller.CoachWebController;
import adapter.in.controller.MemberWebController;
import application.commands.AuthenticatedUser;
import domain.model.Coach;
import domain.model.Member;
import domain.model.User;
import domain.valueobject.UserRole;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriInfo;

import static adapter.in.Links.LinkFactory.coachRegisterLink;
import static adapter.in.Links.LinkFactory.dispatcherLink;
import static adapter.in.Links.LinkFactory.loginLink;
import static adapter.in.Links.LinkFactory.memberRegisterLink;
import static adapter.in.Links.LinkFactory.self;

import java.net.URI;

@ApplicationScoped
public class LoginUserLinks {

    public static Link[] getLinks(UriInfo uriInfo, User user) {
        return switch (user) {
            case Member m -> LoginUserLinks.getCoachLinks(uriInfo, m.getId());
            case Coach c -> LoginUserLinks.getMemberLinks(uriInfo, c.getId());
            default -> throw new IllegalStateException("Unexpected value: " + user);
        };
    }


    private static Link[] getCoachLinks(UriInfo uriInfo, Long coachId) {
        return new Link[]{
                LinkFactory.self(coachSelfUri(uriInfo, coachId)),
                LinkFactory.dispatcherLink(uriInfo)
        };
    }

    private static Link[] getMemberLinks(UriInfo uriInfo, Long memberId) {
        return new Link[]{
                LinkFactory.self(memberSelfUri(uriInfo, memberId)),
                LinkFactory.dispatcherLink(uriInfo)
        };
    }

    private static URI memberSelfUri(UriInfo uriInfo, Long memberId) {
        return uriInfo
                .getBaseUriBuilder()
                .path(MemberWebController.class)
                .path("/" + memberId)
                .build();
        //eigtl.build(memberid);
    }

    public static URI coachSelfUri(UriInfo uriInfo, Long coachId) {
        return uriInfo
                .getBaseUriBuilder()
                .path(CoachWebController.class)
                .path("/" + coachId)
                .build();
        //eigtl.build(coachId);
    }

    public static Link[] getUserNotFoundLinks(UriInfo uriInfo) {
        
        return new Link[]{
            dispatcherLink(uriInfo),
            loginLink(uriInfo),
            coachRegisterLink(uriInfo),
            memberRegisterLink(uriInfo)
        };
    }

    public static Link[] getWrongPasswordLinks(UriInfo uriInfo) {
        return new Link[]{
            dispatcherLink(uriInfo),
            loginLink(uriInfo)
        };
    }
}
