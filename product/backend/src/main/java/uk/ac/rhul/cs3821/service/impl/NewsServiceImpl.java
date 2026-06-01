package uk.ac.rhul.cs3821.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.ac.rhul.cs3821.dto.NewsArticleDto;
import uk.ac.rhul.cs3821.dto.NewsDigestDto;
import uk.ac.rhul.cs3821.dto.UserCryptoDto;
import uk.ac.rhul.cs3821.dto.UserStockDto;
import uk.ac.rhul.cs3821.entity.NewsArticleCache;
import uk.ac.rhul.cs3821.entity.User;
import uk.ac.rhul.cs3821.entity.UserNewsDigest;
import uk.ac.rhul.cs3821.mapper.MarketAuxNewsMapper;
import uk.ac.rhul.cs3821.mapper.NewsArticleMapper;
import uk.ac.rhul.cs3821.marketaux.MarketAuxArticle;
import uk.ac.rhul.cs3821.marketaux.MarketAuxResponse;
import uk.ac.rhul.cs3821.repository.NewsArticleCacheRepository;
import uk.ac.rhul.cs3821.repository.UserNewsDigestRepository;
import uk.ac.rhul.cs3821.repository.UserRepository;
import uk.ac.rhul.cs3821.service.MarketAuxService;
import uk.ac.rhul.cs3821.service.NewsService;
import uk.ac.rhul.cs3821.service.UserCryptoService;
import uk.ac.rhul.cs3821.service.UserStockService;
import uk.ac.rhul.cs3821.util.DigestJsonUtil;
import uk.ac.rhul.cs3821.util.UrlHashUtil;

/**
 * Implementation of {@link NewsService} handling personalised digest generation.
 * Applies caching, rotation, cooldown, and provider integration logic.
 */
