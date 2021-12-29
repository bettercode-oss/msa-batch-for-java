package kr.bettercode.msabatchforjava.batch;

import kr.bettercode.msabatchforjava.model.example.Example;
import org.springframework.batch.item.ItemProcessor;

public class ExampleItemTrimProcessor implements ItemProcessor<Example, Example> {

  @Override
  public Example process(final Example example) throws Exception {
    example.setContent(example.getContent().trim());
    return example;
  }
}
