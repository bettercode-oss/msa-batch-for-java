package kr.bettercode.msabatchforjava.model.example;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Example {

  private Long id;
  private LocalDate date;
  private LocalTime time;
  private String title;
  private String content;
}
