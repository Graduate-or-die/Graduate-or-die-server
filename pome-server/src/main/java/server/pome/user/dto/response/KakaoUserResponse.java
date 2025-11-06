package server.pome.user.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
            @JsonProperty("id") Long id,
            @JsonProperty("kakao_account") KakaoAccount kakaoAccount,
            @JsonProperty("properties") Properties properties
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
                @JsonProperty("nickname") String nickname,
                @JsonProperty("profile_image") String profileImage,
                @JsonProperty("thumbnail_image") String thumbnailImage
        ) {
            this.nickname = nickname;
            this.profileImage = profileImage;
            this.thumbnailImage = thumbnailImage;
        }
    }
}