package com.hitachiconsulting.digitalplatform.exception;

public class LeaderBoardSourceNotFoundException extends Exception {

  private static final long serialVersionUID = 1L;

  public LeaderBoardSourceNotFoundException() {
    super();
  }

  public LeaderBoardSourceNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public LeaderBoardSourceNotFoundException(String message) {
    super(message);
  }

  public LeaderBoardSourceNotFoundException(Throwable cause) {
    super(cause);
  }

}
