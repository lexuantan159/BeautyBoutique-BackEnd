package com.example.beautyboutique.Repositories;

import com.example.beautyboutique.Models.VoucherDetail;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherDetailRepository extends JpaRepository<VoucherDetail, Integer> {
        List<VoucherDetail> findByUserId(Integer userId);
        Optional<VoucherDetail> findByUserIdAndVoucherId(Integer userId , Integer voucherId);
}
