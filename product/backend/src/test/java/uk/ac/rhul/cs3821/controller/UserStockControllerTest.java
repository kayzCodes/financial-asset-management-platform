package uk.ac.rhul.cs3821.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.ac.rhul.cs3821.config.AuthContext;
import uk.ac.rhul.cs3821.dto.AddStockRequestDto;
import uk.ac.rhul.cs3821.dto.ChartPointDto;
import uk.ac.rhul.cs3821.dto.StockDetailsDto;
import uk.ac.rhul.cs3821.dto.StockTransactionRequestDto;
import uk.ac.rhul.cs3821.dto.UserDto;
import uk.ac.rhul.cs3821.dto.UserStockDto;
import uk.ac.rhul.cs3821.service.UserStockService;

public class UserStockControllerTest {

  private static final String firebaseUid = "Hn3iuGlnuxVjcgu6Rtb7WEKSncm1";
  StockTransactionRequestDto stockTransactionRequestDto;
  AddStockRequestDto addStockRequestDto;
  private MockMvc mockMvc;
  @Mock
  private AuthContext authContext;
  @Mock
  private UserStockService userStockService;
  @InjectMocks
  private UserStockController userStockController;
  private ObjectMapper objectMapper;
  private UserStockDto userStockDto;
  private UserDto userDto;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    ObjectMapper mapper = new ObjectMapper();
    mapper.findAndRegisterModules();

    mockMvc = MockMvcBuilders
            .standaloneSetup(userStockController)
            .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
            .build();

    when(authContext.getFirebaseUid())
            .thenReturn(firebaseUid);

    objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();

    LocalDateTime now = LocalDateTime.now();

    userDto = new UserDto(
            1L,
            firebaseUid,
            "Alice",
            "Johnson",
            "alicej",
            "alice.johnson@example.com",
            true,
            null, null, null, null, null,
            "USD",
            "en",
            "Europe/London",
            "standard",
            "system",
            now.minusDays(10),
            now.minusDays(1),
            null
    );

    userStockDto = new UserStockDto(
            1L,
            userDto,
            "AAPL",
            "Apple Inc.",
            BigDecimal.valueOf(10),
            "USD",
            BigDecimal.valueOf(150.25),
            null,
            null,
            false,
            null,
            null,
            null
    );

    // ---------- NEW DTOs ----------
    stockTransactionRequestDto = new StockTransactionRequestDto(
            BigDecimal.valueOf(150.25),
            BigDecimal.valueOf(10),
            now
    );

