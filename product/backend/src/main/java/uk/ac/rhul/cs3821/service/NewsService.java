package uk.ac.rhul.cs3821.service;

import uk.ac.rhul.cs3821.dto.NewsDigestDto;

/**
 * Service defining operations for generating and refreshing user news digests.
 * Coordinates article retrieval, caching, and personalisation logic.
 */
public interface NewsService {

  /**
   * Returns a personalised news digest for a user.
   * If a valid (non-expired) digest exists, it is returned.
   * Otherwise, a new digest is generated and cached.
   *
   * @param firebaseUid the user UID
   * @return personalised NewsDigestDto
   */
  NewsDigestDto getPersonalisedNewsDigest(String firebaseUid);

  /**
   * Regenerates the personalised news digest for the given user.
   *
   * @param firebaseUid the authenticated user identifier
   * @return refreshed {@link NewsDigestDto} instance
   */
  NewsDigestDto refreshDigest(String firebaseUid);
}