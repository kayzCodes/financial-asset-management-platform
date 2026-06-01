package uk.ac.rhul.cs3821.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs3821.config.AuthContext;
import uk.ac.rhul.cs3821.dto.NewsDigestDto;
import uk.ac.rhul.cs3821.service.NewsService;

/**
 * REST controller exposing personalized news digest endpoints.
 * Requires authenticated user context for all operations.
 */

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

  private final NewsService newsService;
  private final AuthContext authContext;


  /**
   * Returns the cached or freshly generated personalized news digest
   * for the authenticated user.
   *
   * @return {@link NewsDigestDto} representing the user's news digest
   */
  @GetMapping("/getDigest")
  public ResponseEntity<NewsDigestDto> getDigest() {

    String firebaseUid = authContext.getFirebaseUid();

    NewsDigestDto digest =
            newsService.getPersonalisedNewsDigest(firebaseUid);

    return ResponseEntity.ok(digest);
  }

  /**
   * Forces regeneration of the personalized news digest for the
   * authenticated user.
   *
   * @return refreshed {@link NewsDigestDto} instance
   */
  @PostMapping("/refresh")
  public ResponseEntity<NewsDigestDto> refreshNews() {

    String firebaseUid = authContext.getFirebaseUid();

    return ResponseEntity.ok(
            newsService.refreshDigest(firebaseUid)
    );
  }
}