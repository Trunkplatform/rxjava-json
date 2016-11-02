package com.trunk.rx.json.impl;

import com.trunk.rx.json.exception.MalformedPathException;

/**
 * (c) Jayway https://github.com/jayway/JsonPath/blob/master/json-path/src/main/java/com/jayway/jsonpath/internal/CharacterIndex.java
 */
public class CharacterIndex {

  private static final char OPEN_PARENTHESIS = '(';
  private static final char CLOSE_PARENTHESIS = ')';
  private static final char CLOSE_SQUARE_BRACKET = ']';
  private static final char SPACE = ' ';
  private static final char ESCAPE = '\\';
  private static final char SINGLE_QUOTE = '\'';
  private static final char DOUBLE_QUOTE = '"';
  private static final char MINUS = '-';
  private static final char PERIOD = '.';
  private static final char REGEX = '/';

  private final CharSequence charSequence;
  private int position;

  public CharacterIndex(CharSequence charSequence) {
    this.charSequence = charSequence;
    this.position = 0;
  }

  public int length() {
    return charSequence.length();
  }

  public char charAt(int idx) {
    return charSequence.charAt(idx);
  }

  public char currentChar() {
    return charSequence.charAt(position);
  }

  public boolean currentCharIs(char c) {
    return (charSequence.charAt(position) == c);
  }

  public boolean nextCharIs(char c) {
    return inBounds(position + 1) && (charSequence.charAt(position + 1) == c);
  }

  public int incrementPosition(int charCount) {
    return setPosition(position + charCount);
  }

  public int setPosition(int newPosition) {
    //position = min(newPosition, charSequence.length() - 1);
    position = newPosition;
    return position;
  }

  public int position() {
    return position;
  }

  public int indexOfClosingSquareBracket(int startPosition) {
    int readPosition = startPosition;
    while(inBounds(readPosition)) {
      if (charAt(readPosition) == CLOSE_SQUARE_BRACKET) {
        return readPosition;
      }
      readPosition++;
    }
    return -1;
  }

  public int indexOfMatchingCloseChar(int startPosition, char openChar, char closeChar, boolean skipStrings, boolean skipRegex) {
    if (charAt(startPosition) != openChar) {
      throw new MalformedPathException("Expected " + openChar + " but found " + charAt(startPosition));
    }

    int opened = 1;
    int readPosition = startPosition + 1;
    while(inBounds(readPosition)) {
      if (skipStrings) {
        char quoteChar = charAt(readPosition);
        if (quoteChar == SINGLE_QUOTE || quoteChar == DOUBLE_QUOTE) {
          readPosition = nextIndexOfUnescaped(readPosition, quoteChar);
          if (readPosition == -1) {
            throw new MalformedPathException("Could not find matching close quote for " + quoteChar + " when parsing : " + charSequence);
          }
          readPosition++;
        }
      }
      if (skipRegex) {
        if (charAt(readPosition) == REGEX) {
          readPosition = nextIndexOfUnescaped(readPosition, REGEX);
          if (readPosition == -1) {
            throw new MalformedPathException("Could not find matching close for " + REGEX + " when parsing regex in : " + charSequence);
          }
          readPosition++;
        }
      }
      if (charAt(readPosition) == openChar) {
        opened++;
      }
      if (charAt(readPosition) == closeChar) {
        opened--;
        if (opened == 0) {
          return readPosition;
        }
      }
      readPosition++;
    }
    return -1;
  }

  public int indexOfClosingBracket(int startPosition, boolean skipStrings, boolean skipRegex) {
    return indexOfMatchingCloseChar(startPosition, OPEN_PARENTHESIS, CLOSE_PARENTHESIS, skipStrings, skipRegex);
  }

  public int indexOfNextSignificantChar(char c) {
    return indexOfNextSignificantChar(position, c);
  }

  public int indexOfNextSignificantChar(int startPosition, char c) {
    int readPosition = startPosition + 1;
    while(!isOutOfBounds(readPosition) && charAt(readPosition) == SPACE) {
      readPosition++;
    }
    if (charAt(readPosition) == c) {
      return readPosition;
    } else {
      return -1;
    }
  }

  public int nextIndexOf(char c) {
    return nextIndexOf(position + 1, c);
  }

