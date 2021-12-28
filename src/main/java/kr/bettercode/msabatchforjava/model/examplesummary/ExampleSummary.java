package kr.bettercode.msabatchforjava.model.examplesummary;

import java.time.LocalDateTime;

public class ExampleSummary {

  private Long id;
  private LocalDateTime datetime;
  private String summary;

  public ExampleSummary(LocalDateTime datetime, String summary) {
    this.datetime = datetime;
    this.summary = summary;
  }

  public ExampleSummary(Long id, LocalDateTime datetime, String summary) {
    this.id = id;
    this.datetime = datetime;
    this.summary = summary;
  }

  @Override
  public String toString() {
    return "ExampleSummary{" +
        "id=" + id +
        ", datetime=" + datetime +
        ", summary='" + summary + '\'' +
        '}';
  }
}
