package com.hotel.service; // 租借資料隔離專用測試。
import org.junit.jupiter.api.*; // 使用既有 JUnit 測試依賴。
import static org.junit.jupiter.api.Assertions.*; // 驗證回應與資料隔離。
import static org.mockito.Mockito.*; // 只用記憶體模擬，不新增帳號。
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; // 透過 HTTP 路由測試權限。
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*; // 驗證指定 HTTP 狀態。
import java.util.*; // 建立既有角色與租借集合。
import java.time.*; // 整日租借邊界測試。
import com.hotel.controller.RentalController; // 受測租借控制器。
import com.hotel.controller.advice.GlobalExceptionHandler; // 包含現有共用錯誤處理以確認不會誤變五百。
import com.hotel.model.entity.Rental; // 使用既有租借資料型別。
import com.hotel.model.entity.Venue; // 模擬真實場地容量與狀態。
import com.hotel.repository.RentalRepository; // 模擬查詢結果。
import com.hotel.repository.VenueRepository; // 不修改實際場地資料。
import com.hotel.dto.RentalCreateRequest; // 前端允許輸入的四個欄位。
import org.springframework.jdbc.core.JdbcTemplate; // 模擬既有資料庫連線。
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // 模擬既有登入者。
import org.springframework.security.core.authority.SimpleGrantedAuthority; // 使用現有角色名稱。
import org.springframework.test.web.servlet.MockMvc; // 不啟動真實資料庫的 HTTP 測試。
import org.springframework.test.web.servlet.setup.MockMvcBuilders; // 建立真實控制器測試環境。
class RentalAccessTest { // 驗證前後台與日期規則。
    private RentalRepository repository; // 模擬租借資料存取。
    private VenueRepository venues; // 模擬場地資料。
    private RentalService service; // 保留真實權限與日期邏輯。
    private MockMvc mvc; // 透過 HTTP 介面驗證權限。
    private final UsernamePasswordAuthenticationToken customer=new UsernamePasswordAuthenticationToken("customer01",null,List.of(new SimpleGrantedAuthority("ROLE_MEMBER"))); // 既有會員一。
    private final UsernamePasswordAuthenticationToken customer2=new UsernamePasswordAuthenticationToken("customer02",null,List.of(new SimpleGrantedAuthority("ROLE_MEMBER"))); // 既有會員二。
    private final UsernamePasswordAuthenticationToken admin=new UsernamePasswordAuthenticationToken("admin01",null,List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"),new SimpleGrantedAuthority("POSITION_總經理"))); // 既有最高權限，不假設管理角色。
    @BeforeEach void setup() { // 每個測試隔離模擬狀態。
        repository=mock(RentalRepository.class); venues=mock(VenueRepository.class); // 不建立假資料庫資料。
        service=spy(new RentalService(repository,venues,mock(JdbcTemplate.class))); // 權限與驗證使用真實服務邏輯。
        doReturn(1).when(service).resolveMemberId("customer01"); doReturn(2).when(service).resolveMemberId("customer02"); // 只模擬既有帳號關聯查詢。
        mvc=MockMvcBuilders.standaloneSetup(new RentalController(service)).setControllerAdvice(new GlobalExceptionHandler()).build(); // 包含共用錯誤處理驗證四零三不變五百。
    }
    private Rental rental(int id,int member) { // 建立只存在記憶體的測試紀錄。
        var rental=new Rental(); rental.setRentalId(id); rental.setMemberId(member); rental.setVenueId(1); rental.setPaymentId(id); rental.setRentalDate(LocalDate.now().plusDays(3).atTime(18,0)); rental.setRentalStatus("PENDING"); // 舊非零時資料仍須占用整天。
        return rental; // 回傳供權限或日期測試使用。
    }
    @Test void memberCannotUseManagementEndpoints() throws Exception { // 所有管理入口先驗證權限。
        mvc.perform(get("/api/rentals").principal(customer)).andExpect(status().isForbidden()); // 會員不能讀全部紀錄。
        mvc.perform(put("/api/rentals/2").principal(customer).contentType("application/json").content("{}")).andExpect(status().isForbidden()); // 會員不能修改別人紀錄。
        mvc.perform(delete("/api/rentals/2").principal(customer)).andExpect(status().isForbidden()); // 會員不能刪除別人紀錄。
        verifyNoInteractions(repository); // 拒絕發生在讀寫資料之前。
    }
    @Test void singleRentalChecksOwnerAndNotFound() throws Exception { // 查詢存在與所有權分開判斷。
        when(repository.findById(2)).thenReturn(Optional.of(rental(2,2))); when(repository.findById(99)).thenReturn(Optional.empty()); // 模擬另一會員與不存在資料。
        mvc.perform(get("/api/rentals/2").principal(customer)).andExpect(status().isForbidden()); // 會員一不能讀會員二。
        mvc.perform(get("/api/rentals/2").principal(customer2)).andExpect(status().isOk()); // 本人可讀。
        mvc.perform(get("/api/rentals/99").principal(customer)).andExpect(status().isNotFound()); // 不存在回傳四零四。
        mvc.perform(get("/api/rentals/2").principal(admin)).andExpect(status().isOk()); // 總經理可查管理資料。
    }
    @Test void mineUsesEachMembersId() throws Exception { // 兩位會員使用各自資料查詢。
        when(repository.findByMemberIdOrderByRentalDateDesc(1)).thenReturn(List.of(rental(1,1))); when(repository.findByMemberIdOrderByRentalDateDesc(2)).thenReturn(List.of(rental(2,2))); // 模擬隔離紀錄。
        var one=mvc.perform(get("/api/rentals/mine").principal(customer)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString(); // 會員一查自己的資料。
        var two=mvc.perform(get("/api/rentals/mine").principal(customer2)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString(); // 會員二查自己的資料。
        assertTrue(one.contains("\"memberId\":1")); assertFalse(one.contains("\"memberId\":2")); assertTrue(two.contains("\"memberId\":2")); // 驗證回應沒有混入別人資料。
    }
    @Test void actualManagerCanList() throws Exception { // 使用實際權限組合而非新角色。
        when(repository.findAll()).thenReturn(List.of()); // 管理列表可以為空。
        mvc.perform(get("/api/rentals").principal(admin)).andExpect(status().isOk()); // 總經理權限可使用管理 API。
        verify(repository).findAll(); // 確實進入管理查詢。
    }
    @Test void actualManagerCanUpdateAndDelete() throws Exception { // 管理員保留既有修改與刪除入口。
        when(repository.findById(2)).thenReturn(Optional.of(rental(2,2))); // 模擬存在的租借。
        doReturn(rental(2,2)).when(service).update(any(Rental.class)); doReturn(true).when(service).deleteById(2); // 不真正修改或刪除資料。
        mvc.perform(put("/api/rentals/2").principal(admin).contentType("application/json").content("{}")).andExpect(status().isOk()); // 總經理可進入更新流程。
        mvc.perform(delete("/api/rentals/2").principal(admin)).andExpect(status().isOk()); // 總經理可進入刪除流程。
    }
    @Test void occupiedDoesNotExposePrivateFields() throws Exception { // 占用 API 僅公開日期資訊。
        var day=LocalDate.now().plusDays(3); when(repository.findOccupied(1,day.atStartOfDay(),day.plusDays(1).atStartOfDay())).thenReturn(List.of(rental(2,2))); // 使用完整租借作為底層查詢結果。
        var body=mvc.perform(get("/api/rentals/occupied-dates").param("venueId","1").param("from",day.toString()).param("to",day.toString()).principal(customer)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString(); // 由控制器轉為最小 DTO。
        assertTrue(body.contains("\"occupied\":true")); for (String field:List.of("memberId","paymentId","eventName","email","rentalId")) assertFalse(body.contains(field)); // 不公開私人欄位。
    }
    @Test void sameDayDifferentTimeIsRejected() { // 新預約與舊非零時資料仍撞期。
        var day=LocalDate.now().plusDays(3); var venue=new Venue(); venue.setVenueId(1); venue.setCapacity(100); venue.setPricePerDay(5000); venue.setVenueStatus("AVAILABLE"); // 模擬既有可租場地。
        when(venues.findById(1)).thenReturn(Optional.of(venue)); when(repository.findOccupied(1,day.atStartOfDay(),day.plusDays(1).atStartOfDay())).thenReturn(List.of(rental(2,2))); // 相同日期晚間已被占用。
        assertThrows(IllegalArgumentException.class,()->service.createForCurrentUser(new RentalCreateRequest(1,"會議",day.atTime(9,0),10),"customer01")); // 早上不能再次預約整日場地。
        verify(repository).findOccupied(1,day.atStartOfDay(),day.plusDays(1).atStartOfDay()); // 確認使用完整日期範圍。
    }
    @Test void pastDateRejectedBeforeMemberLookup() { // 日期驗證不只存在前端。
        assertThrows(IllegalArgumentException.class,()->service.createForCurrentUser(new RentalCreateRequest(1,"會議",LocalDate.now().minusDays(1).atStartOfDay(),10),"customer01")); // 過去日期不能建立租借。
        verifyNoInteractions(repository,venues); // 拒絕時不寫入任何資料。
    }
}