@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

  private static final String PROVIDER = "MARKETAUX";
  private static final int DIGEST_TTL_HOURS = 6;
  private static final String MODEL_VERSION = "v1";
  private static final int MAX_DIGEST_ARTICLES = 15;
  private static final int ARTICLE_POOL_DAYS = 7;
  private static final int REFRESH_COOLDOWN_MINUTES = 3;

  private final MarketAuxService marketAuxService;
  private final UserStockService userStockService;
  private final UserCryptoService userCryptoService;
  private final UserRepository userRepository;
  private final NewsArticleCacheRepository newsArticleCacheRepository;
  private final UserNewsDigestRepository userNewsDigestRepository;

  @Value("${news.rotation.chunk-size}")
  private int rotationChunkSize;

  /**
   * Returns the next rotation chunk of symbols based on the last index.
   * Wraps around the list to ensure cyclic distribution.
   *
   * @param symbols   list of user asset symbols
   * @param lastIndex last processed symbol index
   * @param chunkSize maximum number of symbols to return
   * @return next cyclic subset of symbols
   */
  private List<String> getNextSymbolChunk(
          List<String> symbols,
          int lastIndex,
          int chunkSize
  ) {

    if (symbols.isEmpty()) {
      return List.of();
    }

    int size = symbols.size();
    int actualChunkSize = Math.min(chunkSize, size);

    List<String> chunk = new ArrayList<>();

    for (int i = 0; i < actualChunkSize; i++) {
      int index = (lastIndex + i) % size;
      chunk.add(symbols.get(index));
    }

    return chunk;
  }

  /**
   * Generates or returns a cached personalized news digest for the user.
   * Uses rotation, article pooling, and TTL-based caching strategy.
   *
   * @param firebaseUid the authenticated user identifier
   * @return {@link NewsDigestDto} representing the user's news digest
   */
  @Override
  public NewsDigestDto getPersonalisedNewsDigest(String firebaseUid) {

    User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // 1 Cache-first
    Optional<UserNewsDigest> validDigest =
            userNewsDigestRepository
                    .findTopByUser_IdAndExpiresAtAfterOrderByGeneratedAtDesc(
                            user.getId(), LocalDateTime.now());

    if (validDigest.isPresent()) {
      return mapDigestToDto(validDigest.get());
    }

    // 2 Build user symbols
    List<String> stockSymbols =
            userStockService.getAllUserStocksByFirebaseUid(firebaseUid)
                    .stream()
                    .map(UserStockDto::getTickerSymbol)
                    .toList();

    List<String> cryptoSymbols =
            userCryptoService.getAllUserCryptoByFirebaseUid(firebaseUid)
                    .stream()
                    .map(UserCryptoDto::getSymbol)
                    .toList();

    List<String> combined =
            new ArrayList<>(new HashSet<>(stockSymbols));

    combined.addAll(cryptoSymbols);

    if (combined.isEmpty()) {
      return emptyDigest();
    }

    //️ 3 Load user-filtered 7-day article pool
    LocalDateTime cutoff =
            LocalDateTime.now().minusDays(ARTICLE_POOL_DAYS);

    List<NewsArticleCache> recentArticles =
            combined.stream()
                    .flatMap(symbol ->
                            newsArticleCacheRepository
                                    .findBySymbolWithinEntities(
                                            cutoff,
                                            symbol.toUpperCase()
                                    ).stream()
                    )
                    .distinct()
                    .sorted((a, b) ->
                            b.getPublishedAt().compareTo(a.getPublishedAt()))
                    .toList();

    // Rotation state
    int lastIndex = 0;

    Optional<UserNewsDigest> latest =
            userNewsDigestRepository
                    .findTopByUser_IdOrderByGeneratedAtDesc(user.getId());

    if (latest.isPresent()
            && latest.get().getLastSymbolIndex() != null) {
      lastIndex = latest.get().getLastSymbolIndex();
    }

    // Always compute next chunk
    List<String> nextChunk =
            getNextSymbolChunk(combined, lastIndex, rotationChunkSize);

    // Advance rotation index regardless

    // Only call API if pool is small
    if (recentArticles.size() < MAX_DIGEST_ARTICLES) {

      String symbols = String.join(",", nextChunk);

      MarketAuxResponse response =
              marketAuxService.fetchNewsBySymbols(symbols);

      if (response != null && response.data() != null) {

        response.data().stream()
                .map(this::cacheIfNotExists)
                .toList();

        // reload filtered pool
        recentArticles =
                combined.stream()
                        .flatMap(symbol ->
                                newsArticleCacheRepository
                                        .findBySymbolWithinEntities(
                                                cutoff,
                                                symbol.toUpperCase()
                                        ).stream()
                        )
                        .distinct()
                        .sorted((a, b) ->
                                b.getPublishedAt()
                                        .compareTo(a.getPublishedAt()))
                        .toList();
      }
    }

    // 5 Limit to max articles
    List<NewsArticleCache> finalArticles =
            recentArticles.stream()
                    .limit(MAX_DIGEST_ARTICLES)
                    .toList();

    // Advance rotation index regardless
    int newIndex =
            (lastIndex + nextChunk.size()) % combined.size();

    // 6 Create digest
    UserNewsDigest digest = new UserNewsDigest();
    digest.setUser(user);
    digest.setGeneratedAt(LocalDateTime.now());
    digest.setExpiresAt(
            LocalDateTime.now().plusHours(DIGEST_TTL_HOURS));
    digest.setModelVersion(MODEL_VERSION);
    digest.setLastSymbolIndex(newIndex);

    List<String> urlHashes =
            finalArticles.stream()
                    .map(NewsArticleCache::getUrlHash)
                    .distinct()
                    .toList();

    digest.setDigestJson(
            DigestJsonUtil.toUrlHashJson(urlHashes));

    userNewsDigestRepository.save(digest);

    return mapDigestToDto(digest);
  }

  /**
   * Caches a MarketAux article if not already stored.
   *
   * @param article provider article payload
   * @return persisted {@link NewsArticleCache} entity
   */
  private NewsArticleCache cacheIfNotExists(
          MarketAuxArticle article) {

    String urlHash =
            UrlHashUtil.sha256(article.url());

    return newsArticleCacheRepository
            .findByProviderAndUrlHash(PROVIDER, urlHash)
            .orElseGet(() -> newsArticleCacheRepository.save(
                    MarketAuxNewsMapper.mapToNewsArticleCache(
                            article,
                            PROVIDER,
                            urlHash,
                            article.toString()
                    )
            ));
  }

  /**
   * Maps a stored {@link UserNewsDigest} entity to a DTO.
   *
   * @param digest persisted digest entity
   * @return mapped {@link NewsDigestDto}
   */
  private NewsDigestDto mapDigestToDto(
          UserNewsDigest digest) {

    List<String> hashes =
            DigestJsonUtil.parseUrlHashJson(
                    digest.getDigestJson());

    List<NewsArticleDto> articles =
            hashes.isEmpty()
                    ? List.of()
                    : newsArticleCacheRepository
                    .findAllByProviderAndUrlHashIn(
                            PROVIDER, hashes)
                    .stream()
                    .map(NewsArticleMapper
                            ::mapToNewsArticleDto)
                    .toList();

    return new NewsDigestDto(
            digest.getGeneratedAt(),
            digest.getExpiresAt(),
            digest.getModelVersion(),
            articles
    );
  }

  /**
   * Creates an empty digest when the user has no tracked assets.
   *
   * @return empty {@link NewsDigestDto} instance
   */
  private NewsDigestDto emptyDigest() {
    return new NewsDigestDto(
            LocalDateTime.now(),
            LocalDateTime.now().plusMinutes(15),
            MODEL_VERSION,
            List.of()
    );
  }

  /**
   * Forces digest regeneration subject to cooldown constraints.
   * Returns existing digest if refresh cooldown is active.
   *
   * @param firebaseUid the authenticated user identifier
   * @return refreshed {@link NewsDigestDto} instance
   */
  public NewsDigestDto refreshDigest(String firebaseUid) {

    User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Optional<UserNewsDigest> latest =
            userNewsDigestRepository
                    .findTopByUser_IdOrderByGeneratedAtDesc(user.getId());

    if (latest.isPresent()
            && latest.get().getLastRefreshAt() != null) {

      LocalDateTime lastRefresh =
              latest.get().getLastRefreshAt();

      if (lastRefresh.isAfter(
              LocalDateTime.now()
                      .minusMinutes(REFRESH_COOLDOWN_MINUTES))) {

        return mapDigestToDto(latest.get());
      }
    }

    // Force expiry by deleting current digest
    latest.ifPresent(userNewsDigestRepository::delete);

    NewsDigestDto dto =
            getPersonalisedNewsDigest(firebaseUid);

    // Update last refresh timestamp
    Optional<UserNewsDigest> newDigest =
            userNewsDigestRepository
                    .findTopByUser_IdOrderByGeneratedAtDesc(user.getId());

    newDigest.ifPresent(d -> {
      d.setLastRefreshAt(LocalDateTime.now());
      userNewsDigestRepository.save(d);
    });

    return dto;
  }
}