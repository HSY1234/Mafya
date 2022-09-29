package com.a205.mafya.api.helper;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    static String SHEET = "test_user";

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
        System.out.println("ExcelHelper_excelToTutor");
        System.out.println("000");
        try {
            System.out.println("111");
            Workbook workbook = new XSSFWorkbook(is);

            Sheet sheet = workbook.getSheet(SHEET);
            System.out.println(sheet);
            Iterator<Row> rows = sheet.iterator();
            System.out.println("222");
            System.out.println(rows);
            List<User> tutorials = new ArrayList<User>();
            System.out.println("333");
            System.out.println(tutorials);
            //List<Tutorial> test_user = new ArrayList<Tutorial>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                System.out.println("444");
                Row currentRow = rows.next();
                System.out.println(currentRow);

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();
                System.out.println("555");
                System.out.println(cellsInRow);
                User tutorial = new User();
                System.out.println("666");
                System.out.println(tutorial);
                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    System.out.println("777");
                    System.out.println(currentCell);
                    switch (cellIdx) {
                        case 0:
                            //tutorial.setId(Integer.parseInt(currentCell.getNumericCellValue()));
                            System.out.println("case0");
                            break;

                        case 1:
                            System.out.println("case1");
                            System.out.println(">>>>>"+currentCell);
                            System.out.println(currentCell.getClass().getName());
                            System.out.println(currentCell.getCellType());
                            System.out.println("<<<<<<"+currentCell.getStringCellValue());
                            tutorial.setName(currentCell.getStringCellValue());
                            //tutorial.setName();
                            break;

                        case 2:
                            System.out.println("case2");
                            System.out.println(">>>>>"+currentCell);
                            System.out.println(currentCell.getClass().getName());
                            System.out.println(currentCell.getCellType());

                            System.out.println("<<<<<<"+currentCell.getStringCellValue());
                            tutorial.setUserCode(currentCell.getStringCellValue());
                            //tutorial.setUserCode();
                            break;

                        case 3:
                            tutorial.setPassword(currentCell.getStringCellValue());
                            System.out.println("case3");
                            break;

                        case 4:
                            //tutorial.setStatus(Integer.parseInt(currentCell.getNumericCellValue()));
                            System.out.println("case4");
                            System.out.println(currentCell.getNumericCellValue());
                            break;

                        case 5:
                            tutorial.setTeamCode(currentCell.getStringCellValue());
                            System.out.println("case5");
                            break;

                        case 6:
                            tutorial.setClassCode(currentCell.getStringCellValue());
                            System.out.println("case6");
                            break;

                        case 7:
                            tutorial.setPhoneNum(currentCell.getStringCellValue());
                            System.out.println("case7");
                            break;

                        case 8:
                            tutorial.setTeamLeader(currentCell.getBooleanCellValue());
                            System.out.println("case8");
                            break;

                        case 9:
                            //tutorial.setCreatedAt(currentCell.getStringCellValue()); // 빈칸으로 들어가는지 확인 or 시간값을 자동으로 줄 수 있는지 확인
                            //tutorial.setCreatedAt(currentCell.getLocalDateTimeCellValue());
                            System.out.println("case9");
                            break;

                        case 10:
                            //tutorial.setUpdatedAt(currentCell.getStringCellValue()); // 빈칸으로 들어가는지 확인 or 시간값을 자동으로 줄 수 있는지 확인
                            //tutorial.setUpdatedAt(currentCell.getLocalDateTimeCellValue());
                            System.out.println("case10");
                            break;

                        case 11:
                            tutorial.setAbsent((int) currentCell.getNumericCellValue());
                            System.out.println("case11");
                            break;

                        case 12:
                            tutorial.setTardy((int) currentCell.getNumericCellValue());
                            System.out.println("case12");
                            break;

                        default:
                            break;
                    }

                    cellIdx++;
                }
                System.out.println("888");
                tutorials.add(tutorial);
                //test_user.add(tutorial);
            }
            System.out.println("999");
            workbook.close();

            return tutorials;
            //return test_user;
        } catch (IOException e) {
            System.out.println("10 10 10");
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }
}
