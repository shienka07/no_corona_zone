package com.megait.nocoronazone.service
        ;
import com.megait.nocoronazone.domain.AuthType;
import com.megait.nocoronazone.domain.Member;
import com.megait.nocoronazone.domain.MemberType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
public class OAuthAttributes {
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String name;
    private final String email;

    private final AuthType authType;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes,
                           String nameAttributeKey, String name,
                           String email, String picture, AuthType authType) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.authType = authType;
    }

    public static OAuthAttributes of(String registrationId,
                                     String userNameAttributeName,
                                     Map<String, Object> attributes) {
        if ("facebook".equals(registrationId)) {
            return ofFaceBook("id", attributes);
        }

        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName,
                                            Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .authType(AuthType.GOOGLE)
                .build();
    }

    private static OAuthAttributes ofFaceBook(String userNameAttributeName,
                                           Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .picture((String) response.get("profile_image"))
                .authType(AuthType.FACEBOOK)
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public Member toEntity() {
        return Member.builder()
                .name(name)
                .email(email)
                .memberType(MemberType.ROLE_USER)
                .certification(false)
                .emailCheckToken("{noop}")
                .introduce("{noop}")
                .nickname("{noop}")
                .authType(authType)
                .emailVerified(true)
                .password("{noop}")
                .build();
    }
}