package com.hotel.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.dto.ProductReviewDTO;
import com.hotel.model.dto.ProductReviewRequest;
import com.hotel.model.entity.Account;
import com.hotel.model.entity.Member;
import com.hotel.model.entity.ProductReview;
import com.hotel.model.entity.Profile;
import com.hotel.repository.AccountRepository;
import com.hotel.repository.MemberRepository;
import com.hotel.repository.ProductRepository;
import com.hotel.repository.ProductReviewRepository;
import com.hotel.repository.ProfileRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final ProductRepository productRepository;
    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;

    public ProductReviewService(
            ProductReviewRepository productReviewRepository,
            ProductRepository productRepository,
            AccountRepository accountRepository,
            MemberRepository memberRepository,
            ProfileRepository profileRepository) {
        this.productReviewRepository = productReviewRepository;
        this.productRepository = productRepository;
        this.accountRepository = accountRepository;
        this.memberRepository = memberRepository;
        this.profileRepository = profileRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductReviewDTO> findByProduct(Integer productId, String username) {
        ensureProductExists(productId);
        Member currentMember = findMember(username);
        Integer currentMemberId = currentMember == null ? null : currentMember.getMemberId();
        List<ProductReview> reviews = productReviewRepository
                .findByProductIdOrderByUpdatedAtDesc(productId);

        Map<Integer, Member> members = memberRepository
                .findAllById(reviews.stream().map(ProductReview::getMemberId).toList())
                .stream()
                .collect(Collectors.toMap(Member::getMemberId, Function.identity()));
        Map<Integer, Account> accounts = accountRepository
                .findAllById(members.values().stream().map(Member::getAccountId).toList())
                .stream()
                .collect(Collectors.toMap(Account::getAccountId, Function.identity()));
        Map<Integer, Profile> profiles = profileRepository
                .findByAccountIdIn(members.values().stream().map(Member::getAccountId).toList())
                .stream()
                .collect(Collectors.toMap(Profile::getAccountId, Function.identity(), (first, second) -> first));

        return reviews.stream()
                .map(review -> toDTO(
                        review,
                        resolveDisplayName(review.getMemberId(), members, accounts, profiles),
                        currentMemberId != null && review.getMemberId().equals(currentMemberId)))
                .toList();
    }

    @Transactional
    public ProductReviewDTO save(Integer productId, String username, ProductReviewRequest request) {
        ensureProductExists(productId);
        validate(request);

        Member member = resolveMember(username);
        ProductReview review = productReviewRepository
                .findByProductIdAndMemberId(productId, member.getMemberId())
                .orElseGet(ProductReview::new);
        review.setProductId(productId);
        review.setMemberId(member.getMemberId());
        review.setRating(request.getRating());
        review.setComment(request.getComment().trim());

        ProductReview saved = productReviewRepository.save(review);
        String memberName = profileRepository.findByAccountId(member.getAccountId())
                .map(Profile::getName)
                .filter(name -> !name.isBlank())
                .orElse(username);
        return toDTO(saved, memberName, true);
    }

    @Transactional
    public boolean delete(
            Integer productId,
            Integer reviewId,
            String username,
            boolean canManageReviews) {
        ensureProductExists(productId);
        ProductReview review = productReviewRepository
                .findByReviewIdAndProductId(reviewId, productId)
                .orElseThrow(() -> new EntityNotFoundException("找不到這則商品評論"));

        Member currentMember = findMember(username);
        boolean ownsReview = currentMember != null
                && currentMember.getMemberId().equals(review.getMemberId());
        if (!canManageReviews && !ownsReview) {
            return false;
        }

        productReviewRepository.delete(review);
        return true;
    }

    private void validate(ProductReviewRequest request) {
        if (request == null || request.getRating() == null
                || request.getRating() < 1 || request.getRating() > 5) {
            throw new IllegalArgumentException("星等必須介於 1 到 5 星");
        }
        if (request.getComment() == null || request.getComment().trim().isEmpty()) {
            throw new IllegalArgumentException("請輸入評論內容");
        }
        if (request.getComment().trim().length() > 1000) {
            throw new IllegalArgumentException("評論內容最多 1000 字");
        }
    }

    private void ensureProductExists(Integer productId) {
        if (productId == null || !productRepository.existsById(productId)) {
            throw new EntityNotFoundException("找不到這項商品");
        }
    }

    private Member resolveMember(String username) {
        Member member = findMember(username);
        if (member == null) {
            throw new IllegalArgumentException("只有會員可以發表商品評價");
        }
        return member;
    }

    private Member findMember(String username) {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            return null;
        }
        return memberRepository.findByAccountId(account.getAccountId())
                .orElse(null);
    }

    private String resolveDisplayName(
            Integer memberId,
            Map<Integer, Member> members,
            Map<Integer, Account> accounts,
            Map<Integer, Profile> profiles) {
        Member member = members.get(memberId);
        if (member == null) return "星澄會員";

        Profile profile = profiles.get(member.getAccountId());
        if (profile != null && profile.getName() != null && !profile.getName().isBlank()) {
            return profile.getName();
        }

        Account account = accounts.get(member.getAccountId());
        return account != null && account.getUsername() != null
                ? account.getUsername()
                : "星澄會員";
    }

    private ProductReviewDTO toDTO(ProductReview review, String memberName, boolean ownReview) {
        return new ProductReviewDTO(
                review.getReviewId(),
                review.getRating(),
                review.getComment(),
                memberName,
                review.getCreatedAt(),
                review.getUpdatedAt(),
                ownReview);
    }
}
