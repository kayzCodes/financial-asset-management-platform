package uk.ac.rhul.cs3821.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.ac.rhul.cs3821.config.AuthContext;
import uk.ac.rhul.cs3821.dto.AddCryptoRequestDto;
import uk.ac.rhul.cs3821.dto.ChartPointDto;
import uk.ac.rhul.cs3821.dto.CryptoDetailsDto;
import uk.ac.rhul.cs3821.dto.CryptoTransactionRequestDto;
import uk.ac.rhul.cs3821.dto.UserCryptoDto;
import uk.ac.rhul.cs3821.dto.UserDto;
import uk.ac.rhul.cs3821.service.UserCryptoService;

public class UserCryptoControllerTest {

  private final String firebaseUid = "Hn3iuGlnuxVjcgu6Rtb7WEKSncm1";
  CryptoTransactionRequestDto cryptoTransactionRequestDto;
  AddCryptoRequestDto addCryptoRequestDto;
  private MockMvc mockMvc;
  @Mock
  private AuthContext authContext;
  @Mock
  private UserCryptoService userCryptoService;
  @InjectMocks
  private UserCryptoController userCryptoController;
  private ObjectMapper objectMapper;
  private UserCryptoDto userCryptoDto;
  private UserDto userDto;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    ObjectMapper mapper = new ObjectMapper();
    mapper.findAndRegisterModules();

    mockMvc = MockMvcBuilders
            .standaloneSetup(userCryptoController)
            .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
            .build();

    when(authContext.getFirebaseUid())
            .thenReturn("Hn3iuGlnuxVjcgu6Rtb7WEKSncm1");

    objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();

    LocalDateTime now = LocalDateTime.now();

    userDto = new UserDto(
            1L, firebaseUid, "Alice", "Johnson", "alicej",
            "alice.johnson@example.com", true,
            null, null, null, null, null, "USD",
            "en", "Europe/London", "standard", "system",
            now.minusDays(10), now.minusDays(1), null
    );

    userCryptoDto = new UserCryptoDto(
            1L, userDto,
            "BTC",
            "Bitcoin",
            BigDecimal.valueOf(0.75),
            "USD",
            BigDecimal.valueOf(42000.50),
            now.minusDays(1),
            now,
            false,
            null,
            now.minusDays(2),
            now
    );

    // NEW DTO FOR TRANSACTION
    cryptoTransactionRequestDto = new CryptoTransactionRequestDto(
            BigDecimal.valueOf(42000.50),
            BigDecimal.valueOf(0.75),
            now
    );

