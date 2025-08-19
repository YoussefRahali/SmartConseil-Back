package tn.esprit.microserviceplanification.Service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.microserviceplanification.Entity.ExcelData;
import tn.esprit.microserviceplanification.Entity.ExcelDataEleve;
import tn.esprit.microserviceplanification.Repository.ExcelDataEleveRepository;
import tn.esprit.microserviceplanification.Repository.ExcelDataRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelImportService {

    public List<ExcelDataEleve> getExcelDataEleveByConseilId(Integer conseilId) {
        return excelDataEleveRepository.findByConseilId(conseilId);
    }

    @Autowired
    private ExcelDataRepository excelDataRepository;

    @Autowired
    private ExcelDataEleveRepository excelDataEleveRepository;

    public List<ExcelData> importExcelFile(MultipartFile file,Integer ConseilId) throws IOException {
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
                excelData.setConseilId(ConseilId);
                excelDataList.add(excelData);
            }

            workbook.close();
        }

        // Save all data to database
        return excelDataRepository.saveAll(excelDataList);
    }



    public List<ExcelDataEleve> importExcelFileEleve(MultipartFile file, Integer ConseilId) throws IOException {
        List<ExcelDataEleve> excelDataList = new ArrayList<>();

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
                ExcelDataEleve excelData = new ExcelDataEleve();

                excelData.setId_et(getCellValueAsString(row.getCell(0)));
                excelData.setNom_et(getCellValueAsString(row.getCell(1)));
                excelData.setPnom_et(getCellValueAsString(row.getCell(2)));
                excelData.setClasse_courante_et(getCellValueAsString(row.getCell(3)));
                excelData.setAdresse_mail_et(getCellValueAsString(row.getCell(4)));
                excelData.setSession_principale(getCellValueAsString(row.getCell(5)));
                excelData.setRemarque(getCellValueAsString(row.getCell(6)));
                excelData.setRachat_Moyenne(getCellValueAsString(row.getCell(7)));
                excelData.setRachat_UE(getCellValueAsString(row.getCell(8)));
                excelData.setRachat_Module(getCellValueAsString(row.getCell(9)));
                excelData.setConseil_de_ecole(getCellValueAsString(row.getCell(10)));
                excelData.setDossierMedical(getCellValueAsString(row.getCell(11)));
                excelData.setConseilId(ConseilId);
                excelDataList.add(excelData);
            }

            workbook.close();
        }

        // Save all data to database
        return excelDataEleveRepository.saveAll(excelDataList);
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

    public List<ExcelData> getAllExcelData1() {
        return excelDataRepository.findAll();
    }


}