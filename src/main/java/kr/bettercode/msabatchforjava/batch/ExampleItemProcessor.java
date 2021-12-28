package kr.bettercode.msabatchforjava.batch;

import java.time.LocalDateTime;
import kr.bettercode.msabatchforjava.model.example.Example;
import kr.bettercode.msabatchforjava.model.examplesummary.ExampleSummary;
import org.springframework.batch.item.ItemProcessor;

public class ExampleItemProcessor implements ItemProcessor<Example, ExampleSummary> {

  @Override
  public ExampleSummary process(final Example example) throws Exception {
    final LocalDateTime datetime = example.getDate().atTime(example.getTime());
    final String content = example.getContent();
    final String summary = example.getTitle() + "\n" + content.substring(0, Math.min(content.length(), 500));

    return new ExampleSummary(datetime, summary);
  }
}
