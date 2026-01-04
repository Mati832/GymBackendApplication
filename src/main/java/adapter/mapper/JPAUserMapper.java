package adapter.mapper;

import adapter.out.Entities.*;
import domain.model.Coach;
import domain.model.Member;
import domain.model.User;

import java.util.ArrayList;
import java.util.stream.Collectors;


public class JPAUserMapper {

    private static Member toDomain(MemberEntity memberEntity) {
        return new Member(memberEntity.getId(), memberEntity.getFirstName(), memberEntity.getLastName(), memberEntity.getEmail(),
                memberEntity.getPassword(), memberEntity.getGender(), memberEntity.getBornOn(), memberEntity.getCreatedAt(),
                new ArrayList<>(memberEntity.getExercises().stream().map(ExerciseEntity::getId).toList()),
                new ArrayList<>(memberEntity.getWorkouts().stream().map(WorkoutEntity::getId).toList()),
                new ArrayList<>(memberEntity.getAssignments().stream().map((as)->as.getCoach().getId()).toList()));
    }

    private static Coach toDomain(CoachEntity coachEntity) {
        return new Coach(coachEntity.getId(), coachEntity.getFirstName(), coachEntity.getLastName(), coachEntity.getEmail(),
                coachEntity.getPassword(), coachEntity.getGender(), coachEntity.getBornOn(), coachEntity.getCreatedAt(),
                new ArrayList<>(coachEntity.getExercises().stream().map(ExerciseEntity::getId).toList()),
                new ArrayList<>(coachEntity.getWorkouts().stream().map(WorkoutEntity::getId).toList()),
                new ArrayList<>(coachEntity.getAssignments().stream().map((as)->as.getMember().getId()).toList()));
    }

    public static MemberEntity toEntity(Member member) {
        return new MemberEntity(member.getId(), member.getFirstName(), member.getLastName(), member.getEmail(),
                member.getPassword(), member.getGender(), member.getBornOn(), member.getCreatedAt());
    }

    public static CoachEntity toEntity(Coach coach) {
        return new CoachEntity(coach.getId(), coach.getFirstName(), coach.getLastName(), coach.getEmail(),
                coach.getPassword(), coach.getGender(), coach.getBornOn(), coach.getCreatedAt());
    }

    public static User toDomain(UserEntity user) {
        if(user instanceof MemberEntity member) return JPAUserMapper.toDomain(member);
        return JPAUserMapper.toDomain((CoachEntity) user);
    }
}