package kr.bettercode.msabatchforjava.domain.examplesummary;

import java.time.LocalDateTime;

public class ExampleSummary {

  private Long id;
  private LocalDateTime datetime;
  private String summary;

  public ExampleSummary(LocalDateTime datetime, String summary) {
    this.datetime = datetime;
    this.summary = summary;
  }
}
