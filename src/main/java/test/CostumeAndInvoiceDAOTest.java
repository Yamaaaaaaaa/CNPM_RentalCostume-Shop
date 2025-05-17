package test;

import dao.CostumeDAO;
import dao.InvoiceDAO;
import model.Costume;
import model.Invoice;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CostumeAndInvoiceDAOTest {

    private CostumeDAO costumeDAO;
    private InvoiceDAO invoiceDAO;

    @Before
    public void setUp() {
        costumeDAO = new CostumeDAO();
        invoiceDAO = new InvoiceDAO();
    }

    // ===== Tests for getPopularCostumeInRange =====

    /**
     * Test case 1: Ngày bắt đầu và ngày kết thúc hợp lệ (trong phạm vi có dữ liệu)
     * Kỳ vọng: Trả về danh sách trang phục không rỗng
     */
    @Test
    public void testGetPopularCostumeInRange_ValidDateRange() {
        // Chuẩn bị dữ liệu
        Date startDate = createDate(2020, 1, 1);
        Date endDate = createDate(2023, 12, 31);

        // Thực thi phương thức cần kiểm thử
        Map<Costume, Integer> result = costumeDAO.getPopularCostumeInRange(startDate, endDate);

        // Kiểm tra kết quả
        assertNotNull("Kết quả không được null", result);
        assertFalse("Danh sách trang phục không được rỗng", result.isEmpty());
    }

    /**
     * Test case 2: Ngày bắt đầu bằng ngày kết thúc (biên)
     * Kỳ vọng: Có thể trả về danh sách rỗng hoặc có dữ liệu nếu có hóa đơn vào đúng ngày đó
     */
    @Test
    public void testGetPopularCostumeInRange_SameDay() {
        // Chuẩn bị dữ liệu
        Date sameDay = createDate(2023, 6, 15);

        // Thực thi phương thức cần kiểm thử
        Map<Costume, Integer> result = costumeDAO.getPopularCostumeInRange(sameDay, sameDay);

        // Kiểm tra kết quả
        assertNotNull("Kết quả không được null", result);
        // Không kiểm tra isEmpty vì có thể có hoặc không có dữ liệu vào ngày đó
    }

    /**
     * Test case 3: Ngày bắt đầu sau ngày kết thúc (không hợp lệ)
     * Kỳ vọng: Trả về null hoặc map rỗng (tùy vào cách xử lý lỗi của DAO)
     */
    @Test
    public void testGetPopularCostumeInRange_StartDateAfterEndDate() {
        // Chuẩn bị dữ liệu
        Date startDate = createDate(2023, 12, 31);
        Date endDate = createDate(2023, 1, 1);

        // Thực thi phương thức cần kiểm thử
        Map<Costume, Integer> result = costumeDAO.getPopularCostumeInRange(startDate, endDate);

        // Kiểm tra kết quả - tùy thuộc vào cách xử lý lỗi của DAO
        if (result != null) {
            assertTrue("Danh sách phải rỗng khi ngày bắt đầu sau ngày kết thúc", result.isEmpty());
        }
    }

    /**
     * Test case 4: Khoảng thời gian quá khứ xa (không có dữ liệu)
     * Kỳ vọng: Trả về map rỗng
     */
    @Test
    public void testGetPopularCostumeInRange_FarPastDateRange() {
        // Chuẩn bị dữ liệu
        Date startDate = createDate(1900, 1, 1);
        Date endDate = createDate(1900, 12, 31);

        // Thực thi phương thức cần kiểm thử
        Map<Costume, Integer> result = costumeDAO.getPopularCostumeInRange(startDate, endDate);

        // Kiểm tra kết quả
        assertNotNull("Kết quả không được null", result);
        assertTrue("Danh sách phải rỗng với khoảng thời gian quá khứ xa", result.isEmpty());
    }

    /**
     * Test case 5: Khoảng thời gian tương lai xa (không có dữ liệu)
     * Kỳ vọng: Trả về map rỗng
     */
    @Test
    public void testGetPopularCostumeInRange_FarFutureDateRange() {
        // Chuẩn bị dữ liệu
        Date startDate = createDate(2100, 1, 1);
        Date endDate = createDate(2100, 12, 31);

        // Thực thi phương thức cần kiểm thử
        Map<Costume, Integer> result = costumeDAO.getPopularCostumeInRange(startDate, endDate);

        // Kiểm tra kết quả
        assertNotNull("Kết quả không được null", result);
        assertTrue("Danh sách phải rỗng với khoảng thời gian tương lai xa", result.isEmpty());
    }

    // ===== Tests for getInvoiceRentedCostume =====

    /**
     * Test case 1: ID trang phục hợp lệ và khoảng thời gian hợp lệ
     * Kỳ vọng: Trả về danh sách hóa đơn không rỗng
     */
    @Test
    public void testGetInvoiceRentedCostume_ValidCostumeIdAndDateRange() {
        // Chuẩn bị dữ liệu - giả sử ID 1 là một trang phục có tồn tại
        int costumeId = 1;
        Date startDate = createDate(2023, 1, 1);
        Date endDate = createDate(2023, 12, 31);

        // Thực thi phương thức cần kiểm thử
        List<Invoice> result = invoiceDAO.getInvoiceRentedCostume(costumeId, startDate, endDate);

        // Kiểm tra kết quả
        assertNotNull("Kết quả không được null", result);
        // Không kiểm tra isEmpty vì có thể có hoặc không có hóa đơn cho trang phục này
    }

    /**
     * Test case 2: ID trang phục không tồn tại
     * Kỳ vọng: Trả về danh sách rỗng
     */
    @Test
    public void testGetInvoiceRentedCostume_NonExistentCostumeId() {
        // Chuẩn bị dữ liệu - giả sử ID -999 là một trang phục không tồn tại
        int nonExistentCostumeId = -999;
        Date startDate = createDate(2023, 1, 1);
        Date endDate = createDate(2023, 12, 31);

        // Thực thi phương thức cần kiểm thử
        List<Invoice> result = invoiceDAO.getInvoiceRentedCostume(nonExistentCostumeId, startDate, endDate);

        // Kiểm tra kết quả
        assertNotNull("Kết quả không được null", result);
        assertTrue("Danh sách phải rỗng với ID trang phục không tồn tại", result.isEmpty());
    }

    /**
     * Test case 3: ID trang phục là giá trị biên (0)
     * Kỳ vọng: Trả về danh sách rỗng (giả sử ID 0 không hợp lệ)
     */
    @Test
    public void testGetInvoiceRentedCostume_ZeroCostumeId() {
        // Chuẩn bị dữ liệu
        int zeroCostumeId = 0;
        Date startDate = createDate(2023, 1, 1);
        Date endDate = createDate(2023, 12, 31);

        // Thực thi phương thức cần kiểm thử
        List<Invoice> result = invoiceDAO.getInvoiceRentedCostume(zeroCostumeId, startDate, endDate);

        // Kiểm tra kết quả
        assertNotNull("Kết quả không được null", result);
        assertTrue("Danh sách phải rỗng với ID trang phục bằng 0", result.isEmpty());
    }

    /**
     * Test case 4: ID trang phục là giá trị âm (không hợp lệ)
     * Kỳ vọng: Trả về danh sách rỗng hoặc null (tùy vào cách xử lý lỗi của DAO)
     */
    @Test
    public void testGetInvoiceRentedCostume_NegativeCostumeId() {
        // Chuẩn bị dữ liệu
        int negativeCostumeId = -1;
        Date startDate = createDate(2023, 1, 1);
        Date endDate = createDate(2023, 12, 31);

        // Thực thi phương thức cần kiểm thử
        List<Invoice> result = invoiceDAO.getInvoiceRentedCostume(negativeCostumeId, startDate, endDate);

        // Kiểm tra kết quả
        if (result != null) {
            assertTrue("Danh sách phải rỗng với ID trang phục âm", result.isEmpty());
        }
    }

    /**
     * Test case 5: Ngày bắt đầu sau ngày kết thúc (không hợp lệ)
     * Kỳ vọng: Trả về danh sách rỗng hoặc null (tùy vào cách xử lý lỗi của DAO)
     */
    @Test
    public void testGetInvoiceRentedCostume_StartDateAfterEndDate() {
        // Chuẩn bị dữ liệu
        int costumeId = 1;
        Date startDate = createDate(2023, 12, 31);
        Date endDate = createDate(2023, 1, 1);

        // Thực thi phương thức cần kiểm thử
        List<Invoice> result = invoiceDAO.getInvoiceRentedCostume(costumeId, startDate, endDate);

        // Kiểm tra kết quả
        if (result != null) {
            assertTrue("Danh sách phải rỗng khi ngày bắt đầu sau ngày kết thúc", result.isEmpty());
        }
    }

    /**
     * Test case 6: Ngày bắt đầu bằng ngày kết thúc (biên)
     * Kỳ vọng: Có thể trả về danh sách rỗng hoặc có dữ liệu nếu có hóa đơn vào đúng ngày đó
     */
    @Test
    public void testGetInvoiceRentedCostume_SameDay() {
        // Chuẩn bị dữ liệu
        int costumeId = 1;
        Date sameDay = createDate(2023, 6, 15);

        // Thực thi phương thức cần kiểm thử
        List<Invoice> result = invoiceDAO.getInvoiceRentedCostume(costumeId, sameDay, sameDay);

        // Kiểm tra kết quả
        assertNotNull("Kết quả không được null", result);
        // Không kiểm tra isEmpty vì có thể có hoặc không có hóa đơn vào ngày đó
    }

    /**
     * Test case 7: Khoảng thời gian quá khứ xa (không có dữ liệu)
     * Kỳ vọng: Trả về danh sách rỗng
     */
    @Test
    public void testGetInvoiceRentedCostume_FarPastDateRange() {
        // Chuẩn bị dữ liệu
        int costumeId = 1;
        Date startDate = createDate(1900, 1, 1);
        Date endDate = createDate(1900, 12, 31);

        // Thực thi phương thức cần kiểm thử
        List<Invoice> result = invoiceDAO.getInvoiceRentedCostume(costumeId, startDate, endDate);

        // Kiểm tra kết quả
        assertNotNull("Kết quả không được null", result);
        assertTrue("Danh sách phải rỗng với khoảng thời gian quá khứ xa", result.isEmpty());
    }

    // Phương thức tiện ích để tạo đối tượng Date
    private Date createDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}