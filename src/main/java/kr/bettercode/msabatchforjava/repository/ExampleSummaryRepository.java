package kr.bettercode.msabatchforjava.repository;

import kr.bettercode.msabatchforjava.model.examplesummary.ExampleSummary;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExampleSummaryRepository {

  void save(ExampleSummary exampleSummary);
}
