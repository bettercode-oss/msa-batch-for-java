package kr.bettercode.msabatchforjava.listener;

import kr.bettercode.msabatchforjava.model.examplesummary.ExampleSummary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

  private final JdbcTemplate jdbcTemplate;

  public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
      log.info("!!! JOB FINISHED! Time to verify the results");

      jdbcTemplate.query("SELECT id, datetime, summary FROM example_summary",
          (rs, row) -> new ExampleSummary(
              rs.getLong(1),
              rs.getTimestamp(2).toLocalDateTime(),
              rs.getString(3))
      ).forEach(person -> log.info("Found <" + person + "> in the database."));
    }
  }
}
