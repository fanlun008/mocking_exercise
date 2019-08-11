package sales;

import mock.MockSystem;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(JMockit.class)
public class SalesAppTest {

	@org.mockito.Mock
	SalesDao salesDao;
	@org.mockito.Mock
	SalesReportDao salesReportDao;
	@org.mockito.Mock
	EcmService ecmService;
	@InjectMocks
	SalesApp salesApp = new SalesApp();

	@Before
	public void setup() throws ParseException {
		MockitoAnnotations.initMocks(this);

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date effFrom = df.parse("2019-08-01");
		Date effTo = df.parse("2019-08-29");

		new MockUp<System>() {
			@Mock
			public long currentTimeMillis() {
				Calendar instance = Calendar.getInstance();
				instance.set(2019, 7, 3);
				return instance.getTimeInMillis();
			}
		};
		Sales mockSales = mock(Sales.class);

		when(mockSales.getEffectiveFrom()).thenReturn(effFrom);
		when(mockSales.getEffectiveTo()).thenReturn(effTo);
		when(salesDao.getSalesBySalesId(anyString())).thenReturn(mockSales);

		SalesReportData mockReportData = mock(SalesReportData.class);
		when(mockReportData.getType()).thenReturn("SalesActivity");
		when(mockReportData.isConfidential()).thenReturn(true);

		List<SalesReportData> salesReportDataList = new ArrayList<>();
		salesReportDataList.add(mockReportData);
		when(salesReportDao.getReportData(any())).thenReturn(salesReportDataList);
	}

	@Test
	public void testGenerateReport() {
		SalesApp spySalesApp = spy(salesApp);

		doReturn(new SalesActivityReport()).when(spySalesApp).generateReport(any(), any());
//		SalesApp salesApp = new SalesApp();
		spySalesApp.generateSalesActivityReport("DUMMY", 1000, false, false);
		verify(spySalesApp,times(1)).checkSaleId(anyString(), any());
		verify(spySalesApp, times(1)).generateReport(any(), any());
		verify(spySalesApp).transferReportToXml(any(), any());
	}

	@Test
	public void testcheckSaleId_givenSaleIdAndSaleDao_thenGetSale() throws ParseException {
		//given
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date effFrom = df.parse("2019-08-01");
		Date effTo = df.parse("2019-08-29");

		new MockUp<System>() {
			@Mock
			public long currentTimeMillis() {
				Calendar instance = Calendar.getInstance();
				instance.set(2019, 7, 3);
				return instance.getTimeInMillis();
			}
		};
		System.out.println(new Date());

		SalesDao mockSaleDao = mock(SalesDao.class);
		Sales mockSales = mock(Sales.class);

		when(mockSales.getEffectiveFrom()).thenReturn(effFrom);
		when(mockSales.getEffectiveTo()).thenReturn(effTo);
		when(mockSaleDao.getSalesBySalesId(any())).thenReturn(mockSales);

		SalesApp salesApp = new SalesApp();
		//then
		Assert.assertNotNull(salesApp.checkSaleId("salesId", mockSaleDao));
	}

	@Test
	public void testgetSalesReportData_givenIsSupervisor_And_SalesReportDao_And_Sales(){
		//given
		boolean isSupervisor = true;
		SalesReportDao mockSalesReportDao = mock(SalesReportDao.class);
		SalesReportData mockReportData = mock(SalesReportData.class);
		when(mockReportData.getType()).thenReturn("SalesActivity");
		when(mockReportData.isConfidential()).thenReturn(true);

		SalesReportData mockReportData_2 = mock(SalesReportData.class);
		when(mockReportData_2.getType()).thenReturn("NoActivity");

		List<SalesReportData> salesReportDataList = new ArrayList<>();
		List<SalesReportData> filteredReportDataList = new ArrayList<>();
		salesReportDataList.add(mockReportData);
		salesReportDataList.add(mockReportData_2);

		when(mockSalesReportDao.getReportData(any())).thenReturn(salesReportDataList);

		//when
		SalesApp salesApp = new SalesApp();
		salesApp.getSalesReportData(isSupervisor, mockSalesReportDao, filteredReportDataList, new Sales());

		//then
		Assert.assertSame(1, filteredReportDataList.size());
	}

	@Test
	public void test_setHeaderByNatTradeTrue_givenIsNatTrade_thenContainLocalTime() {
		//given
		SalesApp salesApp = new SalesApp();
		boolean isNatTrade = false;
		//when
		List<String> headers = salesApp.setHeadersByNatTrade(isNatTrade);
		Assert.assertTrue(headers.contains("Local Time"));
	}

	@Test
	public void test_transferReportToXml() {
		//given
		EcmService spyEcmService = spy(new EcmService());
		SalesActivityReport spyActivityReport = spy(new SalesActivityReport());
		//when
		SalesApp salesApp = new SalesApp();
		salesApp.transferReportToXml(spyActivityReport, spyEcmService);
		//then
		verify(spyEcmService, times(1)).uploadDocument(any());
		verify(spyActivityReport, times(1)).toXml();
	}
}