    // NEW REQUEST WRAPPER DTO
    addCryptoRequestDto = new AddCryptoRequestDto(
            userCryptoDto,
            cryptoTransactionRequestDto
    );
  }

  // POST /addCrypto — success + IF-statement logic tested here
  @Test
  void testAddCrypto_ReturnsCreatedCrypto() throws Exception {

    UserCryptoDto fixedDto = new UserCryptoDto(
            1L,
            userDto,
            "BTC",
            "Bitcoin",
            BigDecimal.valueOf(0.75),
            "USD",
            BigDecimal.valueOf(42000.50),
            LocalDateTime.now(),
            LocalDateTime.now(),
            false,
            "",
            LocalDateTime.now(),
            LocalDateTime.now()
    );

    when(userCryptoService.addCrypto(
            eq(firebaseUid),
            any(UserCryptoDto.class),
            any(CryptoTransactionRequestDto.class)
    )).thenReturn(fixedDto);

    mockMvc.perform(post("/api/userCrypto/addCrypto")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(addCryptoRequestDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.symbol").value("BTC"))
            .andExpect(jsonPath("$.name").value("Bitcoin"))
            .andExpect(jsonPath("$.quantity").value(0.75))
            .andExpect(jsonPath("$.lastTransactionAt").exists())
            .andExpect(jsonPath("$.createdAt").exists());
  }

  @Test
  void testGetCryptos_ReturnsCryptoList() throws Exception {

    when(userCryptoService.getAllUserCryptoByFirebaseUid(firebaseUid))
            .thenReturn(java.util.List.of(userCryptoDto));

    mockMvc.perform(get("/api/userCrypto/getCryptos")
                    .requestAttr("firebaseUid", firebaseUid))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].symbol").value("BTC"))
            .andExpect(jsonPath("$[0].name").value("Bitcoin"));
  }

  // Test DELETE /api/userCrypto/deleteCrypto/{holdingId}
  @Test
  void testDeleteCrypto_ReturnsDeletedCrypto() throws Exception {

    Long holdingId = 10L;

    UserCryptoDto deletedCryptoDto = new UserCryptoDto(
            holdingId,
            null,
            "BTC",
            "Bitcoin",
            BigDecimal.valueOf(1.5),
            "USD",
            BigDecimal.valueOf(30000),
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now(),
            true,
            "Sold position",
            LocalDateTime.now().minusDays(10),
            LocalDateTime.now()
    );

    when(userCryptoService.deleteCrypto(firebaseUid, holdingId))
            .thenReturn(deletedCryptoDto);

    mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .delete("/api/userCrypto/deleteCrypto/{holdingId}", holdingId)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.holdingId").value(holdingId))
            .andExpect(jsonPath("$.symbol").value("BTC"))
            .andExpect(jsonPath("$.name").value("Bitcoin"))
            .andExpect(jsonPath("$.quantity").value(1.5));
  }

  // Test GET /api/userCrypto/getLiveCryptoDetails/{holdingId}
  @Test
  void testGetLiveCryptoDetails_ReturnsCryptoDetails() throws Exception {

    Long holdingId = 10L;

    CryptoDetailsDto cryptoDetailsDto = new CryptoDetailsDto(
            "BTC",
            "Bitcoin",
            BigDecimal.valueOf(2),
            BigDecimal.valueOf(30000),
            BigDecimal.valueOf(60000),   // latestClose
            null,                        // percentageChange (intentionally null)
            BigDecimal.valueOf(120000),  // currentValue
            BigDecimal.valueOf(60000),   // priceDifference
            BigDecimal.valueOf(100),     // percentageChangeFromAveragePrice
            List.of(
                    new ChartPointDto("2024-01-01", BigDecimal.valueOf(59000)),
                    new ChartPointDto("2024-01-02", BigDecimal.valueOf(60000))
            ),
            null
    );

    when(userCryptoService.getCryptoDetail(firebaseUid, holdingId))
            .thenReturn(cryptoDetailsDto);

    mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .get("/api/userCrypto/getLiveCryptoDetails/{holdingId}", holdingId)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.symbol").value("BTC"))
            .andExpect(jsonPath("$.name").value("Bitcoin"))
            .andExpect(jsonPath("$.latestClose").value(60000))
            .andExpect(jsonPath("$.currentValue").value(120000))
            .andExpect(jsonPath("$.chartData.length()").value(2));
  }

  // Test PUT /api/userCrypto/updateCryptoBuy/{holdingId}
  @Test
  void testUpdateCryptoBuy_ReturnsUpdatedCrypto() throws Exception {

    Long holdingId = 10L;

    CryptoTransactionRequestDto request =
            new CryptoTransactionRequestDto(
                    BigDecimal.valueOf(35000),
                    BigDecimal.valueOf(1),
                    LocalDateTime.now()
            );

    UserCryptoDto updatedCryptoDto = new UserCryptoDto(
            holdingId,
            null,
            "BTC",
            "Bitcoin",
            BigDecimal.valueOf(3),
            "USD",
            BigDecimal.valueOf(35000),
            LocalDateTime.now(),
            LocalDateTime.now(),
            false,
            "Added position",
            LocalDateTime.now().minusDays(5),
            LocalDateTime.now()
    );

    when(userCryptoService.updateCryptoBuy(
            eq(firebaseUid),
            eq(holdingId),
            any(CryptoTransactionRequestDto.class)
    )).thenReturn(updatedCryptoDto);

    mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .put("/api/userCrypto/updateCryptoBuy/{holdingId}", holdingId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.holdingId").value(holdingId))
            .andExpect(jsonPath("$.symbol").value("BTC"))
            .andExpect(jsonPath("$.quantity").value(3))
            .andExpect(jsonPath("$.averagePurchasePrice").value(35000));
  }

  // Test PUT /api/userCrypto/updateCryptoSell/{holdingId}
  @Test
  void testUpdateCryptoSell_ReturnsUpdatedCrypto() throws Exception {

    Long holdingId = 10L;

    CryptoTransactionRequestDto request =
            new CryptoTransactionRequestDto(
                    null,
                    BigDecimal.valueOf(1),
                    LocalDateTime.now()
            );

    UserCryptoDto updatedCryptoDto = new UserCryptoDto(
            holdingId,
            null,
            "BTC",
            "Bitcoin",
            BigDecimal.valueOf(1),
            "USD",
            BigDecimal.valueOf(30000),
            LocalDateTime.now(),
            LocalDateTime.now(),
            false,
            "Partial sell",
            LocalDateTime.now().minusDays(10),
            LocalDateTime.now()
    );

    when(userCryptoService.updateCryptoSell(
            eq(firebaseUid),
            eq(holdingId),
            any(CryptoTransactionRequestDto.class)
    )).thenReturn(updatedCryptoDto);

    mockMvc.perform(
                    put("/api/userCrypto/updateCryptoSell/{holdingId}", holdingId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.holdingId").value(holdingId))
            .andExpect(jsonPath("$.symbol").value("BTC"))
            .andExpect(jsonPath("$.quantity").value(1));
  }

}
