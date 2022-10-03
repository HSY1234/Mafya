package com.a205.mafya.api.filter.helper;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.a205.mafya.api.response.SearchRes;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

//import com.a205.mafya.api.model.Tutorial;
import com.a205.mafya.db.entity.User;

public class ExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = { "Id", "Name", "User_code", "Password", "Status", "Team_code", "Class_code", "Phone_num", "Team_leader",
            "Created_at", "Updated_at", "Absent", "Tardy" };

    static String[] SEARCH_HEADER = { "날짜", "반", "팀코드", "학번", "이름", "전화번호", "직위", "결석", "지각", "상태" };

    static String SHEET = "SSAFY";


    public static boolean hasExcelFormat(MultipartFile file) {

        if (!TYPE.equals(file.getContentType())) {
            return false;
        }

        return true;
    }

    public static ByteArrayInputStream tutorialsToExcel(List<User> tutorials) {

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet(SHEET);

            // Header
            Row headerRow = sheet.createRow(0);

            for (int col = 0; col < HEADERs.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERs[col]);
            }

            int rowIdx = 1;
            for (User tutorial : tutorials) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(tutorial.getId());
                row.createCell(1).setCellValue(tutorial.getName());
                row.createCell(2).setCellValue(tutorial.getUserCode());
                row.createCell(3).setCellValue(tutorial.getPassword());
                row.createCell(4).setCellValue(tutorial.getStatus());
                row.createCell(5).setCellValue(tutorial.getTeamCode());
                row.createCell(6).setCellValue(tutorial.getClassCode());
                row.createCell(7).setCellValue(tutorial.getPhoneNum());
                row.createCell(8).setCellValue(tutorial.isTeamLeader());//bool
                row.createCell(9).setCellValue(tutorial.getCreatedAt());
                row.createCell(10).setCellValue(tutorial.getUpdatedAt());
                row.createCell(11).setCellValue(tutorial.getAbsent());
                row.createCell(12).setCellValue(tutorial.getTardy());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }

    public static List<User> excelToTutorials(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);

            Sheet sheet = workbook.getSheet(SHEET);
            Iterator<Row> rows = sheet.iterator();
            List<User> tutorials = new ArrayList<User>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();
                User tutorial = new User();
                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    switch (cellIdx) {
                        case 0:
                            break;

                        case 1:
                            tutorial.setName(currentCell.getStringCellValue());
                            break;

                        case 2:
                            tutorial.setUserCode(currentCell.getStringCellValue());
                            break;

                        case 3:
                            tutorial.setPassword(currentCell.getStringCellValue());
                            break;

                        case 4:
                            break;

                        case 5:
                            tutorial.setTeamCode(currentCell.getStringCellValue());
                            break;

                        case 6:
                            tutorial.setClassCode(currentCell.getStringCellValue());
                            break;

                        case 7:
                            tutorial.setPhoneNum(currentCell.getStringCellValue());
                            break;

                        case 8:
                            tutorial.setTeamLeader(currentCell.getBooleanCellValue());
                            break;

                        case 9:
                            break;

                        case 10:
                            break;

                        case 11:
                            tutorial.setAbsent((int) currentCell.getNumericCellValue());
                            break;

                        case 12:
                            tutorial.setTardy((int) currentCell.getNumericCellValue());
                            break;

                        default:
                            break;
                    }

                    cellIdx++;
                }
                tutorials.add(tutorial);
            }
            workbook.close();

            return tutorials;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }

    public static ByteArrayInputStream SearchToExcel(List<SearchRes> searchResList) {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet(SHEET);

            // Header
            Row headerRow = sheet.createRow(0);

            for (int col = 0; col < SEARCH_HEADER.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(SEARCH_HEADER[col]);
            }

            int rowIdx = 1;
            for (SearchRes item : searchResList) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(item.getDate());
                row.createCell(1).setCellValue(item.getClassCode() + "반");
                row.createCell(2).setCellValue(item.getTeamCode());
                row.createCell(3).setCellValue(item.getUserCode());
                row.createCell(4).setCellValue(item.getName());
                row.createCell(5).setCellValue(item.getPhoneNum());
                row.createCell(6).setCellValue(item.isTeamLeader() ? "팀장" : "팀원");
                row.createCell(7).setCellValue(item.getAbsent());
                row.createCell(8).setCellValue(item.getTrady());
                row.createCell(9).setCellValue(item.getTrace());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }
}