package fete.be.domain.member.persistence;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ProfileImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_image_id")
    private Long profileImageId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;


    // 생성 메서드
    public static ProfileImage createProfileImage(Member member, String imageUrl) {
        ProfileImage profileImage = new ProfileImage();
        profileImage.member = member;
        profileImage.imageUrl = imageUrl;

        return profileImage;
    }

    // 업데이트 메서드
    public static ProfileImage updateProfileImage(ProfileImage profileImage, String imageUrl) {
        profileImage.imageUrl = imageUrl;
        return profileImage;
    }
}
