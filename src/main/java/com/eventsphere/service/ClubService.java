package com.eventsphere.service;

import com.eventsphere.dto.ClubDTO;
import java.util.List;
import java.util.Optional;

public interface ClubService {
    ClubDTO createClub(ClubDTO clubDTO);
    Optional<ClubDTO> getClubById(Long id);
    Optional<ClubDTO> getClubByName(String name);
    List<ClubDTO> getAllClubs();
    List<ClubDTO> getClubsByDepartment(Long departmentId);
    ClubDTO updateClub(ClubDTO clubDTO);
    void deleteClub(Long id);
}
