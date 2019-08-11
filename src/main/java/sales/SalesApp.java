package sales;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SalesApp {
	private SalesDao salesDao;
	private SalesReportDao salesReportDao;
	private EcmService ecmService;

	public void generateSalesActivityReport(String salesId, int maxRow, boolean isNatTrade, boolean isSupervisor) {
		
//		SalesDao salesDao = new SalesDao();
//		SalesReportDao salesReportDao = new SalesReportDao();
		List<String> headers = null;
		
		List<SalesReportData> filteredReportDataList = new ArrayList<SalesReportData>();

		Sales sales = checkSaleId(salesId, salesDao);

		if (sales == null) return;

		List<SalesReportData> reportDataList =
				getSalesReportData(isSupervisor, salesReportDao, filteredReportDataList, sales);
		
		List<SalesReportData> tempList = new ArrayList<SalesReportData>();
		for (int i=0; i < reportDataList.size() && i < maxRow; i++) {
			tempList.add(reportDataList.get(i));
		}
		filteredReportDataList = tempList;

		headers = setHeadersByNatTrade(isNatTrade);

		SalesActivityReport report = this.generateReport(headers, reportDataList);

//		EcmService ecmService = new EcmService();
		transferReportToXml(report, ecmService);
		
	}

	protected void transferReportToXml(SalesActivityReport report, EcmService ecmService) {
		ecmService.uploadDocument(report.toXml());
	}

	protected List<String> setHeadersByNatTrade(boolean isNatTrade) {
		List<String> headers;
		if (isNatTrade) {
			headers = Arrays.asList("Sales ID", "Sales Name", "Activity", "Time");
		} else {
			headers = Arrays.asList("Sales ID", "Sales Name", "Activity", "Local Time");
		}
		return headers;
	}

	protected List<SalesReportData> getSalesReportData(boolean isSupervisor, SalesReportDao salesReportDao, List<SalesReportData> filteredReportDataList, Sales sales) {
		List<SalesReportData> reportDataList = salesReportDao.getReportData(sales);

		for (SalesReportData data : reportDataList) {
			if ("SalesActivity".equalsIgnoreCase(data.getType())) {
				if (data.isConfidential()) {
					if (isSupervisor) {
						filteredReportDataList.add(data);
					}
				}else {
					filteredReportDataList.add(data);
				}
			}
		}
		return reportDataList;
	}

	protected Sales checkSaleId(String salesId, SalesDao salesDao) {
		if (salesId == null) {
			return null;
		}

		Sales sales = salesDao.getSalesBySalesId(salesId);

		Date today = new Date();
		if (today.after(sales.getEffectiveTo())
				|| today.before(sales.getEffectiveFrom())){
			return null;
		}
		return sales;
	}

	protected SalesActivityReport generateReport(List<String> headers, List<SalesReportData> reportDataList) {
		// TODO Auto-generated method stub
		return null;
	}

}
