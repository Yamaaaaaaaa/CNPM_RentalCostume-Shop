import dao.CostumeDAO;
import dao.InvoiceDAO;
import model.Costume;
import model.Invoice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CostumeAndInvoiceDAOTest {

    private CostumeDAO costumeDAO;
    private InvoiceDAO invoiceDAO;

    @BeforeEach
    public void setUp() {
        costumeDAO = new CostumeDAO();
        invoiceDAO = new InvoiceDAO();
    }

    // ===== Helper =====
    private Date createDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    // ===== Tests for getPopularCostumeInRange =====

    @Test
    public void testGetPopularCostumeInRange_ValidDateRange() {
        Date startDate = createDate(2020, 1, 1);
        Date endDate = createDate(2023, 12, 31); // Cho cái này về tháng 1, ngày 31 là có thể ra lỗi ngay
        Map<Costume, Integer> result = costumeDAO.getPopularCostumeInRange(startDate, endDate);
        assertNotNull(result, "Kết quả không được null");
        assertFalse(result.isEmpty(), "Danh sách trang phục không được rỗng");
    }

    @Test
    public void testGetPopularCostumeInRange_SameDay() {
        Date sameDay = createDate(2023, 6, 15);
        Map<Costume, Integer> result = costumeDAO.getPopularCostumeInRange(sameDay, sameDay);
        assertNotNull(result, "Kết quả không được null");
    }

    @Test
    public void testGetPopularCostumeInRange_StartDateAfterEndDate() {
        Date startDate = createDate(2023, 12, 31);
        Date endDate = createDate(2023, 1, 1);
        Map<Costume, Integer> result = costumeDAO.getPopularCostumeInRange(startDate, endDate);
        if (result != null) {
            assertTrue(result.isEmpty(), "Danh sách phải rỗng khi ngày bắt đầu sau ngày kết thúc");
        }
    }

    @Test
    public void testGetPopularCostumeInRange_FarPastDateRange() {
        Date startDate = createDate(1900, 1, 1);
        Date endDate = createDate(1900, 12, 31);
        Map<Costume, Integer> result = costumeDAO.getPopularCostumeInRange(startDate, endDate);
        assertNotNull(result, "Kết quả không được null");
        assertTrue(result.isEmpty(), "Danh sách phải rỗng với khoảng thời gian quá khứ xa");
    }

    @Test
    public void testGetPopularCostumeInRange_FarFutureDateRange() {
        Date startDate = createDate(2100, 1, 1);
        Date endDate = createDate(2100, 12, 31);
        Map<Costume, Integer> result = costumeDAO.getPopularCostumeInRange(startDate, endDate);
        assertNotNull(result, "Kết quả không được null");
        assertTrue(result.isEmpty(), "Danh sách phải rỗng với khoảng thời gian tương lai xa");
    }

    // ===== Tests for getInvoiceRentedCostume =====

    @Test
    public void testGetInvoiceRentedCostume_ValidCostumeIdAndDateRange() {
        int costumeId = 1;
        Date startDate = createDate(2023, 1, 1);
        Date endDate = createDate(2023, 12, 31);
        List<Invoice> result = invoiceDAO.getInvoiceRentedCostume(costumeId, startDate, endDate);
        assertNotNull(result, "Kết quả không được null");
    }

    @Test
    public void testGetInvoiceRentedCostume_NonExistentCostumeId() {
        int nonExistentCostumeId = -999;
        Date startDate = createDate(2023, 1, 1);
        Date endDate = createDate(2023, 12, 31);
        List<Invoice> result = invoiceDAO.getInvoiceRentedCostume(nonExistentCostumeId, startDate, endDate);
        assertNotNull(result, "Kết quả không được null");
        assertTrue(result.isEmpty(), "Danh sách phải rỗng với ID trang phục không tồn tại");
    }

    @Test
    public void testGetInvoiceRentedCostume_ZeroCostumeId() {
        int zeroCostumeId = 0;
        Date startDate = createDate(2023, 1, 1);
        Date endDate = createDate(2023, 12, 31);
        List<Invoice> result = invoiceDAO.getInvoiceRentedCostume(zeroCostumeId, startDate, endDate);
        assertNotNull(result, "Kết quả không được null");
        assertTrue(result.isEmpty(), "Danh sách phải rỗng với ID trang phục bằng 0");
    }

    @Test
    public void testGetInvoiceRentedCostume_NegativeCostumeId() {
        int negativeCostumeId = -1;
        Date startDate = createDate(2023, 1, 1);
        Date endDate = createDate(2023, 12, 31);
        List<Invoice> result = invoiceDAO.getInvoiceRentedCostume(negativeCostumeId, startDate, endDate);
        if (result != null) {
            assertTrue(result.isEmpty(), "Danh sách phải rỗng với ID trang phục âm");
        }
    }

    @Test
    public void testGetInvoiceRentedCostume_StartDateAfterEndDate() {
        int costumeId = 1;
        Date startDate = createDate(2023, 12, 31);
        Date endDate = createDate(2023, 1, 1);
        List<Invoice> result = invoiceDAO.getInvoiceRentedCostume(costumeId, startDate, endDate);
        if (result != null) {
            assertTrue(result.isEmpty(), "Danh sách phải rỗng khi ngày bắt đầu sau ngày kết thúc");
        }
    }

    @Test
    public void testGetInvoiceRentedCostume_SameDay() {
        int costumeId = 1;
        Date sameDay = createDate(2023, 6, 15);
        List<Invoice> result = invoiceDAO.getInvoiceRentedCostume(costumeId, sameDay, sameDay);
        assertNotNull(result, "Kết quả không được null");
    }

    @Test
    public void testGetInvoiceRentedCostume_FarPastDateRange() {
        int costumeId = 1;
        Date startDate = createDate(1900, 1, 1);
        Date endDate = createDate(1900, 12, 31);
        List<Invoice> result = invoiceDAO.getInvoiceRentedCostume(costumeId, startDate, endDate);
        assertNotNull(result, "Kết quả không được null");
        assertTrue(result.isEmpty(), "Danh sách phải rỗng với khoảng thời gian quá khứ xa");
    }
}
