package kr.bettercode.msabatchforjava.domain.example;

import java.time.LocalDate;
import java.time.LocalTime;

public class Example {

  private Long id;
  private LocalDate date;
  private LocalTime time;
  private String title;
  private String content;

  public Long getId() {
    return id;
  }

  public LocalDate getDate() {
    return date;
  }

  public LocalTime getTime() {
    return time;
  }

  public String getTitle() {
    return title;
  }

  public String getContent() {
    return content;
  }
}
