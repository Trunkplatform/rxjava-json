package com.trunk.rx.json;

import com.trunk.rx.json.path.JsonPath;

import java.util.Objects;


public class JsonPathEvent {
  private final JsonPath matchedPathFragment;
  private final JsonTokenEvent token;

  public JsonPathEvent(JsonPath matchedPathFragment, JsonTokenEvent token) {
    this.matchedPathFragment = matchedPathFragment;
    this.token = token;
  }

  public JsonPath getMatchedPathFragment() {
    return matchedPathFragment;
  }

  public JsonTokenEvent getTokenEvent() {
    return token;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    JsonPathEvent that = (JsonPathEvent) o;
    return Objects.equals(matchedPathFragment, that.matchedPathFragment) &&
      Objects.equals(token, that.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(matchedPathFragment, token);
  }

  @Override
  public String toString() {
    return "JsonPathEvent{matchedPathFragment='" + matchedPathFragment + '\'' + ", token=" + token + '}';
  }
}
