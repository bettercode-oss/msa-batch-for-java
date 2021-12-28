package kr.bettercode.msabatchforjava.config;

import kr.bettercode.msabatchforjava.batch.ExampleItemProcessor;
import kr.bettercode.msabatchforjava.listener.JobCompletionNotificationListener;
import kr.bettercode.msabatchforjava.model.example.Example;
import kr.bettercode.msabatchforjava.model.examplesummary.ExampleSummary;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisCursorItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final SqlSessionFactory sqlSessionFactory;

  public BatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
      SqlSessionFactory sqlSessionFactory) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
    this.sqlSessionFactory = sqlSessionFactory;
  }

  /**
   * Item을 읽어오는 Reader를 정의합니다.
   */
  @Bean
  public MyBatisCursorItemReader<Example> reader() {
    return new MyBatisCursorItemReaderBuilder<Example>()
        .sqlSessionFactory(sqlSessionFactory)
        .queryId("kr.bettercode.msabatchforjava.repository.ExampleRepository.getExample")
        .build();
  }

  /**
   * 데이터를 가공하는 역할을 합니다. </br> 필수적이지 않으나, 이를 사용하는 이유는 비즈니스 로직을 분리하기 위함입니다.
   */
  @Bean
  public ExampleItemProcessor processor() {
    return new ExampleItemProcessor();
  }

  /**
   * 프로세스가 완료된 데이터를 기록하는 Writer를 정의합니다.
   */
  @Bean
  public MyBatisBatchItemWriter<ExampleSummary> writer() {
    return new MyBatisBatchItemWriterBuilder<ExampleSummary>()
        .sqlSessionFactory(sqlSessionFactory)
        .statementId("kr.bettercode.msabatchforjava.repository.ExampleSummaryRepository.save")
        .build();
  }

  @Bean
  public Job exampleBatchJob(JobCompletionNotificationListener listener, Step step1) {
    return jobBuilderFactory.get("exampleBatchJob") // Job의 이름
        .incrementer(new RunIdIncrementer()) // 동일 Job Parameter로 Job을 다시 실행할 수 있게 해줍니다.
        .listener(listener) // 전후처리 담당(job, step ...)
        .flow(step1) // step의 흐름을 제어합니다. 간단하게 step만 실행하는 flow입니다.
        .end()
        .build();
  }

  @Bean
  public Step step1(MyBatisBatchItemWriter<ExampleSummary> writer) {
    return stepBuilderFactory.get("step1") // Step의 이름
        .<Example, ExampleSummary>chunk(10) // 한 번에 실행할 작업의 개수(한 트랜잭션으로 묶임)
        .faultTolerant().retryLimit(3).retry(DataAccessException.class) // 재시도 설정
        .reader(reader())
        .processor(processor())
        .writer(writer)
        .build();
  }
}