    addStockRequestDto = new AddStockRequestDto(
            userStockDto,
            stockTransactionRequestDto
    );
  }

  // POST /addStock — success + IF-statement logic tested here
  @Test
  void testAddStock_ReturnsCreatedStock() throws Exception {

    UserStockDto fixedDto = new UserStockDto(
            1L,
            userDto,
            "AAPL",
            "Apple Inc.",
            BigDecimal.valueOf(10),
            "USD",
            BigDecimal.valueOf(150.25),
            LocalDateTime.now(),
            LocalDateTime.now(),
            false,
            "",
            LocalDateTime.now(),
            LocalDateTime.now()
    );

    when(userStockService.addStock(
            eq(firebaseUid),
            any(UserStockDto.class),
            any(StockTransactionRequestDto.class)
    )).thenReturn(fixedDto);

    mockMvc.perform(post("/api/userStock/addStock")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(addStockRequestDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.tickerSymbol").value("AAPL"))
            .andExpect(jsonPath("$.companyName").value("Apple Inc."))
            .andExpect(jsonPath("$.quantity").value(10))

            .andExpect(jsonPath("$.createdAt").exists())
            .andExpect(jsonPath("$.updatedAt").exists())
            .andExpect(jsonPath("$.lastTransactionAt").exists())
            .andExpect(jsonPath("$.lastUpdatedPriceAt").exists())
            .andExpect(jsonPath("$.notes").value(""));
  }

  // GET /getStocks — simple list test
  @Test
  void testGetStocks_ReturnsStockList() throws Exception {

    when(userStockService.getAllUserStocksByFirebaseUid(firebaseUid))
            .thenReturn(java.util.List.of(userStockDto));

    mockMvc.perform(get("/api/userStock/getStocks")
                    .requestAttr("firebaseUid", firebaseUid))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].tickerSymbol").value("AAPL"))
            .andExpect(jsonPath("$[0].companyName").value("Apple Inc."));
  }

  // Test DELETE /api/userStock/deleteStock/{holdingId}
  @Test
  void testDeleteStock_ReturnsDeletedStock() throws Exception {

    Long holdingId = 10L;

    UserStockDto deletedStockDto = new UserStockDto(
            holdingId,
            null,
            "AAPL",
            "Apple Inc.",
            BigDecimal.valueOf(5),
            "USD",
            BigDecimal.valueOf(150),
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now(),
            true,
            "Sold position",
            LocalDateTime.now().minusDays(10),
            LocalDateTime.now()
    );

    when(userStockService.deleteStock(firebaseUid, holdingId))
            .thenReturn(deletedStockDto);

    mockMvc.perform(
                    delete("/api/userStock/deleteStock/{holdingId}", holdingId)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.holdingId").value(holdingId))
            .andExpect(jsonPath("$.tickerSymbol").value("AAPL"))
            .andExpect(jsonPath("$.companyName").value("Apple Inc."))
            .andExpect(jsonPath("$.quantity").value(5));

  }

  // Test GET /api/userStock/getLiveStockDetails/{holdingId}
  @Test
  void testGetLiveStockDetails_ReturnsStockDetails() throws Exception {

    Long holdingId = 10L;

    StockDetailsDto stockDetailsDto = new StockDetailsDto(
            "AAPL",
            "Apple Inc.",
            BigDecimal.valueOf(5),
            BigDecimal.valueOf(150),
            BigDecimal.valueOf(200),        // currentPrice
            BigDecimal.valueOf(2.5),        // percentageChange
            BigDecimal.valueOf(1000),       // currentValue
            BigDecimal.valueOf(250),        // priceDifference
            BigDecimal.valueOf(33.33),      // percentageChangeFromAveragePrice
            List.of(
                    new ChartPointDto("2024-01-01", BigDecimal.valueOf(190)),
                    new ChartPointDto("2024-01-02", BigDecimal.valueOf(200))
            ),
            Map.of(
                    "MarketCapitalization", "2500000000000",
                    "PERatio", "28.5"
            ),
            "Apple description"
    );

    when(userStockService.getStockDetail(firebaseUid, holdingId))
            .thenReturn(stockDetailsDto);

    mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .get("/api/userStock/getLiveStockDetails/{holdingId}", holdingId)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tickerSymbol").value("AAPL"))
            .andExpect(jsonPath("$.companyName").value("Apple Inc."))
            .andExpect(jsonPath("$.currentPrice").value(200))
            .andExpect(jsonPath("$.currentValue").value(1000))
            .andExpect(jsonPath("$.chartData.length()").value(2));
  }

  // Test PUT /api/userStock/updateStockBuy/{holdingId}
  @Test
  void testUpdateStockBuy_ReturnsUpdatedStock() throws Exception {

    Long holdingId = 10L;

    StockTransactionRequestDto request =
            new StockTransactionRequestDto(
                    BigDecimal.valueOf(160),
                    BigDecimal.valueOf(2),
                    LocalDateTime.now()
            );

    UserStockDto updatedStockDto = new UserStockDto(
            holdingId,
            null,
            "AAPL",
            "Apple Inc.",
            BigDecimal.valueOf(7),
            "USD",
            BigDecimal.valueOf(160),
            LocalDateTime.now(),
            LocalDateTime.now(),
            false,
            "Added shares",
            LocalDateTime.now().minusDays(5),
            LocalDateTime.now()
    );

    when(userStockService.updateStockBuy(
            eq(firebaseUid),
            eq(holdingId),
            any(StockTransactionRequestDto.class)
    )).thenReturn(updatedStockDto);

    mockMvc.perform(
                    put("/api/userStock/updateStockBuy/{holdingId}", holdingId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.holdingId").value(holdingId))
            .andExpect(jsonPath("$.tickerSymbol").value("AAPL"))
            .andExpect(jsonPath("$.quantity").value(7))
            .andExpect(jsonPath("$.averagePurchasePrice").value(160));
  }

  // Test PUT /api/userStock/updateStockSell/{holdingId}
  @Test
  void testUpdateStockSell_ReturnsUpdatedStock() throws Exception {

    Long holdingId = 10L;

    StockTransactionRequestDto request =
            new StockTransactionRequestDto(
                    null,
                    BigDecimal.valueOf(2),
                    LocalDateTime.now()
            );

    UserStockDto updatedStockDto = new UserStockDto(
            holdingId,
            null,
            "AAPL",
            "Apple Inc.",
            BigDecimal.valueOf(3),
            "USD",
            BigDecimal.valueOf(150),
            LocalDateTime.now(),
            LocalDateTime.now(),
            false,
            "Partial sell",
            LocalDateTime.now().minusDays(10),
            LocalDateTime.now()
    );

    when(userStockService.updateStockSell(
            eq(firebaseUid),
            eq(holdingId),
            any(StockTransactionRequestDto.class)
    )).thenReturn(updatedStockDto);

    mockMvc.perform(
                    put("/api/userStock/updateStockSell/{holdingId}", holdingId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.holdingId").value(holdingId))
            .andExpect(jsonPath("$.tickerSymbol").value("AAPL"))
            .andExpect(jsonPath("$.quantity").value(3));
  }

}
