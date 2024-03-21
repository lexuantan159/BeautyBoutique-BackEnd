package com.example.beautyboutique.Repositories;

import com.example.beautyboutique.Models.Feedback;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback,Integer> {

    List<Feedback> findFeedbacksByProduct_Id(Integer productId, Sort sortByCreateDate);
}
