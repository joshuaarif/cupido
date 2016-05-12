package com.cupidocreative.order;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cupidocreative.domain.PurchaseOrderDtl;
import com.cupidocreative.domain.PurchaseOrderHdr;
import com.google.api.client.util.Maps;
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

	public Set<PurchaseOrderHdr> readOrderFromExcel(String excelFilePath) {
		Map<String, PurchaseOrderHdr> mapOrders = Maps.newHashMap();

		try (FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
				Workbook workbook = getWorkbook(inputStream, excelFilePath)) {
			Sheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = firstSheet.iterator();

			// skip header
			iterator.next();

			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				PurchaseOrderHdr order = null;
				PurchaseOrderDtl orderDtl = new PurchaseOrderDtl();

				while (cellIterator.hasNext()) {
					Cell nextCell = cellIterator.next();
					int columnIndex = nextCell.getColumnIndex();

					switch (columnIndex) {
					// first column must be PO number
					case 0:
						String poNumber = (String) getCellValue(nextCell);

						if (mapOrders.containsKey(poNumber)) {
							order = mapOrders.get(poNumber);
						} else {
							order = new PurchaseOrderHdr();
							order.setPoNumber(poNumber);
							mapOrders.put(poNumber, order);
						}
						orderDtl.setPoHeader(order);
						break;
					case 1:
						if (order != null) {
							order.setEmail((String) getCellValue(nextCell));
						}
						break;
					case 2:
						orderDtl.setWorkbookCode((String) getCellValue(nextCell));
						break;
					case 3:
						Double cellValue = (double) getCellValue(nextCell);
						int intCellValue = cellValue.intValue();
						orderDtl.setWorkbookSize(intCellValue);
						break;
					}
				}
				order.addOrderDetail(orderDtl);
				System.out.println(order);
			}
		} catch (IOException e) {
			LOG.error("Error read order from excel : " + e.getMessage());
		}

		return Sets.newLinkedHashSet(mapOrders.values());
	}

}
