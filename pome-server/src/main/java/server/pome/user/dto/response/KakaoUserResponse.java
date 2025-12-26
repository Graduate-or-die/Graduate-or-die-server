package server.pome.user.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
public class KakaoUserResponse {

    private final Long id;
    private final KakaoAccount kakaoAccount;
    private final Properties properties;

    @JsonCreator
    public KakaoUserResponse(
            @JsonProperty("id")
            @Schema(description = "카카오 회원 고유 ID")
            Long id,

            @JsonProperty("kakao_account")
            @Schema(description = "카카오 계정 정보")
            KakaoAccount kakaoAccount,

            @JsonProperty("properties")
            @Schema(description = "카카오 기본 프로필 정보 ")
            Properties properties
    ) {
        this.id = id;
        this.kakaoAccount = kakaoAccount;
        this.properties = properties;
    }

    @Getter
    @ToString
    public static class KakaoAccount {
        private final String email;

        @JsonCreator
        public KakaoAccount(@JsonProperty("email") String email) {
            this.email = email;
        }
    }

    @Getter
    @ToString
    public static class Properties {
        private final String nickname;
        private final String profileImage;
        private final String thumbnailImage;

        @JsonCreator
        public Properties(
                @JsonProperty("nickname")
                @Schema(description = "카카오 닉네임D")
                String nickname,

                @JsonProperty("profile_image")
                @Schema(description = "카카오 프로필 사진 (원본)")
                String profileImage,

                @JsonProperty("thumbnail_image")
                @Schema(description = "카카오 프로필 사진 (썸네일)")
                String thumbnailImage
        ) {
            this.nickname = nickname;
            this.profileImage = profileImage;
            this.thumbnailImage = thumbnailImage;
        }
    }
}