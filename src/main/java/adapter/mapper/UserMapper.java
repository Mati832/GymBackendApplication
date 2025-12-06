package adapter.mapper;

import adapter.out.Entities.CoachEntity;
import adapter.out.Entities.MemberEntity;
import adapter.out.Entities.UserEntity;
import domain.model.Coach;
import domain.model.Member;

import java.util.stream.Collectors;


public class UserMapper {



    public static Member toDomain(MemberEntity memberEntity) {
        return new Member(memberEntity.getId(), memberEntity.getFirstName(), memberEntity.getLastName(), memberEntity.getEmail(),
                memberEntity.getPassword(), memberEntity.getGender(), memberEntity.getBornOn(), memberEntity.getCreatedAt(),
                memberEntity.getAssignments().stream().map((as)->as.getCoach().getId()).collect(Collectors.toList()));
    }

    public static Coach toDomain(CoachEntity coachEntity) {
        return new Coach(coachEntity.getId(), coachEntity.getFirstName(), coachEntity.getLastName(), coachEntity.getEmail(),
                coachEntity.getPassword(), coachEntity.getGender(), coachEntity.getBornOn(), coachEntity.getCreatedAt(),
                coachEntity.getAssignments().stream().map((as)->as.getMember().getId()).collect(Collectors.toList()));
    }

    public static MemberEntity toEntity(Member member) {
        return new MemberEntity(member.getId(), member.getFirstName(), member.getLastName(), member.getEmail(),
                member.getPassword(), member.getGender(), member.getBornOn(), member.getCreatedAt());
    }

    public static CoachEntity toEntity(Coach coach) {
        return new CoachEntity(coach.getId(), coach.getFirstName(), coach.getLastName(), coach.getEmail(),
                coach.getPassword(), coach.getGender(), coach.getBornOn(), coach.getCreatedAt());
    }
}
