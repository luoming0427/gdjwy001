package com.hirisun.cloud.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.springframework.stereotype.Component;

/**
 * @author junglefisher
 * @date 2020/2/28 17:26
 */
@Component
public class UnitExcelExportUtil {


    /**
     * Excel2003和Excel2007+创建方式不同
     * Excel2003使用HSSFWorkbook 后缀xls
     * Excel2007+使用XSSFWorkbook 后缀xlsx
     * 此方法可保证动态创建Workbook
     *
     * @param is
     * @return
     */
    public static Workbook createWorkbook(InputStream is) throws IOException, InvalidFormatException {
        return WorkbookFactory.create(is);
    }

    /**
     *导入数据获取数据列表
     * @param wb
     * @return
     */
    public static List<List<Object>> getDataListByCellNumber(Workbook wb,int num,int sheetNum,int cellNum) {

        List<List<Object>> rowList = new ArrayList<>();
        Sheet sheet = wb.getSheetAt(sheetNum);
        for (int i = sheet.getFirstRowNum()+num; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (null == row){
                continue;
            }
            List<Object> cellList = new ArrayList<>();
            Cell cell =row.getCell(cellNum);
            if (num == 3) {
                cellList.add(getCellValue(cell));
            } else if (num == 1) {
                if ("null".equals(getCellValue(cell))) {
                    cellList.add("null");
                } else {
                    cellList.add(getCellValue(cell));
                }
            }
            rowList.add(cellList);
        }
        return rowList;
    
    }
    
    /**
     *导入数据获取数据列表
     * @param wb
     * @return
     */
    public static List<List<Object>> getDataList(Workbook wb,int num) {
        List<List<Object>> rowList = new ArrayList<>();
        Sheet sheet = wb.getSheetAt(0);
        for (int i = sheet.getFirstRowNum()+num; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (null == row){
                continue;
            }
            List<Object> cellList = new ArrayList<>();
            for (int j = row.getFirstCellNum(); j < row.getLastCellNum(); j++) {
                //区分省直表和疫情云桌面获取逻辑
                Cell cell = row.getCell(j);
                if (num == 3) {
                    cellList.add(getCellValue(cell));
                } else if (num == 1) {
                    if ("null".equals(getCellValue(cell))) {
                        cellList.add("null");
                    } else {
                        cellList.add(getCellValue(cell));
                    }
                }
            }
            rowList.add(cellList);
        }
        return rowList;
    }

    /**
     *导入数据获取数据列表
     * @param wb
     * @return
     */
    public static List<List<Object>> getDataListBySheet(Workbook wb,int num,int sheetNum) {
        List<List<Object>> rowList = new ArrayList<>();
        Sheet sheet = wb.getSheetAt(sheetNum);
        for (int i = sheet.getFirstRowNum()+num; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (null == row){
                continue;
            }
            List<Object> cellList = new ArrayList<>();
            for (int j = row.getFirstCellNum(); j < row.getLastCellNum(); j++) {
                //区分省直表和疫情云桌面获取逻辑
                Cell cell = row.getCell(j);
                if (num == 3) {
                    cellList.add(getCellValue(cell));
                } else if (num == 1) {
                    if ("null".equals(getCellValue(cell))) {
                        cellList.add("null");
                    } else {
                        cellList.add(getCellValue(cell));
                    }
                }
            }
            rowList.add(cellList);
        }
        return rowList;
    }

    private static String getCellValue(Cell cell) {
        String cellvalue = "";
        if (cell != null) {
            // 判断当前Cell的Type
            switch (cell.getCellType()) {
                // 如果当前Cell的Type为NUMERIC
                case HSSFCell.CELL_TYPE_NUMERIC: {
                    short format = cell.getCellStyle().getDataFormat();
                    //excel中的时间格式
                    if (format == 14 || format == 31 || format == 57 || format == 58) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        double value = cell.getNumericCellValue();
                        Date date = DateUtil.getJavaDate(value);
                        cellvalue = sdf.format(date);
                    }
                    // 判断当前的cell是否为Date
                    //先注释日期类型的转换，在实际测试中发现HSSFDateUtil.isCellDateFormatted(cell)只识别2014/02/02这种格式。
                    else if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        // 如果是Date类型则，取得该Cell的Date值
                        // 对2014-02-02格式识别不出是日期格式
                        Date date = cell.getDateCellValue();
                        DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
                        cellvalue = formater.format(date);
                    } else { // 如果是纯数字
                        // 取得当前Cell的数值
                        cellvalue = NumberToTextConverter.toText(cell.getNumericCellValue());
                    }
                    break;
                }
                // 如果当前Cell的Type为STRIN
                case HSSFCell.CELL_TYPE_STRING:
                    // 取得当前的Cell字符串
                    cellvalue = cell.getStringCellValue();
                    break;
                case HSSFCell.CELL_TYPE_BLANK:
                    cellvalue = null;
                    break;
                // 默认的Cell值
                default: {
                    cellvalue = " ";
                }
            }
        } else {
            cellvalue = "";
        }
        return cellvalue;
    }
}
