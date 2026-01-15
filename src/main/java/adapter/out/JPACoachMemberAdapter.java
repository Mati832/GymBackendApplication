package adapter.out;

import adapter.out.Entities.CoachEntity;
import adapter.out.Entities.CoachMemberEntity;
import adapter.out.Entities.MemberEntity;
import application.port.out.UserPorts.FindCoachMemberRelationPort;
import application.port.out.UserPorts.SaveCoachMemberRelationPort;
import domain.model.CoachMember;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class JPACoachMemberAdapter implements FindCoachMemberRelationPort, SaveCoachMemberRelationPort {

    @Inject
    EntityManager em;


    @Override
    public CoachMember findRelationByCoachAndMember(Long coachId, Long memberId) {
        CoachMemberEntity coachMemberEntity = em.createQuery("select u from CoachMemberEntity u where u.coach.id=:coachId and u.member.id=:memberId", CoachMemberEntity.class)
                .setParameter("coachId", coachId)
                .setParameter("memberId", memberId)
                .getSingleResultOrNull();
        if (coachMemberEntity == null) {
            return null;
        }
        return new CoachMember(coachMemberEntity.getId(), coachMemberEntity.getCoach().getId(), coachMemberEntity.getMember().getId(), coachMemberEntity.getAssignedAt(), coachMemberEntity.getEtag());
    }

    @Transactional
    @Override
    public CoachMember save(CoachMember coachMember) {
        CoachEntity coachEntity = em.find(CoachEntity.class, coachMember.getCoachId());
        MemberEntity memberEntity = em.find(MemberEntity.class, coachMember.getMemberId());
        CoachMemberEntity coachMemberEntity = new CoachMemberEntity(coachEntity, memberEntity);
        em.persist(coachMemberEntity);
        coachEntity.getAssignments().add(coachMemberEntity);
        memberEntity.getAssignments().add(coachMemberEntity);
        em.merge(memberEntity);
        em.merge(coachEntity);
        return new CoachMember(coachMemberEntity.getId(), coachMemberEntity.getCoach().getId(), coachMemberEntity.getMember().getId(), coachMemberEntity.getAssignedAt(), coachMemberEntity.getEtag());
    }
}
