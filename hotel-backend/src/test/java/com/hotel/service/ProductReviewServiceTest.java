package com.hotel.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hotel.model.entity.Account;
import com.hotel.model.entity.Member;
import com.hotel.model.entity.ProductReview;
import com.hotel.repository.AccountRepository;
import com.hotel.repository.MemberRepository;
import com.hotel.repository.ProductRepository;
import com.hotel.repository.ProductReviewRepository;
import com.hotel.repository.ProfileRepository;

@ExtendWith(MockitoExtension.class)
class ProductReviewServiceTest {

    @Mock
    private ProductReviewRepository productReviewRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ProfileRepository profileRepository;

    private ProductReviewService service;
    private ProductReview review;

    @BeforeEach
    void setUp() {
        service = new ProductReviewService(
                productReviewRepository,
                productRepository,
                accountRepository,
                memberRepository,
                profileRepository);

        review = new ProductReview();
        review.setReviewId(10);
        review.setProductId(20);
        review.setMemberId(30);

        when(productRepository.existsById(20)).thenReturn(true);
        when(productReviewRepository.findByReviewIdAndProductId(10, 20))
                .thenReturn(Optional.of(review));
    }

    @Test
    void ownerCanDeleteOwnReview() {
        Account account = new Account();
        account.setAccountId(40);
        Member member = new Member();
        member.setMemberId(30);
        member.setAccountId(40);
        when(accountRepository.findByUsername("member01")).thenReturn(account);
        when(memberRepository.findByAccountId(40)).thenReturn(Optional.of(member));

        assertTrue(service.delete(20, 10, "member01", false));
        verify(productReviewRepository).delete(review);
    }

    @Test
    void anotherMemberCannotDeleteReview() {
        Account account = new Account();
        account.setAccountId(41);
        Member member = new Member();
        member.setMemberId(31);
        member.setAccountId(41);
        when(accountRepository.findByUsername("member02")).thenReturn(account);
        when(memberRepository.findByAccountId(41)).thenReturn(Optional.of(member));

        assertFalse(service.delete(20, 10, "member02", false));
        verify(productReviewRepository, never()).delete(review);
    }

    @Test
    void managerCanDeleteAnyReview() {
        assertTrue(service.delete(20, 10, "employee01", true));
        verify(productReviewRepository).delete(review);
    }
}
