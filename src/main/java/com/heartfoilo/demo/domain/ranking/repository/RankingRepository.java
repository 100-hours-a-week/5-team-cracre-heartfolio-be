package com.heartfoilo.demo.domain.ranking.repository;

import com.heartfoilo.demo.domain.ranking.entity.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RankingRepository extends JpaRepository<Ranking, Long> {
    List<Ranking> findTop10ByOrderByMonthlyReturnDesc();
    List<Ranking> findTop10ByOrderBySumReturnDesc();
    List<Ranking> findTop10ByOrderByDonationDescIdAsc();

    @Query(value = "SELECT ranked.`rank` FROM (SELECT user_id, RANK() OVER (ORDER BY sum_return DESC) AS `rank` FROM ranking) ranked WHERE ranked.user_id = :userId", nativeQuery = true)
    Optional<Integer> findUserRankBySumReturn(Long userId);

    // monthlyReturn에 대한 특정 사용자의 랭크 반환
    @Query(value = "SELECT ranked.`rank` FROM (SELECT user_id, RANK() OVER (ORDER BY monthly_return DESC) AS `rank` FROM ranking) AS ranked WHERE ranked.user_id = :userId", nativeQuery = true)
    Optional<Integer> findUserRankByMonthlyReturn(Long userId);

    // donation에 대한 특정 사용자의 랭크 반환
    @Query(value = "SELECT ranked.`rank` FROM (SELECT user_id, RANK() OVER (ORDER BY donation DESC, id ASC) AS `rank` FROM ranking) ranked WHERE ranked.user_id = :userId", nativeQuery = true)
    Optional<Integer> findUserRankByDonation(Long userId);

    @Query("SELECT r FROM Ranking r WHERE r.user.id = :userId")
    Optional<Ranking> findByUserId(Long userId);

    @Modifying
    @Query("UPDATE Ranking r SET r.monthlyReturn = 0")
    void resetAllMonthlyReturns();
}
