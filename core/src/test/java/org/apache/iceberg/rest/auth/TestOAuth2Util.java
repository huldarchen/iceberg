/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.iceberg.rest.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class TestOAuth2Util {
  @Test
  public void testOAuthScopeTokenValidation() {
    // valid scope tokens are from ascii 0x21 to 0x7E, excluding 0x22 (") and 0x5C (\)
    // test characters that are outside of the ! to ~ range and the excluded characters, " and \
    assertThat(OAuth2Util.isValidScopeToken("a\\b"))
        .as("Should reject scope token with \\")
        .isFalse();
    assertThat(OAuth2Util.isValidScopeToken("a b"))
        .as("Should reject scope token with space")
        .isFalse();
    assertThat(OAuth2Util.isValidScopeToken("a\"b"))
        .as("Should reject scope token with \"")
        .isFalse();
    assertThat(OAuth2Util.isValidScopeToken("\u007F"))
        .as("Should reject scope token with DEL")
        .isFalse();
    // test all characters that are inside of the ! to ~ range and are not excluded
    assertThat(OAuth2Util.isValidScopeToken("!#$%&'()*+,-./"))
        .as("Should accept scope token with !-/")
        .isTrue();
    assertThat(OAuth2Util.isValidScopeToken("0123456789"))
        .as("Should accept scope token with 0-9")
        .isTrue();
    assertThat(OAuth2Util.isValidScopeToken(":;<=>?@"))
        .as("Should accept scope token with :-@")
        .isTrue();
    assertThat(OAuth2Util.isValidScopeToken("ABCDEFGHIJKLM"))
        .as("Should accept scope token with A-M")
        .isTrue();
    assertThat(OAuth2Util.isValidScopeToken("NOPQRSTUVWXYZ"))
        .as("Should accept scope token with N-Z")
        .isTrue();
    assertThat(OAuth2Util.isValidScopeToken("[]^_`"))
        .as("Should accept scope token with [-`, not \\")
        .isTrue();
    assertThat(OAuth2Util.isValidScopeToken("abcdefghijklm"))
        .as("Should accept scope token with a-m")
        .isTrue();
    assertThat(OAuth2Util.isValidScopeToken("nopqrstuvwxyz"))
        .as("Should accept scope token with n-z")
        .isTrue();
    assertThat(OAuth2Util.isValidScopeToken("{|}~"))
        .as("Should accept scope token with {-~")
        .isTrue();
  }

  @Test
  public void testExpiresAt() {
    assertThat(OAuth2Util.expiresAtMillis(null)).isNull();
    assertThat(OAuth2Util.expiresAtMillis("not a token")).isNull();
    assertThat(OAuth2Util.expiresAtMillis("a.b.c")).isNull();

    // expires at epoch second = 1
    String token =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjF9.gQADTbdEv-rpDWKSkGLbmafyB5UUjTdm9B_1izpuZ6E";
    assertThat(OAuth2Util.expiresAtMillis(token)).isEqualTo(1000L);

    // expires at epoch second = 19999999999
    token =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE5OTk5OTk5OTk5fQ._3k92KJi2NTyTG6V1s2mzJ__GiQtL36DnzsZSkBdYPw";
    assertThat(OAuth2Util.expiresAtMillis(token)).isEqualTo(19999999999000L);

    // no expiration
    token =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    assertThat(OAuth2Util.expiresAtMillis(token)).isNull();
  }
}
