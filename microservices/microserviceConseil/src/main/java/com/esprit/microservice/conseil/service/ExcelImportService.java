package com.esprit.microservice.conseil.service;

import com.esprit.microservice.conseil.model.ExcelData;
import com.esprit.microservice.conseil.repository.ExcelDataRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelImportService {

    @Autowired
    private ExcelDataRepository excelDataRepository;

    public List<ExcelData> importExcelFile(MultipartFile file) throws IOException {
        List<ExcelData> excelDataList = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // Skip header row
            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) {
                rowIterator.next(); // Skip header row
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                ExcelData excelData = new ExcelData();

                excelData.setNo(getCellValueAsString(row.getCell(0)));
                excelData.setOption(getCellValueAsString(row.getCell(1)));
                excelData.setIdEt(getCellValueAsString(row.getCell(2)));
                excelData.setNom(getCellValueAsString(row.getCell(3)));
                excelData.setPrenom(getCellValueAsString(row.getCell(4)));
                excelData.setEmailEt(getCellValueAsString(row.getCell(5)));
                excelData.setAdresseMailEsp(getCellValueAsString(row.getCell(6)));

                excelDataList.add(excelData);
            }

            workbook.close();
        }

        // Save all data to database
        return excelDataRepository.saveAll(excelDataList);
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    public List<ExcelData> getAllExcelData() {
        return excelDataRepository.findAll();
    }
}