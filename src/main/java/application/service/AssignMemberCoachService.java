package application.service;

import application.commands.AssignCoachMemberRelationCommand;
import application.port.in.AssignCoachMemberRelationUseCase;
import application.port.out.UserPorts.FindCoachMemberRelationPort;
import application.port.out.UserPorts.FindUserByEmailPort;
import application.port.out.UserPorts.SaveCoachMemberRelationPort;
import domain.Results.AssignCoachMemberRelationResult;
import domain.model.Coach;
import domain.model.CoachMember;
import domain.model.Member;
import domain.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AssignMemberCoachService implements AssignCoachMemberRelationUseCase {
    @Inject
    FindUserByEmailPort findUserByEmailPort;
    @Inject
    FindCoachMemberRelationPort findCoachMemberRelationPort;
    @Inject
    SaveCoachMemberRelationPort saveCoachMemberRelationPort;

    @Override
    public AssignCoachMemberRelationResult assign(AssignCoachMemberRelationCommand command) {
        if (command.requestedBy()==null){
            return new AssignCoachMemberRelationResult.Failure(AssignCoachMemberRelationResult.AssignRelationFailureReason.UNAUTHORIZED);
        }

        User coach = findUserByEmailPort.findByEmail(command.coachEmail());
        if (coach == null || !(coach instanceof Coach)) {
            return new AssignCoachMemberRelationResult.Failure(AssignCoachMemberRelationResult.AssignRelationFailureReason.COACH_NOT_FOUND);
        }
        User member = findUserByEmailPort.findByEmail(command.memberEmail());
        if (member == null || !(member instanceof Member)) {
            return new AssignCoachMemberRelationResult.Failure(AssignCoachMemberRelationResult.AssignRelationFailureReason.MEMBER_NOT_FOUND);
        }
        if (command.requestedBy()!= coach.getId()&&command.requestedBy()!= member.getId()) {
            return new AssignCoachMemberRelationResult.Failure(AssignCoachMemberRelationResult.AssignRelationFailureReason.FORBIDDEN);
        }
        if (findCoachMemberRelationPort.findRelationByCoachAndMember(coach.getId(), member.getId()) != null) {
            return new AssignCoachMemberRelationResult.Failure(AssignCoachMemberRelationResult.AssignRelationFailureReason.RELATION_ALREADY_EXISTS);
        }
        CoachMember saved = saveCoachMemberRelationPort.save(new CoachMember(coach.getId(), member.getId()));
        return new AssignCoachMemberRelationResult.Success(saved);
    }
}
