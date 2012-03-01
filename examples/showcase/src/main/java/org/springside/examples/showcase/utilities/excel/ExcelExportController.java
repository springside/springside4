package org.springside.examples.showcase.utilities.excel;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springside.examples.showcase.utilities.excel.DummyDataGenerator.TemperatureAnomaly;
import org.springside.modules.web.Servlets;

import com.google.common.collect.Maps;

/**
 * 基于POI导出Excel文件的Controller.
 * 
 * @author calvin
 */
@Controller
public class ExcelExportController {

	private Map<String, CellStyle> styles;
	private int rowIndex = 0;

	/**
	 * 生成Excel格式的内容.
	 */
	@RequestMapping(value = "/excel/export")
	public void export(HttpServletResponse response) throws Exception {
		//生成Excel文件.
		Workbook wb = exportExcelWorkbook();

		//输出Excel文件.
		response.setContentType(Servlets.EXCEL_TYPE);
		Servlets.setFileDownloadHeader(response, "温度年表.xls");

		wb.write(response.getOutputStream());
		response.getOutputStream().flush();
	}

	private Workbook exportExcelWorkbook() {
		TemperatureAnomaly[] temperatureAnomalyArray = DummyDataGenerator.getDummyData();

		//创建Workbook
		Workbook wb = new HSSFWorkbook();

		//创建所有Cell Style
		createStyles(wb);

		//创建工作表.
		Sheet s = wb.createSheet("1970-1999");

		//设定冻结表头
		s.createFreezePane(0, 2, 0, 2);

		//设定所有Column宽度自动配合内容宽度
		s.autoSizeColumn(0);
		s.autoSizeColumn(1);
		s.autoSizeColumn(2);

		//产生标题
		generateTitle(s);
		//产生表头
		generateHeader(s);
		//产生内容
		generateContent(s, temperatureAnomalyArray);
		//产生合计
		generateTotals(s);

		return wb;
	}

	private void generateTitle(Sheet s) {
		Row r = s.createRow(rowIndex++);
		Cell c1 = r.createCell(0);
		c1.setCellValue("Temperature Anomaly(1970-1999)");
		c1.setCellStyle(styles.get("header"));
		//合并单元格
		s.addMergedRegion(CellRangeAddress.valueOf("$A$1:$C$1"));
	}

	private void generateHeader(Sheet s) {

		Row r = s.createRow(rowIndex++);
		CellStyle headerStyle = styles.get("header");

		Cell c1 = r.createCell(0);
		c1.setCellValue("Year");
		c1.setCellStyle(headerStyle);

		Cell c2 = r.createCell(1);
		c2.setCellValue("Anomaly");
		c2.setCellStyle(headerStyle);

		Cell c3 = r.createCell(2);
		c3.setCellValue("Smoothed");
		c3.setCellStyle(headerStyle);
	}

	private void generateContent(Sheet s, TemperatureAnomaly[] temperatureAnomalys) {
		CellStyle dateCellStyle = styles.get("dateCell");
		CellStyle numberCellStyle = styles.get("numberCell");

		for (TemperatureAnomaly temperatureAnomaly : temperatureAnomalys) {
			Row r = s.createRow(rowIndex++);

			Cell c1 = r.createCell(0);
			c1.setCellValue(new DateTime(temperatureAnomaly.getYear(), 1, 1, 0, 0, 0, 0).toDate());
			c1.setCellStyle(dateCellStyle);

			Cell c2 = r.createCell(1);
			c2.setCellValue(temperatureAnomaly.getAnomaly());
			c2.setCellStyle(numberCellStyle);

			Cell c3 = r.createCell(2);
			c3.setCellValue(temperatureAnomaly.getSmoothed());
			c3.setCellStyle(numberCellStyle);
		}
	}

	private void generateTotals(Sheet s) {

		Row r = s.createRow(rowIndex++);
		CellStyle totalStyle = styles.get("total");

		//Cell强行分行
		Cell c1 = r.createCell(0);
		c1.setCellStyle(totalStyle);
		c1.setCellValue("合\n计");

		//合计公式
		Cell c2 = r.createCell(1);
		c2.setCellStyle(totalStyle);
		c2.setCellFormula("SUM(B3:B32)");

		Cell c3 = r.createCell(2);
		c3.setCellStyle(totalStyle);
		c3.setCellFormula("SUM(C3:C32)");
	}

	private Map<String, CellStyle> createStyles(Workbook wb) {
		styles = Maps.newHashMap();
		DataFormat df = wb.createDataFormat();

		// --字体设定 --//

		//普通字体
		Font normalFont = wb.createFont();
		normalFont.setFontHeightInPoints((short) 10);

		//加粗字体
		Font boldFont = wb.createFont();
		boldFont.setFontHeightInPoints((short) 10);
		boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);

		//蓝色加粗字体
		Font blueBoldFont = wb.createFont();
		blueBoldFont.setFontHeightInPoints((short) 10);
		blueBoldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		blueBoldFont.setColor(IndexedColors.BLUE.getIndex());

		// --Cell Style设定-- //

		//标题格式
		CellStyle headerStyle = wb.createCellStyle();
		headerStyle.setFont(boldFont);
		styles.put("header", headerStyle);

		//日期格式
		CellStyle dateCellStyle = wb.createCellStyle();
		dateCellStyle.setFont(normalFont);
		dateCellStyle.setDataFormat(df.getFormat("yyyy"));
		setBorder(dateCellStyle);
		styles.put("dateCell", dateCellStyle);

		//数字格式
		CellStyle numberCellStyle = wb.createCellStyle();
		numberCellStyle.setFont(normalFont);
		numberCellStyle.setDataFormat(df.getFormat("#,##0.00"));
		setBorder(numberCellStyle);
		styles.put("numberCell", numberCellStyle);

		//合计列格式
		CellStyle totalStyle = wb.createCellStyle();
		totalStyle.setFont(blueBoldFont);
		totalStyle.setWrapText(true);
		totalStyle.setAlignment(CellStyle.ALIGN_RIGHT);
		setBorder(totalStyle);
		styles.put("total", totalStyle);

		return styles;
	}

	private void setBorder(CellStyle style) {
		//设置边框
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());

		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());

		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());

		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
	}
}
