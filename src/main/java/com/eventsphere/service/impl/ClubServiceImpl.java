package com.eventsphere.service.impl;

import com.eventsphere.dto.ClubDTO;
import com.eventsphere.entity.Club;
import com.eventsphere.entity.Department;
import com.eventsphere.repository.ClubRepository;
import com.eventsphere.repository.DepartmentRepository;
import com.eventsphere.service.ClubService;
import com.eventsphere.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClubServiceImpl implements ClubService {
    
    @Autowired
    private ClubRepository clubRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Override
    public ClubDTO createClub(ClubDTO clubDTO) {
        if (clubRepository.existsByName(clubDTO.getName())) {
            throw new IllegalArgumentException("Club with this name already exists");
        }
        
        Department department = departmentRepository.findById(clubDTO.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        
        Club club = new Club();
        club.setName(clubDTO.getName());
        club.setDescription(clubDTO.getDescription());
        club.setDepartment(department);
        
        Club saved = clubRepository.save(club);
        return mapToDTO(saved);
    }
    
    @Override
    public Optional<ClubDTO> getClubById(Long id) {
        return clubRepository.findById(id).map(this::mapToDTO);
    }
    
    @Override
    public Optional<ClubDTO> getClubByName(String name) {
        return clubRepository.findByName(name).map(this::mapToDTO);
    }
    
    @Override
    public List<ClubDTO> getAllClubs() {
        return clubRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ClubDTO> getClubsByDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        return clubRepository.findByDepartment(department).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public ClubDTO updateClub(ClubDTO clubDTO) {
        Club club = clubRepository.findById(clubDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Club not found"));
        
        if (!club.getName().equals(clubDTO.getName()) && 
            clubRepository.existsByName(clubDTO.getName())) {
            throw new IllegalArgumentException("Club with this name already exists");
        }
        
        club.setName(clubDTO.getName());
        club.setDescription(clubDTO.getDescription());
        
        Club updated = clubRepository.save(club);
        return mapToDTO(updated);
    }
    
    @Override
    public void deleteClub(Long id) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found"));
        clubRepository.delete(club);
    }
    
    private ClubDTO mapToDTO(Club club) {
        ClubDTO dto = new ClubDTO();
        dto.setId(club.getId());
        dto.setName(club.getName());
        dto.setDescription(club.getDescription());
        dto.setDepartmentId(club.getDepartment().getId());
        dto.setDepartmentName(club.getDepartment().getName());
        return dto;
    }
}
