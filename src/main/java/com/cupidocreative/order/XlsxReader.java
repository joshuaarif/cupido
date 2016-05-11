package com.cupidocreative.order;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cupidocreative.domain.CCPurchaseOrder;
import com.google.common.collect.Sets;

public class XlsxReader {

	private static final Log LOG = LogFactory.getLog(XlsxReader.class);

	private Object getCellValue(Cell cell) {
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			return cell.getStringCellValue();

		case Cell.CELL_TYPE_BOOLEAN:
			return cell.getBooleanCellValue();

		case Cell.CELL_TYPE_NUMERIC:
			return cell.getNumericCellValue();
		}

		return null;
	}

	private Workbook getWorkbook(FileInputStream inputStream, String excelFilePath) throws IOException {
		Workbook workbook = null;

		if (excelFilePath.toLowerCase().endsWith("xlsx")) {
			workbook = new XSSFWorkbook(inputStream);
		} else if (excelFilePath.toLowerCase().endsWith("xls")) {
			workbook = new HSSFWorkbook(inputStream);
		} else {
			throw new IllegalArgumentException("The specified file is not Excel file");
		}

		return workbook;
	}

	public Set<CCPurchaseOrder> readOrderFromExcel(String excelFilePath) {
		Set<CCPurchaseOrder> result = Sets.newLinkedHashSet();

		try (FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
				Workbook workbook = getWorkbook(inputStream, excelFilePath)) {
			Sheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = firstSheet.iterator();

			// skip header
			iterator.next();

			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				CCPurchaseOrder order = new CCPurchaseOrder();

				while (cellIterator.hasNext()) {
					Cell nextCell = cellIterator.next();
					int columnIndex = nextCell.getColumnIndex();

					switch (columnIndex) {
					case 0:
						order.setEmail((String) getCellValue(nextCell));
						break;
					case 1:
						order.setWorkbookCode((String) getCellValue(nextCell));
						break;
					case 2:
						Double cellValue = (double) getCellValue(nextCell);
						int intCellValue = cellValue.intValue();
						order.setWorkbookSize(intCellValue);
						break;
					case 3:
						order.setPoNumber((String) getCellValue(nextCell));
						break;
					}
				}

				result.add(order);
			}
		} catch (IOException e) {
			LOG.error("Error read order from excel : " + e.getMessage());
		}

		return result;
	}

}
