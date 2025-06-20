package com.research.repository;

import com.research.model.DetectionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetectionRecordRepository extends JpaRepository<DetectionRecord, Long> {


    DetectionRecord findTopByOrderByDetectTimeDesc();
    // ✅ 获取所有记录，按时间倒序（用于历史页）
    List<DetectionRecord> findAllByOrderByDetectTimeDesc();


}
