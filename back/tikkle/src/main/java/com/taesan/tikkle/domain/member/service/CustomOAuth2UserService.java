package com.taesan.tikkle.domain.member.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.taesan.tikkle.domain.account.entity.Account;
import com.taesan.tikkle.domain.account.repository.AccountRepository;
import com.taesan.tikkle.domain.member.entity.Member;
import com.taesan.tikkle.domain.member.repository.MemberRepository;
import com.taesan.tikkle.domain.organization.entity.Organization;
import com.taesan.tikkle.domain.organization.repository.OrganizationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);
	private final MemberRepository memberRepository;
	private final OrganizationRepository organizationRepository;
	private final AccountRepository accountRepository;
	private final OAuth2AuthorizedClientService authorizedClientService;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		// 기본 OAuth2UserService 사용하여 사용자 정보를 가져옴
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest);

		logger.debug("CustomOAuth2UserService 도달");

		// Mattermost로부터 가져온 사용자 정보
		String email = oAuth2User.getAttribute("email");
		String name = oAuth2User.getAttribute("username"); // OAuth 프로필의 이름
		String nickname = oAuth2User.getAttribute("nickname"); // 필요에 따라 가져올 수 있음

		// 사용자를 조회하거나 생성
		Member member = saveOrUpdateMember(email, name, nickname);

		String userId = oAuth2User.getAttribute("id");
		getMemberProfileImage(userId);

		Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
		attributes.put("memberId", member.getId());
		// OAuth2User 객체를 반환
		return new DefaultOAuth2User(
			Collections.singleton(new OAuth2UserAuthority(oAuth2User.getAttributes())),
			attributes,
			"memberId" // 기본 사용자 이름 속성
		);
	}

	private Member saveOrUpdateMember(String email, String name, String nickname) {
		// 이미 존재하는지 확인
		Optional<Member> existingMemberOpt = memberRepository.findByEmail(email);

		// 존재하는 경우 업데이트
		if (existingMemberOpt.isPresent()) {
			Member existingMember = existingMemberOpt.get();
			existingMember = updateExistingMember(existingMember, name, nickname);
			return memberRepository.save(existingMember);
		} else {
			// 존재하지 않으면 새로 생성
			Organization defaultOrganization = getDefaultOrganization();
			Member newMember = Member.builder()
				.organization(defaultOrganization)
				.name(name)
				.nickname(nickname)
				.email(email)
				.build();

			Member savedMember = memberRepository.save(newMember);

			// Account 생성
			Account account = Account.builder()
				.member(savedMember)  // Member를 Account에 설정
				.timeQnt(10)         // 초기 시간 양 설정
				.build();

			// Account 저장
			accountRepository.save(account);

			return savedMember;

		}
	}

	// 기존 Member를 업데이트하는 메서드
	private Member updateExistingMember(Member existingMember, String name, String nickname) {
		// 필요한 정보 업데이트
		existingMember.changeName(name);
		existingMember.changeNickname(nickname);
		return existingMember;
	}

	public void getMemberProfileImage(String userId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
			"mattermost",
			authentication.getName()
		);

		if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
			// Mattermost Access Token을 가져옴
			String accessToken = authorizedClient.getAccessToken().getTokenValue();

			// Access Token을 사용하여 Mattermost API 호출
			callMattermostApi(accessToken, userId);
		}
	}

	private void callMattermostApi(String accessToken, String userId) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(
			"https://j11a501.p.ssafy.io/mattermost/api/v4/users/" + userId + "/image",
			HttpMethod.GET,
			entity,
			String.class
		);

		// Mattermost API 응답 처리
		String responseBody = response.getBody();
		logger.debug("Mattermost User Profile: {}", responseBody);
	}

	/*
		NOTE: Lazy Evaluation을 위한 orElseGet으로 수정
	 */
	private Organization getDefaultOrganization() {
		return organizationRepository.findByName("DEFAULT").orElseGet(() -> organizationRepository.save(
			new Organization("FREE",
				"j11a501.p.ssafy.io",
				1,
				null,
				"DEFAULT")));
	}
}
