package adapter.in.Controller;

import adapter.in.DTOs.RequestDTOs.member.AssignCoachDTO;
import adapter.in.Presenter.Member.HttpAssignCoachPresenter;
import adapter.in.mapper.MemberMapper;
import application.port.in.AssignCoachMemberRelationUseCase;
import domain.Results.AssignCoachMemberRelationResult;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/users/members")
public class MemberWebController {
    @Inject
    HttpAssignCoachPresenter httpAssignCoachPresenter;
    @Inject
    AssignCoachMemberRelationUseCase assignCoach;

    @Context
    UriInfo uriInfo;

    @POST
    @Path("/assign")
    public Response assignCoach(AssignCoachDTO assignCoachDTO) {
        AssignCoachMemberRelationResult result = assignCoach.assign(MemberMapper.toDomain(assignCoachDTO));
        return httpAssignCoachPresenter.toHttp(result, uriInfo);

    }
}
