package com.hongyun.accounting.repository;

import com.hongyun.accounting.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 验证码仓库。
 * 查询用户最新验证码记录用于校验。
 */
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findTopByUserIdAndTypeAndTargetOrderByExpiresAtDesc(Long userId, VerificationCode.CodeType type, String target);
}
