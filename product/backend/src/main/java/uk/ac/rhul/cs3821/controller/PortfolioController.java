package uk.ac.rhul.cs3821.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs3821.config.AuthContext;
import uk.ac.rhul.cs3821.dto.PortfolioOverviewDto;
import uk.ac.rhul.cs3821.service.impl.PortfolioOverviewServiceImpl;

/**
 * REST controller exposing portfolio overview endpoints for authenticated users.
 */
@CrossOrigin(origins = "http://localhost:5173")
@AllArgsConstructor
@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

  private final AuthContext authContext;
  private final PortfolioOverviewServiceImpl portfolioOverviewServiceImpl;

  /**
   * Returns the portfolio overview for the authenticated user.
   */
  @GetMapping("/overview")
  public ResponseEntity<PortfolioOverviewDto> getOverview() {

    String firebaseUid = authContext.getFirebaseUid();

    PortfolioOverviewDto overview =
            portfolioOverviewServiceImpl.getOverview(firebaseUid);

    return ResponseEntity.ok(overview);
  }

  /**
   * Forces refresh of the portfolio overview for the authenticated user.
   *
   * @return refreshed {@link PortfolioOverviewDto}
   */
  @PostMapping("/overview/refresh")
  public ResponseEntity<PortfolioOverviewDto> refreshOverview() {

    String firebaseUid = authContext.getFirebaseUid();

    return ResponseEntity.ok(
            portfolioOverviewServiceImpl.refreshOverview(firebaseUid)
    );
  }
}
