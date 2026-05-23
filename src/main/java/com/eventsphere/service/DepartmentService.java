package com.eventsphere.service;

import com.eventsphere.dto.DepartmentDTO;
import java.util.List;
import java.util.Optional;

public interface DepartmentService {
    DepartmentDTO createDepartment(DepartmentDTO departmentDTO);
    Optional<DepartmentDTO> getDepartmentById(Long id);
    Optional<DepartmentDTO> getDepartmentByName(String name);
    List<DepartmentDTO> getAllDepartments();
    DepartmentDTO updateDepartment(DepartmentDTO departmentDTO);
    void deleteDepartment(Long id);
}
