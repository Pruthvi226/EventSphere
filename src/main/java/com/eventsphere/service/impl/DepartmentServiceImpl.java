package com.eventsphere.service.impl;

import com.eventsphere.dto.DepartmentDTO;
import com.eventsphere.entity.Department;
import com.eventsphere.repository.DepartmentRepository;
import com.eventsphere.service.DepartmentService;
import com.eventsphere.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Override
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        if (departmentRepository.existsByName(departmentDTO.getName())) {
            throw new IllegalArgumentException("Department with this name already exists");
        }
        
        Department department = new Department();
        department.setName(departmentDTO.getName());
        department.setDescription(departmentDTO.getDescription());
        
        Department saved = departmentRepository.save(department);
        return mapToDTO(saved);
    }
    
    @Override
    public Optional<DepartmentDTO> getDepartmentById(Long id) {
        return departmentRepository.findById(id).map(this::mapToDTO);
    }
    
    @Override
    public Optional<DepartmentDTO> getDepartmentByName(String name) {
        return departmentRepository.findByName(name).map(this::mapToDTO);
    }
    
    @Override
    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public DepartmentDTO updateDepartment(DepartmentDTO departmentDTO) {
        Department department = departmentRepository.findById(departmentDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        
        if (!department.getName().equals(departmentDTO.getName()) && 
            departmentRepository.existsByName(departmentDTO.getName())) {
            throw new IllegalArgumentException("Department with this name already exists");
        }
        
        department.setName(departmentDTO.getName());
        department.setDescription(departmentDTO.getDescription());
        
        Department updated = departmentRepository.save(department);
        return mapToDTO(updated);
    }
    
    @Override
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        departmentRepository.delete(department);
    }
    
    private DepartmentDTO mapToDTO(Department department) {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setDescription(department.getDescription());
        return dto;
    }
}
