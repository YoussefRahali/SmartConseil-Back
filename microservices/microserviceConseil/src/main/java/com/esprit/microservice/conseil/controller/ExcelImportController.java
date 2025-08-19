package com.esprit.microservice.conseil.controller;

import com.esprit.microservice.conseil.model.ExcelData;
import com.esprit.microservice.conseil.service.ExcelImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/conseils")
public class ExcelImportController {

    @Autowired
    private ExcelImportService excelImportService;

    @PostMapping("/importExcel")
    public ResponseEntity<?> importExcelFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return new ResponseEntity<>("Veuillez sélectionner un fichier", HttpStatus.BAD_REQUEST);
            }

            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
                return new ResponseEntity<>("Veuillez télécharger un fichier Excel valide", HttpStatus.BAD_REQUEST);
            }

            List<ExcelData> importedData = excelImportService.importExcelFile(file);
            return new ResponseEntity<>(importedData, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erreur lors de l'import: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/excelData")
    public ResponseEntity<List<ExcelData>> getAllExcelData() {
        List<ExcelData> excelDataList = excelImportService.getAllExcelData();
        return new ResponseEntity<>(excelDataList, HttpStatus.OK);
    }
}