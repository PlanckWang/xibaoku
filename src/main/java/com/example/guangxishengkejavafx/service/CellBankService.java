package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.entity.CellBank;
import com.example.guangxishengkejavafx.model.repository.CellBankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CellBankService {

    private final CellBankRepository cellBankRepository;

    @Autowired
    public CellBankService(CellBankRepository cellBankRepository) {
        this.cellBankRepository = cellBankRepository;
    }

    public List<CellBank> findAll() {
        // Retrieve only entities with BankType = "工作细胞库"
        return cellBankRepository.findByBankType("工作细胞库");
    }

    public Optional<CellBank> findById(Integer id) {
        return cellBankRepository.findById(id);
    }

    public CellBank save(CellBank cellBank) {
        // Ensure BankType is correctly set before saving
        cellBank.setBankType("工作细胞库");
        return cellBankRepository.save(cellBank);
    }

    public void deleteById(Integer id) {
        cellBankRepository.deleteById(id);
    }

    // Add other business logic methods as needed, for example:
    // public List<WorkingCellBank> findByStatus(String status) {
    //     return workingCellBankRepository.findByBankTypeAndStatus("工作细胞库", status);
    // }
}

