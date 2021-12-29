package kr.bettercode.msabatchforjava.config;

import kr.bettercode.msabatchforjava.batch.ExampleItemProcessor;
import kr.bettercode.msabatchforjava.batch.ExampleItemTrimProcessor;
import kr.bettercode.msabatchforjava.listener.JobCompletionNotificationListener;
import kr.bettercode.msabatchforjava.model.example.Example;
import kr.bettercode.msabatchforjava.model.examplesummary.ExampleSummary;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisCursorItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
@EnableBatchProcessing
public class BatchConfiguration {

  private static final String SUMMARY_JOB = "summary";

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final SqlSessionFactory sqlSessionFactory;
  private final JobCompletionNotificationListener listener;
  private final JobLauncher jobLauncher;

  public BatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
      SqlSessionFactory sqlSessionFactory, JobCompletionNotificationListener listener, JobLauncher jobLauncher) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
    this.sqlSessionFactory = sqlSessionFactory;
    this.listener = listener;
    this.jobLauncher = jobLauncher;
  }

  /**
   * Item을 읽어오는 Reader를 정의합니다.
   */
  @Bean(name = SUMMARY_JOB + "_reader")
  @StepScope
  public MyBatisCursorItemReader<Example> reader() {
    return new MyBatisCursorItemReaderBuilder<Example>()
        .sqlSessionFactory(sqlSessionFactory)
        .queryId("kr.bettercode.msabatchforjava.repository.ExampleRepository.getExample")
        .build();
  }

  /**
   * <p>
   * 데이터를 가공하는 역할을 합니다.
   * </p>
   * <p>
   * 필수적이지 않으나, 이를 사용하는 이유는 비즈니스 로직을 분리하기 위함입니다.
   * </p>
   */
  @Bean(name = SUMMARY_JOB + "_trim_processsor")
  @StepScope
  public ExampleItemTrimProcessor trimProcessor() {
    return new ExampleItemTrimProcessor();
  }

  /**
   * <p>
   * 데이터를 가공하는 역할을 합니다.
   * </p>
   * <p>
   * 필수적이지 않으나, 이를 사용하는 이유는 비즈니스 로직을 분리하기 위함입니다.
   * </p>
   */
  @Bean(name = SUMMARY_JOB + "_processsor")
  @StepScope
  public ExampleItemProcessor processor() {
    return new ExampleItemProcessor();
  }

  /**
   * 프로세스가 완료된 데이터를 기록하는 Writer를 정의합니다.
   */
  @Bean(name = SUMMARY_JOB + "_trim_writer")
  @StepScope
  public MyBatisBatchItemWriter<Example> trimWriter() {
    return new MyBatisBatchItemWriterBuilder<Example>()
        .sqlSessionFactory(sqlSessionFactory)
        .statementId("kr.bettercode.msabatchforjava.repository.ExampleRepository.update")
        .build();
  }

  /**
   * 프로세스가 완료된 데이터를 기록하는 Writer를 정의합니다.
   */
  @Bean(name = SUMMARY_JOB + "_writer")
  @StepScope
  public MyBatisBatchItemWriter<ExampleSummary> writer() {
    return new MyBatisBatchItemWriterBuilder<ExampleSummary>()
        .sqlSessionFactory(sqlSessionFactory)
        .statementId("kr.bettercode.msabatchforjava.repository.ExampleSummaryRepository.save")
        .build();
  }

  @Bean(name = SUMMARY_JOB)
  public Job exampleBatchJob() {
    return jobBuilderFactory.get("exampleBatchJob") // Job의 이름
//        .incrementer(new RunIdIncrementer()) // 동일 Job Parameter로 Job을 다시 실행할 수 있게 해줍니다.
        .listener(listener) // 전후처리 담당(job, step ...)
        .flow(step1()) // step의 흐름을 제어합니다. 간단하게 step만 실행하는 flow입니다.
        .next(step2()) // 다음 flow를 정의합니다.
        .end()
        .build();
  }

  /**
   * content의 trim을 수행합니다.
   */
  @Bean(name = SUMMARY_JOB + "_step1")
  public Step step1() {
    return stepBuilderFactory.get("step1") // Step의 이름
        .<Example, Example>chunk(10) // 한 번에 실행할 작업의 개수(한 트랜잭션으로 묶임)
        .faultTolerant().retryLimit(3).retry(DataAccessException.class) // 재시도 설정
        .reader(reader())
        .processor(trimProcessor())
        .writer(trimWriter())
        .build();
  }

  /**
   * 요약정보를 만들고 이를 요약 테이블에 저장합니다.
   */
  @Bean(name = SUMMARY_JOB + "_step2")
  public Step step2() {
    return stepBuilderFactory.get("step2") // Step의 이름
        .<Example, ExampleSummary>chunk(10) // 한 번에 실행할 작업의 개수(한 트랜잭션으로 묶임)
        .faultTolerant().retryLimit(3).retry(DataAccessException.class) // 재시도 설정
        .reader(reader())
        .processor(processor())
        .writer(writer())
        .build();
  }

  @Scheduled(cron = "0 * * * * *") // https://spring.io/blog/2020/11/10/new-in-spring-5-3-improved-cron-expressions
  public void runJob() {
    JobParameters jobParameters = new JobParametersBuilder()
        .addLong("time", System.currentTimeMillis())
        .toJobParameters();

    try {
      final JobExecution execution = jobLauncher.run(exampleBatchJob(), jobParameters);
      log.info("Job이 {} 상태로 종료되었습니다.", execution.getStatus());
    } catch (JobInstanceAlreadyCompleteException e) {
      log.info("이미 완료된 Job 입니다. 호출한 파라미터: {}", jobParameters);
    } catch (JobExecutionAlreadyRunningException | JobRestartException | JobParametersInvalidException e) {
      log.error("Job 실행 중 오류가 발생했습니다.", e);
    }
  }
}
