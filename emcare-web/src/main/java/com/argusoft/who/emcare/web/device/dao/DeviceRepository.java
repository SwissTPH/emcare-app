package com.argusoft.who.emcare.web.device.dao;

import com.argusoft.who.emcare.web.device.model.DeviceMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * @author jay
 */
public interface DeviceRepository extends JpaRepository<DeviceMaster, Integer> {

    @Query(value = "select * from device_master where imei_number = :imei", nativeQuery = true)
    public DeviceMaster getDeviceByImei(@Param("imei") String imei);

    @Query(value = "select * from device_master where mac_address = :macAddress", nativeQuery = true)
    public DeviceMaster getDeviceByMacAddress(@Param("macAddress") String macAddress);

    @Query(value = "select * from device_master where device_uuid = :deviceUUID", nativeQuery = true)
    public Optional<DeviceMaster> getDeviceByDeviceUUID(@Param("deviceUUID") String deviceUUID);

    @Query(value = "select * from device_master where user_id = :userId", nativeQuery = true)
    public DeviceMaster getDeviceByuserId(@Param("userId") String userId);

    @Modifying
    @Transactional
    @Query(value = "update device_master set android_version = :androidVersion,"
            + " last_logged_in_user= :lastLoggedInUser, is_blocked = :isBlocked," +
            "modified_by =:lastLoggedInUser, modified_on = now() where device_id = :deviceId",
            nativeQuery = true)
    public void updateDevice(
            @Param("androidVersion") String androidVersion,
            @Param("lastLoggedInUser") String lastLoggedInUser,
            @Param("isBlocked") Boolean isBlocked,
            @Param("deviceId") Integer deviceId
    );

    public List<DeviceMaster> findByAndroidVersionContainingIgnoreCaseOrDeviceNameContainingIgnoreCaseOrDeviceOsContainingIgnoreCaseOrDeviceModelContainingIgnoreCaseOrDeviceUUIDContainingIgnoreCaseOrUserNameContainingIgnoreCase(
            String searchString1,
            String searchString2,
            String searchString3,
            String searchString4,
            String searchString5,
            String searchString6
            );

    public Page<DeviceMaster> findByAndroidVersionContainingIgnoreCaseOrDeviceNameContainingIgnoreCaseOrDeviceOsContainingIgnoreCaseOrDeviceModelContainingIgnoreCaseOrDeviceUUIDContainingIgnoreCaseOrUserNameContainingIgnoreCase(
            String searchString1,
            String searchString2,
            String searchString3,
            String searchString4,
            String searchString5,
            String searchString6,
            Pageable pageable);
}