  public int nextIndexOf(int startPosition, char c) {
    int readPosition = startPosition;
    while(!isOutOfBounds(readPosition)) {
      if (charAt(readPosition) == c) {
        return readPosition;
      }
      readPosition++;
    }
    return -1;
  }

  public int nextIndexOfUnescaped(char c) {
    return nextIndexOfUnescaped(position, c);
  }

  public int nextIndexOfUnescaped(int startPosition, char c) {

    int readPosition = startPosition + 1;
    boolean inEscape = false;
    while(!isOutOfBounds(readPosition)) {
      if (inEscape) {
        inEscape = false;
      } else if ('\\' == charAt(readPosition)) {
        inEscape = true;
      } else if (c == charAt(readPosition)) {
        return readPosition;
      }
      readPosition++;
    }
    return -1;
  }

  public char charAtOr(int postition, char defaultChar) {
    if (!inBounds(postition)) {
      return defaultChar;
    } else {
      return charAt(postition);
    }
  }

  public boolean nextSignificantCharIs(int startPosition, char c) {
    int readPosition = startPosition + 1;
    while(!isOutOfBounds(readPosition) && charAt(readPosition) == SPACE) {
      readPosition++;
    }
    return !isOutOfBounds(readPosition) && charAt(readPosition) == c;
  }

  public boolean nextSignificantCharIs(char c) {
    return nextSignificantCharIs(position, c);
  }

  public char nextSignificantChar() {
    return nextSignificantChar(position);
  }

  public char nextSignificantChar(int startPosition) {
    int readPosition = startPosition + 1;
    while(!isOutOfBounds(readPosition) && charAt(readPosition) == SPACE) {
      readPosition++;
    }
    if (!isOutOfBounds(readPosition)) {
      return charAt(readPosition);
    } else {
      return ' ';
    }
  }

  public void readSignificantChar(char c) {
    if (skipBlanks().currentChar() != c) {
      throw new MalformedPathException(String.format("Expected character: %c", c));
    }
    incrementPosition(1);
  }

  public void readSignificantSubSequence(CharSequence s) {
    skipBlanks();
    if (!inBounds(position + s.length() - 1)) {
      throw new MalformedPathException(String.format("End of string reached while expecting: %s", s));
    }
    if (!subSequence(position, position + s.length()).equals(s)) {
      throw new MalformedPathException(String.format("Expected: %s", s));
    }

    incrementPosition(s.length());
  }

  public int indexOfPreviousSignificantChar(int startPosition) {
    int readPosition = startPosition - 1;
    while(!isOutOfBounds(readPosition) && charAt(readPosition) == SPACE) {
      readPosition--;
    }
    if (!isOutOfBounds(readPosition)) {
      return readPosition;
    } else {
      return -1;
    }
  }

  public int indexOfPreviousSignificantChar() {
    return indexOfPreviousSignificantChar(position);
  }

  public char previousSignificantChar(int startPosition) {
    int previousSignificantCharIndex = indexOfPreviousSignificantChar(startPosition);
    if (previousSignificantCharIndex == -1) {
      return ' ';
    } else {
      return charAt(previousSignificantCharIndex);
    }
  }

  public char previousSignificantChar() {
    return previousSignificantChar(position);
  }

  public boolean currentIsTail() {
    return position >= charSequence.length() - 1;
  }

  public boolean hasMoreCharacters() {
    return inBounds(position + 1);
  }

  public boolean inBounds(int idx) {
    return (idx >= 0) && (idx < charSequence.length());
  }

  public boolean inBounds() {
    return inBounds(position);
  }

  public boolean isOutOfBounds(int idx) {
    return !inBounds(idx);
  }

  public CharSequence subSequence(int start, int end) {
    return charSequence.subSequence(start, end);
  }

  public CharSequence charSequence() {
    return charSequence;
  }

  @Override
  public String toString() {
    return charSequence.toString();
  }

  public boolean isNumberCharacter(int readPosition) {
    char c = charAt(readPosition);
    return Character.isDigit(c) || c == MINUS || c == PERIOD;
  }

  public CharacterIndex skipBlanks() {
    while(inBounds() && currentChar() == SPACE) {
      incrementPosition(1);
    }
    return this;
  }
}
