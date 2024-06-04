package fete.be.domain.member.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    /**
     * 회원가입 API
     * @param Member member
     */
    public void save(Member member) {
        if (member.getMemberId() == null) {
            em.persist(member);
        } else {
            em.merge(member);
        }
    }

    public Optional<Member> findById(String email) {
        return findAll().stream()
                .filter(m -> m.getEmail().equals(email))
                .findAny();
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }

    public Boolean isExistEmail(String email) {
        try {
            Member member = em.createQuery("select m.email from Member m where m.email = :email", Member.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

}
