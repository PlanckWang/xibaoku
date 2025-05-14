package com.example.guangxishengkejavafx.service;

import com.example.guangxishengkejavafx.model.dto.ApplicationReportEntry;
import com.example.guangxishengkejavafx.model.dto.DerivativeProductReportEntry;
import com.example.guangxishengkejavafx.model.dto.DetectionReportEntry;
import com.example.guangxishengkejavafx.model.dto.StorageReportEntry;
import com.example.guangxishengkejavafx.model.entity.DirectProductRelease;
import com.example.guangxishengkejavafx.model.entity.Product;
import com.example.guangxishengkejavafx.model.entity.SamplePreparation;
import com.example.guangxishengkejavafx.model.entity.SampleTestResult;
import com.example.guangxishengkejavafx.model.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final FrozenSampleRepository frozenSampleRepository;
    private final ProductRepository productRepository;
    private final DepositorRepository depositorRepository;
    private final StorageContractRepository storageContractRepository;
    private final DirectProductReleaseRepository directProductReleaseRepository;
    private final SamplePreparationRepository samplePreparationRepository;
    private final SampleTestResultRepository sampleTestResultRepository;

    @Autowired
    public ReportService(FrozenSampleRepository frozenSampleRepository,
                         ProductRepository productRepository,
                         DepositorRepository depositorRepository,
                         StorageContractRepository storageContractRepository,
                         DirectProductReleaseRepository directProductReleaseRepository,
                         SamplePreparationRepository samplePreparationRepository,
                         SampleTestResultRepository sampleTestResultRepository) {
        this.frozenSampleRepository = frozenSampleRepository;
        this.productRepository = productRepository;
        this.depositorRepository = depositorRepository;
        this.storageContractRepository = storageContractRepository;
        this.directProductReleaseRepository = directProductReleaseRepository;
        this.samplePreparationRepository = samplePreparationRepository;
        this.sampleTestResultRepository = sampleTestResultRepository;
    }

    @Transactional(readOnly = true)
    public List<StorageReportEntry> generateProductStorageReport() {
        return frozenSampleRepository.findAll().stream()
                .filter(fs -> fs.getSamplePreparation() != null && fs.getSamplePreparation().getProduct() != null)
                .collect(Collectors.groupingBy(fs -> fs.getSamplePreparation().getProduct(), Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new StorageReportEntry(
                        "产品库存统计",
                        entry.getKey().getProductName(),
                        entry.getValue(),
                        frozenSampleRepository.findBySamplePreparation_Product(entry.getKey()).stream()
                                .mapToDouble(fs -> fs.getVolume() != null ? fs.getVolume() : 0.0).sum(),
                        "按产品统计冻存样本数量和总体积"
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StorageReportEntry> generateDepositorStorageReport() {
        return storageContractRepository.findAll().stream()
                .filter(sc -> sc.getDepositor() != null)
                .collect(Collectors.groupingBy(sc -> sc.getDepositor().getDepositorName(), Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new StorageReportEntry(
                        "储户合同统计",
                        entry.getKey(),
                        entry.getValue(),
                        null,
                        "按储户统计合同数量 (示例)"
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StorageReportEntry> generateStorageLocationReport() {
        return frozenSampleRepository.findAll().stream()
                .collect(Collectors.groupingBy(FrozenSample::getStorageLocation, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new StorageReportEntry(
                        "存储位置统计",
                        entry.getKey(),
                        entry.getValue(),
                        frozenSampleRepository.findByStorageLocation(entry.getKey()).stream()
                            .mapToDouble(fs -> fs.getVolume() != null ? fs.getVolume() : 0.0).sum(),
                        "按存储位置统计冻存样本数量和总体积"
                ))
                .collect(Collectors.toList());
    }

    // --- Application Reports --- 

    @Transactional(readOnly = true)
    public List<ApplicationReportEntry> generateProductReleaseReport(LocalDate startDate, LocalDate endDate) {
        List<DirectProductRelease> releases = directProductReleaseRepository.findAll().stream()
                .filter(dpr -> dpr.getReleaseDate() != null && !dpr.getReleaseDate().toLocalDate().isBefore(startDate) && !dpr.getReleaseDate().toLocalDate().isAfter(endDate))
                .collect(Collectors.toList());

        return releases.stream()
                .filter(dpr -> dpr.getProduct() != null)
                .collect(Collectors.groupingBy(DirectProductRelease::getProduct))
                .entrySet().stream()
                .map(entry -> {
                    long count = entry.getValue().size();
                    double totalQuantity = entry.getValue().stream().mapToDouble(DirectProductRelease::getQuantityReleased).sum();
                    String unit = entry.getValue().stream().map(DirectProductRelease::getUnit).findFirst().orElse("N/A");
                    return new ApplicationReportEntry(
                            "产品发放统计",
                            "产品名称",
                            entry.getKey().getProductName(),
                            count,
                            totalQuantity,
                            unit,
                            startDate,
                            endDate,
                            "统计周期内各产品的发放次数和总量"
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ApplicationReportEntry> generateSamplePreparationPurposeReport(LocalDate startDate, LocalDate endDate) {
        List<SamplePreparation> preparations = samplePreparationRepository.findAll().stream()
            .filter(sp -> sp.getPreparationDate() != null && !sp.getPreparationDate().toLocalDate().isBefore(startDate) && !sp.getPreparationDate().toLocalDate().isAfter(endDate))
            .collect(Collectors.toList());

        return preparations.stream()
            .filter(sp -> sp.getPurpose() != null && !sp.getPurpose().isEmpty())
            .collect(Collectors.groupingBy(SamplePreparation::getPurpose, Collectors.counting()))
            .entrySet().stream()
            .map(entry -> new ApplicationReportEntry(
                "样本制备用途统计",
                "制备用途",
                entry.getKey(),
                entry.getValue(), 
                null, 
                null, 
                startDate,
                endDate,
                "统计周期内不同制备用途的样本制备次数"
            ))
            .collect(Collectors.toList());
    }

    // --- Detection Reports --- 
    @Transactional(readOnly = true)
    public List<DetectionReportEntry> generateTestItemPassRateReport(LocalDate startDate, LocalDate endDate) {
        List<SampleTestResult> testResults = sampleTestResultRepository.findAll().stream()
            .filter(str -> str.getTestDate() != null && !str.getTestDate().toLocalDate().isBefore(startDate) && !str.getTestDate().toLocalDate().isAfter(endDate))
            .collect(Collectors.toList());

        return testResults.stream()
            .filter(str -> str.getTestItem() != null && !str.getTestItem().isEmpty())
            .collect(Collectors.groupingBy(SampleTestResult::getTestItem))
            .entrySet().stream()
            .map(entry -> {
                String testItem = entry.getKey();
                List<SampleTestResult> resultsForTestItem = entry.getValue();
                long totalSamples = resultsForTestItem.size();
                long positiveSamples = resultsForTestItem.stream().filter(str -> "合格".equalsIgnoreCase(str.getResult()) || "阳性".equalsIgnoreCase(str.getResult())).count();
                double positiveRate = totalSamples > 0 ? (double) positiveSamples / totalSamples * 100 : 0.0;
                return new DetectionReportEntry(
                    "检测项目合格/阳性率统计",
                    testItem,
                    null, 
                    totalSamples,
                    positiveSamples,
                    positiveRate,
                    "%", 
                    null, 
                    startDate,
                    endDate,
                    "统计周期内各检测项目的样本总数、合格/阳性数及比率"
                );
            })
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DetectionReportEntry> generateSampleTypeQualificationReport(LocalDate startDate, LocalDate endDate) {
        List<SampleTestResult> testResults = sampleTestResultRepository.findAll().stream()
            .filter(str -> str.getTestDate() != null && !str.getTestDate().toLocalDate().isBefore(startDate) && !str.getTestDate().toLocalDate().isAfter(endDate))
            .collect(Collectors.toList());

        return testResults.stream()
            .filter(str -> str.getSampleInspectionRequest() != null && str.getSampleInspectionRequest().getSampleRegistration() != null && str.getSampleInspectionRequest().getSampleRegistration().getSampleType() != null)
            .collect(Collectors.groupingBy(str -> str.getSampleInspectionRequest().getSampleRegistration().getSampleType()))
            .entrySet().stream()
            .map(entry -> {
                String sampleType = entry.getKey();
                List<SampleTestResult> resultsForSampleType = entry.getValue();
                long totalSamples = resultsForSampleType.size();
                long qualifiedSamples = resultsForSampleType.stream().filter(str -> "合格".equalsIgnoreCase(str.getResult())).count();
                double qualificationRate = totalSamples > 0 ? (double) qualifiedSamples / totalSamples * 100 : 0.0;
                return new DetectionReportEntry(
                    "样本类型合格率统计",
                    null, 
                    sampleType,
                    totalSamples,
                    qualifiedSamples,
                    qualificationRate,
                    "%", 
                    null, 
                    startDate,
                    endDate,
                    "统计周期内各样本类型的总数、合格数及合格率"
                );
            })
            .collect(Collectors.toList());
    }

    // --- Derivative Product Reports ---
    @Transactional(readOnly = true)
    public List<DerivativeProductReportEntry> generateDerivativeProductTypeReport(LocalDate startDate, LocalDate endDate) {
        List<SamplePreparation> preparations = samplePreparationRepository.findAll().stream()
            .filter(sp -> sp.getPreparationDate() != null && !sp.getPreparationDate().toLocalDate().isBefore(startDate) && !sp.getPreparationDate().toLocalDate().isAfter(endDate))
            .filter(sp -> sp.getProduct() != null && sp.getProduct().getProductType() != null)
            .collect(Collectors.toList());

        return preparations.stream()
            .collect(Collectors.groupingBy(sp -> sp.getProduct().getProductType()))
            .entrySet().stream()
            .map(entry -> {
                String productType = entry.getKey();
                List<SamplePreparation> prepsForType = entry.getValue();
                long quantityProduced = prepsForType.size(); // Number of preparation batches
                double totalAmount = prepsForType.stream().mapToDouble(sp -> sp.getQuantityPrepared() != null ? sp.getQuantityPrepared().doubleValue() : 0.0).sum();
                String unit = prepsForType.stream().map(SamplePreparation::getUnit).findFirst().orElse("N/A");
                // Assuming first prep in group is representative for sample source and creator for this summary
                SamplePreparation firstPrep = prepsForType.get(0);
                String sourceSampleId = firstPrep.getSampleRegistration() != null ? firstPrep.getSampleRegistration().getSampleId() : "N/A";
                String sourceSampleType = firstPrep.getSampleRegistration() != null ? firstPrep.getSampleRegistration().getSampleType() : "N/A";
                String createdBy = firstPrep.getPreparedBy();

                return new DerivativeProductReportEntry(
                    "衍生品类型统计",
                    null, // ProductName not grouped here
                    productType,
                    sourceSampleId, // Simplified for summary
                    sourceSampleType, // Simplified for summary
                    quantityProduced,
                    totalAmount,
                    unit,
                    startDate,
                    endDate,
                    createdBy, // Simplified for summary
                    "按衍生品类型统计生产批次数和总量"
                );
            })
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DerivativeProductReportEntry> generateDerivativeProductProductionReport(LocalDate startDate, LocalDate endDate) {
        List<SamplePreparation> preparations = samplePreparationRepository.findAll().stream()
            .filter(sp -> sp.getPreparationDate() != null && !sp.getPreparationDate().toLocalDate().isBefore(startDate) && !sp.getPreparationDate().toLocalDate().isAfter(endDate))
            .filter(sp -> sp.getProduct() != null && sp.getProduct().getProductName() != null)
            .collect(Collectors.toList());

        return preparations.stream()
            .collect(Collectors.groupingBy(sp -> sp.getProduct().getProductName()))
            .entrySet().stream()
            .map(entry -> {
                String productName = entry.getKey();
                List<SamplePreparation> prepsForProduct = entry.getValue();
                long quantityProduced = prepsForProduct.size(); // Number of preparation batches
                double totalAmount = prepsForProduct.stream().mapToDouble(sp -> sp.getQuantityPrepared() != null ? sp.getQuantityPrepared().doubleValue() : 0.0).sum();
                String unit = prepsForProduct.stream().map(SamplePreparation::getUnit).findFirst().orElse("N/A");
                // Assuming first prep in group is representative for sample source, type and creator for this summary
                SamplePreparation firstPrep = prepsForProduct.get(0);
                String productType = firstPrep.getProduct() != null ? firstPrep.getProduct().getProductType() : "N/A";
                String sourceSampleId = firstPrep.getSampleRegistration() != null ? firstPrep.getSampleRegistration().getSampleId() : "N/A";
                String sourceSampleType = firstPrep.getSampleRegistration() != null ? firstPrep.getSampleRegistration().getSampleType() : "N/A";
                String createdBy = firstPrep.getPreparedBy();

                return new DerivativeProductReportEntry(
                    "特定衍生品产量统计",
                    productName,
                    productType, // Add product type for context
                    sourceSampleId, // Simplified for summary
                    sourceSampleType, // Simplified for summary
                    quantityProduced,
                    totalAmount,
                    unit,
                    startDate,
                    endDate,
                    createdBy, // Simplified for summary
                    "按衍生品名称统计生产批次数和总量"
                );
            })
            .collect(Collectors.toList());
    }
}

