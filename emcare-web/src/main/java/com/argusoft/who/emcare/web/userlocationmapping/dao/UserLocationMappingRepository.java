package com.argusoft.who.emcare.web.userlocationmapping.dao;

import com.argusoft.who.emcare.web.dashboard.dto.ChartDto;
import com.argusoft.who.emcare.web.dashboard.dto.DashboardDto;
import com.argusoft.who.emcare.web.dashboard.dto.ScatterCharDto;
import com.argusoft.who.emcare.web.userlocationmapping.model.UserLocationMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserLocationMappingRepository extends JpaRepository<UserLocationMapping, Integer> {

    List<UserLocationMapping> findByUserId(String userId);

    @Query(value = "WITH RECURSIVE CHILD AS\n" +
            " (SELECT *\n" +
            "   FROM LOCATION_MASTER\n" +
            "   WHERE PARENT = :id\n" +
            "       OR ID = :id\n" +
            "   UNION SELECT L.*\n" +
            "  FROM LOCATION_MASTER L\n" +
            "       INNER JOIN CHILD S ON S.ID = L.PARENT)\n" +
            "SELECT DISTINCT ULM.USER_ID\n" +
            "FROM CHILD AS CH\n" +
            "LEFT JOIN LOCATION_RESOURCES AS LR ON CH.ID = LR.LOCATION_ID\n" +
            "LEFT JOIN USER_LOCATION_MAPPING AS ULM ON LR.RESOURCE_ID = ULM.FACILITY_ID\n" +
            "WHERE ULM.USER_ID IS NOT NULL\n" +
            "   AND ULM.FACILITY_ID IS NOT NULL offset :pageNo * :pageSize limit :pageSize", nativeQuery = true)
    public List<String> getAllUserOnChildLocationsWithPage(@Param("id") Integer id, @Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize);

    @Query(value = "WITH RECURSIVE CHILD AS\n" +
            " (SELECT *\n" +
            "   FROM LOCATION_MASTER\n" +
            "   WHERE PARENT = :id\n" +
            "       OR ID = :id\n" +
            "   UNION SELECT L.*\n" +
            "  FROM LOCATION_MASTER L\n" +
            "       INNER JOIN CHILD S ON S.ID = L.PARENT)\n" +
            "SELECT DISTINCT ULM.USER_ID\n" +
            "FROM CHILD AS CH\n" +
            "LEFT JOIN LOCATION_RESOURCES AS LR ON CH.ID = LR.LOCATION_ID\n" +
            "LEFT JOIN USER_LOCATION_MAPPING AS ULM ON LR.RESOURCE_ID = ULM.FACILITY_ID\n" +
            "WHERE ULM.USER_ID IS NOT NULL\n" +
            "   AND ULM.FACILITY_ID IS NOT NULL", nativeQuery = true)
    public List<String> getAllUserOnChildLocations(@Param("id") Integer id);


    List<UserLocationMapping> findByRegRequestFromAndIsFirst(String regRequestFrom, boolean isFirst);

    List<UserLocationMapping> findByIsFirst(boolean isFirst);

    UserLocationMapping findByUserIdAndLocationId(String userId, Integer locationId);

    UserLocationMapping findByUserIdAndFacilityId(String userId, String facilityId);

    @Query(value = "WITH TOTAL_USER AS\n" +
            " (SELECT COUNT(DISTINCT USER_ID) AS \"totalUser\"\n" +
            "  FROM USER_LOCATION_MAPPING),\n" +
            " PENDING_REQUEST AS\n" +
            " (SELECT COUNT(*) AS \"pendingRequest\"\n" +
            "  FROM USER_LOCATION_MAPPING\n" +
            "  WHERE IS_FIRST = TRUE),\n" +
            " LAST_SEVEN_DAY_REQUEST AS\n" +
            "(SELECT COUNT (DISTINCT ID) AS \"totalPatient\"\n" +
            " FROM emcare_resources where type = 'PATIENT')\n" +
            "SELECT *\n" +
            "FROM TOTAL_USER,\n" +
            " PENDING_REQUEST,\n" +
            " LAST_SEVEN_DAY_REQUEST;", nativeQuery = true)
    DashboardDto getDashboardData();

    @Query(value = "SELECT FACILITY_ID AS \"facilityId\", \n" +
            " COUNT(*) AS \"count\"\n" +
            "FROM USER_LOCATION_MAPPING\n" +
            "WHERE FACILITY_ID IS NOT NULL\n" +
            "GROUP BY FACILITY_ID;", nativeQuery = true)
    List<ChartDto> getDashboardBarChartData();

    @Query(value = "SELECT FACILITY_ID AS \"facilityId\", \n" +
            " COUNT(*) AS \"count\"\n" +
            "FROM EMCARE_RESOURCES\n" +
            "WHERE FACILITY_ID IS NOT NULL AND TYPE = 'PATIENT'\n" +
            "GROUP BY FACILITY_ID;", nativeQuery = true)
    List<ChartDto> getDashboardPieChartData();

    @Query(value = "SELECT date_part('week', created_on) AS \"weekly\",\n" +
            "       COUNT(resource_id) as \"count\"           \n" +
            "FROM emcare_resources\n" +
            "where type = 'PATIENT' and date_part('year', created_on) = '2022'\n" +
            "GROUP BY  weekly\n" +
            "ORDER BY weekly DESC limit 10;", nativeQuery = true)
    List<ScatterCharDto> getDashboardScatterChartData();

}
