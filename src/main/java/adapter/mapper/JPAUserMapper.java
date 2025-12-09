package adapter.mapper;

import adapter.out.Entities.CoachEntity;
import adapter.out.Entities.CoachMemberEntity;
import adapter.out.Entities.MemberEntity;
import adapter.out.Entities.UserEntity;
import domain.model.Coach;
import domain.model.Member;
import domain.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.stream.Collectors;


public class JPAUserMapper {

    @PersistenceContext
    private static EntityManager em;

    private static Member toDomain(MemberEntity memberEntity) {
        return new Member(memberEntity.getId(), memberEntity.getFirstName(), memberEntity.getLastName(), memberEntity.getEmail(),
                memberEntity.getPassword(), memberEntity.getGender(), memberEntity.getBornOn(), memberEntity.getCreatedAt(),
                memberEntity.getAssignments().stream().map((as)->as.getCoach().getId()).toList());
    }

    private static Coach toDomain(CoachEntity coachEntity) {
        return new Coach(coachEntity.getId(), coachEntity.getFirstName(), coachEntity.getLastName(), coachEntity.getEmail(),
                coachEntity.getPassword(), coachEntity.getGender(), coachEntity.getBornOn(), coachEntity.getCreatedAt(),
                coachEntity.getAssignments().stream().map((as)->as.getMember().getId()).toList());
    }

    private static MemberEntity toEntity(Member member) {
        return new MemberEntity(member.getId(), member.getFirstName(), member.getLastName(), member.getEmail(),
                member.getPassword(), member.getGender(), member.getBornOn(), member.getCreatedAt());
    }

    private static CoachEntity toEntity(Coach coach) {
        return new CoachEntity(coach.getId(), coach.getFirstName(), coach.getLastName(), coach.getEmail(),
                coach.getPassword(), coach.getGender(), coach.getBornOn(), coach.getCreatedAt());
    }

    public static UserEntity toEntity(User user) {
        if(user instanceof Member member){
            MemberEntity memberEntity = toEntity(member);
            memberEntity.setAssignments(member.getCoaches().stream().map(id -> new CoachMemberEntity(em.find(CoachEntity.class, id),
                    memberEntity)).toList());
            return memberEntity;
        }
        Coach coach = (Coach)user;
        CoachEntity coachEntity = toEntity(coach);
        coachEntity.setAssignments(coach.getClients().stream().map(id -> new CoachMemberEntity(coachEntity,
                em.find(MemberEntity.class, id))).toList());
        return coachEntity;
    }

    public static User toDomain(UserEntity entity) {
        if(entity instanceof MemberEntity memberEntity){
            return toDomain(memberEntity);
        }
        return toDomain((CoachEntity)entity);
    }
}
