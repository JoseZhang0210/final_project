package com.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.RoomTask;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
public interface RoomTaskRepository extends JpaRepository<RoomTask, Integer> {

    List<RoomTask> findByPriority(String priority);

    List<RoomTask> findByRoomId(Integer roomId);

    // 因為 RoomTask 實體中是直接定義 Integer employeeId;，所以不需要跨表查詢
    List<RoomTask> findByEmployeeId(Integer employeeId);

    // 找出目前工作量最少（或沒有工作）的房務專員
    @Query("SELECT e.employeeId FROM Employee e " +
           "LEFT JOIN RoomTask rt ON e.employeeId = rt.employeeId AND rt.taskStatus IN ('待處理', '進行中') " +
           "WHERE e.departmentId = (SELECT d.departmentId FROM Department d WHERE d.departmentName = '房務部') " +
           "GROUP BY e.employeeId " +
           "ORDER BY COUNT(rt.taskId) ASC")
    List<Integer> findLeastLoadedHousekeeperIds(Pageable pageable);

    default Integer findLeastLoadedHousekeeper() {
        List<Integer> result = findLeastLoadedHousekeeperIds(PageRequest.of(0, 1));
        return result.isEmpty() ? null : result.get(0);
    }

}