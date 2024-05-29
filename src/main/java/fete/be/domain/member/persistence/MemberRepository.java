package fete.be.domain.member.persistence;

import jakarta.persistence.EntityManager;
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
     * @param member
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
}
