package com.a205.mafya.util.filter.helper;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

//import com.a205.mafya.api.model.Tutorial;
import com.a205.mafya.db.repository.entity.User;

public class ExcelHelper {
    private static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = { "Id", "Name", "User_code", "Password", "Team_code", "Class_code", "Phone_num", "Team_leader",
            "Absent", "Tardy" };

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
                String phone = tutorial.getPhoneNum();

                if(phone.length() == 12){
                    phone = phone.substring(0, 3) + "-" + phone.substring(3, 7) + "-" + phone.substring(7, 11);
                }

                row.createCell(0).setCellValue(tutorial.getId());
                row.createCell(1).setCellValue(tutorial.getName());
                row.createCell(2).setCellValue(tutorial.getUserCode());
                row.createCell(3).setCellValue(tutorial.getPassword());
                row.createCell(4).setCellValue(tutorial.getTeamCode());
                row.createCell(5).setCellValue(tutorial.getClassCode() + "반");
                row.createCell(6).setCellValue(phone);
                row.createCell(7).setCellValue((tutorial.isTeamLeader() ? "팀장" : "팀원"));//bool
                row.createCell(8).setCellValue(tutorial.getAbsent());
                row.createCell(9).setCellValue(tutorial.getTardy());
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
                System.out.println(tutorial);
                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    switch (cellIdx) {
                        case 0:
                            System.out.println("case 0");
                            System.out.println("---" + cellIdx + "---");
                            System.out.println(currentCell.getStringCellValue());
                            tutorial.setName(currentCell.getStringCellValue());
                            break;

                        case 1:
                            System.out.println("case 1");
                            System.out.println("---" + cellIdx + "---");
                            System.out.println(currentCell.getStringCellValue());
                            tutorial.setUserCode(currentCell.getStringCellValue());
                            break;

                        case 2:
                            System.out.println("case 2");
                            System.out.println("---" + cellIdx + "---");
                            System.out.println(currentCell.getStringCellValue());
                            String password = currentCell.getStringCellValue();
                            tutorial.setPassword(passwordEncoder.encode(password));
                            break;

                        case 3:
                            System.out.println("case 3");
                            System.out.println("---" + cellIdx + "---");
                            System.out.println(currentCell.getStringCellValue());
                            tutorial.setTeamCode(currentCell.getStringCellValue());
                            break;

                        case 4:
                            System.out.println("case 4");
                            System.out.println("---" + cellIdx + "---");
                            System.out.println(currentCell.getStringCellValue());
                            tutorial.setClassCode(currentCell.getStringCellValue());
                            break;

                        case 5:
                            System.out.println("case 5");
                            System.out.println("---" + cellIdx + "---");
                            System.out.println(currentCell.getStringCellValue());
                            tutorial.setPhoneNum(currentCell.getStringCellValue());
                            break;

                        case 6:
                            System.out.println("case 6");
                            System.out.println("---" + cellIdx + "---");
                            System.out.println(currentCell.getBooleanCellValue());
                            tutorial.setTeamLeader(currentCell.getBooleanCellValue());
                            break;

                        case 7:
                            System.out.println("case 8");
                            System.out.println("---" + cellIdx + "---");
                            System.out.println(currentCell.getNumericCellValue());
                            tutorial.setAbsent((int) currentCell.getNumericCellValue());
                            break;

                        case 8:
                            System.out.println("case 8");
                            System.out.println("---" + cellIdx + "---");
                            System.out.println(currentCell.getNumericCellValue());
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